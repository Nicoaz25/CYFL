package com.football.cyfl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.football.cyfl.models.User;
import com.football.cyfl.repositories.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User admin = userRepository.findByEmail("admin@cyfl.com").orElse(null);
        if (admin == null) {
            admin = new User();
            admin.setEmail("admin@cyfl.com");
            admin.setNombre("Administrator");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setEnabled(1);
            System.out.println("Admin user created: admin@cyfl.com / admin123");
        } else {
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setEnabled(1);
            System.out.println("Admin user updated: admin@cyfl.com / admin123");
        }
        userRepository.save(admin);
    }
}
