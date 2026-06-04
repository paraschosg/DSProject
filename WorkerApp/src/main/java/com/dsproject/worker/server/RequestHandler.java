package com.dsproject.worker.server;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.function.Consumer;

/**
 * Handles socket requests from the 1st server (ServerApp).
 * Manages persistent file storage for users, doctors, appointments, bookings, reviews.
 * All in-memory maps are shared across threads via synchronization.
 */
public class RequestHandler implements Runnable {

    private final Socket socket;

    // Shared in-memory stores (thread-safe via synchronized wrappers)
    private static final Map<String, String[]> users        = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final List<String[]>        doctors      = Collections.synchronizedList(new ArrayList<>());
    private static final Map<Integer, String[]> appointments = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<Integer, String[]> bookings     = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final List<String[]>        reviews      = Collections.synchronizedList(new ArrayList<>());

    private static final String USERS_FILE        = "users.txt";
    private static final String DOCTORS_FILE      = "doctors.txt";
    private static final String APPOINTMENTS_FILE = "appointments.txt";
    private static final String BOOKINGS_FILE     = "bookings.txt";
    private static final String REVIEWS_FILE      = "reviews.txt";

    // Load persisted data on first class load
    static {
        loadFromFile(USERS_FILE, line -> {
            String[] p = line.split(";", -1);
            if (p.length >= 7) users.put(p[0], p);
        });
        loadFromFile(DOCTORS_FILE, line -> {
            String[] p = line.split(";", -1);
            if (p.length >= 6) doctors.add(p);
        });
        loadFromFile(APPOINTMENTS_FILE, line -> {
            String[] p = line.split(";", -1);
            if (p.length >= 7) {
                try { appointments.put(Integer.parseInt(p[0]), p); } catch (NumberFormatException ignored) {}
            }
        });
        loadFromFile(BOOKINGS_FILE, line -> {
            String[] p = line.split(";", -1);
            if (p.length >= 3) {
                try { bookings.put(Integer.parseInt(p[0]), p); } catch (NumberFormatException ignored) {}
            }
        });
        loadFromFile(REVIEWS_FILE, line -> {
            String[] p = line.split(";", -1);
            if (p.length >= 5) reviews.add(p);
        });

        // Ensure default admin exists
        if (!users.containsKey("admin")) {
            String[] admin = {"admin", "1234", "Admin", "000000000000000", "0000000000", "admin@clinic.com", "admin"};
            users.put("admin", admin);
            appendToFile(USERS_FILE, String.join(";", admin));
            System.out.println("[Worker] Default admin created.");
        }

        System.out.println("[Worker] Loaded: " + users.size() + " users, "
                + doctors.size() + " doctors, " + appointments.size() + " appointments.");
    }

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String request = in.readLine();
            if (request == null || request.trim().isEmpty()) { out.println("FAIL"); return; }

            System.out.println("[Worker] " + request.substring(0, Math.min(request.length(), 100)));

            String[] parts = request.split(";", -1);
            String cmd = parts[0].trim();

            switch (cmd) {
                case "register"          -> handleRegister(parts, out);
                case "login"             -> handleLogin(parts, out);
                case "delete"            -> handleDeleteUser(parts, out);
                case "getAllUsers"        -> handleGetAllUsers(out);
                case "addDoctor"         -> handleAddDoctor(parts, out);
                case "getAllDoctors"      -> handleGetAllDoctors(out);
                case "addAppointment"    -> handleAddAppointment(parts, out);
                case "updateAppointment" -> handleUpdateAppointment(parts, out);
                case "deleteAppointment" -> handleDeleteAppointment(parts, out);
                case "getAllAppointments" -> handleGetAllAppointments(out);
                case "bookAppointment"   -> handleBookAppointment(parts, out);
                case "cancelBooking"     -> handleCancelBooking(parts, out);
                case "getAllBookings"     -> handleGetAllBookings(out);
                case "addReview"         -> handleAddReview(parts, out);
                case "getAllReviews"      -> handleGetAllReviews(out);
                default                  -> out.println("UNKNOWN");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (Exception ignored) {}
        }
    }

    // ==================== USER HANDLERS ====================

    // format: register;username;password;fullName;amka;phone;email;role
    private void handleRegister(String[] p, PrintWriter out) {
        if (p.length < 8) { out.println("FAIL"); return; }
        String username = p[1].trim();
        synchronized (users) {
            if (users.containsKey(username)) { out.println("FAIL"); return; }
            String[] data = {p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim(), p[5].trim(), p[6].trim(), p[7].trim()};
            users.put(username, data);
            appendToFile(USERS_FILE, String.join(";", data));
        }
        out.println("OK");
    }

    private void handleLogin(String[] p, PrintWriter out) {
        if (p.length < 3) { out.println("FAIL"); return; }
        String[] user = users.get(p[1].trim());
        out.println(user != null && user[1].equals(p[2].trim()) ? "OK" : "FAIL");
    }

    private void handleDeleteUser(String[] p, PrintWriter out) {
        if (p.length < 2) { out.println("FAIL"); return; }
        boolean removed;
        synchronized (users) {
            removed = users.remove(p[1].trim()) != null;
            if (removed) rewriteUsersFile();
        }
        out.println(removed ? "OK" : "FAIL");
    }

    // Response: count\nfield1;field2;...\n...
    private void handleGetAllUsers(PrintWriter out) {
        List<String[]> all;
        synchronized (users) { all = new ArrayList<>(users.values()); }
        out.println(all.size());
        for (String[] u : all) out.println(String.join(";", u));
    }

    // ==================== DOCTOR HANDLERS ====================

    // format: addDoctor;fullName;specialty;department;phone;email;cost
    private void handleAddDoctor(String[] p, PrintWriter out) {
        if (p.length < 7) { out.println("FAIL"); return; }
        String[] doc = {p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim(), p[5].trim(), p[6].trim()};
        synchronized (doctors) {
            doctors.add(doc);
            appendToFile(DOCTORS_FILE, String.join(";", doc));
        }
        out.println("OK");
    }

    private void handleGetAllDoctors(PrintWriter out) {
        List<String[]> all;
        synchronized (doctors) { all = new ArrayList<>(doctors); }
        out.println(all.size());
        for (String[] d : all) out.println(String.join(";", d));
    }

    // ==================== APPOINTMENT HANDLERS ====================

    // format: addAppointment;id;doctorName;dateTime;duration;cost
    // stored : id;doctorName;dateTime;duration;cost;available;bookedBy
    private void handleAddAppointment(String[] p, PrintWriter out) {
        if (p.length < 6) { out.println("FAIL"); return; }
        try {
            int id = Integer.parseInt(p[1].trim());
            String[] ap = {p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim(), p[5].trim(), "true", ""};
            synchronized (appointments) {
                appointments.put(id, ap);
                rewriteAppointmentsFile();
            }
            out.println("OK");
        } catch (NumberFormatException e) { out.println("FAIL"); }
    }

    // format: updateAppointment;id;newDateTime;newCost
    private void handleUpdateAppointment(String[] p, PrintWriter out) {
        if (p.length < 4) { out.println("FAIL"); return; }
        try {
            int id = Integer.parseInt(p[1].trim());
            synchronized (appointments) {
                String[] ap = appointments.get(id);
                if (ap == null) { out.println("FAIL"); return; }
                ap[2] = p[2].trim(); // dateTime
                ap[4] = p[3].trim(); // cost
                rewriteAppointmentsFile();
            }
            out.println("OK");
        } catch (NumberFormatException e) { out.println("FAIL"); }
    }

    // format: deleteAppointment;id
    private void handleDeleteAppointment(String[] p, PrintWriter out) {
        if (p.length < 2) { out.println("FAIL"); return; }
        try {
            int id = Integer.parseInt(p[1].trim());
            synchronized (appointments) {
                appointments.remove(id);
                rewriteAppointmentsFile();
            }
            out.println("OK");
        } catch (NumberFormatException e) { out.println("FAIL"); }
    }

    private void handleGetAllAppointments(PrintWriter out) {
        List<String[]> all;
        synchronized (appointments) { all = new ArrayList<>(appointments.values()); }
        out.println(all.size());
        for (String[] a : all) out.println(String.join(";", a));
    }

    // ==================== BOOKING HANDLERS ====================

    // format: bookAppointment;bookingId;username;appointmentId
    private void handleBookAppointment(String[] p, PrintWriter out) {
        if (p.length < 4) { out.println("FAIL"); return; }
        try {
            int bookingId     = Integer.parseInt(p[1].trim());
            String username   = p[2].trim();
            int appointmentId = Integer.parseInt(p[3].trim());

            synchronized (bookings) {
                String[] booking = {p[1].trim(), username, p[3].trim()};
                bookings.put(bookingId, booking);
                appendToFile(BOOKINGS_FILE, String.join(";", booking));
            }
            synchronized (appointments) {
                String[] ap = appointments.get(appointmentId);
                if (ap != null) { ap[5] = "false"; ap[6] = username; rewriteAppointmentsFile(); }
            }
            out.println("OK");
        } catch (NumberFormatException e) { out.println("FAIL"); }
    }

    // format: cancelBooking;bookingId
    private void handleCancelBooking(String[] p, PrintWriter out) {
        if (p.length < 2) { out.println("FAIL"); return; }
        try {
            int bookingId = Integer.parseInt(p[1].trim());
            String[] booking;
            synchronized (bookings) {
                booking = bookings.remove(bookingId);
                if (booking != null) rewriteBookingsFile();
            }
            if (booking != null) {
                int appointmentId = Integer.parseInt(booking[2]);
                synchronized (appointments) {
                    String[] ap = appointments.get(appointmentId);
                    if (ap != null) { ap[5] = "true"; ap[6] = ""; rewriteAppointmentsFile(); }
                }
            }
            out.println("OK");
        } catch (NumberFormatException e) { out.println("FAIL"); }
    }

    private void handleGetAllBookings(PrintWriter out) {
        List<String[]> all;
        synchronized (bookings) { all = new ArrayList<>(bookings.values()); }
        out.println(all.size());
        for (String[] b : all) out.println(String.join(";", b));
    }

    // ==================== REVIEW HANDLERS ====================

    // format: addReview;bookingId;doctorName;rating;comment;username
    private void handleAddReview(String[] p, PrintWriter out) {
        if (p.length < 6) { out.println("FAIL"); return; }
        try {
            int bookingId = Integer.parseInt(p[1].trim());
            synchronized (reviews) {
                for (String[] r : reviews) {
                    if (Integer.parseInt(r[0]) == bookingId) { out.println("FAIL"); return; }
                }
                String[] review = {p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim(), p[5].trim()};
                reviews.add(review);
                appendToFile(REVIEWS_FILE, String.join(";", review));
            }
            out.println("OK");
        } catch (NumberFormatException e) { out.println("FAIL"); }
    }

    private void handleGetAllReviews(PrintWriter out) {
        List<String[]> all;
        synchronized (reviews) { all = new ArrayList<>(reviews); }
        out.println(all.size());
        for (String[] r : all) out.println(String.join(";", r));
    }

    // ==================== FILE HELPERS ====================

    private static void loadFromFile(String filename, Consumer<String> processor) {
        File file = new File(filename);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try { processor.accept(line); } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static synchronized void appendToFile(String filename, String content) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filename, true))) {
            w.write(content); w.newLine();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static synchronized void rewriteFile(String filename, List<String> lines) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) { w.write(line); w.newLine(); }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void rewriteUsersFile() {
        List<String> lines = new ArrayList<>();
        for (String[] u : users.values()) lines.add(String.join(";", u));
        rewriteFile(USERS_FILE, lines);
    }

    private void rewriteAppointmentsFile() {
        List<String> lines = new ArrayList<>();
        for (String[] a : appointments.values()) lines.add(String.join(";", a));
        rewriteFile(APPOINTMENTS_FILE, lines);
    }

    private void rewriteBookingsFile() {
        List<String> lines = new ArrayList<>();
        for (String[] b : bookings.values()) lines.add(String.join(";", b));
        rewriteFile(BOOKINGS_FILE, lines);
    }
}
