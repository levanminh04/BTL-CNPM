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
    private UserRepository userRepository;
    
    @Transactional
    public void completeContract(Integer contractId) throws RuntimeException {
        Optional<Contract> contractOpt = contractRepository.findById(contractId);
        if (contractOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy hợp đồng");
        }
        
        Contract contract = contractOpt.get();
        
        // 1. Cập nhật trạng thái hợp đồng
        contract.setStatus("COMPLETED");
        contractRepository.save(contract);
        
        // 2. Cập nhật trạng thái xe
        List<ContractCar> contractCars = contractCarRepository.findByContractId(contractId);
        for (ContractCar contractCar : contractCars) {
            Car car = contractCar.getCar();
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
    }
    
    private void createInvoice(Contract contract, List<ContractCar> contractCars) {
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
        invoice.setPaymentDate(new Date().toString());
        invoice.setDiscountAmount(0f);
        invoice.setUser(currentUser);
        invoice.setContract(contract);
        
        // Tính toán tổng tiền
        float totalAmount = 0;
        
        // Lưu invoice
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        // Tạo invoice detail cho từng xe
        for (ContractCar contractCar : contractCars) {
            // Phí thuê xe
            float rentalFee = contractCar.getPricePerDay() * contractCar.getEndDate();
            InvoiceDetail rentalDetail = new InvoiceDetail();
            rentalDetail.setFeeType("RENTAL_FEE");
            rentalDetail.setFeeAmount(rentalFee);
            rentalDetail.setDescription("Phí thuê xe " + contractCar.getCar().getLicensePlate());
            rentalDetail.setDiscount(0f);
            rentalDetail.setContractCar(contractCar);
            rentalDetail.setInvoice(savedInvoice);
            invoiceDetailRepository.save(rentalDetail);
            
            totalAmount += rentalFee;
            
            // Phí hư hỏng nếu có
            List<DamageItem> damages = damageItemRepository.findByContractCarId(contractCar.getId());
            for (DamageItem damage : damages) {
                InvoiceDetail damageDetail = new InvoiceDetail();
                damageDetail.setFeeType("DAMAGE_FEE");
                damageDetail.setFeeAmount(damage.getRepairCostEstimate());
                damageDetail.setDescription("Phí sửa chữa: " + damage.getDescription());
                damageDetail.setDiscount(0f);
                damageDetail.setContractCar(contractCar);
                damageDetail.setInvoice(savedInvoice);
                invoiceDetailRepository.save(damageDetail);
                
                totalAmount += damage.getRepairCostEstimate();
            }
        }
        
        // Cập nhật tổng tiền
        savedInvoice.setAmountPaid(totalAmount);
        invoiceRepository.save(savedInvoice);
    }
}
