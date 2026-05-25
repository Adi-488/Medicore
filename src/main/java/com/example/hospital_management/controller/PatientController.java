package com.example.hospital_management.controller;

import com.example.hospital_management.entity.Patient;
import com.example.hospital_management.service.PatientService;
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

@Component
@Scope("prototype")
public class PatientController {

    private final PatientService patientService;

    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, Long> colId;
    @FXML private TableColumn<Patient, String> colName;
    @FXML private TableColumn<Patient, Integer> colAge;
    @FXML private TableColumn<Patient, String> colGender;
    @FXML private TableColumn<Patient, String> colPhone;
    @FXML private TableColumn<Patient, String> colType;
    @FXML private TableColumn<Patient, String> colStatus;
    @FXML private TableColumn<Patient, String> colDiagnosis;
    @FXML private TableColumn<Patient, String> colBed;
    @FXML private TableColumn<Patient, Void> colActions;

    @FXML private TextField searchField;
    @FXML private Button filterAll;
    @FXML private Button filterInpatient;
    @FXML private Button filterOutpatient;
    @FXML private Button filterAdmitted;
    @FXML private Button filterDischarged;
    @FXML private Label lblPatientCount;

    private ObservableList<Patient> patientData;
    private List<Button> filterButtons;
    private String currentFilter = "ALL";

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @FXML
    public void initialize() {
        filterButtons = List.of(filterAll, filterInpatient, filterOutpatient, filterAdmitted, filterDischarged);
        setupColumns();
        setupSearch();
        loadData();
    }

    private void setupColumns() {
        colId.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getId()));
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colAge.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getAge()));
        colGender.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getGender()));
        colPhone.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPhone()));

        colType.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPatientType()));
        colType.setCellFactory(col -> new TableCell<>() {
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

        colDiagnosis.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDiagnosis()));
        colBed.setCellValueFactory(cd -> {
            Integer bed = cd.getValue().getBedNumber();
            return new SimpleStringProperty(bed != null ? String.valueOf(bed) : "—");
        });

        // Actions column
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(6, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().addAll("btn-secondary", "btn-small");
                deleteBtn.getStyleClass().addAll("btn-danger", "btn-small");
                box.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    onEditPatient(patient);
                });

                deleteBtn.setOnAction(e -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    onDeletePatient(patient);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.isBlank()) {
                applyFilter(currentFilter);
            } else {
                List<Patient> results = patientService.searchByName(newVal.trim());
                patientData = FXCollections.observableArrayList(results);
                patientTable.setItems(patientData);
                updateCountBadge();
            }
        });
    }

    private void loadData() {
        patientData = FXCollections.observableArrayList(patientService.findAll());
        patientTable.setItems(patientData);
        updateCountBadge();
    }

    private void updateCountBadge() {
        if (lblPatientCount != null) {
            lblPatientCount.setText(String.valueOf(patientData.size()));
        }
    }

    // ── Filter handlers ──

    @FXML private void onFilterAll() { applyFilter("ALL"); setActiveFilter(filterAll); }
    @FXML private void onFilterInpatient() { applyFilter("INPATIENT"); setActiveFilter(filterInpatient); }
    @FXML private void onFilterOutpatient() { applyFilter("OUTPATIENT"); setActiveFilter(filterOutpatient); }
    @FXML private void onFilterAdmitted() { applyFilter("ADMITTED"); setActiveFilter(filterAdmitted); }
    @FXML private void onFilterDischarged() { applyFilter("DISCHARGED"); setActiveFilter(filterDischarged); }

    private void applyFilter(String filter) {
        currentFilter = filter;
        List<Patient> data;
        if ("ALL".equals(filter)) {
            data = patientService.findAll();
        } else if ("INPATIENT".equals(filter) || "OUTPATIENT".equals(filter)) {
            data = patientService.findByPatientType(filter);
        } else {
            data = patientService.findByStatus(filter);
        }
        patientData = FXCollections.observableArrayList(data);
        patientTable.setItems(patientData);
        updateCountBadge();
    }

    private void setActiveFilter(Button active) {
        filterButtons.forEach(b -> b.getStyleClass().remove("filter-button-active"));
        active.getStyleClass().add("filter-button-active");
    }

    // ── CRUD handlers ──

    @FXML
    private void onAddPatient() {
        showPatientDialog(null);
    }

    private void onEditPatient(Patient patient) {
        showPatientDialog(patient);
    }

    private void onDeletePatient(Patient patient) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Patient");
        confirm.setHeaderText("Delete " + patient.getName() + "?");
        confirm.setContentText("This action cannot be undone.");
        styleDialog(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            patientService.delete(patient.getId());
            loadData();
        }
    }

    private void showPatientDialog(Patient existing) {
        Dialog<Patient> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Patient" : "Edit Patient");
        dialog.setHeaderText(existing == null ? "Enter patient details" : "Update patient details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.setPadding(new Insets(24, 24, 16, 24));

        TextField nameField = new TextField(existing != null ? existing.getName() : "");
        nameField.setPromptText("Full Name");
        nameField.getStyleClass().add("text-field");

        TextField ageField = new TextField(existing != null ? String.valueOf(existing.getAge()) : "");
        ageField.setPromptText("Age");
        ageField.getStyleClass().add("text-field");

        ComboBox<String> genderBox = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
        genderBox.setValue(existing != null ? existing.getGender() : "Male");
        genderBox.setMaxWidth(Double.MAX_VALUE);

        TextField phoneField = new TextField(existing != null ? existing.getPhone() : "");
        phoneField.setPromptText("Phone Number");
        phoneField.getStyleClass().add("text-field");

        TextField emailField = new TextField(existing != null ? existing.getEmail() : "");
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("text-field");

        TextField addressField = new TextField(existing != null ? existing.getAddress() : "");
        addressField.setPromptText("Address");
        addressField.getStyleClass().add("text-field");

        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList("INPATIENT", "OUTPATIENT"));
        typeBox.setValue(existing != null ? existing.getPatientType() : "OUTPATIENT");
        typeBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> statusBox = new ComboBox<>(FXCollections.observableArrayList("ADMITTED", "DISCHARGED", "ACTIVE"));
        statusBox.setValue(existing != null ? existing.getStatus() : "ACTIVE");
        statusBox.setMaxWidth(Double.MAX_VALUE);

        TextField diagnosisField = new TextField(existing != null ? existing.getDiagnosis() : "");
        diagnosisField.setPromptText("Diagnosis");
        diagnosisField.getStyleClass().add("text-field");

        TextField bedField = new TextField(existing != null && existing.getBedNumber() != null ?
                String.valueOf(existing.getBedNumber()) : "");
        bedField.setPromptText("Bed Number");
        bedField.getStyleClass().add("text-field");

        // Layout
        int row = 0;
        grid.add(createFormLabel("Name"), 0, row); grid.add(nameField, 1, row++);
        grid.add(createFormLabel("Age"), 0, row); grid.add(ageField, 1, row++);
        grid.add(createFormLabel("Gender"), 0, row); grid.add(genderBox, 1, row++);
        grid.add(createFormLabel("Phone"), 0, row); grid.add(phoneField, 1, row++);
        grid.add(createFormLabel("Email"), 0, row); grid.add(emailField, 1, row++);
        grid.add(createFormLabel("Address"), 0, row); grid.add(addressField, 1, row++);
        grid.add(createFormLabel("Type"), 0, row); grid.add(typeBox, 1, row++);
        grid.add(createFormLabel("Status"), 0, row); grid.add(statusBox, 1, row++);
        grid.add(createFormLabel("Diagnosis"), 0, row); grid.add(diagnosisField, 1, row++);
        grid.add(createFormLabel("Bed #"), 0, row); grid.add(bedField, 1, row++);

        dialog.getDialogPane().setContent(grid);
        styleDialog(dialog);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                Patient patient = existing != null ? existing : new Patient();
                patient.setName(nameField.getText());
                try { patient.setAge(Integer.parseInt(ageField.getText())); } catch (NumberFormatException e) { patient.setAge(0); }
                patient.setGender(genderBox.getValue());
                patient.setPhone(phoneField.getText());
                patient.setEmail(emailField.getText());
                patient.setAddress(addressField.getText());
                patient.setPatientType(typeBox.getValue());
                patient.setStatus(statusBox.getValue());
                patient.setDiagnosis(diagnosisField.getText());
                try { patient.setBedNumber(Integer.parseInt(bedField.getText())); } catch (NumberFormatException e) { patient.setBedNumber(null); }
                if (patient.getAdmittedDate() == null) patient.setAdmittedDate(java.time.LocalDate.now().toString());
                return patient;
            }
            return null;
        });

        Optional<Patient> result = dialog.showAndWait();
        result.ifPresent(patient -> {
            patientService.save(patient);
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
