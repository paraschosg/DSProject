package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.Appointment;
import com.dsproject.server.models.Doctor;
import com.dsproject.server.models.Review;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdminView {

    private static final String SIDEBAR_BG   = "-fx-background-color: #1B2A41;";
    private static final String CONTENT_BG   = "-fx-background-color: #F1F5F9;";
    private static final String CARD_STYLE   = "-fx-background-color: white; -fx-background-radius: 12; "
            + "-fx-effect: dropshadow(gaussian, #00000015, 10, 0, 0, 2);";
    private static final String BTN_PRIMARY  = "-fx-background-color: #2563EB; -fx-text-fill: white; "
            + "-fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 8 18;";
    private static final String BTN_DANGER   = "-fx-background-color: #DC2626; -fx-text-fill: white; "
            + "-fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 8 18;";
    private static final String BTN_SIDEBAR  = "-fx-background-color: transparent; -fx-text-fill: #94A3B8; "
            + "-fx-font-size: 14px; -fx-alignment: center-left; -fx-pref-width: 180px; "
            + "-fx-padding: 10 15; -fx-background-radius: 8; -fx-cursor: hand;";
    private static final String BTN_SIDEBAR_ACTIVE = "-fx-background-color: #2563EB; -fx-text-fill: white; "
            + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center-left; -fx-pref-width: 180px; "
            + "-fx-padding: 10 15; -fx-background-radius: 8; -fx-cursor: hand;";

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Stage            stage;
    private final ClientController controller;
    private final String           username;

    private final StackPane contentArea = new StackPane();

    public AdminView(Stage stage, ClientController controller, String username) {
        this.stage      = stage;
        this.controller = controller;
        this.username   = username;

        // ====== SIDEBAR ======
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(0, 10, 20, 10));
        sidebar.setPrefWidth(210);
        sidebar.setStyle(SIDEBAR_BG);

        // Logo area
        VBox logo = new VBox(4);
        logo.setPadding(new Insets(24, 10, 20, 10));
        logo.setStyle(SIDEBAR_BG);
        Label appLabel = new Label("MediBook");
        appLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        appLabel.setStyle("-fx-text-fill: white;");
        Label roleLabel = new Label("Administrator");
        roleLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        logo.getChildren().addAll(appLabel, roleLabel);

        Button dashBtn     = sidebarBtn("  Dashboard");
        Button doctorsBtn  = sidebarBtn("  Manage Doctors");
        Button scheduleBtn = sidebarBtn("  Manage Schedule");
        Button reviewsBtn  = sidebarBtn("  Reviews");
        Button logoutBtn   = sidebarBtn("  Logout");
        logoutBtn.setStyle(logoutBtn.getStyle()
                + "-fx-text-fill: #F87171; -fx-margin-top: 30;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(logo, dashBtn, doctorsBtn, scheduleBtn, reviewsBtn, spacer, logoutBtn);

        // ====== TOP BAR ======
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(14, 24, 14, 24));
        topBar.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");
        topBar.setAlignment(Pos.CENTER_RIGHT);
        Label userLabel = new Label("Logged in as: " + username);
        userLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        topBar.getChildren().add(userLabel);

        // ====== CONTENT AREA ======
        contentArea.setStyle(CONTENT_BG);
        contentArea.setPadding(new Insets(24));
        showDashboard(dashBtn, doctorsBtn, scheduleBtn, reviewsBtn);

        VBox mainArea = new VBox(topBar, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(mainArea);

        Scene scene = new Scene(root, 1000, 640);
        stage.setTitle("MediBook - Admin Panel");
        stage.setScene(scene);
        stage.setResizable(true);

        // ====== SIDEBAR ACTIONS ======
        dashBtn.setOnAction(e -> {
            setActive(dashBtn, doctorsBtn, scheduleBtn, reviewsBtn);
            showDashboard(dashBtn, doctorsBtn, scheduleBtn, reviewsBtn);
        });
        doctorsBtn.setOnAction(e -> {
            setActive(doctorsBtn, dashBtn, scheduleBtn, reviewsBtn);
            showDoctorsPanel();
        });
        scheduleBtn.setOnAction(e -> {
            setActive(scheduleBtn, dashBtn, doctorsBtn, reviewsBtn);
            showSchedulePanel();
        });
        reviewsBtn.setOnAction(e -> {
            setActive(reviewsBtn, dashBtn, doctorsBtn, scheduleBtn);
            showReviewsPanel();
        });
        logoutBtn.setOnAction(e -> new LoginView(stage, controller));
    }

    // ==================== DASHBOARD ====================

    private void showDashboard(Button... btns) {
        setActive(btns[0], btns[1], btns[2], btns[3]);
        int doctorCount = controller.getDoctors().size();
        int apCount     = controller.getAllAppointments().size();
        int avCount     = controller.getAvailableAppointments().size();
        int rvCount     = controller.getAllReviews().size();

        VBox dash = new VBox(20);
        dash.setAlignment(Pos.TOP_LEFT);

        Label title = sectionTitle("Dashboard");

        HBox cards = new HBox(16);
        cards.getChildren().addAll(
            statCard("Doctors",       String.valueOf(doctorCount), "#2563EB"),
            statCard("Total Slots",   String.valueOf(apCount),     "#16A34A"),
            statCard("Available",     String.valueOf(avCount),     "#D97706"),
            statCard("Reviews",       String.valueOf(rvCount),     "#7C3AED")
        );

        Label welcome = new Label("Welcome, " + username + "! Use the sidebar to manage the clinic.");
        welcome.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");

        dash.getChildren().addAll(title, cards, welcome);
        contentArea.getChildren().setAll(dash);
    }

    private VBox statCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(20));
        card.setPrefWidth(170);
        card.setStyle(CARD_STYLE);
        Label val = new Label(value);
        val.setFont(Font.font("System", FontWeight.BOLD, 32));
        val.setStyle("-fx-text-fill: " + color + ";");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        card.getChildren().addAll(val, lbl);
        return card;
    }

    // ==================== DOCTORS PANEL ====================

    private void showDoctorsPanel() {
        VBox panel = new VBox(16);

        Label title = sectionTitle("Manage Doctors");

        // Form card
        VBox formCard = new VBox(12);
        formCard.setPadding(new Insets(20));
        formCard.setStyle(CARD_STYLE);
        Label formTitle = new Label("Add New Doctor");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        formTitle.setStyle("-fx-text-fill: #1E293B;");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(10);

        TextField nameF  = formField("Full Name");
        TextField specF  = formField("Specialty (e.g. Cardiologist)");
        TextField deptF  = formField("Department");
        TextField phoneF = formField("Internal Phone");
        TextField emailF = formField("Email");
        TextField costF  = formField("Base Cost (€)");

        form.add(label("Full Name"), 0, 0); form.add(nameF,  1, 0);
        form.add(label("Specialty"), 2, 0); form.add(specF,  3, 0);
        form.add(label("Department"),0, 1); form.add(deptF,  1, 1);
        form.add(label("Phone"),     2, 1); form.add(phoneF, 3, 1);
        form.add(label("Email"),     0, 2); form.add(emailF, 1, 2);
        form.add(label("Cost (€)"),  2, 2); form.add(costF,  3, 2);

        Label errLabel = new Label(""); errLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px;");

        Button addBtn = new Button("Add Doctor");
        addBtn.setStyle(BTN_PRIMARY);
        HBox btnRow = new HBox(10, addBtn, errLabel);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        formCard.getChildren().addAll(formTitle, form, btnRow);

        // Doctor list card
        VBox listCard = new VBox(10);
        listCard.setPadding(new Insets(16));
        listCard.setStyle(CARD_STYLE);
        Label listTitle = new Label("Registered Doctors");
        listTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        listTitle.setStyle("-fx-text-fill: #1E293B;");

        TableView<Doctor> doctorTable = new TableView<>();
        doctorTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        doctorTable.setPrefHeight(200);

        TableColumn<Doctor, String> nameCol = col("Name",       d -> d.getFullName());
        TableColumn<Doctor, String> specCol = col("Specialty",  d -> d.getSpecialty());
        TableColumn<Doctor, String> deptCol = col("Department", d -> d.getDepartment());
        TableColumn<Doctor, String> costCol = col("Cost (€)",   d -> String.format("%.2f", d.getCost()));

        doctorTable.getColumns().addAll(nameCol, specCol, deptCol, costCol);
        refreshDoctorTable(doctorTable);

        listCard.getChildren().addAll(listTitle, doctorTable);

        panel.getChildren().addAll(title, formCard, listCard);
        contentArea.getChildren().setAll(panel);

        addBtn.setOnAction(e -> {
            String name  = nameF.getText().trim();
            String spec  = specF.getText().trim();
            String dept  = deptF.getText().trim();
            String phone = phoneF.getText().trim();
            String email = emailF.getText().trim();
            String costStr = costF.getText().trim();
            if (name.isEmpty() || spec.isEmpty() || dept.isEmpty()
                    || phone.isEmpty() || email.isEmpty() || costStr.isEmpty()) {
                errLabel.setText("All fields required.");
                return;
            }
            try {
                double cost = Double.parseDouble(costStr);
                Doctor doc  = new Doctor(name, spec, dept, phone, email, cost);
                if (controller.addDoctor(doc, "admin")) {
                    alert("Doctor added successfully.");
                    nameF.clear(); specF.clear(); deptF.clear();
                    phoneF.clear(); emailF.clear(); costF.clear();
                    errLabel.setText("");
                    refreshDoctorTable(doctorTable);
                } else {
                    errLabel.setText("Failed to add doctor.");
                }
            } catch (NumberFormatException ex) {
                errLabel.setText("Cost must be a number.");
            }
        });
    }

    private void refreshDoctorTable(TableView<Doctor> table) {
        table.getItems().setAll(controller.getDoctors());
    }

    // ==================== SCHEDULE PANEL ====================

    private void showSchedulePanel() {
        VBox panel = new VBox(16);

        Label title = sectionTitle("Manage Appointment Slots");

        // Add slot form
        VBox formCard = new VBox(12);
        formCard.setPadding(new Insets(20));
        formCard.setStyle(CARD_STYLE);
        Label formTitle = new Label("Add New Slot");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        formTitle.setStyle("-fx-text-fill: #1E293B;");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(10);

        TextField docF      = formField("Doctor Name");
        TextField dateF     = formField("Date/Time  (yyyy-MM-ddTHH:mm)");
        TextField durF      = formField("Duration (minutes)");
        TextField costF2    = formField("Cost (€)");

        form.add(label("Doctor"),   0, 0); form.add(docF,   1, 0);
        form.add(label("DateTime"), 2, 0); form.add(dateF,  3, 0);
        form.add(label("Duration"), 0, 1); form.add(durF,   1, 1);
        form.add(label("Cost (€)"), 2, 1); form.add(costF2, 3, 1);

        Label slotErr = new Label(""); slotErr.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px;");
        Button addSlotBtn = new Button("Add Slot");
        addSlotBtn.setStyle(BTN_PRIMARY);
        HBox addRow = new HBox(10, addSlotBtn, slotErr);
        addRow.setAlignment(Pos.CENTER_LEFT);
        formCard.getChildren().addAll(formTitle, form, addRow);

        // Slot table
        VBox tableCard = new VBox(10);
        tableCard.setPadding(new Insets(16));
        tableCard.setStyle(CARD_STYLE);
        Label tableTitle = new Label("All Appointment Slots");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        tableTitle.setStyle("-fx-text-fill: #1E293B;");

        TableView<Appointment> apTable = new TableView<>();
        apTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        apTable.setPrefHeight(220);

        apTable.getColumns().addAll(
            col("ID",       a -> String.valueOf(a.getId())),
            col("Doctor",   a -> a.getDoctorName()),
            col("DateTime", a -> a.getDateTime().format(FMT)),
            col("Min",      a -> String.valueOf(a.getDuration())),
            col("Cost €",   a -> String.format("%.2f", a.getCost())),
            col("Status",   a -> a.isAvailable() ? "Available" : "Booked by " + a.getBookedBy())
        );

        refreshApTable(apTable);

        Button editBtn   = new Button("Edit Selected");
        Button deleteBtn = new Button("Delete Selected");
        Button refreshBtn = new Button("Refresh");
        editBtn.setStyle(BTN_PRIMARY);
        deleteBtn.setStyle(BTN_DANGER);
        refreshBtn.setStyle("-fx-background-color: #64748B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 8 18;");

        HBox btnRow2 = new HBox(10, editBtn, deleteBtn, refreshBtn);
        tableCard.getChildren().addAll(tableTitle, apTable, btnRow2);

        panel.getChildren().addAll(title, formCard, tableCard);
        contentArea.getChildren().setAll(panel);

        // Actions
        addSlotBtn.setOnAction(e -> {
            try {
                String doc   = docF.getText().trim();
                LocalDateTime dt = LocalDateTime.parse(dateF.getText().trim());
                int dur      = Integer.parseInt(durF.getText().trim());
                double cost  = Double.parseDouble(costF2.getText().trim());
                if (doc.isEmpty()) { slotErr.setText("Doctor name required."); return; }
                int id = controller.addAppointment(doc, dt, dur, cost);
                if (id != -1) {
                    alert("Slot added (ID: " + id + ").");
                    docF.clear(); dateF.clear(); durF.clear(); costF2.clear();
                    slotErr.setText("");
                    refreshApTable(apTable);
                } else { slotErr.setText("Failed to add slot."); }
            } catch (Exception ex) { slotErr.setText("Invalid input. Use format: yyyy-MM-ddTHH:mm"); }
        });

        editBtn.setOnAction(e -> {
            Appointment sel = apTable.getSelectionModel().getSelectedItem();
            if (sel == null) { alert("Select a slot first."); return; }
            showEditAppointmentDialog(sel, apTable);
        });

        deleteBtn.setOnAction(e -> {
            Appointment sel = apTable.getSelectionModel().getSelectedItem();
            if (sel == null) { alert("Select a slot first."); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete slot #" + sel.getId() + " (" + sel.getDoctorName() + ")?\n"
                    + "Any patient booked will be notified automatically.");
            confirm.setHeaderText("Confirm Delete");
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                boolean ok = controller.deleteAppointment(sel.getId());
                alert(ok ? "Slot deleted." : "Failed to delete slot.");
                if (ok) refreshApTable(apTable);
            }
        });

        refreshBtn.setOnAction(e -> refreshApTable(apTable));
    }

    private void showEditAppointmentDialog(Appointment ap, TableView<Appointment> table) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Appointment Slot #" + ap.getId());
        dialog.setHeaderText("Update date/time and cost for Dr. " + ap.getDoctorName());

        TextField dtField   = new TextField(ap.getDateTime().format(FMT));
        TextField costField = new TextField(String.valueOf(ap.getCost()));

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(12); grid.setPadding(new Insets(20));
        grid.add(new Label("New DateTime (yyyy-MM-ddTHH:mm):"), 0, 0);
        grid.add(dtField, 1, 0);
        grid.add(new Label("New Cost (€):"), 0, 1);
        grid.add(costField, 1, 1);
        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == saveBtn) {
                try {
                    LocalDateTime newDt = LocalDateTime.parse(dtField.getText().trim());
                    double newCost = Double.parseDouble(costField.getText().trim());
                    boolean ok = controller.updateAppointment(ap.getId(), newDt, newCost);
                    alert(ok ? "Slot updated." : "Update failed.");
                    if (ok) refreshApTable(table);
                    return ok;
                } catch (Exception e) {
                    alert("Invalid input.");
                }
            }
            return false;
        });
        dialog.showAndWait();
    }

    private void refreshApTable(TableView<Appointment> table) {
        table.getItems().setAll(controller.getAllAppointments());
    }

    // ==================== REVIEWS PANEL ====================

    private void showReviewsPanel() {
        VBox panel = new VBox(16);
        Label title = sectionTitle("Doctor Reviews");

        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(CARD_STYLE);

        // Doctor selector
        ComboBox<String> doctorBox = new ComboBox<>();
        List<Doctor> docs = controller.getDoctors();
        for (Doctor d : docs) doctorBox.getItems().add(d.getFullName());
        doctorBox.setPromptText("Select a doctor...");
        doctorBox.setStyle("-fx-font-size: 13px; -fx-pref-width: 300px;");

        TableView<Review> reviewTable = new TableView<>();
        reviewTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        reviewTable.setPrefHeight(280);

        TableColumn<Review, String> ratingCol = col("Rating", r -> "★".repeat(r.getRating()));
        TableColumn<Review, String> patCol    = col("Patient", r -> r.getPatientUsername());
        TableColumn<Review, String> commentCol = col("Comment", r -> r.getComment());

        reviewTable.getColumns().addAll(ratingCol, patCol, commentCol);

        Label avgLabel = new Label("Select a doctor to view reviews.");
        avgLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        doctorBox.setOnAction(e -> {
            String doc = doctorBox.getValue();
            if (doc == null) return;
            List<Review> revs = controller.getDoctorReviews(doc);
            reviewTable.getItems().setAll(revs);
            if (revs.isEmpty()) {
                avgLabel.setText("No reviews for " + doc + " yet.");
            } else {
                double avg = revs.stream().mapToInt(Review::getRating).average().orElse(0);
                avgLabel.setText(String.format("Average rating: %.1f ★  (%d reviews)", avg, revs.size()));
            }
        });

        card.getChildren().addAll(label("Select Doctor:"), doctorBox, avgLabel, reviewTable);
        panel.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(panel);
    }

    // ==================== HELPERS ====================

    private Button sidebarBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(BTN_SIDEBAR);
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private void setActive(Button active, Button... others) {
        active.setStyle(BTN_SIDEBAR_ACTIVE);
        for (Button b : others) b.setStyle(BTN_SIDEBAR);
    }

    private Label sectionTitle(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 22));
        lbl.setStyle("-fx-text-fill: #0F172A;");
        return lbl;
    }

    private Label label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        return l;
    }

    private TextField formField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle("-fx-background-color: white; -fx-border-color: #D1D5DB; "
                + "-fx-border-radius: 7; -fx-background-radius: 7; -fx-font-size: 13px;");
        f.setPrefHeight(36);
        f.setPrefWidth(220);
        return f;
    }

    private <T> TableColumn<T, String> col(String name, java.util.function.Function<T, String> mapper) {
        TableColumn<T, String> c = new TableColumn<>(name);
        c.setCellValueFactory(data -> new SimpleStringProperty(mapper.apply(data.getValue())));
        return c;
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
