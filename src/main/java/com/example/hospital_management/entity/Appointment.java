package com.example.hospital_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientName;

    @Column(nullable = false)
    private String doctorName;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String appointmentDate;

    @Column(nullable = false)
    private String appointmentTime;

    @Column(nullable = false)
    private String status; // SCHEDULED, COMPLETED, CANCELLED

    private String notes;
}
