package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PatientView {

    public PatientView(Stage stage, ClientController controller, String username) {

        Label welcomeLabel = new Label("Welcome " + username);

        Button viewAppointmentsBtn = new Button("View Appointments");
        Button deleteBtn = new Button("Delete Account");
        Button logoutBtn = new Button("Logout");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        layout.getChildren().addAll(
                welcomeLabel,
                viewAppointmentsBtn,
                deleteBtn,
                logoutBtn
        );

        viewAppointmentsBtn.setOnAction(e -> {
            new AppointmentView(stage, controller, username);
        });

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean ok = controller.deleteUser(username);

                    if (ok) {
                        new Alert(Alert.AlertType.INFORMATION, "Account deleted").show();
                        new LoginView(stage, controller);
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed").show();
                    }
                }
            });
        });

        logoutBtn.setOnAction(e -> {
            new LoginView(stage, controller);
        });

        Scene scene = new Scene(layout, 300, 200);
        stage.setTitle("Patient Menu");
        stage.setScene(scene);
        stage.show();
    }
}