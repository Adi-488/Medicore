package com.example.patientservice.repository;

import com.example.patientservice.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByStatus(String status);
    List<Patient> findByPatientType(String patientType);
    List<Patient> findByNameContainingIgnoreCase(String name);
}
