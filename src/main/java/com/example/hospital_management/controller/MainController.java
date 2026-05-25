package com.example.hospital_management.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class MainController {

    private final ApplicationContext springContext;

    @FXML private StackPane contentArea;
    @FXML private Button navDashboard;
    @FXML private Button navPatients;
    @FXML private Button navAppointments;
    @FXML private Button navBills;
    @FXML private Label statusBadge;

    private List<Button> navButtons;
    private Button activeButton;

    public MainController(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    @FXML
    public void initialize() {
        navButtons = Arrays.asList(navDashboard, navPatients, navAppointments, navBills);
        activeButton = navDashboard;
        loadView("/fxml/DashboardView.fxml");
    }

    @FXML
    private void onNavDashboard() {
        setActiveNav(navDashboard);
        loadView("/fxml/DashboardView.fxml");
    }

    @FXML
    private void onNavPatients() {
        setActiveNav(navPatients);
        loadView("/fxml/PatientView.fxml");
    }

    @FXML
    private void onNavAppointments() {
        setActiveNav(navAppointments);
        loadView("/fxml/AppointmentView.fxml");
    }

    @FXML
    private void onNavBills() {
        setActiveNav(navBills);
        loadView("/fxml/BillView.fxml");
    }

    private void setActiveNav(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("nav-button-active");
        }
        button.getStyleClass().add("nav-button-active");
        activeButton = button;
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean);
            Node view = loader.load();

            // Smooth fade-in transition
            view.setOpacity(0);
            contentArea.getChildren().setAll(view);

            FadeTransition fade = new FadeTransition(Duration.millis(250), view);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
