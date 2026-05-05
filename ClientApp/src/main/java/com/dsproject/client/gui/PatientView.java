package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PatientView {

    public PatientView(Stage stage, ClientController controller, String username) {

        Label title = new Label("Patient Dashboard");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        HBox header = new HBox(title);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50;");
        header.setAlignment(Pos.CENTER_LEFT);

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: #34495e;");

        Label menuLabel = new Label("Menu");
        menuLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Button viewAppointmentsBtn = new Button("Appointments");
        Button deleteBtn = new Button("Delete Account");
        Button logoutBtn = new Button("Logout");

        String sidebarBtnStyle =
                "-fx-background-color: #2c3e50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-pref-width: 160px;" +
                        "-fx-background-radius: 8;";

        viewAppointmentsBtn.setStyle(sidebarBtnStyle);
        deleteBtn.setStyle(sidebarBtnStyle);
        logoutBtn.setStyle(sidebarBtnStyle);

        sidebar.getChildren().addAll(
                menuLabel,
                viewAppointmentsBtn,
                deleteBtn,
                logoutBtn
        );

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label welcome = new Label("Welcome, " + username);
        welcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label info = new Label("Manage your appointments easily.");
        info.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        content.getChildren().addAll(welcome, info);

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setLeft(sidebar);
        root.setCenter(content);
        root.setStyle("-fx-background-color: #ecf0f1;");

        Scene scene = new Scene(root, 700, 450);

        stage.setTitle("Patient Panel");
        stage.setScene(scene);

        viewAppointmentsBtn.setOnAction(e -> {
            new AppointmentView(stage, controller, username, false);
        });

        deleteBtn.setOnAction(e -> {
            boolean ok = controller.deleteUser(username);

            if (ok) {
                new LoginView(stage, controller);
            }
        });

        logoutBtn.setOnAction(e -> {
            new LoginView(stage, controller);
        });
    }
}