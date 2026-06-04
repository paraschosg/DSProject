package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.Appointment;
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
import java.util.stream.Collectors;

public class PatientView {

    private static final String SIDEBAR_BG   = "-fx-background-color: #1B2A41;";
    private static final String CARD_STYLE   = "-fx-background-color: white; -fx-background-radius: 12; "
            + "-fx-effect: dropshadow(gaussian, #00000015, 10, 0, 0, 2);";
    private static final String BTN_PRIMARY  = "-fx-background-color: #2563EB; -fx-text-fill: white; "
            + "-fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 8 18;";
    private static final String BTN_DANGER   = "-fx-background-color: #DC2626; -fx-text-fill: white; "
            + "-fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 8 18;";
    private static final String BTN_SUCCESS  = "-fx-background-color: #16A34A; -fx-text-fill: white; "
            + "-fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 8 18;";
    private static final String BTN_SECONDARY = "-fx-background-color: #64748B; -fx-text-fill: white; "
            + "-fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 8 18;";
    private static final String BTN_SIDEBAR  = "-fx-background-color: transparent; -fx-text-fill: #94A3B8; "
            + "-fx-font-size: 14px; -fx-alignment: center-left; -fx-pref-width: 180px; "
            + "-fx-padding: 10 15; -fx-background-radius: 8; -fx-cursor: hand;";
    private static final String BTN_SIDEBAR_ACTIVE = "-fx-background-color: #2563EB; -fx-text-fill: white; "
            + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center-left; -fx-pref-width: 180px; "
            + "-fx-padding: 10 15; -fx-background-radius: 8; -fx-cursor: hand;";

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Stage            stage;
    private final ClientController controller;
    private final String           username;
    private final String           fullName;
    private final StackPane        contentArea = new StackPane();

    public PatientView(Stage stage, ClientController controller, String username, String fullName) {
        this.stage      = stage;
        this.controller = controller;
        this.username   = username;
        this.fullName   = fullName;

        // ====== SIDEBAR ======
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(0, 10, 20, 10));
        sidebar.setPrefWidth(210);
        sidebar.setStyle(SIDEBAR_BG);

        VBox logo = new VBox(4);
        logo.setPadding(new Insets(24, 10, 20, 10));
        logo.setStyle(SIDEBAR_BG);
        Label appLabel  = new Label("MediBook");
        appLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        appLabel.setStyle("-fx-text-fill: white;");
        Label roleLabel = new Label("Patient Portal");
        roleLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        logo.getChildren().addAll(appLabel, roleLabel);

        Button dashBtn   = sidebarBtn("  Dashboard");
        Button findBtn   = sidebarBtn("  Find Appointment");
        Button myApBtn   = sidebarBtn("  My Appointments");
        Button deleteBtn = sidebarBtn("  Delete Account");
        Button logoutBtn = sidebarBtn("  Logout");
        deleteBtn.setStyle(BTN_SIDEBAR + "-fx-text-fill: #F87171;");
        logoutBtn.setStyle(BTN_SIDEBAR + "-fx-text-fill: #F87171;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(logo, dashBtn, findBtn, myApBtn, spacer, deleteBtn, logoutBtn);

        // ====== TOP BAR ======
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(14, 24, 14, 24));
        topBar.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");
        topBar.setAlignment(Pos.CENTER_RIGHT);
        Label userLabel = new Label("Hello, " + fullName);
        userLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        topBar.getChildren().add(userLabel);

        contentArea.setStyle("-fx-background-color: #F1F5F9;");
        contentArea.setPadding(new Insets(24));

        VBox mainArea = new VBox(topBar, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(mainArea);

        Scene scene = new Scene(root, 1000, 640);
        stage.setTitle("MediBook - Patient Portal");
        stage.setScene(scene);
        stage.setResizable(true);

        showDashboard();
        setActive(dashBtn, findBtn, myApBtn);

        // ====== SIDEBAR ACTIONS ======
        dashBtn.setOnAction(e -> { setActive(dashBtn, findBtn, myApBtn); showDashboard(); });
        findBtn.setOnAction(e -> { setActive(findBtn, dashBtn, myApBtn); showFindAppointment(); });
        myApBtn.setOnAction(e -> { setActive(myApBtn, dashBtn, findBtn); showMyAppointments(); });

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete your account? This cannot be undone.");
            confirm.setHeaderText("Delete Account");
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                if (controller.deleteUser(username)) new LoginView(stage, controller);
                else alert("Could not delete account.");
            }
        });
        logoutBtn.setOnAction(e -> new LoginView(stage, controller));
    }

    // ==================== DASHBOARD ====================

    private void showDashboard() {
        VBox dash = new VBox(20);
        dash.setAlignment(Pos.TOP_LEFT);

        Label title = sectionTitle("Welcome, " + fullName);
        Label sub   = new Label("Manage your appointments from the sidebar.");
        sub.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");

        List<Appointment> myAps = controller.getUserAppointments(username);
        long upcoming = myAps.stream().filter(a -> a.getDateTime().isAfter(LocalDateTime.now())).count();
        long past     = myAps.stream().filter(a -> a.getDateTime().isBefore(LocalDateTime.now())).count();
        long avail    = controller.getAvailableAppointments().size();

        HBox cards = new HBox(16);
        cards.getChildren().addAll(
            statCard("Upcoming",        String.valueOf(upcoming), "#2563EB"),
            statCard("Past",            String.valueOf(past),     "#16A34A"),
            statCard("Available Slots", String.valueOf(avail),    "#D97706")
        );
        dash.getChildren().addAll(title, sub, cards);
        contentArea.getChildren().setAll(dash);
    }

    private VBox statCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(20));
        card.setPrefWidth(180);
        card.setStyle(CARD_STYLE);
        Label val = new Label(value);
        val.setFont(Font.font("System", FontWeight.BOLD, 32));
        val.setStyle("-fx-text-fill: " + color + ";");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        card.getChildren().addAll(val, lbl);
        return card;
    }

    // ==================== FIND APPOINTMENT ====================

    private void showFindAppointment() {
        VBox panel = new VBox(16);
        Label title = sectionTitle("Find & Book Appointment");

        // Search filters card
        VBox filterCard = new VBox(12);
        filterCard.setPadding(new Insets(16));
        filterCard.setStyle(CARD_STYLE);
        Label filterTitle = new Label("Search Filters");
        filterTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        filterTitle.setStyle("-fx-text-fill: #1E293B;");

        GridPane filters = new GridPane();
        filters.setHgap(14);
        filters.setVgap(8);
        TextField docF  = filterField("Doctor name...");
        TextField specF = filterField("Specialty...");
        TextField minCF = filterField("Min cost €");
        TextField maxCF = filterField("Max cost €");

        filters.add(lbl("Doctor:"),    0, 0); filters.add(docF,  1, 0);
        filters.add(lbl("Specialty:"), 2, 0); filters.add(specF, 3, 0);
        filters.add(lbl("Min Cost:"),  0, 1); filters.add(minCF, 1, 1);
        filters.add(lbl("Max Cost:"),  2, 1); filters.add(maxCF, 3, 1);

        Button searchBtn  = primaryBtn("Search");
        Button clearBtn   = secondaryBtn("Clear");
        Button refreshBtn = secondaryBtn("Refresh");

        HBox filterBtns = new HBox(10, searchBtn, clearBtn, refreshBtn);
        filterBtns.setAlignment(Pos.CENTER_LEFT);
        filterCard.getChildren().addAll(filterTitle, filters, filterBtns);

        // Results table card
        VBox tableCard = new VBox(10);
        tableCard.setPadding(new Insets(16));
        tableCard.setStyle(CARD_STYLE);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        TableView<Appointment> apTable = buildApTable();
        VBox.setVgrow(apTable, Priority.ALWAYS);

        Button bookBtn = new Button("Book Selected");
        bookBtn.setStyle(BTN_SUCCESS);

        HBox tableBtns = new HBox(10, bookBtn);
        tableBtns.setAlignment(Pos.CENTER_LEFT);
        tableCard.getChildren().addAll(apTable, tableBtns);

        panel.getChildren().addAll(title, filterCard, tableCard);
        contentArea.getChildren().setAll(panel);
        VBox.setVgrow(panel, Priority.ALWAYS);

        // Load all available appointments
        final List<Appointment>[] cache = new List[]{controller.getAvailableAppointments()};
        apTable.getItems().setAll(cache[0]);

        searchBtn.setOnAction(e -> {
            String doc  = docF.getText().trim().toLowerCase();
            String spec = specF.getText().trim().toLowerCase();
            double minC = parseOrZero(minCF.getText());
            double maxC = parseOrMax(maxCF.getText());
            apTable.getItems().setAll(
                cache[0].stream()
                    .filter(a -> doc.isEmpty()  || a.getDoctorName().toLowerCase().contains(doc))
                    .filter(a -> minC <= 0      || a.getCost() >= minC)
                    .filter(a -> maxC >= Double.MAX_VALUE || a.getCost() <= maxC)
                    .collect(Collectors.toList())
            );
        });

        clearBtn.setOnAction(e -> {
            docF.clear(); specF.clear(); minCF.clear(); maxCF.clear();
            apTable.getItems().setAll(cache[0]);
        });

        refreshBtn.setOnAction(e -> {
            cache[0] = controller.getAvailableAppointments();
            apTable.getItems().setAll(cache[0]);
        });

        bookBtn.setOnAction(e -> {
            Appointment sel = apTable.getSelectionModel().getSelectedItem();
            if (sel == null) { alert("Select an appointment first."); return; }
            boolean ok = controller.bookAppointment(username, sel.getId());
            if (ok) {
                showPaymentDialog(sel);
                cache[0] = controller.getAvailableAppointments();
                apTable.getItems().setAll(cache[0]);
            } else {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "This slot is currently booked.\nJoin the waitlist? You will be notified when it becomes available.");
                confirm.setHeaderText("Slot Unavailable");
                Optional<ButtonType> res = confirm.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.OK) {
                    // Server already adds to waitlist when bookAppointment fails (slot not available)
                    alert("You have been added to the waitlist. We will notify you when the slot is free.");
                }
            }
        });
    }

    private void showPaymentDialog(Appointment ap) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Complete Payment");
        dialog.setHeaderText("Appointment Booked — Please complete payment");

        VBox box = new VBox(12);
        box.setPadding(new Insets(20, 30, 10, 20));

        Label costVal = new Label(String.format("Amount due: €%.2f", ap.getCost()));
        costVal.setFont(Font.font("System", FontWeight.BOLD, 20));
        costVal.setStyle("-fx-text-fill: #2563EB;");

        Label apInfo = new Label(ap.getDoctorName() + "  |  " + ap.getDateTime().format(FMT));
        apInfo.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");

        Separator sep = new Separator();

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        TextField nameField = new TextField(fullName);
        nameField.setPrefWidth(260);
        TextField cardField = new TextField();
        cardField.setPromptText("**** **** **** ****");
        cardField.setPrefWidth(260);

        form.add(new Label("Cardholder Name:"), 0, 0); form.add(nameField, 1, 0);
        form.add(new Label("Card Number:"),     0, 1); form.add(cardField, 1, 1);

        Label note = new Label("Payment is simulated for demonstration purposes.");
        note.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px;");

        box.getChildren().addAll(costVal, apInfo, sep, form, note);
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Pay Now", ButtonBar.ButtonData.OK_DONE),
                ButtonType.CANCEL);

        dialog.showAndWait();
        alert("Payment processed successfully! Your appointment is confirmed.");
    }

    // ==================== MY APPOINTMENTS ====================

    private void showMyAppointments() {
        VBox panel = new VBox(16);
        VBox.setVgrow(panel, Priority.ALWAYS);
        Label title = sectionTitle("My Appointments");

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabs, Priority.ALWAYS);

        // Tab 1: Upcoming
        TableView<Appointment> upTable  = buildApTable();
        Button cancelBtn  = new Button("Cancel Booking");
        Button refreshUp  = secondaryBtn("Refresh");
        cancelBtn.setStyle(BTN_DANGER);
        VBox upBox = new VBox(10, upTable, new HBox(10, cancelBtn, refreshUp));
        upBox.setPadding(new Insets(12));
        VBox.setVgrow(upTable, Priority.ALWAYS);
        Tab upcomingTab = new Tab("Upcoming Appointments", upBox);

        // Tab 2: Past / Review
        TableView<Appointment> pastTable = buildApTable();
        Button reviewBtn   = new Button("Submit Review");
        Button refreshPast = secondaryBtn("Refresh");
        reviewBtn.setStyle(BTN_SUCCESS);
        VBox pastBox = new VBox(10, pastTable, new HBox(10, reviewBtn, refreshPast));
        pastBox.setPadding(new Insets(12));
        VBox.setVgrow(pastTable, Priority.ALWAYS);
        Tab pastTab = new Tab("Past Appointments", pastBox);

        tabs.getTabs().addAll(upcomingTab, pastTab);

        loadUpcomingAps(upTable);
        loadPastAps(pastTable);

        VBox tabCard = new VBox(tabs);
        tabCard.setStyle(CARD_STYLE);
        VBox.setVgrow(tabCard, Priority.ALWAYS);

        panel.getChildren().addAll(title, tabCard);
        contentArea.getChildren().setAll(panel);

        // ====== ACTIONS ======
        refreshUp.setOnAction(e   -> loadUpcomingAps(upTable));
        refreshPast.setOnAction(e -> loadPastAps(pastTable));

        cancelBtn.setOnAction(e -> {
            Appointment sel = upTable.getSelectionModel().getSelectedItem();
            if (sel == null) { alert("Select an appointment first."); return; }
            int bookingId = controller.getBookingId(username, sel.getId());
            if (bookingId == -1) { alert("Booking record not found."); return; }
            boolean ok = controller.cancelBooking(bookingId);
            if (ok) {
                alert("Booking cancelled successfully.");
                loadUpcomingAps(upTable);
            } else {
                alert("Cannot cancel: appointment is within 24 hours,\nor booking was not found.");
            }
        });

        reviewBtn.setOnAction(e -> {
            Appointment sel = pastTable.getSelectionModel().getSelectedItem();
            if (sel == null) { alert("Select an appointment first."); return; }
            int bookingId = controller.getBookingId(username, sel.getId());
            if (bookingId == -1) { alert("Booking record not found."); return; }
            showReviewDialog(bookingId, sel.getDoctorName());
        });
    }

    private void showReviewDialog(int bookingId, String doctorName) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Review Dr. " + doctorName);
        dialog.setHeaderText("Share your experience");

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        ComboBox<Integer> ratingBox = new ComboBox<>();
        ratingBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingBox.setValue(5);
        ratingBox.setPromptText("Rating (1–5)");

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Write your comment here...");
        commentArea.setPrefRowCount(4);
        commentArea.setWrapText(true);

        box.getChildren().addAll(
            lbl("Rating (1-5):"), ratingBox,
            lbl("Comment (optional):"), commentArea
        );
        dialog.getDialogPane().setContent(box);

        ButtonType submitBtn = new ButtonType("Submit Review", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitBtn, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == submitBtn) {
                return controller.submitReview(bookingId, ratingBox.getValue(), commentArea.getText().trim());
            }
            return null; // null = user cancelled, don't show alert
        });

        Optional<Boolean> result = dialog.showAndWait();
        result.ifPresent(ok -> alert(ok
                ? "Review submitted! Thank you for your feedback."
                : "Could not submit review. It may already exist or the appointment was not completed."));
    }

    // ==================== HELPERS ====================

    private TableView<Appointment> buildApTable() {
        TableView<Appointment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(260);
        table.getColumns().addAll(
            col("Doctor",   a -> a.getDoctorName()),
            col("DateTime", a -> a.getDateTime().format(FMT)),
            col("Duration", a -> a.getDuration() + " min"),
            col("Cost",     a -> String.format("€%.2f", a.getCost()))
        );
        return table;
    }

    private void loadUpcomingAps(TableView<Appointment> table) {
        table.getItems().setAll(
            controller.getUserAppointments(username).stream()
                .filter(a -> a.getDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList())
        );
    }

    private void loadPastAps(TableView<Appointment> table) {
        table.getItems().setAll(
            controller.getUserAppointments(username).stream()
                .filter(a -> a.getDateTime().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList())
        );
    }

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

    private Label lbl(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        return l;
    }

    private Button primaryBtn(String text) {
        Button b = new Button(text); b.setStyle(BTN_PRIMARY); return b;
    }

    private Button secondaryBtn(String text) {
        Button b = new Button(text); b.setStyle(BTN_SECONDARY); return b;
    }

    private TextField filterField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle("-fx-background-color: white; -fx-border-color: #D1D5DB; "
                + "-fx-border-radius: 7; -fx-background-radius: 7; -fx-font-size: 13px;");
        f.setPrefHeight(34);
        f.setPrefWidth(160);
        return f;
    }

    private <T> TableColumn<T, String> col(String name, java.util.function.Function<T, String> mapper) {
        TableColumn<T, String> c = new TableColumn<>(name);
        c.setCellValueFactory(data -> new SimpleStringProperty(mapper.apply(data.getValue())));
        return c;
    }

    private double parseOrZero(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0; }
    }

    private double parseOrMax(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return Double.MAX_VALUE; }
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
