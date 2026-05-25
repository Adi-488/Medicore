package com.example.hospital_management.rest;

import com.example.hospital_management.service.AppointmentService;
import com.example.hospital_management.service.BillService;
import com.example.hospital_management.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard statistics API")
public class DashboardRestController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final BillService billService;

    public DashboardRestController(PatientService patientService,
                                   AppointmentService appointmentService,
                                   BillService billService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.billService = billService;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics", description = "Returns aggregate counts and financial summary")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // Patient stats
        Map<String, Object> patients = new LinkedHashMap<>();
        patients.put("total", patientService.count());
        patients.put("admitted", patientService.countByStatus("ADMITTED"));
        patients.put("discharged", patientService.countByStatus("DISCHARGED"));
        patients.put("active", patientService.countByStatus("ACTIVE"));
        patients.put("inpatient", patientService.countByPatientType("INPATIENT"));
        patients.put("outpatient", patientService.countByPatientType("OUTPATIENT"));
        stats.put("patients", patients);

        // Appointment stats
        Map<String, Object> appointments = new LinkedHashMap<>();
        appointments.put("total", appointmentService.count());
        appointments.put("scheduled", appointmentService.countByStatus("SCHEDULED"));
        appointments.put("completed", appointmentService.countByStatus("COMPLETED"));
        appointments.put("cancelled", appointmentService.countByStatus("CANCELLED"));
        stats.put("appointments", appointments);

        // Billing stats
        Map<String, Object> billing = new LinkedHashMap<>();
        billing.put("totalBills", billService.count());
        billing.put("totalRevenue", billService.getTotalRevenue());
        billing.put("paidRevenue", billService.getPaidRevenue());
        billing.put("pendingAmount", billService.getPendingAmount());
        billing.put("paidCount", billService.countByStatus("PAID"));
        billing.put("pendingCount", billService.countByStatus("PENDING"));
        stats.put("billing", billing);

        return ResponseEntity.ok(stats);
    }
}
