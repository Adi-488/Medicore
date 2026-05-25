package com.example.hospital_management.controller;

import com.example.hospital_management.entity.Appointment;
import com.example.hospital_management.service.AppointmentService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Long> colId;
    @FXML private TableColumn<Appointment, String> colPatient;
    @FXML private TableColumn<Appointment, String> colDoctor;
    @FXML private TableColumn<Appointment, String> colDepartment;
    @FXML private TableColumn<Appointment, String> colDate;
    @FXML private TableColumn<Appointment, String> colTime;
    @FXML private TableColumn<Appointment, String> colStatus;
    @FXML private TableColumn<Appointment, String> colNotes;
    @FXML private TableColumn<Appointment, Void> colActions;

    @FXML private TextField searchField;
    @FXML private Button filterAll;
    @FXML private Button filterScheduled;
    @FXML private Button filterCompleted;
    @FXML private Button filterCancelled;
    @FXML private Label lblAppointmentCount;

    private ObservableList<Appointment> appointmentData;
    private List<Button> filterButtons;
    private String currentFilter = "ALL";

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @FXML
    public void initialize() {
        filterButtons = List.of(filterAll, filterScheduled, filterCompleted, filterCancelled);
        setupColumns();
        setupSearch();
        loadData();
    }

    private void setupColumns() {
        colId.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getId()));
        colPatient.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPatientName()));
        colDoctor.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDoctorName()));
        colDepartment.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDepartment()));
        colDate.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAppointmentDate()));
        colTime.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAppointmentTime()));
        colNotes.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNotes()));

        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus()));
        colStatus.setCellFactory(col -> new TableCell<>() {
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

        // Actions column
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button cancelBtn = new Button("Cancel");
            private final HBox box = new HBox(6, editBtn, cancelBtn);

            {
                editBtn.getStyleClass().addAll("btn-secondary", "btn-small");
                cancelBtn.getStyleClass().addAll("btn-danger", "btn-small");
                box.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    onEditAppointment(appt);
                });

                cancelBtn.setOnAction(e -> {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    onCancelAppointment(appt);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    cancelBtn.setDisable("CANCELLED".equals(appt.getStatus()) || "COMPLETED".equals(appt.getStatus()));
                    setGraphic(box);
                }
            }
        });
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.isBlank()) {
                applyFilter(currentFilter);
            } else {
                String query = newVal.trim().toLowerCase();
                List<Appointment> filtered = appointmentService.findAll().stream()
                        .filter(a -> a.getPatientName().toLowerCase().contains(query)
                                || a.getDoctorName().toLowerCase().contains(query))
                        .collect(Collectors.toList());
                appointmentData = FXCollections.observableArrayList(filtered);
                appointmentTable.setItems(appointmentData);
                updateCountBadge();
            }
        });
    }

    private void loadData() {
        appointmentData = FXCollections.observableArrayList(appointmentService.findAll());
        appointmentTable.setItems(appointmentData);
        updateCountBadge();
    }

    private void updateCountBadge() {
        if (lblAppointmentCount != null) {
            lblAppointmentCount.setText(String.valueOf(appointmentData.size()));
        }
    }

    // ── Filters ──

    @FXML private void onFilterAll() { applyFilter("ALL"); setActiveFilter(filterAll); }
    @FXML private void onFilterScheduled() { applyFilter("SCHEDULED"); setActiveFilter(filterScheduled); }
    @FXML private void onFilterCompleted() { applyFilter("COMPLETED"); setActiveFilter(filterCompleted); }
    @FXML private void onFilterCancelled() { applyFilter("CANCELLED"); setActiveFilter(filterCancelled); }

    private void applyFilter(String filter) {
        currentFilter = filter;
        List<Appointment> data = "ALL".equals(filter)
                ? appointmentService.findAll()
                : appointmentService.findByStatus(filter);
        appointmentData = FXCollections.observableArrayList(data);
        appointmentTable.setItems(appointmentData);
        updateCountBadge();
    }

    private void setActiveFilter(Button active) {
        filterButtons.forEach(b -> b.getStyleClass().remove("filter-button-active"));
        active.getStyleClass().add("filter-button-active");
    }

    // ── CRUD ──

    @FXML
    private void onAddAppointment() {
        showAppointmentDialog(null);
    }

    private void onEditAppointment(Appointment appointment) {
        showAppointmentDialog(appointment);
    }

    private void onCancelAppointment(Appointment appointment) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Appointment");
        confirm.setHeaderText("Cancel appointment for " + appointment.getPatientName() + "?");
        confirm.setContentText("This will mark the appointment as cancelled.");
        styleDialog(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            appointment.setStatus("CANCELLED");
            appointmentService.save(appointment);
            applyFilter(currentFilter);
        }
    }

    private void showAppointmentDialog(Appointment existing) {
        Dialog<Appointment> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "New Appointment" : "Edit Appointment");
        dialog.setHeaderText(existing == null ? "Schedule a new appointment" : "Update appointment details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.setPadding(new Insets(24, 24, 16, 24));

        TextField patientField = new TextField(existing != null ? existing.getPatientName() : "");
        patientField.setPromptText("Patient Name");

        TextField doctorField = new TextField(existing != null ? existing.getDoctorName() : "");
        doctorField.setPromptText("Doctor Name");

        TextField deptField = new TextField(existing != null ? existing.getDepartment() : "");
        deptField.setPromptText("Department");

        TextField dateField = new TextField(existing != null ? existing.getAppointmentDate() : "");
        dateField.setPromptText("YYYY-MM-DD");

        TextField timeField = new TextField(existing != null ? existing.getAppointmentTime() : "");
        timeField.setPromptText("HH:MM");

        ComboBox<String> statusBox = new ComboBox<>(
                FXCollections.observableArrayList("SCHEDULED", "COMPLETED", "CANCELLED"));
        statusBox.setValue(existing != null ? existing.getStatus() : "SCHEDULED");
        statusBox.setMaxWidth(Double.MAX_VALUE);

        TextField notesField = new TextField(existing != null ? existing.getNotes() : "");
        notesField.setPromptText("Notes");

        int row = 0;
        grid.add(createFormLabel("Patient"), 0, row); grid.add(patientField, 1, row++);
        grid.add(createFormLabel("Doctor"), 0, row); grid.add(doctorField, 1, row++);
        grid.add(createFormLabel("Department"), 0, row); grid.add(deptField, 1, row++);
        grid.add(createFormLabel("Date"), 0, row); grid.add(dateField, 1, row++);
        grid.add(createFormLabel("Time"), 0, row); grid.add(timeField, 1, row++);
        grid.add(createFormLabel("Status"), 0, row); grid.add(statusBox, 1, row++);
        grid.add(createFormLabel("Notes"), 0, row); grid.add(notesField, 1, row++);

        dialog.getDialogPane().setContent(grid);
        styleDialog(dialog);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                Appointment appt = existing != null ? existing : new Appointment();
                appt.setPatientName(patientField.getText());
                appt.setDoctorName(doctorField.getText());
                appt.setDepartment(deptField.getText());
                appt.setAppointmentDate(dateField.getText());
                appt.setAppointmentTime(timeField.getText());
                appt.setStatus(statusBox.getValue());
                appt.setNotes(notesField.getText());
                return appt;
            }
            return null;
        });

        Optional<Appointment> result = dialog.showAndWait();
        result.ifPresent(appt -> {
            appointmentService.save(appt);
            applyFilter(currentFilter);
        });
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        label.setMinWidth(80);
        return label;
    }

    private void styleDialog(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/fxml/styles.css").toExternalForm());
        pane.getStyleClass().add("dialog-pane");
    }
}
