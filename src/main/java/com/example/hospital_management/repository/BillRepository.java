package com.example.hospital_management.repository;

import com.example.hospital_management.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByStatus(String status);
    List<Bill> findByPatientName(String patientName);
}
