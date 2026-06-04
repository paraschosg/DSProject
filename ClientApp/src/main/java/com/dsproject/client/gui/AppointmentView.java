package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.Appointment;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Standalone appointment view (legacy – kept for compatibility).
 * The main AdminView and PatientView now embed appointment management directly.
 */
public class AppointmentView {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public AppointmentView(Stage stage, ClientController controller, String username, boolean isAdmin) {

        // ====== HEADER ======
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #64748B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 8 16;");
        backBtn.setOnAction(e -> {
            if (isAdmin) new AdminView(stage, controller, username);
            else         new PatientView(stage, controller, username, username);
        });

        Label title = new Label("Appointments");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: #0F172A;");

        HBox header = new HBox(14, backBtn, title);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 20, 16, 20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");

        // ====== AVAILABLE TAB ======
        TableView<Appointment> availTable = buildTable();
        Button refreshAvail = new Button("Refresh");
        Button bookBtn      = new Button("Book");
        refreshAvail.setStyle("-fx-background-color: #64748B; -fx-text-fill: white; "
                + "-fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 8 16;");
        bookBtn.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 8 16;");

        VBox availLayout = new VBox(10, availTable, new HBox(10, refreshAvail, bookBtn));
        availLayout.setPadding(new Insets(14));
        Tab availTab = new Tab("Available", availLayout);
        availTab.setClosable(false);

        // ====== MY APPOINTMENTS TAB ======
        TableView<Appointment> myTable = buildTable();
        Button refreshMy  = new Button("Refresh");
        Button cancelBtn  = new Button("Cancel");
        refreshMy.setStyle("-fx-background-color: #64748B; -fx-text-fill: white; "
                + "-fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 8 16;");
        cancelBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 8 16;");

        VBox myLayout = new VBox(10, myTable, new HBox(10, refreshMy, cancelBtn));
        myLayout.setPadding(new Insets(14));
        Tab myTab = new Tab("My Appointments", myLayout);
        myTab.setClosable(false);

        TabPane tabPane = new TabPane(availTab, myTab);
        tabPane.setStyle("-fx-background-color: white;");

        // ====== ROOT ======
        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(tabPane);
        root.setStyle("-fx-background-color: #F1F5F9;");

        Scene scene = new Scene(root, 800, 540);
        stage.setTitle("MediBook - Appointments");
        stage.setScene(scene);

        // ====== ACTIONS ======
        refreshAvail.setOnAction(e -> {
            List<Appointment> apps = controller.getAvailableAppointments();
            availTable.getItems().setAll(apps);
        });

        refreshMy.setOnAction(e -> {
            List<Appointment> apps = controller.getUserAppointments(username);
            myTable.getItems().setAll(apps);
        });

        bookBtn.setOnAction(e -> {
            Appointment sel = availTable.getSelectionModel().getSelectedItem();
            if (sel == null) { alert("Select an appointment first."); return; }
            boolean ok = controller.bookAppointment(username, sel.getId());
            if (ok) { alert("Appointment booked!"); refreshAvail.fire(); refreshMy.fire(); }
            else    { alert("Booking failed (slot may be taken or you're now on the waitlist)."); }
        });

        cancelBtn.setOnAction(e -> {
            Appointment sel = myTable.getSelectionModel().getSelectedItem();
            if (sel == null) { alert("Select an appointment first."); return; }
            int bookingId = controller.getBookingId(username, sel.getId());
            if (bookingId == -1) { alert("Booking not found."); return; }
            boolean ok = controller.cancelBooking(bookingId);
            if (ok) { alert("Booking cancelled."); refreshMy.fire(); refreshAvail.fire(); }
            else    { alert("Cancel failed: appointment is within 24 hours."); }
        });

        refreshAvail.fire();
        refreshMy.fire();
    }

    private TableView<Appointment> buildTable() {
        TableView<Appointment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);

        TableColumn<Appointment, String> docCol = new TableColumn<>("Doctor");
        docCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDoctorName()));

        TableColumn<Appointment, String> dtCol = new TableColumn<>("Date & Time");
        dtCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDateTime().format(FMT)));

        TableColumn<Appointment, String> durCol = new TableColumn<>("Duration");
        durCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDuration() + " min"));

        TableColumn<Appointment, String> costCol = new TableColumn<>("Cost");
        costCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("€%.2f", d.getValue().getCost())));

        table.getColumns().addAll(docCol, dtCol, durCol, costCol);
        return table;
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
