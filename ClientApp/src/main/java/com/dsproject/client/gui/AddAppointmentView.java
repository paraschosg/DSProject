package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class AddAppointmentView {

    public AddAppointmentView(Stage stage, ClientController controller) {

        TextField doctorField = new TextField();
        TextField dateField = new TextField();
        TextField durationField = new TextField();
        TextField costField = new TextField();

        Button addBtn = new Button("Add");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Doctor Name:"), 0, 0);
        grid.add(doctorField, 1, 0);

        grid.add(new Label("DateTime (yyyy-MM-ddTHH:mm):"), 0, 1);
        grid.add(dateField, 1, 1);

        grid.add(new Label("Duration:"), 0, 2);
        grid.add(durationField, 1, 2);

        grid.add(new Label("Cost:"), 0, 3);
        grid.add(costField, 1, 3);

        grid.add(addBtn, 1, 4);

        addBtn.setOnAction(e -> {
            try {
                String doctor = doctorField.getText();
                LocalDateTime date = LocalDateTime.parse(dateField.getText());
                int duration = Integer.parseInt(durationField.getText());
                double cost = Double.parseDouble(costField.getText());

                int id = controller.addAppointment(doctor, date, duration, cost);

                if (id != -1) {
                    new Alert(Alert.AlertType.INFORMATION, "Appointment added!").show();
                    doctorField.clear();
                    dateField.clear();
                    durationField.clear();
                    costField.clear();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed!").show();
                }

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid input!").show();
            }
        });

        Scene scene = new Scene(grid, 350, 250);
        stage.setTitle("Add Appointment");
        stage.setScene(scene);
        stage.show();
    }
}