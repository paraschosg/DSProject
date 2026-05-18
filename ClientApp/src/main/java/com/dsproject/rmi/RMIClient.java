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

    public RMIClient() { //Constructor που συνδέεται με τον RMI Server
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099); //Συνδέεται με το RMI registry στον localhost και την προεπιλεγμένη θύρα 1099
            remote = (RemoteInterface) registry.lookup("ServerService"); //Αναζητά το απομακρυσμένο αντικείμενο με το όνομα "ServerService" και το αποθηκεύει στο πεδίο remote

            System.out.println("Connected to RMI Server");

        } catch (Exception e) { //Εάν η σύνδεση αποτύχει, εκτυπώνει ένα μήνυμα σφάλματος και το stack trace
            System.out.println("Connection to server failed");
            e.printStackTrace();
        }
    }

    public User login(String username, String password) { //Καλεί τη μέθοδο login του απομακρυσμένου αντικειμένου, περνώντας το όνομα χρήστη και τον κωδικό πρόσβασης. Επιστρέφει ένα αντικείμενο User εάν η σύνδεση είναι επιτυχής, ή null εάν αποτύχει
        try {
            return remote.login(username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(User user) { //Καλεί τη μέθοδο register του απομακρυσμένου αντικειμένου, περνώντας ένα αντικείμενο User. Επιστρέφει true εάν η εγγραφή είναι επιτυχής, ή false εάν αποτύχει
        try {
            return remote.register(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(String username) { //Καλεί τη μέθοδο deleteUser του απομακρυσμένου αντικειμένου, περνώντας το όνομα χρήστη. Επιστρέφει true εάν η διαγραφή είναι επιτυχής, ή false εάν αποτύχει
        try {
            return remote.deleteUser(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addDoctor(Doctor doctor, String role) { //Καλεί τη μέθοδο addDoctor του απομακρυσμένου αντικειμένου, περνώντας ένα αντικείμενο Doctor και έναν ρόλο. Επιστρέφει true εάν η προσθήκη είναι επιτυχής, ή false εάν αποτύχει
        try {
            return remote.addDoctor(doctor, role);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Καλεί τη μέθοδο addAppointment του απομακρυσμένου αντικειμένου, περνώντας το όνομα του γιατρού, την ημερομηνία και ώρα του ραντεβού, τη διάρκεια και το κόστος. Επιστρέφει το ID του νέου ραντεβού εάν η προσθήκη είναι επιτυχής, ή -1 εάν αποτύχει
    public int addAppointment(String doctor, LocalDateTime date, int duration, double cost) {
        try {
            return remote.addAppointment(doctor, date, duration, cost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Appointment> getUserAppointments(String username) { //Καλεί τη μέθοδο getUserAppointments του απομακρυσμένου αντικειμένου, περνώντας το όνομα χρήστη. Επιστρέφει μια λίστα με τα ραντεβού του χρήστη εάν η ανάκτηση είναι επιτυχής, ή null εάν αποτύχει
        try {
            return remote.getUserAppointments(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean bookAppointment(String username, int appointmentId) {
        try {
            return remote.bookAppointment(username, appointmentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getBookingId(String username, int appointmentId) {
        try {
            return remote.getBookingId(username, appointmentId);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
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
            CallbackImpl callback = new CallbackImpl(); //Δημιουργεί μια νέα υλοποίηση του CallbackImpl η οποία θα χειρίζεται τις κλήσεις επιστροφής από τον server
            remote.registerCallback(username, callback); //Καλεί τη μέθοδο registerCallback του απομακρυσμένου αντικειμένου, περνώντας το όνομα χρήστη και την υλοποίηση του callback. Αυτό επιτρέπει στον server να ενημερώνει τον client για αλλαγές στα ραντεβού
            System.out.println("Callback registered");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}