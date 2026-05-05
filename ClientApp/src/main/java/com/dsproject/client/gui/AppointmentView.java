package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.Appointment;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class AppointmentView {

    public AppointmentView(Stage stage, ClientController controller, String username, boolean isAdmin) {

        Label title = new Label("Appointments");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");

        backBtn.setOnAction(e -> {
            if (isAdmin) {
                new AdminView(stage, controller, username);
            } else {
                new PatientView(stage, controller, username);
            }
        });

        HBox header = new HBox(10, backBtn, title);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10));

        TabPane tabPane = new TabPane();

        ListView<Appointment> availableList = new ListView<>();
        Button refreshAvailable = new Button("Refresh");
        Button bookBtn = new Button("Book Appointment");

        VBox availableLayout = new VBox(10,
                availableList,
                new HBox(10, refreshAvailable, bookBtn)
        );
        availableLayout.setPadding(new Insets(15));

        Tab availableTab = new Tab("Available", availableLayout);
        availableTab.setClosable(false);

        ListView<Appointment> myList = new ListView<>();
        Button refreshMy = new Button("Refresh");
        Button cancelBtn = new Button("Cancel Booking");

        VBox myLayout = new VBox(10,
                myList,
                new HBox(10, refreshMy, cancelBtn)
        );
        myLayout.setPadding(new Insets(15));

        Tab myTab = new Tab("My Appointments", myLayout);
        myTab.setClosable(false);

        tabPane.getTabs().addAll(availableTab, myTab);

        String primary = "-fx-background-color: #2c7be5; -fx-text-fill: white; -fx-font-weight: bold;";
        String danger = "-fx-background-color: #e5533d; -fx-text-fill: white; -fx-font-weight: bold;";
        String secondary = "-fx-background-color: #6c757d; -fx-text-fill: white;";

        bookBtn.setStyle(primary);
        cancelBtn.setStyle(danger);
        refreshAvailable.setStyle(secondary);
        refreshMy.setStyle(secondary);

        refreshAvailable.setOnAction(e -> {
            availableList.getItems().clear();
            List<Appointment> apps = controller.getAvailableAppointments();
            if (apps != null) {
                availableList.getItems().addAll(apps);
            }
        });

        refreshMy.setOnAction(e -> {
            myList.getItems().clear();
            List<Appointment> apps = controller.getUserAppointments(username);
            if (apps != null) {
                myList.getItems().addAll(apps);
            }
        });

        bookBtn.setOnAction(e -> {
            Appointment selected = availableList.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert("Error", "Select an appointment first");
                return;
            }

            boolean ok = controller.bookAppointment(username, selected.getId());

            if (ok) {
                showAlert("Success", "Appointment booked");
                refreshAvailable.fire();
                refreshMy.fire();
            } else {
                showAlert("Error", "Booking failed");
            }
        });

        cancelBtn.setOnAction(e -> {
            Appointment selected = myList.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert("Error", "Select an appointment first");
                return;
            }

            int bookingId = controller.getBookingId(username, selected.getId());

            if (bookingId == -1) {
                showAlert("Error", "Booking not found");
                return;
            }

            boolean ok = controller.cancelBooking(bookingId);

            if (ok) {
                showAlert("Success", "Booking cancelled");
                refreshMy.fire();
                refreshAvailable.fire();
            } else {
                showAlert("Error", "Cancel failed");
            }
        });

        refreshAvailable.fire();
        refreshMy.fire();

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(tabPane);
        root.setStyle("-fx-background-color: #f8f9fa;");

        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("Appointments");
        stage.setScene(scene);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}