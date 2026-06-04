package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {

    public LoginView(Stage stage, ClientController controller) {

        // ====== LEFT PANEL (brand) ======
        VBox leftPanel = new VBox(20);
        leftPanel.setPrefWidth(320);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(50));
        leftPanel.setStyle("-fx-background-color: #1B2A41;");

        Label appName = new Label("MediBook");
        appName.setFont(Font.font("System", FontWeight.BOLD, 32));
        appName.setTextFill(Color.WHITE);

        Label tagline = new Label("Appointment Management\nSystem");
        tagline.setStyle("-fx-font-size: 14px; -fx-text-fill: #94A3B8; -fx-text-alignment: center;");
        tagline.setAlignment(Pos.CENTER);

        Label divider = new Label("──────────────");
        divider.setStyle("-fx-text-fill: #334155;");

        Label feature1 = new Label("✓  Book appointments instantly");
        Label feature2 = new Label("✓  Real-time notifications");
        Label feature3 = new Label("✓  Secure & reliable");
        for (Label f : new Label[]{feature1, feature2, feature3}) {
            f.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        }

        leftPanel.getChildren().addAll(appName, tagline, divider, feature1, feature2, feature3);

        // ====== RIGHT PANEL (form) ======
        VBox rightPanel = new VBox(18);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(60, 50, 60, 50));
        rightPanel.setStyle("-fx-background-color: #F8FAFC;");
        rightPanel.setPrefWidth(380);

        Label loginTitle = new Label("Welcome back");
        loginTitle.setFont(Font.font("System", FontWeight.BOLD, 26));
        loginTitle.setStyle("-fx-text-fill: #0F172A;");

        Label subtitle = new Label("Sign in to your account");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");

        VBox usernameBox = labeledField("Username");
        TextField usernameField = (TextField) usernameBox.getChildren().get(1);

        VBox passwordBox = labeledField("Password");
        PasswordField passwordField = new PasswordField();
        styleField(passwordField);
        passwordBox.getChildren().set(1, passwordField);

        Button loginBtn = new Button("Sign In");
        loginBtn.setPrefWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(42);
        loginBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; "
                + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");

        Separator sep = new Separator();
        sep.setStyle("-fx-border-color: #E2E8F0;");

        Button registerBtn = new Button("Create an account");
        registerBtn.setPrefWidth(Double.MAX_VALUE);
        registerBtn.setPrefHeight(40);
        registerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563EB; "
                + "-fx-font-size: 13px; -fx-border-color: #2563EB; -fx-border-radius: 8; "
                + "-fx-background-radius: 8; -fx-cursor: hand;");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px;");

        rightPanel.getChildren().addAll(loginTitle, subtitle, usernameBox, passwordBox,
                loginBtn, sep, registerBtn, errorLabel);

        // ====== ROOT ======
        HBox root = new HBox(leftPanel, rightPanel);
        root.setStyle("-fx-background-color: #F8FAFC;");
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // ====== ACTIONS ======
        loginBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText();
            if (user.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Please enter username and password.");
                return;
            }
            User loggedUser = controller.login(user, pass);
            if (loggedUser != null) {
                controller.registerCallback(user);
                if ("admin".equals(loggedUser.getRole())) {
                    new AdminView(stage, controller, user);
                } else {
                    new PatientView(stage, controller, user, loggedUser.getFullName());
                }
            } else {
                errorLabel.setText("Invalid username or password.");
                passwordField.clear();
            }
        });

        registerBtn.setOnAction(e -> new RegisterView(stage, controller));

        // Allow Enter key on password
        passwordField.setOnAction(e -> loginBtn.fire());

        Scene scene = new Scene(root, 700, 480);
        stage.setTitle("MediBook - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private VBox labeledField(String labelText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        TextField field = new TextField();
        styleField(field);
        VBox box = new VBox(5, label, field);
        return box;
    }

    private void styleField(Control field) {
        field.setStyle("-fx-background-color: white; -fx-border-color: #D1D5DB; "
                + "-fx-border-radius: 8; -fx-background-radius: 8; "
                + "-fx-padding: 9 12; -fx-font-size: 13px;");
        field.setPrefHeight(40);
    }
}
