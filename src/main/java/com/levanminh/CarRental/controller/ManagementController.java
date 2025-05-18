package com.levanminh.CarRental.controller;

import com.levanminh.CarRental.entity.*;
import com.levanminh.CarRental.repository.*;
import com.levanminh.CarRental.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/management")
public class ManagementController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private ContractCarRepository contractCarRepository;
    
    @Autowired
    private DamageItemRepository damageItemRepository;
    
    @Autowired
    private ContractService contractService;

    @GetMapping("/cars")
    public String manageCars(Model model) {
        List<Car> cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        return "management/cars";
    }

    @GetMapping("/customers")
    public String manageCustomers(Model model) {
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        return "management/customers";
    }

    @GetMapping("/contracts")
    public String manageContracts(Model model, @RequestParam(required = false) Integer searchId) {
        if (searchId != null) {
            Optional<Contract> contract = contractRepository.findById(searchId);
            if (contract.isPresent()) {
                return "redirect:/management/contracts/view/" + searchId;
            } else {
                model.addAttribute("searchError", "Không tìm thấy hợp đồng với ID: " + searchId);
            }
        }
        
        List<Contract> contracts = contractRepository.findAll();
        model.addAttribute("contracts", contracts);
        return "management/contracts";
    }
    
    @GetMapping("/contracts/view/{id}")
    public String viewContract(@PathVariable("id") Integer id, Model model) {
        Optional<Contract> contractOpt = contractRepository.findById(id);
        if (contractOpt.isEmpty()) {
            return "redirect:/management/contracts";
        }
        
        Contract contract = contractOpt.get();
        List<ContractCar> contractCars = contractCarRepository.findByContractId(id);
        
        model.addAttribute("contract", contract);
        model.addAttribute("contractCars", contractCars);
        model.addAttribute("damageItem", new DamageItem());
        
        return "management/contract-detail";
    }      @PostMapping("/contracts/damage/add")
    public String addDamage(@ModelAttribute DamageItem damageItem, 
                          @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                          RedirectAttributes redirectAttributes) {
        try {
            // Set a default image value if none is provided
            if (imageFile == null || imageFile.isEmpty()) {
                damageItem.setImage("noimage");
            } else {
                // Handle image file
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                // Save the file to uploads folder - simplified for now
                damageItem.setImage(fileName.substring(0, Math.min(fileName.length(), 10)));
            }
            
            // Fetch the car from the repository to ensure we have a valid entity
            if (damageItem.getCar() != null && damageItem.getCar().getId() != null) {
                Car car = carRepository.findById(damageItem.getCar().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy xe"));
                damageItem.setCar(car);
            }
            
            // Same for contractCar
            if (damageItem.getContractCar() != null && damageItem.getContractCar().getId() != null) {
                ContractCar contractCar = contractCarRepository.findById(damageItem.getContractCar().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin xe trong hợp đồng"));
                damageItem.setContractCar(contractCar);
            }
            
            damageItemRepository.save(damageItem);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm thông tin hư hỏng thành công!");            return "redirect:/management/contracts/view/" + damageItem.getContractCar().getContract().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm thông tin hư hỏng: " + e.getMessage());
            return "redirect:/management/contracts";
        }
    }
    
    @GetMapping("/contracts/invoice/{id}")
    public String generateInvoice(@PathVariable("id") Integer id, Model model) {
        Optional<Contract> contractOpt = contractRepository.findById(id);
        if (contractOpt.isEmpty()) {
            return "redirect:/management/contracts";
        }
          Contract contract = contractOpt.get();
        List<ContractCar> contractCars = contractCarRepository.findByContractId(id);
        
        // Tính toán chi phí
        double rentalTotal = 0;
        double damageTotal = 0;
        
        // Pre-load damage items for each contract car
        for (ContractCar contractCar : contractCars) {
            // Tính phí thuê xe
            rentalTotal += contractCar.getPricePerDay() * contractCar.getEndDate();
            
            // Tính phí hư hỏng nếu có
            List<DamageItem> damages = damageItemRepository.findByContractCarId(contractCar.getId());
            contractCar.setDamageItems(damages); // Set the damage items explicitly
            
            for (DamageItem damage : damages) {
                damageTotal += damage.getRepairCostEstimate();
            }
        }
        
        double total = rentalTotal + damageTotal;
        
        model.addAttribute("contract", contract);
        model.addAttribute("contractCars", contractCars);
        model.addAttribute("rentalTotal", rentalTotal);
        model.addAttribute("damageTotal", damageTotal);
        model.addAttribute("total", total);
        
        return "management/invoice-preview";
    }
    
    @PostMapping("/contracts/invoice/confirm/{id}")
    public String confirmPayment(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            contractService.completeContract(id);
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán thành công! Hợp đồng đã được hoàn thành.");
            return "redirect:/management/contracts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xử lý thanh toán: " + e.getMessage());
            return "redirect:/management/contracts/invoice/" + id;
        }
    }
}
