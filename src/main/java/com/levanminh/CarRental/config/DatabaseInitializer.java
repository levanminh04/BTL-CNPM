package com.levanminh.CarRental.config;

import com.levanminh.CarRental.entity.User;
import com.levanminh.CarRental.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DatabaseInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initialize() {
        // Kiểm tra xem đã có admin chưa
        Optional<User> adminUser = userRepository.findByUsername("admin");
        
        if (adminUser.isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullname("Administrator");
            admin.setRole("ADMIN");
            userRepository.save(admin);
            
            // Thêm một user thông thường
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setFullname("Regular User");
            user.setRole("USER");
            userRepository.save(user);
            
            System.out.println("Default users created successfully");
        }
    }
}
