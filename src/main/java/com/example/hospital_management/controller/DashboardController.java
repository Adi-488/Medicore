package com.example.hospital_management.controller;

import com.example.hospital_management.entity.Appointment;
import com.example.hospital_management.entity.Patient;
import com.example.hospital_management.service.AppointmentService;
import com.example.hospital_management.service.BillService;
import com.example.hospital_management.service.PatientService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Scope("prototype")
public class DashboardController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final BillService billService;

    // Welcome banner
    @FXML private Label lblWelcome;
    @FXML private Label lblWelcomeDate;
    @FXML private Label lblClock;

    // Stat card labels
    @FXML private Label lblTotalPatients;
    @FXML private Label lblPatientDetail;
    @FXML private Label lblTotalAppointments;
    @FXML private Label lblAppointmentDetail;
    @FXML private Label lblPendingBills;
    @FXML private Label lblPendingAmount;
    @FXML private Label lblRevenue;
    @FXML private Label lblRevenueDetail;

    // Section counts
    @FXML private Label lblRecentCount;
    @FXML private Label lblUpcomingCount;

    // Quick actions
    @FXML private VBox qaNewPatient;
    @FXML private VBox qaNewAppointment;
    @FXML private VBox qaNewBill;
    @FXML private VBox qaViewReports;

    // Recent patients table
    @FXML private TableView<Patient> recentPatientsTable;
    @FXML private TableColumn<Patient, String> rpNameCol;
    @FXML private TableColumn<Patient, String> rpTypeCol;
    @FXML private TableColumn<Patient, String> rpStatusCol;
    @FXML private TableColumn<Patient, String> rpDiagnosisCol;

    // Upcoming appointments table
    @FXML private TableView<Appointment> upcomingAppointmentsTable;
    @FXML private TableColumn<Appointment, String> uaPatientCol;
    @FXML private TableColumn<Appointment, String> uaDoctorCol;
    @FXML private TableColumn<Appointment, String> uaDateCol;
    @FXML private TableColumn<Appointment, String> uaTimeCol;
    @FXML private TableColumn<Appointment, String> uaStatusCol;

    public DashboardController(PatientService patientService,
                                AppointmentService appointmentService,
                                BillService billService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.billService = billService;
    }

    @FXML
    public void initialize() {
        setupWelcomeBanner();
        startClock();
        loadStats();
        setupTables();
        loadTableData();
    }

    private void setupWelcomeBanner() {
        // Time-based greeting
        int hour = LocalTime.now().getHour();
        String greeting;
        if (hour < 12) greeting = "Good Morning";
        else if (hour < 17) greeting = "Good Afternoon";
        else greeting = "Good Evening";

        lblWelcome.setText(greeting + ", Dr. Admin 👋");

        // Formatted date
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
        lblWelcomeDate.setText(formattedDate);
    }

    private void startClock() {
        updateClock();
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateClock()));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void updateClock() {
        lblClock.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private void loadStats() {
        // Patients
        long totalPatients = patientService.count();
        long inpatients = patientService.countByPatientType("INPATIENT");
        long outpatients = patientService.countByPatientType("OUTPATIENT");
        lblTotalPatients.setText(String.valueOf(totalPatients));
        lblPatientDetail.setText(inpatients + " inpatient · " + outpatients + " outpatient");

        // Appointments
        long totalAppts = appointmentService.count();
        long scheduled = appointmentService.countByStatus("SCHEDULED");
        long completed = appointmentService.countByStatus("COMPLETED");
        lblTotalAppointments.setText(String.valueOf(totalAppts));
        lblAppointmentDetail.setText(scheduled + " scheduled · " + completed + " completed");

        // Bills
        long pendingBills = billService.countByStatus("PENDING");
        double pendingAmount = billService.getPendingAmount();
        lblPendingBills.setText(String.valueOf(pendingBills));
        lblPendingAmount.setText("₹" + String.format("%,.0f", pendingAmount) + " outstanding");

        // Revenue
        double totalRevenue = billService.getTotalRevenue();
        double paidRevenue = billService.getPaidRevenue();
        lblRevenue.setText("₹" + String.format("%,.0f", totalRevenue));
        lblRevenueDetail.setText("₹" + String.format("%,.0f", paidRevenue) + " collected");
    }

    private void setupTables() {
        // Recent patients columns
        rpNameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));

        rpTypeCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPatientType()));
        rpTypeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    String style = "INPATIENT".equals(item) ? "badge, badge-admitted" : "badge, badge-active";
                    badge.getStyleClass().addAll(style.split(", "));
                    setGraphic(badge);
                }
            }
        });

        rpStatusCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus()));
        rpStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    String styleClass = switch (item) {
                        case "ADMITTED" -> "badge-admitted";
                        case "DISCHARGED" -> "badge-discharged";
                        case "ACTIVE" -> "badge-active";
                        default -> "badge-pending";
                    };
                    badge.getStyleClass().addAll("badge", styleClass);
                    setGraphic(badge);
                }
            }
        });

        rpDiagnosisCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDiagnosis()));

        // Upcoming appointments columns
        uaPatientCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPatientName()));
        uaDoctorCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDoctorName()));
        uaDateCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAppointmentDate()));
        uaTimeCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAppointmentTime()));

        uaStatusCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus()));
        uaStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    String styleClass = switch (item) {
                        case "SCHEDULED" -> "badge-scheduled";
                        case "COMPLETED" -> "badge-completed";
                        case "CANCELLED" -> "badge-cancelled";
                        default -> "badge-pending";
                    };
                    badge.getStyleClass().addAll("badge", styleClass);
                    setGraphic(badge);
                }
            }
        });
    }

    private void loadTableData() {
        // Show last 5 patients
        List<Patient> patients = patientService.findAll();
        int from = Math.max(0, patients.size() - 5);
        List<Patient> recentPatients = patients.subList(from, patients.size());
        recentPatientsTable.setItems(FXCollections.observableArrayList(recentPatients));
        lblRecentCount.setText("(" + recentPatients.size() + ")");

        // Show scheduled appointments
        List<Appointment> scheduled = appointmentService.findByStatus("SCHEDULED");
        upcomingAppointmentsTable.setItems(FXCollections.observableArrayList(scheduled));
        lblUpcomingCount.setText("(" + scheduled.size() + ")");
    }
}
