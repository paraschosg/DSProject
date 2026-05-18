package com.dsproject.server.controller;

import com.dsproject.server.models.*;
import com.dsproject.rmi.CallbackInterface;
import com.dsproject.server.service.WorkerClient;

import java.time.LocalDateTime;
import java.util.*;

public class ServerController { //η κλάση αυτή είναι ο κεντρικός ελεγκτής της εφαρμογής του server. Διαχειρίζεται τους χρήστες, τους γιατρούς, τα ραντεβού, τις κρατήσεις και τις λίστες αναμονής. Επίσης, επικοινωνεί με τον worker για να εκτελεί τις εργασίες και να ενημερώνει τους χρήστες μέσω callbacks

    private HashMap<String, User> users; //χρησιμοποιώ HashMap για να αποθηκεύω τους χρήστες με κλειδί το username, ώστε να μπορώ να κάνω γρήγορη αναζήτηση κατά το login και την εγγραφή
    private ArrayList<Doctor> doctors; //χρησιμοποιώ ArrayList για να αποθηκεύω τους γιατρούς, καθώς δεν χρειάζομαι γρήγορη αναζήτηση με κλειδί, αλλά απλά μια λίστα με όλους τους γιατρούς

    private HashMap<Integer, Booking> bookings;
    private HashMap<Integer, Waitlist> waitlists;
    private HashMap<String, CallbackInterface> callbacks;

    private WorkerClient workerClient = new WorkerClient(); //δημιουργώ ένα instance του WorkerClient για να μπορώ να στέλνω αιτήσεις στον worker και να λαμβάνω απαντήσεις

    private HashMap<Integer, Appointment> appointments;

    private int appointmentCounter = 1;
    private int bookingCounter = 1;

    public ServerController() { //στο constructor αρχικοποιώ τις δομές δεδομένων και προσθέτω έναν admin χρήστη και μερικά ραντεβού για δοκιμή

        users = new HashMap<>();
        doctors = new ArrayList<>();
        appointments = new HashMap<>();
        bookings = new HashMap<>();
        waitlists = new HashMap<>();

        //Προσθέτουμε έναν admin χρήστη για δοκιμή
        users.put("admin", new User("Admin", "000", "000", "admin@mail.com",
                "admin", "1234", "admin"));

        //Προσθέτουμε μερικά ραντεβού για δοκιμή
        appointments.put(appointmentCounter, new Appointment(
                appointmentCounter++, "Doctor A",
                LocalDateTime.now().plusDays(1),
                30, 50));

        //Προσθέτουμε ένα ραντεβού που είναι ήδη κλεισμένο για να δοκιμάσουμε τη λίστα αναμονής
        appointments.put(appointmentCounter, new Appointment(
                appointmentCounter++, "Doctor B",
                LocalDateTime.now().plusDays(2),
                45, 70));
    }

    public User login(String username, String password) { //η μέθοδος αυτή ελέγχει αν υπάρχει ο χρήστης με το δοσμένο username και αν ο κωδικός είναι σωστός. Αν ναι, επιστρέφει το αντικείμενο User, αλλιώς επιστρέφει null

        if (users.containsKey(username)) {
            User user = users.get(username);

            if (user.getPassword().equals(password)) {
                return user;
            }
        }

        return null;
    }

    public boolean register(User user) {

        String request = "register;" + user.getUsername() + ";" + user.getPassword();

        String response = workerClient.sendRequest(request);

        if (response.equals("OK")) {
            users.put(user.getUsername(), user);
            return true;
        }

        return false;
    }

    public boolean addDoctor(Doctor doctor, String role) {

        if (!role.equals("admin")) {
            return false;
        }

        doctors.add(doctor);
        return true;
    }

    public int addAppointment(String doctorName, LocalDateTime dateTime, int duration, double cost) {

        Appointment ap = new Appointment(
                appointmentCounter++, doctorName, dateTime, duration, cost
        );

        appointments.put(ap.getId(), ap);
        return ap.getId();
    }

    public List<Appointment> getAvailableAppointments() {

        System.out.println("Server: appointments = " + appointments.size());

        List<Appointment> list = new ArrayList<>();

        for (Appointment a : appointments.values()) {
            if (a.isAvailable()) list.add(a);
        }

        return list;
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments.values());
    }

    public boolean bookAppointment(String username, int appointmentId) {

        Appointment ap = appointments.get(appointmentId);

        if (ap == null) return false;

        if (!ap.isAvailable()) { //αν το ραντεβού δεν είναι διαθέσιμο, προσθέτουμε τον χρήστη στη λίστα αναμονής και επιστρέφουμε false
            addToWaitlist(username, appointmentId);
            return false;
        }

        //δημιουργούμε μια νέα κράτηση και την αποθηκεύουμε στο HashMap με κλειδί το bookingId, ώστε να μπορούμε να την ακυρώσουμε αργότερα με βάση το bookingId
        Booking booking = new Booking(bookingCounter++, username, appointmentId);
        bookings.put(booking.getId(), booking);

        ap.setAvailable(false);
        ap.setBookedBy(username);

        return true;
    }

    public int getBookingId(String username, int appointmentId) {
        for (Booking b : bookings.values()) {
            if (b.getUsername().equals(username) && b.getAppointmentId() == appointmentId) {
                return b.getId();
            }
        }
        return -1;
    }

    public boolean cancelBooking(int bookingId) {

        Booking booking = bookings.get(bookingId);
        if (booking == null) return false;

        Appointment ap = appointments.get(booking.getAppointmentId()); //αποθηκεύουμε το ραντεβού που αντιστοιχεί στην κράτηση, ώστε να το ενημερώσουμε μετά την ακύρωση της κράτησης
        if (ap != null) { //αν το ραντεβού υπάρχει, το κάνουμε διαθέσιμο ξανά και αφαιρούμε τον χρήστη που το είχε κλείσει
            ap.setAvailable(true);
            ap.setBookedBy(null);
        }

        bookings.remove(bookingId);
        return true;
    }

    private void addToWaitlist(String username, int appointmentId) { //η μέθοδος αυτή προσθέτει τον χρήστη στη λίστα αναμονής για το συγκεκριμένο ραντεβού. Αν δεν υπάρχει ήδη λίστα αναμονής για αυτό το ραντεβού, δημιουργεί μια νέα λίστα και την αποθηκεύει στο HashMap με κλειδί το appointmentId

        waitlists.putIfAbsent(appointmentId, new Waitlist(appointmentId));
        waitlists.get(appointmentId).addUser(username);
    }

    private void notifyWaitlist(int appointmentId) { //η μέθοδος αυτή ειδοποιεί τον επόμενο χρήστη στη λίστα αναμονής για το συγκεκριμένο ραντεβού ότι το ραντεβού είναι διαθέσιμο ξανά. Αν δεν υπάρχει λίστα αναμονής ή αν η λίστα είναι άδεια, δεν κάνει τίποτα

        Waitlist wl = waitlists.get(appointmentId); //αποθηκεύουμε τη λίστα αναμονής για το συγκεκριμένο ραντεβού, ώστε να την ελέγξουμε και να πάρουμε τον επόμενο χρήστη

        if (wl == null || wl.isEmpty()) return; //αν δεν υπάρχει λίστα αναμονής ή αν η λίστα είναι άδεια, δεν κάνουμε τίποτα

        String nextUser = wl.getNextUser(); //παίρνουμε τον επόμενο χρήστη από τη λίστα αναμονής, ώστε να τον ειδοποιήσουμε ότι το ραντεβού είναι διαθέσιμο ξανά

        System.out.println("Notify user: " + nextUser);

        CallbackInterface callback = callbacks.get(nextUser); //αποθηκεύουμε το callback του επόμενου χρήστη, ώστε να τον ειδοποιήσουμε μέσω του callback ότι το ραντεβού είναι διαθέσιμο ξανά

        if (callback != null) { //αν υπάρχει callback για τον επόμενο χρήστη, τον ειδοποιούμε μέσω του callback ότι το ραντεβού είναι διαθέσιμο ξανά. Αν δεν υπάρχει callback, δεν κάνουμε τίποτα
            try {
                callback.notifyUser("Appointment available again!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registerCallback(String username, CallbackInterface callback) {
        callbacks.put(username, callback);
    }
}