package com.dsproject.client.controller;

import com.dsproject.rmi.RMIClient;
import com.dsproject.server.models.User;
import com.dsproject.server.models.Doctor;

import java.time.LocalDateTime;
import java.util.List;
import com.dsproject.server.models.Appointment;

public class ClientController {

    private RMIClient rmiClient;

    public ClientController() {
        rmiClient = new RMIClient();
    }

    public void registerCallback(String username) {
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

    public List<Appointment> getAvailableAppointments() {
        return rmiClient.getAvailableAppointments();
    }

    public boolean cancelBooking(int bookingId) {
        return rmiClient.cancelBooking(bookingId);
    }
}