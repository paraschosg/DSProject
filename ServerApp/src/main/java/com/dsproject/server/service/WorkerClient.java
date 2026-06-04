package com.dsproject.server.service;

import com.dsproject.server.models.*;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Client that communicates with the WorkerApp (2nd server) via Java Sockets.
 * Each call opens a new socket connection, sends a request, and reads the response.
 */
public class WorkerClient {

    private static final String HOST = "localhost";
    private static final int    PORT = 5000;

    /** Send a single-line request and return the single-line response. */
    public String sendRequest(String request) {
        try (
            Socket       socket = new Socket(HOST, PORT);
            BufferedReader in   = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    out  = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println(request);
            return in.readLine();
        } catch (Exception e) {
            System.err.println("[WorkerClient] sendRequest failed: " + e.getMessage());
            return "Error";
        }
    }

    /**
     * Send a request that expects a multi-record response.
     * Response format: first line = count (N), followed by N lines of records.
     */
    private List<String[]> sendMultiRecordRequest(String request) {
        List<String[]> results = new ArrayList<>();
        try (
            Socket         socket = new Socket(HOST, PORT);
            BufferedReader in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    out    = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println(request);
            String countLine = in.readLine();
            if (countLine == null) return results;
            int count = Integer.parseInt(countLine.trim());
            for (int i = 0; i < count; i++) {
                String line = in.readLine();
                if (line != null) results.add(line.split(";", -1));
            }
        } catch (Exception e) {
            System.err.println("[WorkerClient] multiRecord failed: " + e.getMessage());
        }
        return results;
    }

    // ==================== USER OPERATIONS ====================

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        for (String[] r : sendMultiRecordRequest("getAllUsers")) {
            if (r.length >= 7) {
                // stored: username;password;fullName;amka;phone;email;role
                list.add(new User(r[2], r[3], r[4], r[5], r[0], r[1], r[6]));
            }
        }
        return list;
    }

    public boolean deleteUser(String username) {
        return "OK".equals(sendRequest("delete;" + username));
    }

    // ==================== DOCTOR OPERATIONS ====================

    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        for (String[] r : sendMultiRecordRequest("getAllDoctors")) {
            if (r.length >= 6) {
                try { list.add(new Doctor(r[0], r[1], r[2], r[3], r[4], Double.parseDouble(r[5]))); }
                catch (NumberFormatException ignored) {}
            }
        }
        return list;
    }

    // ==================== APPOINTMENT OPERATIONS ====================

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        for (String[] r : sendMultiRecordRequest("getAllAppointments")) {
            // stored: id;doctorName;dateTime;duration;cost;available;bookedBy
            if (r.length >= 7) {
                try {
                    Appointment ap = new Appointment(
                            Integer.parseInt(r[0]), r[1],
                            LocalDateTime.parse(r[2]),
                            Integer.parseInt(r[3]),
                            Double.parseDouble(r[4])
                    );
                    ap.setAvailable("true".equals(r[5]));
                    if (!r[6].isEmpty()) ap.setBookedBy(r[6]);
                    list.add(ap);
                } catch (Exception ignored) {}
            }
        }
        return list;
    }

    // ==================== BOOKING OPERATIONS ====================

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        for (String[] r : sendMultiRecordRequest("getAllBookings")) {
            if (r.length >= 3) {
                try { list.add(new Booking(Integer.parseInt(r[0]), r[1], Integer.parseInt(r[2]))); }
                catch (NumberFormatException ignored) {}
            }
        }
        return list;
    }

    // ==================== REVIEW OPERATIONS ====================

    public List<Review> getAllReviews() {
        List<Review> list = new ArrayList<>();
        for (String[] r : sendMultiRecordRequest("getAllReviews")) {
            // stored: bookingId;doctorName;rating;comment;username
            if (r.length >= 5) {
                try { list.add(new Review(Integer.parseInt(r[0]), r[1], Integer.parseInt(r[2]), r[3], r[4])); }
                catch (NumberFormatException ignored) {}
            }
        }
        return list;
    }
}
