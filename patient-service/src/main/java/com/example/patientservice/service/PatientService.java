package com.example.patientservice.service;

import com.example.patientservice.entity.Patient;
import com.example.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    public void delete(Long id) {
        patientRepository.deleteById(id);
    }

    public List<Patient> searchByName(String name) {
        return patientRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Patient> findByStatus(String status) {
        return patientRepository.findByStatus(status);
    }

    public List<Patient> findByPatientType(String type) {
        return patientRepository.findByPatientType(type);
    }

    public long count() {
        return patientRepository.count();
    }

    public long countByStatus(String status) {
        return patientRepository.findByStatus(status).size();
    }

    public long countByPatientType(String type) {
        return patientRepository.findByPatientType(type).size();
    }
}
