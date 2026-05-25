package com.example.hospital_management.rest;

import com.example.hospital_management.dto.PatientDTO;
import com.example.hospital_management.entity.Patient;
import com.example.hospital_management.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@Tag(name = "Patients", description = "Patient management APIs")
public class PatientRestController {

    private final PatientService patientService;

    public PatientRestController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get all patients", description = "Retrieve all patients, optionally filtered by status or type")
    public ResponseEntity<List<Patient>> getAllPatients(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {

        List<Patient> patients;
        if (status != null && !status.isBlank()) {
            patients = patientService.findByStatus(status.toUpperCase());
        } else if (type != null && !type.isBlank()) {
            patients = patientService.findByPatientType(type.toUpperCase());
        } else {
            patients = patientService.findAll();
        }
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search patients by name")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam String name) {
        return ResponseEntity.ok(patientService.searchByName(name));
    }

    @PostMapping
    @Operation(summary = "Create a new patient")
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody PatientDTO dto) {
        Patient patient = Patient.builder()
                .name(dto.getName())
                .age(dto.getAge())
                .gender(dto.getGender())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .patientType(dto.getPatientType())
                .bedNumber(dto.getBedNumber())
                .status(dto.getStatus())
                .diagnosis(dto.getDiagnosis())
                .admittedDate(dto.getAdmittedDate() != null ? dto.getAdmittedDate() : java.time.LocalDate.now().toString())
                .dischargedDate(dto.getDischargedDate())
                .build();

        Patient saved = patientService.save(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing patient")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientDTO dto) {
        return patientService.findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    existing.setAge(dto.getAge());
                    existing.setGender(dto.getGender());
                    existing.setPhone(dto.getPhone());
                    existing.setEmail(dto.getEmail());
                    existing.setAddress(dto.getAddress());
                    existing.setPatientType(dto.getPatientType());
                    existing.setBedNumber(dto.getBedNumber());
                    existing.setStatus(dto.getStatus());
                    existing.setDiagnosis(dto.getDiagnosis());
                    if (dto.getAdmittedDate() != null) existing.setAdmittedDate(dto.getAdmittedDate());
                    if (dto.getDischargedDate() != null) existing.setDischargedDate(dto.getDischargedDate());
                    return ResponseEntity.ok(patientService.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        if (patientService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
