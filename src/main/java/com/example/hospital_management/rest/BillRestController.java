package com.example.hospital_management.rest;

import com.example.hospital_management.dto.BillDTO;
import com.example.hospital_management.entity.Bill;
import com.example.hospital_management.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@Tag(name = "Bills", description = "Billing management APIs")
public class BillRestController {

    private final BillService billService;

    public BillRestController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping
    @Operation(summary = "Get all bills", description = "Retrieve all bills, optionally filtered by status or patient name")
    public ResponseEntity<List<Bill>> getAllBills(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String patient) {

        List<Bill> bills;
        if (status != null && !status.isBlank()) {
            bills = billService.findByStatus(status.toUpperCase());
        } else if (patient != null && !patient.isBlank()) {
            bills = billService.findByPatientName(patient);
        } else {
            bills = billService.findAll();
        }
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bill by ID")
    public ResponseEntity<Bill> getBillById(@PathVariable Long id) {
        return billService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new bill")
    public ResponseEntity<Bill> createBill(@Valid @RequestBody BillDTO dto) {
        Bill bill = Bill.builder()
                .patientName(dto.getPatientName())
                .description(dto.getDescription())
                .amount(dto.getAmount())
                .billDate(dto.getBillDate())
                .status(dto.getStatus())
                .paymentMethod(dto.getPaymentMethod())
                .build();

        Bill saved = billService.save(bill);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing bill")
    public ResponseEntity<Bill> updateBill(@PathVariable Long id, @Valid @RequestBody BillDTO dto) {
        return billService.findById(id)
                .map(existing -> {
                    existing.setPatientName(dto.getPatientName());
                    existing.setDescription(dto.getDescription());
                    existing.setAmount(dto.getAmount());
                    existing.setBillDate(dto.getBillDate());
                    existing.setStatus(dto.getStatus());
                    existing.setPaymentMethod(dto.getPaymentMethod());
                    return ResponseEntity.ok(billService.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a bill")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        if (billService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        billService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
