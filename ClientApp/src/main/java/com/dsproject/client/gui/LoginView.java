package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginView {

    public LoginView(Stage stage, ClientController controller) {

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);

        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        grid.add(loginButton, 0, 2);
        grid.add(registerButton, 1, 2);

        loginButton.setOnAction(e -> {

            String user = usernameField.getText();
            String pass = passwordField.getText();

            User loggedUser = controller.login(user, pass);

            if (loggedUser != null) {

                new Alert(Alert.AlertType.INFORMATION, "Login Successful").show();

                controller.registerCallback(user);

                if (loggedUser.getRole().equals("admin")) {
                    new AdminView(stage, controller, user);
                } else {
                    new PatientView(stage, controller, user);
                }

            } else {
                new Alert(Alert.AlertType.ERROR, "Login Failed").show();
            }
        });

        registerButton.setOnAction(e -> {
            new RegisterView(stage, controller);
        });

        Scene scene = new Scene(grid, 300, 200);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}