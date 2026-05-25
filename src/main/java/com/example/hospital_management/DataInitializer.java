package com.example.hospital_management;

import com.example.hospital_management.entity.Appointment;
import com.example.hospital_management.entity.Bill;
import com.example.hospital_management.entity.Patient;
import com.example.hospital_management.repository.AppointmentRepository;
import com.example.hospital_management.repository.BillRepository;
import com.example.hospital_management.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;

    public DataInitializer(PatientRepository patientRepository,
                           AppointmentRepository appointmentRepository,
                           BillRepository billRepository) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
    }

    @Override
    public void run(String... args) {
        if (patientRepository.count() == 0) {
            // Seed patients
            patientRepository.save(Patient.builder()
                    .name("Aditya Sharma").age(28).gender("Male").phone("9876543210")
                    .email("aditya@email.com").address("Mumbai, Maharashtra")
                    .patientType("INPATIENT").bedNumber(1).status("ADMITTED")
                    .diagnosis("Pneumonia").admittedDate("2026-05-18").build());

            patientRepository.save(Patient.builder()
                    .name("Priya Patel").age(34).gender("Female").phone("9876543211")
                    .email("priya@email.com").address("Delhi, India")
                    .patientType("OUTPATIENT").status("ACTIVE")
                    .diagnosis("Routine Checkup").admittedDate("2026-05-20").build());

            patientRepository.save(Patient.builder()
                    .name("Rahul Verma").age(45).gender("Male").phone("9876543212")
                    .email("rahul@email.com").address("Bangalore, Karnataka")
                    .patientType("INPATIENT").bedNumber(3).status("ADMITTED")
                    .diagnosis("Fracture - Right Leg").admittedDate("2026-05-15").build());

            patientRepository.save(Patient.builder()
                    .name("Sneha Gupta").age(22).gender("Female").phone("9876543213")
                    .email("sneha@email.com").address("Pune, Maharashtra")
                    .patientType("INPATIENT").status("DISCHARGED")
                    .diagnosis("Appendicitis Surgery").admittedDate("2026-05-10")
                    .dischargedDate("2026-05-17").build());

            patientRepository.save(Patient.builder()
                    .name("Mahimna Desai").age(52).gender("Male").phone("9876543214")
                    .email("mahimna@email.com").address("Ahmedabad, Gujarat")
                    .patientType("OUTPATIENT").status("ACTIVE")
                    .diagnosis("Diabetes Follow-up").admittedDate("2026-05-19").build());
        }

        if (appointmentRepository.count() == 0) {
            // Seed appointments
            appointmentRepository.save(Appointment.builder()
                    .patientName("Priya Patel").doctorName("Dr. Ananya Singh")
                    .department("General Medicine").appointmentDate("2026-05-21")
                    .appointmentTime("10:00").status("SCHEDULED")
                    .notes("Annual health checkup").build());

            appointmentRepository.save(Appointment.builder()
                    .patientName("Mahimna Desai").doctorName("Dr. Rajesh Kumar")
                    .department("Endocrinology").appointmentDate("2026-05-22")
                    .appointmentTime("14:30").status("SCHEDULED")
                    .notes("Blood sugar review").build());

            appointmentRepository.save(Appointment.builder()
                    .patientName("Aditya Sharma").doctorName("Dr. Meera Joshi")
                    .department("Pulmonology").appointmentDate("2026-05-20")
                    .appointmentTime("09:00").status("COMPLETED")
                    .notes("Follow-up X-ray review").build());

            appointmentRepository.save(Appointment.builder()
                    .patientName("Sneha Gupta").doctorName("Dr. Vikram Reddy")
                    .department("Surgery").appointmentDate("2026-05-19")
                    .appointmentTime("11:00").status("COMPLETED")
                    .notes("Post-surgery checkup").build());
        }

        if (billRepository.count() == 0) {
            // Seed bills
            billRepository.save(Bill.builder()
                    .patientName("Aditya Sharma").description("ICU + Medication (3 days)")
                    .amount(15000.0).billDate("2026-05-20").status("PENDING")
                    .paymentMethod("INSURANCE").build());

            billRepository.save(Bill.builder()
                    .patientName("Sneha Gupta").description("Appendicitis Surgery + Room")
                    .amount(45000.0).billDate("2026-05-17").status("PAID")
                    .paymentMethod("CARD").build());

            billRepository.save(Bill.builder()
                    .patientName("Priya Patel").description("Consultation Fee")
                    .amount(1500.0).billDate("2026-05-20").status("PENDING")
                    .paymentMethod("CASH").build());

            billRepository.save(Bill.builder()
                    .patientName("Rahul Verma").description("Orthopedic Treatment + Cast")
                    .amount(22000.0).billDate("2026-05-15").status("PENDING")
                    .paymentMethod("UPI").build());

            billRepository.save(Bill.builder()
                    .patientName("Mahimna Desai").description("Lab Tests + Consultation")
                    .amount(3500.0).billDate("2026-05-19").status("PAID")
                    .paymentMethod("UPI").build());
        }
    }
}
