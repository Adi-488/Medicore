package com.example.patientservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Min(value = 0, message = "Age must be a positive number")
    @Max(value = 150, message = "Age must be realistic")
    private int age;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @Email(message = "Email should be valid")
    private String email;

    private String address;

    @NotBlank(message = "Patient type is required (INPATIENT or OUTPATIENT)")
    private String patientType;

    private Integer bedNumber;

    @NotBlank(message = "Status is required (ADMITTED, DISCHARGED, or ACTIVE)")
    private String status;

    private String diagnosis;

    private String admittedDate;

    private String dischargedDate;
}
