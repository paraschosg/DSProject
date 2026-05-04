package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.Doctor;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AddDoctorView {

    public AddDoctorView(Stage stage, ClientController controller) {

        TextField nameField = new TextField();
        TextField specialtyField = new TextField();

        Button addBtn = new Button("Add");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Specialty:"), 0, 1);
        grid.add(specialtyField, 1, 1);

        grid.add(addBtn, 1, 2);

        addBtn.setOnAction(e -> {

            String name = nameField.getText();
            String specialty = specialtyField.getText();

            if (name.isEmpty() || specialty.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Fill all fields!").show();
                return;
            }

            Doctor doctor = new Doctor(
                    name,
                    specialty,
                    "General",
                    "1234567890",
                    name.toLowerCase() + "@mail.com",
                    50.0
            );

            boolean ok = controller.addDoctor(doctor, "admin");

            if (ok) {
                new Alert(Alert.AlertType.INFORMATION, "Doctor added!").show();
                nameField.clear();
                specialtyField.clear();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed!").show();
            }
        });

        Scene scene = new Scene(grid, 300, 200);
        stage.setTitle("Add Doctor");
        stage.setScene(scene);
        stage.show();
    }
}