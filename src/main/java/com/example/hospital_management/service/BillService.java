package com.example.hospital_management.service;

import com.example.hospital_management.entity.Bill;
import com.example.hospital_management.repository.BillRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillService {

    private final BillRepository billRepository;

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public List<Bill> findAll() {
        return billRepository.findAll();
    }

    public Optional<Bill> findById(Long id) {
        return billRepository.findById(id);
    }

    public Bill save(Bill bill) {
        return billRepository.save(bill);
    }

    public void delete(Long id) {
        billRepository.deleteById(id);
    }

    public List<Bill> findByStatus(String status) {
        return billRepository.findByStatus(status);
    }

    public List<Bill> findByPatientName(String patientName) {
        return billRepository.findByPatientName(patientName);
    }

    public long count() {
        return billRepository.count();
    }

    public double getTotalRevenue() {
        return billRepository.findAll().stream()
                .mapToDouble(Bill::getAmount)
                .sum();
    }

    public double getPaidRevenue() {
        return billRepository.findByStatus("PAID").stream()
                .mapToDouble(Bill::getAmount)
                .sum();
    }

    public double getPendingAmount() {
        return billRepository.findByStatus("PENDING").stream()
                .mapToDouble(Bill::getAmount)
                .sum();
    }

    public long countByStatus(String status) {
        return billRepository.findByStatus(status).size();
    }
}
