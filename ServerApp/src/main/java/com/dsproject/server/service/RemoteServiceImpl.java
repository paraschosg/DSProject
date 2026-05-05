package com.dsproject.server.service;

import com.dsproject.rmi.RemoteInterface;
import com.dsproject.server.controller.ServerController;
import com.dsproject.server.models.User;
import com.dsproject.server.models.Doctor;
import com.dsproject.server.models.Appointment;
import com.dsproject.rmi.CallbackInterface;
import java.util.List;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;

public class RemoteServiceImpl extends UnicastRemoteObject implements RemoteInterface {

    private ServerController controller;
    private WorkerClient workerClient;

    public RemoteServiceImpl() throws RemoteException {
        super();
        controller = new ServerController();
        workerClient = new WorkerClient();
    }

    @Override
    public User login(String username, String password) throws RemoteException {
        return controller.login(username, password);
    }

    @Override
    public boolean register(User user) throws RemoteException {
        System.out.println("Register request: " + user.getUsername());
        return controller.register(user);
    }

    @Override
    public boolean deleteUser(String username) throws RemoteException {
        return workerClient.deleteUser(username);
    }

    @Override
    public boolean addDoctor(Doctor doctor, String role) throws RemoteException {
        return controller.addDoctor(doctor, role);
    }

    @Override
    public int addAppointment(String doctorName, LocalDateTime dateTime,
                              int duration, double cost) throws RemoteException {
        return controller.addAppointment(doctorName, dateTime, duration, cost);
    }

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
        System.out.println("Booking request from: " + username);
        return controller.bookAppointment(username, appointmentId);
    }

    @Override
    public int getBookingId(String username, int appointmentId) throws RemoteException {
        return controller.getBookingId(username, appointmentId);
    }

    @Override
    public boolean cancelBooking(int bookingId) throws RemoteException {
        return controller.cancelBooking(bookingId);
    }

    @Override
    public void registerCallback(String username, CallbackInterface callback) throws RemoteException {
        controller.registerCallback(username, callback);
    }
}