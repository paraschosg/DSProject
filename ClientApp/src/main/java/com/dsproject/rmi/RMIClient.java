package com.dsproject.rmi;

import com.dsproject.server.models.Appointment;
import com.dsproject.server.models.Doctor;
import com.dsproject.server.models.Review;
import com.dsproject.server.models.User;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.List;

public class RMIClient {

    private RemoteInterface remote;

    public RMIClient() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            remote = (RemoteInterface) registry.lookup("ServerService");
            System.out.println("[RMI] Connected to server.");
        } catch (Exception e) {
            System.err.println("[RMI] Connection failed: " + e.getMessage());
        }
    }

    // ==================== AUTH ====================

    public User login(String username, String password) {
        try { return remote.login(username, password); }
        catch (Exception e) { e.printStackTrace(); return null; }
    }

    public boolean register(User user) {
        try { return remote.register(user); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean deleteUser(String username) {
        try { return remote.deleteUser(username); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    // ==================== DOCTORS ====================

    public boolean addDoctor(Doctor doctor, String role) {
        try { return remote.addDoctor(doctor, role); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public List<Doctor> getDoctors() {
        try { return remote.getDoctors(); }
        catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    // ==================== APPOINTMENTS (ADMIN) ====================

    public int addAppointment(String doctor, LocalDateTime date, int duration, double cost) {
        try { return remote.addAppointment(doctor, date, duration, cost); }
        catch (Exception e) { e.printStackTrace(); return -1; }
    }

    public boolean updateAppointment(int id, LocalDateTime newDateTime, double newCost) {
        try { return remote.updateAppointment(id, newDateTime, newCost); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean deleteAppointment(int id) {
        try { return remote.deleteAppointment(id); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public List<Appointment> getAllAppointments() {
        try { return remote.getAllAppointments(); }
        catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    // ==================== APPOINTMENTS (PATIENT) ====================

    public List<Appointment> getAvailableAppointments() {
        try { return remote.getAvailableAppointments(); }
        catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    public List<Appointment> getUserAppointments(String username) {
        try { return remote.getUserAppointments(username); }
        catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    public boolean bookAppointment(String username, int appointmentId) {
        try { return remote.bookAppointment(username, appointmentId); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public int getBookingId(String username, int appointmentId) {
        try { return remote.getBookingId(username, appointmentId); }
        catch (Exception e) { e.printStackTrace(); return -1; }
    }

    public boolean cancelBooking(int bookingId) {
        try { return remote.cancelBooking(bookingId); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    // ==================== REVIEWS ====================

    public boolean submitReview(int bookingId, int rating, String comment) {
        try { return remote.submitReview(bookingId, rating, comment); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public List<Review> getDoctorReviews(String doctorName) {
        try { return remote.getDoctorReviews(doctorName); }
        catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    public List<Review> getAllReviews() {
        try { return remote.getAllReviews(); }
        catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    // ==================== CALLBACKS ====================

    public void registerCallback(String username) {
        try {
            CallbackImpl callback = new CallbackImpl();
            remote.registerCallback(username, callback);
            System.out.println("[RMI] Callback registered for " + username);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
