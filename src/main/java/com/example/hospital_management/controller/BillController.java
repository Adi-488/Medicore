package com.example.hospital_management.controller;

import com.example.hospital_management.entity.Bill;
import com.example.hospital_management.service.BillService;
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
public class BillController {

    private final BillService billService;

    @FXML private TableView<Bill> billTable;
    @FXML private TableColumn<Bill, Long> colId;
    @FXML private TableColumn<Bill, String> colPatient;
    @FXML private TableColumn<Bill, String> colDescription;
    @FXML private TableColumn<Bill, String> colAmount;
    @FXML private TableColumn<Bill, String> colDate;
    @FXML private TableColumn<Bill, String> colStatus;
    @FXML private TableColumn<Bill, String> colPayment;
    @FXML private TableColumn<Bill, Void> colActions;

    @FXML private TextField searchField;
    @FXML private Button filterAll;
    @FXML private Button filterPending;
    @FXML private Button filterPaid;
    @FXML private Button filterOverdue;

    @FXML private Label lblTotalRevenue;
    @FXML private Label lblCollected;
    @FXML private Label lblPending;
    @FXML private Label lblTotalBills;
    @FXML private Label lblBillCount;

    private ObservableList<Bill> billData;
    private List<Button> filterButtons;
    private String currentFilter = "ALL";

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @FXML
    public void initialize() {
        filterButtons = List.of(filterAll, filterPending, filterPaid, filterOverdue);
        setupColumns();
        setupSearch();
        loadData();
        updateSummary();
    }

    private void setupColumns() {
        colId.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getId()));
        colPatient.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPatientName()));
        colDescription.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDescription()));
        colDate.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBillDate()));
        colPayment.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPaymentMethod()));

        colAmount.setCellValueFactory(cd ->
                new SimpleStringProperty("₹" + String.format("%,.0f", cd.getValue().getAmount())));

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
                        case "PAID" -> "badge-paid";
                        case "PENDING" -> "badge-pending";
                        case "OVERDUE" -> "badge-overdue";
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
            private final Button payBtn = new Button("Mark Paid");
            private final Button deleteBtn = new Button("✕");
            private final HBox box = new HBox(6, editBtn, payBtn, deleteBtn);

            {
                editBtn.getStyleClass().addAll("btn-secondary", "btn-small");
                payBtn.getStyleClass().addAll("btn-success", "btn-small");
                deleteBtn.getStyleClass().addAll("btn-danger", "btn-small");
                box.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    Bill bill = getTableView().getItems().get(getIndex());
                    onEditBill(bill);
                });

                payBtn.setOnAction(e -> {
                    Bill bill = getTableView().getItems().get(getIndex());
                    onMarkPaid(bill);
                });

                deleteBtn.setOnAction(e -> {
                    Bill bill = getTableView().getItems().get(getIndex());
                    onDeleteBill(bill);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Bill bill = getTableView().getItems().get(getIndex());
                    payBtn.setDisable("PAID".equals(bill.getStatus()));
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
                List<Bill> filtered = billService.findAll().stream()
                        .filter(b -> b.getPatientName().toLowerCase().contains(query))
                        .collect(Collectors.toList());
                billData = FXCollections.observableArrayList(filtered);
                billTable.setItems(billData);
                updateCountBadge();
            }
        });
    }

    private void loadData() {
        billData = FXCollections.observableArrayList(billService.findAll());
        billTable.setItems(billData);
        updateCountBadge();
    }

    private void updateCountBadge() {
        if (lblBillCount != null) {
            lblBillCount.setText(String.valueOf(billData.size()));
        }
    }

    private void updateSummary() {
        double total = billService.getTotalRevenue();
        double paid = billService.getPaidRevenue();
        double pending = billService.getPendingAmount();
        long count = billService.count();

        lblTotalRevenue.setText("₹" + String.format("%,.0f", total));
        lblCollected.setText("₹" + String.format("%,.0f", paid));
        lblPending.setText("₹" + String.format("%,.0f", pending));
        lblTotalBills.setText(String.valueOf(count));
    }

    // ── Filters ──

    @FXML private void onFilterAll() { applyFilter("ALL"); setActiveFilter(filterAll); }
    @FXML private void onFilterPending() { applyFilter("PENDING"); setActiveFilter(filterPending); }
    @FXML private void onFilterPaid() { applyFilter("PAID"); setActiveFilter(filterPaid); }
    @FXML private void onFilterOverdue() { applyFilter("OVERDUE"); setActiveFilter(filterOverdue); }

    private void applyFilter(String filter) {
        currentFilter = filter;
        List<Bill> data = "ALL".equals(filter) ? billService.findAll() : billService.findByStatus(filter);
        billData = FXCollections.observableArrayList(data);
        billTable.setItems(billData);
        updateCountBadge();
    }

    private void setActiveFilter(Button active) {
        filterButtons.forEach(b -> b.getStyleClass().remove("filter-button-active"));
        active.getStyleClass().add("filter-button-active");
    }

    // ── CRUD ──

    @FXML
    private void onAddBill() {
        showBillDialog(null);
    }

    private void onEditBill(Bill bill) {
        showBillDialog(bill);
    }

    private void onMarkPaid(Bill bill) {
        bill.setStatus("PAID");
        billService.save(bill);
        applyFilter(currentFilter);
        updateSummary();
    }

    private void onDeleteBill(Bill bill) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Bill");
        confirm.setHeaderText("Delete bill for " + bill.getPatientName() + "?");
        confirm.setContentText("Amount: ₹" + String.format("%,.0f", bill.getAmount()));
        styleDialog(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            billService.delete(bill.getId());
            loadData();
            updateSummary();
        }
    }

    private void showBillDialog(Bill existing) {
        Dialog<Bill> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "New Bill" : "Edit Bill");
        dialog.setHeaderText(existing == null ? "Create a new bill" : "Update bill details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.setPadding(new Insets(24, 24, 16, 24));

        TextField patientField = new TextField(existing != null ? existing.getPatientName() : "");
        patientField.setPromptText("Patient Name");

        TextField descField = new TextField(existing != null ? existing.getDescription() : "");
        descField.setPromptText("Description");

        TextField amountField = new TextField(existing != null ? String.valueOf(existing.getAmount()) : "");
        amountField.setPromptText("Amount (₹)");

        TextField dateField = new TextField(existing != null ? existing.getBillDate() : "");
        dateField.setPromptText("YYYY-MM-DD");

        ComboBox<String> statusBox = new ComboBox<>(
                FXCollections.observableArrayList("PENDING", "PAID", "OVERDUE"));
        statusBox.setValue(existing != null ? existing.getStatus() : "PENDING");
        statusBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> paymentBox = new ComboBox<>(
                FXCollections.observableArrayList("CASH", "CARD", "UPI", "INSURANCE"));
        paymentBox.setValue(existing != null ? existing.getPaymentMethod() : "CASH");
        paymentBox.setMaxWidth(Double.MAX_VALUE);

        int row = 0;
        grid.add(createFormLabel("Patient"), 0, row); grid.add(patientField, 1, row++);
        grid.add(createFormLabel("Description"), 0, row); grid.add(descField, 1, row++);
        grid.add(createFormLabel("Amount"), 0, row); grid.add(amountField, 1, row++);
        grid.add(createFormLabel("Date"), 0, row); grid.add(dateField, 1, row++);
        grid.add(createFormLabel("Status"), 0, row); grid.add(statusBox, 1, row++);
        grid.add(createFormLabel("Payment"), 0, row); grid.add(paymentBox, 1, row++);

        dialog.getDialogPane().setContent(grid);
        styleDialog(dialog);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                Bill bill = existing != null ? existing : new Bill();
                bill.setPatientName(patientField.getText());
                bill.setDescription(descField.getText());
                try { bill.setAmount(Double.parseDouble(amountField.getText())); } catch (NumberFormatException e) { bill.setAmount(0); }
                bill.setBillDate(dateField.getText().isBlank() ? java.time.LocalDate.now().toString() : dateField.getText());
                bill.setStatus(statusBox.getValue());
                bill.setPaymentMethod(paymentBox.getValue());
                return bill;
            }
            return null;
        });

        Optional<Bill> result = dialog.showAndWait();
        result.ifPresent(bill -> {
            billService.save(bill);
            applyFilter(currentFilter);
            updateSummary();
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
