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

        Label title = new Label("Admin Dashboard - " + username);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button logoutBtn = new Button("Logout");

        BorderPane topPanel = new BorderPane();
        topPanel.setCenter(title);
        topPanel.setRight(logoutBtn);

        Button viewAppointmentsBtn = new Button("View Appointments");
        Button addAppointmentBtn = new Button("Add Appointment");

        viewAppointmentsBtn.setPrefWidth(180);
        addAppointmentBtn.setPrefWidth(180);

        HBox buttonsPanel = new HBox(20, viewAppointmentsBtn, addAppointmentBtn);
        buttonsPanel.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();
        TextField specialtyField = new TextField();
        TextField departmentField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField costField = new TextField();

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Doctor Name:"), 0, 0);
        form.add(nameField, 1, 0);

        form.add(new Label("Specialty:"), 0, 1);
        form.add(specialtyField, 1, 1);

        form.add(new Label("Department:"), 0, 2);
        form.add(departmentField, 1, 2);

        form.add(new Label("Phone:"), 0, 3);
        form.add(phoneField, 1, 3);

        form.add(new Label("Email:"), 0, 4);
        form.add(emailField, 1, 4);

        form.add(new Label("Visit Cost:"), 0, 5);
        form.add(costField, 1, 5);

        Button addButton = new Button("Add Doctor");
        addButton.setPrefWidth(150);

        form.add(new Label(""), 0, 6);
        form.add(addButton, 1, 6);

        VBox root = new VBox(20, topPanel, buttonsPanel, form);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 550, 450);

        stage.setTitle("Admin Panel");
        stage.setScene(scene);
        stage.show();

        logoutBtn.setOnAction(e -> {
            new LoginView(stage, controller);
        });

        viewAppointmentsBtn.setOnAction(e -> {
            new AppointmentView(stage, controller, username);
        });

        addAppointmentBtn.setOnAction(e -> {
            new AddAppointmentView(stage, controller);
        });

        addButton.setOnAction(e -> {

            String name = nameField.getText();
            String specialty = specialtyField.getText();
            String department = departmentField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String costText = costField.getText();

            if (name.isEmpty() || specialty.isEmpty() || department.isEmpty()
                    || phone.isEmpty() || email.isEmpty() || costText.isEmpty()) {

                new Alert(Alert.AlertType.ERROR, "Fill all fields!").show();
                return;
            }

            double cost;

            try {
                cost = Double.parseDouble(costText);
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Cost must be a number!").show();
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
                new Alert(Alert.AlertType.INFORMATION, "Doctor added!").show();

                nameField.clear();
                specialtyField.clear();
                departmentField.clear();
                phoneField.clear();
                emailField.clear();
                costField.clear();

            } else {
                new Alert(Alert.AlertType.ERROR, "Failed!").show();
            }
        });
    }
}