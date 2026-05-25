package com.example.hospital_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String billDate;

    @Column(nullable = false)
    private String status; // PENDING, PAID, OVERDUE

    private String paymentMethod; // CASH, CARD, UPI, INSURANCE
}
