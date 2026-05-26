package com.example.patientservice.controller;

import com.example.patientservice.dto.PatientDTO;
import com.example.patientservice.entity.Patient;
import com.example.patientservice.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Patient Service Running");
    }

    @GetMapping
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
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam String name) {
        return ResponseEntity.ok(patientService.searchByName(name));
    }

    @PostMapping
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
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        if (patientService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
