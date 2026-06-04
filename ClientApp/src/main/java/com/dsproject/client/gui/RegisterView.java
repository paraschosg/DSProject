package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RegisterView {

    public RegisterView(Stage stage, ClientController controller) {

        // ====== HEADER ======
        HBox header = new HBox();
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setStyle("-fx-background-color: #1B2A41;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);

        Button backBtn = new Button("← Back to Login");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; "
                + "-fx-font-size: 13px; -fx-cursor: hand; -fx-border-color: #334155; "
                + "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12;");

        Label title = new Label("MediBook  —  Create Account");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: white;");

        header.getChildren().addAll(backBtn, title);

        // ====== FORM ======
        GridPane form = new GridPane();
        form.setHgap(20);
        form.setVgap(14);
        form.setPadding(new Insets(40));

        TextField nameField     = styledField("e.g. Giorgos Papadopoulos");
        TextField amkaField     = styledField("15-digit AMKA number");
        TextField phoneField    = styledField("e.g. 6901234567");
        TextField emailField    = styledField("e.g. user@email.com");
        TextField usernameField = styledField("Choose a username");
        PasswordField passField = new PasswordField();
        passField.setPromptText("At least 6 characters");
        styleControl(passField);

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("patient", "admin");
        roleBox.setValue("patient");
        roleBox.setStyle("-fx-font-size: 13px; -fx-background-color: white; -fx-border-color: #D1D5DB; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");
        roleBox.setPrefHeight(40);

        // Column 1
        addRow(form, "Full Name",  nameField,  0, 0);
        addRow(form, "AMKA",       amkaField,  0, 1);
        addRow(form, "Phone",      phoneField, 0, 2);
        addRow(form, "Email",      emailField, 0, 3);

        // Column 2
        addRow(form, "Username",  usernameField, 2, 0);
        addRow(form, "Password",  passField,     2, 1);
        addRow(form, "Role",      roleBox,       2, 2);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px;");
        GridPane.setColumnSpan(errorLabel, 4);
        form.add(errorLabel, 0, 4);

        Button registerBtn = new Button("Create Account");
        registerBtn.setPrefWidth(220);
        registerBtn.setPrefHeight(42);
        registerBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; "
                + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        GridPane.setColumnSpan(registerBtn, 4);
        form.add(registerBtn, 0, 5);

        // ====== ROOT ======
        VBox root = new VBox(header, form);
        root.setStyle("-fx-background-color: #F8FAFC;");
        VBox.setVgrow(form, Priority.ALWAYS);

        // ====== ACTIONS ======
        backBtn.setOnAction(e -> new LoginView(stage, controller));

        registerBtn.setOnAction(e -> {
            String fullName  = nameField.getText().trim();
            String amka      = amkaField.getText().trim();
            String phone     = phoneField.getText().trim();
            String email     = emailField.getText().trim();
            String username  = usernameField.getText().trim();
            String password  = passField.getText();
            String role      = roleBox.getValue();

            if (fullName.isEmpty() || amka.isEmpty() || phone.isEmpty()
                    || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("All fields are required.");
                return;
            }

            User user = new User(fullName, amka, phone, email, username, password, role);
            boolean ok = controller.register(user);

            if (ok) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Account created successfully! You can now log in.");
                alert.setHeaderText(null);
                alert.showAndWait();
                new LoginView(stage, controller);
            } else {
                errorLabel.setText("Username already exists. Please choose another.");
            }
        });

        Scene scene = new Scene(root, 780, 480);
        stage.setTitle("MediBook - Register");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void addRow(GridPane grid, String labelText, Control field, int col, int row) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        VBox box = new VBox(5, lbl, field);
        box.setPrefWidth(260);
        grid.add(box, col, row);
    }

    private TextField styledField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        styleControl(f);
        return f;
    }

    private void styleControl(Control c) {
        c.setStyle("-fx-background-color: white; -fx-border-color: #D1D5DB; "
                + "-fx-border-radius: 8; -fx-background-radius: 8; "
                + "-fx-padding: 9 12; -fx-font-size: 13px;");
        c.setPrefHeight(40);
        c.setPrefWidth(260);
    }
}
