package com.dsproject.client.controller;

import com.dsproject.rmi.RMIClient;
import com.dsproject.server.models.User;
import com.dsproject.server.models.Doctor;
import com.dsproject.server.models.Appointment;

import java.time.LocalDateTime;
import java.util.List;

public class ClientController { //ο controller που συνδέει το GUI με την RMIClient για να εκτελεί τις λειτουργίες που απαιτούνται από τον χρήστη

    private RMIClient rmiClient;

    public ClientController() { //δημιουργεί ένα νέο RMIClient για να επικοινωνεί με τον server
        rmiClient = new RMIClient();
    }

    public void registerCallback(String username) { //καταχωρεί τον callback του χρήστη για να λαμβάνει ενημερώσεις
        rmiClient.registerCallback(username);
    }

    public User login(String username, String password) {
        return rmiClient.login(username, password);
    }

    public boolean register(User user) {
        return rmiClient.register(user);
    }

    public boolean deleteUser(String username) {
        return rmiClient.deleteUser(username);
    }

    public int addAppointment(String doctor, LocalDateTime date, int duration, double cost) {
        return rmiClient.addAppointment(doctor, date, duration, cost);
    }

    public boolean addDoctor(Doctor doctor, String role) {
        return rmiClient.addDoctor(doctor, role);
    }

    public boolean bookAppointment(String username, int appointmentId) {
        return rmiClient.bookAppointment(username, appointmentId);
    }

    public int getBookingId(String username, int appointmentId) {
        return rmiClient.getBookingId(username, appointmentId);
    }

    public List<Appointment> getAvailableAppointments() {
        return rmiClient.getAvailableAppointments();
    }

    public List<Appointment> getUserAppointments(String username) {
        return rmiClient.getUserAppointments(username);
    }

    public boolean cancelBooking(int bookingId) {
        return rmiClient.cancelBooking(bookingId);
    }
}