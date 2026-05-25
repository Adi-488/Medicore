package com.example.hospital_management.repository;

import com.example.hospital_management.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStatus(String status);
    List<Appointment> findByDoctorName(String doctorName);
    List<Appointment> findByAppointmentDate(String appointmentDate);
}
