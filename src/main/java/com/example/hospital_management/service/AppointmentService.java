package com.example.hospital_management.service;

import com.example.hospital_management.entity.Appointment;
import com.example.hospital_management.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void delete(Long id) {
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> findByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    public List<Appointment> findByDoctorName(String doctorName) {
        return appointmentRepository.findByDoctorName(doctorName);
    }

    public List<Appointment> findByDate(String date) {
        return appointmentRepository.findByAppointmentDate(date);
    }

    public long count() {
        return appointmentRepository.count();
    }

    public long countByStatus(String status) {
        return appointmentRepository.findByStatus(status).size();
    }
}
