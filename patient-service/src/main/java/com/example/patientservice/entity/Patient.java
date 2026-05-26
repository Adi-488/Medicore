package com.example.patientservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false, unique = true)
    private String phone;

    private String email;

    private String address;

    @Column(nullable = false)
    private String patientType; // INPATIENT or OUTPATIENT

    private Integer bedNumber;

    @Column(nullable = false)
    private String status; // ADMITTED, DISCHARGED, ACTIVE

    private String diagnosis;

    @Column(name = "admitted_date")
    private String admittedDate;

    @Column(name = "discharged_date")
    private String dischargedDate;
}
