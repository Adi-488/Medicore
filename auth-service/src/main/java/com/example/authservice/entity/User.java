package com.example.authservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Stored as a BCrypt hash

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String role; // e.g. "ROLE_ADMIN", "ROLE_DOCTOR", "ROLE_RECEPTIONIST"
}
