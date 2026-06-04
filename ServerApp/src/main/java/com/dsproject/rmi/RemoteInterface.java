package com.dsproject.rmi;

import com.dsproject.server.models.Appointment;
import com.dsproject.server.models.Doctor;
import com.dsproject.server.models.Review;
import com.dsproject.server.models.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * RMI interface exposed by the 1st server to all clients.
 * Covers all required and bonus features of the specification.
 */
public interface RemoteInterface extends Remote {

    // --- Auth ---
    User    login(String username, String password) throws RemoteException;
    boolean register(User user)                    throws RemoteException;
    boolean deleteUser(String username)            throws RemoteException;

    // --- Doctors ---
    boolean      addDoctor(Doctor doctor, String role) throws RemoteException;
    List<Doctor> getDoctors()                          throws RemoteException;

    // --- Appointments (admin) ---
    int     addAppointment(String doctorName, LocalDateTime dateTime, int duration, double cost) throws RemoteException;
    boolean updateAppointment(int appointmentId, LocalDateTime newDateTime, double newCost)      throws RemoteException;
    boolean deleteAppointment(int appointmentId)                                                 throws RemoteException;
    List<Appointment> getAllAppointments()                                                        throws RemoteException;

    // --- Appointments (patient) ---
    List<Appointment> getAvailableAppointments()              throws RemoteException;
    List<Appointment> getUserAppointments(String username)    throws RemoteException;
    boolean           bookAppointment(String username, int appointmentId) throws RemoteException;
    int               getBookingId(String username, int appointmentId)    throws RemoteException;
    boolean           cancelBooking(int bookingId)                        throws RemoteException;

    // --- Reviews (bonus) ---
    boolean      submitReview(int bookingId, int rating, String comment) throws RemoteException;
    List<Review> getDoctorReviews(String doctorName)                     throws RemoteException;
    List<Review> getAllReviews()                                          throws RemoteException;

    // --- Callbacks ---
    void registerCallback(String username, CallbackInterface callback) throws RemoteException;
}
