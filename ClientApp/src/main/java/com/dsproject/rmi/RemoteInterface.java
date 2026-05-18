package com.dsproject.rmi;

import com.dsproject.server.models.User;
import com.dsproject.server.models.Doctor;
import com.dsproject.server.models.Appointment;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.List;

public interface RemoteInterface extends Remote { //Αυτή η διεπαφή ορίζει τις μεθόδους που θα καλεί ο client για να αλληλεπιδράσει με τον server. Περιλαμβάνει μεθόδους για login, εγγραφή, διαχείριση ραντεβού και άλλες λειτουργίες που απαιτούνται από την εφαρμογή

    User login(String username, String password) throws RemoteException;

    boolean register(User user) throws RemoteException;

    boolean deleteUser(String username) throws RemoteException;

    boolean addDoctor(Doctor doctor, String role) throws RemoteException;

    int addAppointment(String doctorName, LocalDateTime dateTime, int duration, double cost) throws RemoteException;

    List<Appointment> getAvailableAppointments() throws RemoteException;

    List<Appointment> getUserAppointments(String username) throws RemoteException;

    boolean bookAppointment(String username, int appointmentId) throws RemoteException;

    int getBookingId(String username, int appointmentId) throws RemoteException;

    boolean cancelBooking(int bookingId) throws RemoteException;

    void registerCallback(String username, CallbackInterface callback) throws RemoteException;

}