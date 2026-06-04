package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDateTime;

/**
 * Standalone "Add Appointment Slot" view (kept for compatibility).
 * Slot management is also embedded in AdminView's Schedule panel.
 */
public class AddAppointmentView {

    public AddAppointmentView(Stage stage, ClientController controller) {

        // ====== HEADER ======
        HBox header = new HBox();
        header.setPadding(new Insets(16, 20, 16, 20));
        header.setStyle("-fx-background-color: #1B2A41;");
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("MediBook  —  Add Appointment Slot");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: white;");
        header.getChildren().add(title);

        // ====== FORM ======
        VBox formCard = new VBox(14);
        formCard.setPadding(new Insets(30, 40, 30, 40));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        formCard.setMaxWidth(500);

        Label formTitle = new Label("New Appointment Slot");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        formTitle.setStyle("-fx-text-fill: #1E293B;");

        TextField doctorField   = field("Doctor's full name");
        TextField dateField     = field("e.g. 2026-06-15T09:00");
        TextField durationField = field("Duration in minutes");
        TextField costField     = field("Cost in euros");

        Label errLabel = new Label("");
        errLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px;");

        Button addBtn = new Button("Add Slot");
        addBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 24;");

        formCard.getChildren().addAll(
            formTitle,
            lbl("Doctor Name:"),  doctorField,
            lbl("Date & Time (yyyy-MM-ddTHH:mm):"), dateField,
            lbl("Duration (minutes):"),   durationField,
            lbl("Cost (€):"),             costField,
            errLabel,
            addBtn
        );

        // ====== ROOT ======
        StackPane center = new StackPane(formCard);
        center.setPadding(new Insets(40));
        center.setStyle("-fx-background-color: #F1F5F9;");
        StackPane.setAlignment(formCard, Pos.CENTER);

        VBox root = new VBox(header, center);
        VBox.setVgrow(center, Priority.ALWAYS);

        // ====== ACTION ======
        addBtn.setOnAction(e -> {
            try {
                String doc  = doctorField.getText().trim();
                if (doc.isEmpty()) { errLabel.setText("Doctor name required."); return; }
                LocalDateTime dt  = LocalDateTime.parse(dateField.getText().trim());
                int    dur   = Integer.parseInt(durationField.getText().trim());
                double cost  = Double.parseDouble(costField.getText().trim());

                int id = controller.addAppointment(doc, dt, dur, cost);
                if (id != -1) {
                    Alert ok = new Alert(Alert.AlertType.INFORMATION,
                            "Slot added successfully! ID: " + id);
                    ok.setHeaderText(null);
                    ok.showAndWait();
                    doctorField.clear(); dateField.clear(); durationField.clear(); costField.clear();
                    errLabel.setText("");
                } else {
                    errLabel.setText("Failed to add slot. Check server connection.");
                }
            } catch (Exception ex) {
                errLabel.setText("Invalid input. Use format: yyyy-MM-ddTHH:mm");
            }
        });

        Scene scene = new Scene(root, 560, 520);
        stage.setTitle("MediBook - Add Slot");
        stage.setScene(scene);
        stage.show();
    }

    private TextField field(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #D1D5DB; "
                + "-fx-border-radius: 7; -fx-background-radius: 7; -fx-padding: 8 10; -fx-font-size: 13px;");
        f.setPrefHeight(38);
        return f;
    }

    private Label lbl(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        return l;
    }
}
