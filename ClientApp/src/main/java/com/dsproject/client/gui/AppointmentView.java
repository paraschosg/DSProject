package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class AppointmentView {

    public AppointmentView(Stage stage, ClientController controller, String username) {

        ObservableList<Appointment> data = FXCollections.observableArrayList();
        ListView<Appointment> listView = new ListView<>(data);

        Button refresh = new Button("Refresh");
        Button book = new Button("Book");
        Button cancel = new Button("Cancel");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        root.setCenter(listView);

        HBox bottom = new HBox(10);
        bottom.setPadding(new Insets(10));
        bottom.getChildren().addAll(refresh, book, cancel);

        root.setBottom(bottom);

        refresh.setOnAction(e -> {
            data.clear();

            List<Appointment> apps = controller.getAvailableAppointments();

            if (apps != null) {
                data.addAll(apps);
            } else {
                new Alert(Alert.AlertType.ERROR, "No data from server").show();
            }
        });

        book.setOnAction(e -> {
            Appointment selected = listView.getSelectionModel().getSelectedItem();

            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Select an appointment first").show();
                return;
            }

            boolean result = controller.bookAppointment(username, selected.getId());

            if (result) {
                new Alert(Alert.AlertType.INFORMATION, "Booked successfully").show();
                refresh.fire();
            } else {
                new Alert(Alert.AlertType.ERROR, "Booking failed").show();
            }
        });

        cancel.setOnAction(e -> {
            Appointment selected = listView.getSelectionModel().getSelectedItem();

            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Select an appointment first").show();
                return;
            }

            boolean result = controller.cancelBooking(selected.getId());

            if (result) {
                new Alert(Alert.AlertType.INFORMATION, "Cancelled successfully").show();
                refresh.fire();
            } else {
                new Alert(Alert.AlertType.ERROR, "Cancel failed").show();
            }
        });

        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Appointments");
        stage.setScene(scene);
        stage.show();

        refresh.fire();
    }
}