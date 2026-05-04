package com.dsproject.rmi;

import com.dsproject.server.models.User;
import com.dsproject.server.models.Doctor;

import java.time.LocalDateTime;
import java.util.List;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import com.dsproject.server.models.Appointment;

public class RMIClient {

    private RemoteInterface remote;

    public RMIClient() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            remote = (RemoteInterface) registry.lookup("ServerService");

            System.out.println("Connected to RMI Server!");

        } catch (Exception e) {
            System.out.println("Connection to server failed...");
            e.printStackTrace();
        }
    }

    public User login(String username, String password) {
        try {
            return remote.login(username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(User user) {
        try {
            return remote.register(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(String username) {
        try {
            return remote.deleteUser(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addDoctor(Doctor doctor, String role) {
        try {
            return remote.addDoctor(doctor, role);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int addAppointment(String doctor, LocalDateTime date, int duration, double cost) {
        try {
            return remote.addAppointment(doctor, date, duration, cost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean bookAppointment(String username, int appointmentId) {
        try {
            return remote.bookAppointment(username, appointmentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Appointment> getAvailableAppointments() {
        try {
            List<Appointment> list = remote.getAvailableAppointments();
            System.out.println("Client got: " + list);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean cancelBooking(int bookingId) {
        try {
            return remote.cancelBooking(bookingId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void registerCallback(String username) {
        try {
            CallbackImpl callback = new CallbackImpl();
            remote.registerCallback(username, callback);
            System.out.println("Callback registered!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}