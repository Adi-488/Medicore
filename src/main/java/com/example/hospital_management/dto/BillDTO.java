package com.example.hospital_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillDTO {

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotBlank(message = "Description is required")
    private String description;

    @Positive(message = "Amount must be a positive number")
    private double amount;

    @NotBlank(message = "Bill date is required")
    private String billDate;

    @NotBlank(message = "Status is required (PENDING, PAID, or OVERDUE)")
    private String status;

    private String paymentMethod;
}
