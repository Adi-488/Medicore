package com.example.hospital_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotBlank(message = "Doctor name is required")
    private String doctorName;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Appointment date is required")
    private String appointmentDate;

    @NotBlank(message = "Appointment time is required")
    private String appointmentTime;

    @NotBlank(message = "Status is required (SCHEDULED, COMPLETED, or CANCELLED)")
    private String status;

    private String notes;
}
