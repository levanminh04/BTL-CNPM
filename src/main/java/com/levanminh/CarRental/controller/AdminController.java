package com.levanminh.CarRental.controller;

import com.levanminh.CarRental.entity.User;
import com.levanminh.CarRental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String adminDashboard() {
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String userManagement(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    @GetMapping("/reports")
    public String reports() {
        return "admin/reports";
    }
    
    @GetMapping("/settings")
    public String settings() {
        return "admin/settings";
    }
}
