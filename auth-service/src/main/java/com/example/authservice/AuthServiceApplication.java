package com.example.authservice;

import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("Aditya@2005"))
                        .email("admin@medicore.com")
                        .role("ROLE_ADMIN")
                        .build();
                userRepository.save(admin);
                System.out.println(">>> Database Seeded: Default admin user created (admin / Aditya@2005) <<<");
            } else {
                System.out.println(">>> Database Seeding: Admin user already exists <<<");
            }
        };
    }
}
