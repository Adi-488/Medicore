package com.example.hospital_management.rest;

import com.example.hospital_management.dto.AppointmentDTO;
import com.example.hospital_management.entity.Appointment;
import com.example.hospital_management.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "Appointment management APIs")
public class AppointmentRestController {

    private final AppointmentService appointmentService;

    public AppointmentRestController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    @Operation(summary = "Get all appointments", description = "Retrieve all appointments, optionally filtered by status, doctor, or date")
    public ResponseEntity<List<Appointment>> getAllAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String doctor,
            @RequestParam(required = false) String date) {

        List<Appointment> appointments;
        if (status != null && !status.isBlank()) {
            appointments = appointmentService.findByStatus(status.toUpperCase());
        } else if (doctor != null && !doctor.isBlank()) {
            appointments = appointmentService.findByDoctorName(doctor);
        } else if (date != null && !date.isBlank()) {
            appointments = appointmentService.findByDate(date);
        } else {
            appointments = appointmentService.findAll();
        }
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return appointmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new appointment")
    public ResponseEntity<Appointment> createAppointment(@Valid @RequestBody AppointmentDTO dto) {
        Appointment appointment = Appointment.builder()
                .patientName(dto.getPatientName())
                .doctorName(dto.getDoctorName())
                .department(dto.getDepartment())
                .appointmentDate(dto.getAppointmentDate())
                .appointmentTime(dto.getAppointmentTime())
                .status(dto.getStatus())
                .notes(dto.getNotes())
                .build();

        Appointment saved = appointmentService.save(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing appointment")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentDTO dto) {
        return appointmentService.findById(id)
                .map(existing -> {
                    existing.setPatientName(dto.getPatientName());
                    existing.setDoctorName(dto.getDoctorName());
                    existing.setDepartment(dto.getDepartment());
                    existing.setAppointmentDate(dto.getAppointmentDate());
                    existing.setAppointmentTime(dto.getAppointmentTime());
                    existing.setStatus(dto.getStatus());
                    existing.setNotes(dto.getNotes());
                    return ResponseEntity.ok(appointmentService.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an appointment")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        if (appointmentService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
