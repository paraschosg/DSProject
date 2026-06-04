package com.dsproject.client.controller;

import com.dsproject.rmi.RMIClient;
import com.dsproject.server.models.Appointment;
import com.dsproject.server.models.Doctor;
import com.dsproject.server.models.Review;
import com.dsproject.server.models.User;

import java.time.LocalDateTime;
import java.util.List;

public class ClientController {

    private final RMIClient rmiClient = new RMIClient();

    // Auth
    public User    login(String username, String password) { return rmiClient.login(username, password); }
    public boolean register(User user)                    { return rmiClient.register(user); }
    public boolean deleteUser(String username)            { return rmiClient.deleteUser(username); }
    public void    registerCallback(String username)      { rmiClient.registerCallback(username); }

    // Doctors
    public boolean      addDoctor(Doctor doctor, String role) { return rmiClient.addDoctor(doctor, role); }
    public List<Doctor> getDoctors()                          { return rmiClient.getDoctors(); }

    // Appointments (admin)
    public int     addAppointment(String doctor, LocalDateTime date, int duration, double cost) {
        return rmiClient.addAppointment(doctor, date, duration, cost);
    }
    public boolean updateAppointment(int id, LocalDateTime dt, double cost) {
        return rmiClient.updateAppointment(id, dt, cost);
    }
    public boolean deleteAppointment(int id)         { return rmiClient.deleteAppointment(id); }
    public List<Appointment> getAllAppointments()     { return rmiClient.getAllAppointments(); }

    // Appointments (patient)
    public List<Appointment> getAvailableAppointments()           { return rmiClient.getAvailableAppointments(); }
    public List<Appointment> getUserAppointments(String username) { return rmiClient.getUserAppointments(username); }
    public boolean bookAppointment(String username, int appointmentId) {
        return rmiClient.bookAppointment(username, appointmentId);
    }
    public int     getBookingId(String username, int appointmentId) {
        return rmiClient.getBookingId(username, appointmentId);
    }
    public boolean cancelBooking(int bookingId) { return rmiClient.cancelBooking(bookingId); }

    // Reviews
    public boolean      submitReview(int bookingId, int rating, String comment) {
        return rmiClient.submitReview(bookingId, rating, comment);
    }
    public List<Review> getDoctorReviews(String doctorName) { return rmiClient.getDoctorReviews(doctorName); }
    public List<Review> getAllReviews()                     { return rmiClient.getAllReviews(); }
}
