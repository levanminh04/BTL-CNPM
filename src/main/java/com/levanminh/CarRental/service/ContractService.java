package com.levanminh.CarRental.service;

import com.levanminh.CarRental.entity.*;
import com.levanminh.CarRental.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ContractService {
    
    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private ContractCarRepository contractCarRepository;
    
    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private CollateralRepository collateralRepository;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;
    
    @Autowired
    private DamageItemRepository damageItemRepository;
    
    @Autowired
    private UserRepository userRepository;      @Transactional
    public void completeContract(Integer contractId) throws RuntimeException {
        if (contractId == null) {
            throw new IllegalArgumentException("Contract ID cannot be null");
        }
        
        try {
            Optional<Contract> contractOpt = contractRepository.findById(contractId);
            if (contractOpt.isEmpty()) {
                throw new RuntimeException("Không tìm thấy hợp đồng với ID: " + contractId);
            }
            
            Contract contract = contractOpt.get();
            
            if ("COMPLETED".equals(contract.getStatus())) {
                throw new RuntimeException("Hợp đồng đã được hoàn thành trước đó");
            }
            
            contract.setStatus("COMPLETED");
            contractRepository.save(contract);
            
            // 2. Cập nhật trạng thái xe
            List<ContractCar> contractCars = contractCarRepository.findByContractId(contractId);
            if (contractCars.isEmpty()) {
                throw new RuntimeException("Không tìm thấy thông tin xe trong hợp đồng");
            }
            
            for (ContractCar contractCar : contractCars) {
                Car car = contractCar.getCar();
                if (car == null) {
                    throw new RuntimeException("Lỗi: Thông tin xe bị thiếu trong hợp đồng");
                }
                car.setStatus("AVAILABLE");
                carRepository.save(car);
            }
            
            // 3. Cập nhật trạng thái tài sản thế chấp
            List<Collateral> collaterals = collateralRepository.findByContractId(contractId);
            for (Collateral collateral : collaterals) {
                collateral.setReturnStatus("RETURNED");
                collateralRepository.save(collateral);
            }
            
            // 4. Tạo hóa đơn mới
            createInvoice(contract, contractCars);
        } catch (Exception e) {
            // Log the error for diagnosis
            System.err.println("Error completing contract " + contractId + ": " + e.getMessage());
            e.printStackTrace();
            
            // Rethrow with a user-friendly message
            throw new RuntimeException("Lỗi xử lý thanh toán: " + e.getMessage(), e);
        }
    }
    
    private void createInvoice(Contract contract, List<ContractCar> contractCars) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null");
        }
        
        if (contractCars == null || contractCars.isEmpty()) {
            throw new IllegalArgumentException("No contract cars provided");
        }
        
        try {
            // Lấy thông tin người dùng đăng nhập hiện tại
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Tạo invoice mới
            Invoice invoice = new Invoice();
            invoice.setInvoiceDate((int)(new Date().getTime() / 1000)); // Unix timestamp
            invoice.setPaymentMethod("CASH"); // Mặc định là tiền mặt
            invoice.setStatus("PAID");
            invoice.setInvoiceType("LEASE_END");
            
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
                String formattedDate = sdf.format(new Date());
                
                if (formattedDate.length() > 25) {
                    sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    formattedDate = sdf.format(new Date());
                }
                
                invoice.setPaymentDate(formattedDate);
            } catch (Exception e) {
                invoice.setPaymentDate(new Date().toString().substring(0, 25));
                System.err.println("Warning: Error formatting payment date: " + e.getMessage());
            }



            invoice.setDiscountAmount(0f);
            invoice.setUser(currentUser);
            invoice.setContract(contract);
            
            // Tính toán tổng tiền
            float totalAmount = 0;
            
            // Lưu invoice
            Invoice savedInvoice = invoiceRepository.save(invoice);
            
            // Tạo invoice detail cho từng xe
            for (ContractCar contractCar : contractCars) {
                if (contractCar == null) {
                    System.err.println("Warning: Null contract car found in collection, skipping");
                    continue;
                }
                  try {
                    float pricePerDay = contractCar.getPricePerDay() != null ? contractCar.getPricePerDay() : 0;
                    int days = contractCar.getRentalDays();
                    
                    // Phí thuê xe
                    float rentalFee = pricePerDay * days;
                    
                    InvoiceDetail rentalDetail = new InvoiceDetail();
                    rentalDetail.setFeeType("RENTAL_FEE");
                    rentalDetail.setFeeAmount(rentalFee);
                    
                    String licensePlate = "Unknown";
                    if (contractCar.getCar() != null && contractCar.getCar().getLicensePlate() != null) {
                        licensePlate = contractCar.getCar().getLicensePlate();
                    }
                    
                    rentalDetail.setDescription("Phí thuê xe " + licensePlate);
                    rentalDetail.setDiscount(0f);
                    rentalDetail.setContractCar(contractCar);
                    rentalDetail.setInvoice(savedInvoice);
                    invoiceDetailRepository.save(rentalDetail);
                    
                    totalAmount += rentalFee;
                    
                    // Phí hư hỏng nếu có
                    List<DamageItem> damages = damageItemRepository.findByContractCarId(contractCar.getId());
                    if (damages != null) {
                        for (DamageItem damage : damages) {
                            if (damage == null) continue;
                            
                            float repairCost = damage.getRepairCostEstimate() != null ? damage.getRepairCostEstimate() : 0;
                            
                            InvoiceDetail damageDetail = new InvoiceDetail();
                            damageDetail.setFeeType("DAMAGE_FEE");
                            damageDetail.setFeeAmount(repairCost);
                            

                            damageDetail.setDescription("Phí sửa chữa: " + (damage.getDescription() != null ? damage.getDescription() : ""));
                            damageDetail.setDiscount(0f);
                            damageDetail.setContractCar(contractCar);
                            damageDetail.setInvoice(savedInvoice);
                            invoiceDetailRepository.save(damageDetail);
                            
                            totalAmount += repairCost;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error processing contract car " + contractCar.getId() + ": " + e.getMessage());
                    // Continue processing other cars instead of failing completely
                }
            }
            
            // Cập nhật tổng tiền
            savedInvoice.setAmountPaid(totalAmount);
            invoiceRepository.save(savedInvoice);
        } catch (Exception e) {
            System.err.println("Error creating invoice: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tạo hóa đơn: " + e.getMessage(), e);
        }
    }
}
