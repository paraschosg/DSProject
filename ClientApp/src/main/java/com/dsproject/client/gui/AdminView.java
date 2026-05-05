package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.Doctor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminView {

    public AdminView(Stage stage, ClientController controller, String username) {

        // ================= SIDEBAR =================
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(180);
        sidebar.setStyle("-fx-background-color: #e57363;");

        Label menuTitle = new Label("Admin Panel");
        menuTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Button dashboardBtn = new Button("Dashboard");
        Button appointmentsBtn = new Button("Appointments");
        Button addAppointmentBtn = new Button("Add Appointment");
        Button logoutBtn = new Button("Logout");

        String sideBtnStyle = "-fx-background-color: #333; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 140px;";
        dashboardBtn.setStyle(sideBtnStyle);
        appointmentsBtn.setStyle(sideBtnStyle);
        addAppointmentBtn.setStyle(sideBtnStyle);
        logoutBtn.setStyle(sideBtnStyle);

        sidebar.getChildren().addAll(menuTitle, dashboardBtn, appointmentsBtn, addAppointmentBtn, logoutBtn);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f8f9fa;");

        Label welcome = new Label("Welcome, " + username);
        welcome.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        TextField nameField = new TextField();
        TextField specialtyField = new TextField();
        TextField departmentField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField costField = new TextField();

        Label l1 = new Label("Doctor Name:");
        Label l2 = new Label("Specialty:");
        Label l3 = new Label("Department:");
        Label l4 = new Label("Phone:");
        Label l5 = new Label("Email:");
        Label l6 = new Label("Visit Cost:");

        String labelStyle = "-fx-text-fill: black;";
        l1.setStyle(labelStyle);
        l2.setStyle(labelStyle);
        l3.setStyle(labelStyle);
        l4.setStyle(labelStyle);
        l5.setStyle(labelStyle);
        l6.setStyle(labelStyle);

        form.add(l1, 0, 0);
        form.add(nameField, 1, 0);

        form.add(l2, 0, 1);
        form.add(specialtyField, 1, 1);

        form.add(l3, 0, 2);
        form.add(departmentField, 1, 2);

        form.add(l4, 0, 3);
        form.add(phoneField, 1, 3);

        form.add(l5, 0, 4);
        form.add(emailField, 1, 4);

        form.add(l6, 0, 5);
        form.add(costField, 1, 5);

        Button addBtn = new Button("Add Doctor");
        addBtn.setStyle("-fx-background-color: #2c7be5; -fx-text-fill: white; -fx-font-weight: bold;");

        form.add(addBtn, 1, 6);

        content.getChildren().addAll(welcome, form);

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(content);

        Scene scene = new Scene(root, 800, 500);

        stage.setTitle("Admin Dashboard");
        stage.setScene(scene);

        logoutBtn.setOnAction(e -> {
            new LoginView(stage, controller);
        });

        appointmentsBtn.setOnAction(e -> {
            new AppointmentView(stage, controller, username, true);
        });

        addAppointmentBtn.setOnAction(e -> {
            new AddAppointmentView(stage, controller);
        });

        addBtn.setOnAction(e -> {

            String name = nameField.getText();
            String specialty = specialtyField.getText();
            String department = departmentField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String costText = costField.getText();

            if (name.isEmpty() || specialty.isEmpty() || department.isEmpty()
                    || phone.isEmpty() || email.isEmpty() || costText.isEmpty()) {

                showAlert("Error", "Fill all fields!");
                return;
            }

            double cost;

            try {
                cost = Double.parseDouble(costText);
            } catch (Exception ex) {
                showAlert("Error", "Cost must be a number!");
                return;
            }

            Doctor doctor = new Doctor(
                    name,
                    specialty,
                    department,
                    phone,
                    email,
                    cost
            );

            boolean result = controller.addDoctor(doctor, "admin");

            if (result) {
                showAlert("Success", "Doctor added!");

                nameField.clear();
                specialtyField.clear();
                departmentField.clear();
                phoneField.clear();
                emailField.clear();
                costField.clear();

            } else {
                showAlert("Error", "Failed!");
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}