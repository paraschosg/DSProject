package com.dsproject.server.service;

import com.dsproject.rmi.CallbackInterface;
import com.dsproject.rmi.RemoteInterface;
import com.dsproject.server.controller.ServerController;
import com.dsproject.server.models.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.List;

/**
 * RMI implementation that delegates all operations to ServerController.
 * A single instance is shared by all RMI threads, so ServerController
 * must be thread-safe (which it is via ConcurrentHashMap + synchronized blocks).
 */
public class RemoteServiceImpl extends UnicastRemoteObject implements RemoteInterface {

    private final ServerController controller;

    public RemoteServiceImpl() throws RemoteException {
        super();
        controller = new ServerController();
    }

    // ==================== AUTH ====================

    @Override
    public User login(String username, String password) throws RemoteException {
        return controller.login(username, password);
    }

    @Override
    public boolean register(User user) throws RemoteException {
        System.out.println("[Server] Register: " + user.getUsername());
        return controller.register(user);
    }

    @Override
    public boolean deleteUser(String username) throws RemoteException {
        System.out.println("[Server] Delete user: " + username);
        return controller.deleteUser(username);
    }

    // ==================== DOCTORS ====================

    @Override
    public boolean addDoctor(Doctor doctor, String role) throws RemoteException {
        System.out.println("[Server] Add doctor: " + doctor.getFullName());
        return controller.addDoctor(doctor, role);
    }

    @Override
    public List<Doctor> getDoctors() throws RemoteException {
        return controller.getDoctors();
    }

    // ==================== APPOINTMENTS (ADMIN) ====================

    @Override
    public int addAppointment(String doctorName, LocalDateTime dateTime, int duration, double cost) throws RemoteException {
        System.out.println("[Server] Add appointment for " + doctorName);
        return controller.addAppointment(doctorName, dateTime, duration, cost);
    }

    @Override
    public boolean updateAppointment(int appointmentId, LocalDateTime newDateTime, double newCost) throws RemoteException {
        System.out.println("[Server] Update appointment " + appointmentId);
        return controller.updateAppointment(appointmentId, newDateTime, newCost);
    }

    @Override
    public boolean deleteAppointment(int appointmentId) throws RemoteException {
        System.out.println("[Server] Delete appointment " + appointmentId);
        return controller.deleteAppointment(appointmentId);
    }

    @Override
    public List<Appointment> getAllAppointments() throws RemoteException {
        return controller.getAllAppointments();
    }

    // ==================== APPOINTMENTS (PATIENT) ====================

    @Override
    public List<Appointment> getAvailableAppointments() throws RemoteException {
        return controller.getAvailableAppointments();
    }

    @Override
    public List<Appointment> getUserAppointments(String username) throws RemoteException {
        return controller.getAllAppointments().stream()
                .filter(a -> username.equals(a.getBookedBy()))
                .toList();
    }

    @Override
    public boolean bookAppointment(String username, int appointmentId) throws RemoteException {
        System.out.println("[Server] Book appointment " + appointmentId + " by " + username);
        return controller.bookAppointment(username, appointmentId);
    }

    @Override
    public int getBookingId(String username, int appointmentId) throws RemoteException {
        return controller.getBookingId(username, appointmentId);
    }

    @Override
    public boolean cancelBooking(int bookingId) throws RemoteException {
        System.out.println("[Server] Cancel booking " + bookingId);
        return controller.cancelBooking(bookingId);
    }

    // ==================== REVIEWS ====================

    @Override
    public boolean submitReview(int bookingId, int rating, String comment) throws RemoteException {
        System.out.println("[Server] Submit review for booking " + bookingId);
        return controller.submitReview(bookingId, rating, comment);
    }

    @Override
    public List<Review> getDoctorReviews(String doctorName) throws RemoteException {
        return controller.getDoctorReviews(doctorName);
    }

    @Override
    public List<Review> getAllReviews() throws RemoteException {
        return controller.getAllReviews();
    }

    // ==================== CALLBACKS ====================

    @Override
    public void registerCallback(String username, CallbackInterface callback) throws RemoteException {
        controller.registerCallback(username, callback);
    }
}
