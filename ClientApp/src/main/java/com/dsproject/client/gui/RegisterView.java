package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RegisterView {

    public RegisterView(Stage stage, ClientController controller) {

        TextField nameField = new TextField();
        TextField amkaField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("patient", "admin");
        roleBox.setValue("patient");

        Button registerBtn = new Button("Register");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("AMKA:"), 0, 1);
        grid.add(amkaField, 1, 1);

        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);

        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);

        grid.add(new Label("Username:"), 0, 4);
        grid.add(usernameField, 1, 4);

        grid.add(new Label("Password:"), 0, 5);
        grid.add(passwordField, 1, 5);

        grid.add(new Label("Role:"), 0, 6);
        grid.add(roleBox, 1, 6);

        grid.add(registerBtn, 1, 7);

        registerBtn.setOnAction(e -> {

            String fullName = nameField.getText();
            String amka = amkaField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleBox.getValue();

            if (fullName.isEmpty() || amka.isEmpty() || phone.isEmpty()
                    || email.isEmpty() || username.isEmpty() || password.isEmpty()) {

                new Alert(Alert.AlertType.ERROR, "Fill all fields!").show();
                return;
            }

            User user = new User(fullName, amka, phone, email, username, password, role);

            boolean success = controller.register(user);

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Registered successfully!").show();
                new LoginView(stage, controller);
            } else {
                new Alert(Alert.AlertType.ERROR, "User already exists!").show();
            }
        });

        Scene scene = new Scene(grid, 350, 400);
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }
}