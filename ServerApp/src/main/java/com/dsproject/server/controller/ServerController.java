package com.dsproject.server.controller;

import com.dsproject.server.models.*;
import com.dsproject.rmi.CallbackInterface;
import com.dsproject.server.service.WorkerClient;

import java.time.LocalDateTime;
import java.util.*;

public class ServerController {

    private HashMap<String, User> users;
    private ArrayList<Doctor> doctors;

    private HashMap<Integer, Booking> bookings;
    private HashMap<Integer, Waitlist> waitlists;
    private HashMap<String, CallbackInterface> callbacks;

    private WorkerClient workerClient = new WorkerClient();

    private HashMap<Integer, Appointment> appointments;

    private int appointmentCounter = 1;
    private int bookingCounter = 1;

    public ServerController() {

        users = new HashMap<>();
        doctors = new ArrayList<>();
        appointments = new HashMap<>();
        bookings = new HashMap<>();
        waitlists = new HashMap<>();

        users.put("admin", new User("Admin", "000", "000", "admin@mail.com",
                "admin", "1234", "admin"));

        appointments.put(appointmentCounter, new Appointment(
                appointmentCounter++, "Doctor A",
                LocalDateTime.now().plusDays(1),
                30, 50));

        appointments.put(appointmentCounter, new Appointment(
                appointmentCounter++, "Doctor B",
                LocalDateTime.now().plusDays(2),
                45, 70));
    }

    public User login(String username, String password) {

        if (users.containsKey(username)) {
            User user = users.get(username);

            if (user.getPassword().equals(password)) {
                return user; // επιστρέφεις ΟΛΟ το user
            }
        }

        return null;
    }

    public boolean register(User user) {

        String request = "register;" + user.getUsername() + ";" + user.getPassword();

        String response = workerClient.sendRequest(request);

        if (response.equals("OK")) {
            users.put(user.getUsername(), user);
            return true;
        }

        return false;
    }

    public boolean addDoctor(Doctor doctor, String role) {

        if (!role.equals("admin")) {
            return false;
        }

        doctors.add(doctor);
        return true;
    }

    public int addAppointment(String doctorName, LocalDateTime dateTime, int duration, double cost) {

        Appointment ap = new Appointment(
                appointmentCounter++, doctorName, dateTime, duration, cost
        );

        appointments.put(ap.getId(), ap);
        return ap.getId();
    }

    public List<Appointment> getAvailableAppointments() {

        System.out.println("Server: appointments = " + appointments.size());

        List<Appointment> list = new ArrayList<>();

        for (Appointment a : appointments.values()) {
            if (a.isAvailable()) list.add(a);
        }

        return list;
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments.values());
    }

    public boolean bookAppointment(String username, int appointmentId) {

        Appointment ap = appointments.get(appointmentId);

        if (ap == null) return false;

        if (!ap.isAvailable()) {
            addToWaitlist(username, appointmentId);
            return false;
        }

        Booking booking = new Booking(bookingCounter++, username, appointmentId);
        bookings.put(booking.getId(), booking);

        ap.setAvailable(false);
        ap.setBookedBy(username); // 🔥 ΤΟ ΠΙΟ ΣΗΜΑΝΤΙΚΟ

        return true;
    }

    public int getBookingId(String username, int appointmentId) {
        for (Booking b : bookings.values()) {
            if (b.getUsername().equals(username) && b.getAppointmentId() == appointmentId) {
                return b.getId();
            }
        }
        return -1;
    }

    public boolean cancelBooking(int bookingId) {

        Booking booking = bookings.get(bookingId);
        if (booking == null) return false;

        Appointment ap = appointments.get(booking.getAppointmentId());
        if (ap != null) {
            ap.setAvailable(true);
            ap.setBookedBy(null); // 🔥 reset
        }

        bookings.remove(bookingId);
        return true;
    }

    private void addToWaitlist(String username, int appointmentId) {

        waitlists.putIfAbsent(appointmentId, new Waitlist(appointmentId));
        waitlists.get(appointmentId).addUser(username);
    }

    private void notifyWaitlist(int appointmentId) {

        Waitlist wl = waitlists.get(appointmentId);

        if (wl == null || wl.isEmpty()) return;

        String nextUser = wl.getNextUser();

        System.out.println("Notify user: " + nextUser);

        CallbackInterface callback = callbacks.get(nextUser);

        if (callback != null) {
            try {
                callback.notifyUser("Appointment available again!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registerCallback(String username, CallbackInterface callback) {
        callbacks.put(username, callback);
    }
}

