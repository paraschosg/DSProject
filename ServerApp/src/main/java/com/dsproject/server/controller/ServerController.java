package com.dsproject.server.controller;

import com.dsproject.server.models.*;
import com.dsproject.rmi.CallbackInterface;
import com.dsproject.server.service.WorkerClient;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Central controller for the 1st server.
 * All shared state uses ConcurrentHashMap and AtomicInteger for thread safety.
 * bookAppointment and cancelBooking are synchronized on the Appointment object
 * to prevent race conditions (double-booking, concurrent cancellation).
 */
public class ServerController {

    // Thread-safe in-memory stores
    private final Map<String, User>              users       = new ConcurrentHashMap<>();
    private final Map<Integer, Appointment>      appointments = new ConcurrentHashMap<>();
    private final Map<Integer, Booking>          bookings    = new ConcurrentHashMap<>();
    private final Map<Integer, Waitlist>         waitlists   = new ConcurrentHashMap<>();
    private final Map<String, CallbackInterface> callbacks   = new ConcurrentHashMap<>();
    private final List<Doctor>  doctors = Collections.synchronizedList(new ArrayList<>());
    private final List<Review>  reviews = Collections.synchronizedList(new ArrayList<>());

    private final AtomicInteger appointmentCounter = new AtomicInteger(1);
    private final AtomicInteger bookingCounter     = new AtomicInteger(1);

    private final WorkerClient workerClient = new WorkerClient();

    public ServerController() {
        // Load persisted state from WorkerApp
        loadUsersFromWorker();
        loadDoctorsFromWorker();
        loadAppointmentsFromWorker();
        loadBookingsFromWorker();
        loadReviewsFromWorker();

        // Guarantee admin exists in memory
        users.putIfAbsent("admin", new User("Admin", "000000000000000", "0000000000",
                "admin@clinic.com", "admin", "1234", "admin"));
    }

    // ==================== STARTUP LOADERS ====================

    private void loadUsersFromWorker() {
        try {
            for (User u : workerClient.getAllUsers()) users.put(u.getUsername(), u);
            System.out.println("[Server] Loaded " + users.size() + " users from Worker.");
        } catch (Exception e) {
            System.err.println("[Server] Could not load users from Worker: " + e.getMessage());
        }
    }

    private void loadDoctorsFromWorker() {
        try {
            doctors.addAll(workerClient.getAllDoctors());
            System.out.println("[Server] Loaded " + doctors.size() + " doctors from Worker.");
        } catch (Exception e) {
            System.err.println("[Server] Could not load doctors from Worker: " + e.getMessage());
        }
    }

    private void loadAppointmentsFromWorker() {
        try {
            for (Appointment a : workerClient.getAllAppointments()) {
                appointments.put(a.getId(), a);
                if (a.getId() >= appointmentCounter.get())
                    appointmentCounter.set(a.getId() + 1);
            }
            System.out.println("[Server] Loaded " + appointments.size() + " appointments from Worker.");
        } catch (Exception e) {
            System.err.println("[Server] Could not load appointments from Worker: " + e.getMessage());
        }
    }

    private void loadBookingsFromWorker() {
        try {
            for (Booking b : workerClient.getAllBookings()) {
                bookings.put(b.getId(), b);
                if (b.getId() >= bookingCounter.get())
                    bookingCounter.set(b.getId() + 1);
            }
            System.out.println("[Server] Loaded " + bookings.size() + " bookings from Worker.");
        } catch (Exception e) {
            System.err.println("[Server] Could not load bookings from Worker: " + e.getMessage());
        }
    }

    private void loadReviewsFromWorker() {
        try {
            reviews.addAll(workerClient.getAllReviews());
            System.out.println("[Server] Loaded " + reviews.size() + " reviews from Worker.");
        } catch (Exception e) {
            System.err.println("[Server] Could not load reviews from Worker: " + e.getMessage());
        }
    }

    // ==================== USER OPERATIONS ====================

    public User login(String username, String password) {
        User user = users.get(username);
        return (user != null && user.getPassword().equals(password)) ? user : null;
    }

    public boolean register(User user) {
        if (users.containsKey(user.getUsername())) return false;
        String request = "register;" + user.getUsername() + ";" + user.getPassword() + ";"
                + user.getFullName() + ";" + user.getAmka() + ";" + user.getPhone() + ";"
                + user.getEmail() + ";" + user.getRole();
        if ("OK".equals(workerClient.sendRequest(request))) {
            users.put(user.getUsername(), user);
            return true;
        }
        return false;
    }

    public boolean deleteUser(String username) {
        if (workerClient.deleteUser(username)) {
            users.remove(username);
            callbacks.remove(username);
            return true;
        }
        return false;
    }

    // ==================== DOCTOR OPERATIONS ====================

    public boolean addDoctor(Doctor doctor, String role) {
        if (!"admin".equals(role)) return false;
        String request = "addDoctor;" + doctor.getFullName() + ";" + doctor.getSpecialty() + ";"
                + doctor.getDepartment() + ";" + doctor.getPhone() + ";"
                + doctor.getEmail() + ";" + doctor.getCost();
        if ("OK".equals(workerClient.sendRequest(request))) {
            doctors.add(doctor);
            return true;
        }
        return false;
    }

    public List<Doctor> getDoctors() {
        return new ArrayList<>(doctors);
    }

    // ==================== APPOINTMENT OPERATIONS ====================

    public synchronized int addAppointment(String doctorName, LocalDateTime dateTime, int duration, double cost) {
        int id = appointmentCounter.getAndIncrement();
        Appointment ap = new Appointment(id, doctorName, dateTime, duration, cost);
        String request = "addAppointment;" + id + ";" + doctorName + ";" + dateTime + ";" + duration + ";" + cost;
        if ("OK".equals(workerClient.sendRequest(request))) {
            appointments.put(id, ap);
            return id;
        }
        appointmentCounter.decrementAndGet();
        return -1;
    }

    public boolean updateAppointment(int appointmentId, LocalDateTime newDateTime, double newCost) {
        Appointment ap = appointments.get(appointmentId);
        if (ap == null) return false;
        String request = "updateAppointment;" + appointmentId + ";" + newDateTime + ";" + newCost;
        if ("OK".equals(workerClient.sendRequest(request))) {
            ap.setDateTime(newDateTime);
            ap.setCost(newCost);
            // Notify booked patient of the change
            if (ap.getBookedBy() != null) {
                notifyUser(ap.getBookedBy(), "Your appointment with " + ap.getDoctorName()
                        + " has been updated. New time: " + newDateTime + ", Cost: " + newCost + "€");
            }
            return true;
        }
        return false;
    }

    public boolean deleteAppointment(int appointmentId) {
        Appointment ap = appointments.get(appointmentId);
        if (ap == null) return false;

        // Find and cancel existing booking → notify patient
        for (Booking b : new ArrayList<>(bookings.values())) {
            if (b.getAppointmentId() == appointmentId) {
                notifyUser(b.getUsername(), "Your appointment with " + ap.getDoctorName()
                        + " on " + ap.getDateTime() + " was cancelled by admin.");
                bookings.remove(b.getId());
                workerClient.sendRequest("cancelBooking;" + b.getId());
                break;
            }
        }

        appointments.remove(appointmentId);
        waitlists.remove(appointmentId);
        workerClient.sendRequest("deleteAppointment;" + appointmentId);
        return true;
    }

    public List<Appointment> getAvailableAppointments() {
        List<Appointment> list = new ArrayList<>();
        for (Appointment a : appointments.values()) {
            if (a.isAvailable()) list.add(a);
        }
        return list;
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments.values());
    }

    // ==================== BOOKING OPERATIONS ====================

    /**
     * Books an appointment. Synchronized on the appointment object to prevent
     * race conditions (two clients booking the same slot simultaneously).
     */
    public boolean bookAppointment(String username, int appointmentId) {
        Appointment ap = appointments.get(appointmentId);
        if (ap == null) return false;

        synchronized (ap) {
            if (!ap.isAvailable()) {
                addToWaitlist(username, appointmentId);
                return false;
            }
            int bookId = bookingCounter.getAndIncrement();
            Booking booking = new Booking(bookId, username, appointmentId);
            bookings.put(bookId, booking);
            ap.setAvailable(false);
            ap.setBookedBy(username);
            workerClient.sendRequest("bookAppointment;" + bookId + ";" + username + ";" + appointmentId);
        }
        return true;
    }

    public int getBookingId(String username, int appointmentId) {
        for (Booking b : bookings.values()) {
            if (b.getUsername().equals(username) && b.getAppointmentId() == appointmentId)
                return b.getId();
        }
        return -1;
    }

    /**
     * Cancels a booking. Enforces the 24-hour rule: cancellation is rejected
     * if the appointment is within 24 hours (same-day or less).
     */
    public boolean cancelBooking(int bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) return false;

        Appointment ap = appointments.get(booking.getAppointmentId());
        if (ap == null) return false;

        // 24-hour cancellation rule
        long hoursUntil = ChronoUnit.HOURS.between(LocalDateTime.now(), ap.getDateTime());
        if (hoursUntil < 24) return false;

        synchronized (ap) {
            ap.setAvailable(true);
            ap.setBookedBy(null);
        }

        bookings.remove(bookingId);
        workerClient.sendRequest("cancelBooking;" + bookingId);

        // Notify waitlist
        notifyWaitlist(ap.getId());
        return true;
    }

    // ==================== REVIEW OPERATIONS ====================

    public boolean submitReview(int bookingId, int rating, String comment) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) return false;

        Appointment ap = appointments.get(booking.getAppointmentId());
        if (ap == null) return false;

        // Appointment must be in the past (completed)
        if (!ap.getDateTime().isBefore(LocalDateTime.now())) return false;

        // Check for duplicate review
        synchronized (reviews) {
            for (Review r : reviews) {
                if (r.getBookingId() == bookingId) return false;
            }
            Review review = new Review(bookingId, ap.getDoctorName(), rating, comment, booking.getUsername());
            reviews.add(review);
            workerClient.sendRequest("addReview;" + bookingId + ";" + ap.getDoctorName()
                    + ";" + rating + ";" + comment + ";" + booking.getUsername());
        }
        return true;
    }

    public List<Review> getDoctorReviews(String doctorName) {
        List<Review> result = new ArrayList<>();
        synchronized (reviews) {
            for (Review r : reviews) {
                if (r.getDoctorName().equals(doctorName)) result.add(r);
            }
        }
        return result;
    }

    public List<Review> getAllReviews() {
        return new ArrayList<>(reviews);
    }

    // ==================== CALLBACK OPERATIONS ====================

    public void registerCallback(String username, CallbackInterface callback) {
        callbacks.put(username, callback);
    }

    // ==================== PRIVATE HELPERS ====================

    private void addToWaitlist(String username, int appointmentId) {
        waitlists.computeIfAbsent(appointmentId, Waitlist::new).addUser(username);
    }

    /**
     * Notifies the first connected patient in the waitlist.
     * Skips offline patients (no callback registered) and removes them from the queue.
     */
    private void notifyWaitlist(int appointmentId) {
        Waitlist wl = waitlists.get(appointmentId);
        if (wl == null || wl.isEmpty()) return;

        while (!wl.isEmpty()) {
            String nextUser = wl.getNextUser();
            CallbackInterface callback = callbacks.get(nextUser);
            if (callback != null) {
                try {
                    callback.notifyUser("A slot you were waiting for is now available! Book it now.");
                    break; // Successfully notified one patient
                } catch (Exception e) {
                    System.err.println("[Server] Callback failed for " + nextUser);
                }
            }
            // Patient offline or callback failed → try next in queue
        }
    }

    private void notifyUser(String username, String message) {
        CallbackInterface callback = callbacks.get(username);
        if (callback != null) {
            try { callback.notifyUser(message); }
            catch (Exception e) { System.err.println("[Server] Notify failed for " + username); }
        }
    }
}
