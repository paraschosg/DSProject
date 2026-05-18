package com.dsproject.server.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    //Η κλάση Appointment αντιπροσωπεύει ένα ραντεβού με έναν γιατρό. Περιέχει πληροφορίες όπως το όνομα του γιατρού, την ημερομηνία και ώρα του ραντεβού, τη διάρκεια, το κόστος, και αν είναι διαθέσιμο ή όχι. Επίσης, περιέχει πληροφορίες για το ποιος έχει κλείσει το ραντεβού.
    private int id;
    private String doctorName;
    private LocalDateTime dateTime;
    private int duration;
    private double cost;
    private boolean available;
    private String bookedBy;

    //Ο constructor της κλάσης Appointment αρχικοποιεί τα πεδία της κλάσης με τις τιμές που δίνονται ως παραμέτρους
    public Appointment(int id, String doctorName, LocalDateTime dateTime,
                       int duration, double cost) {

        this.id = id;
        this.doctorName = doctorName;
        this.dateTime = dateTime;
        this.duration = duration;
        this.cost = cost;
        this.available = true;
    }

    //Οι μέθοδοι getter και setter της κλάσης Appointment επιτρέπουν την πρόσβαση και την τροποποίηση των πεδίων της κλάσης
    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }

    public int getId() {
        return id;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getDuration() {
        return duration;
    }

    public double getCost() {
        return cost;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    @Override //Η μέθοδος toString της κλάσης Appointment επιστρέφει μια συμβολοσειρά που περιέχει τις βασικές πληροφορίες του ραντεβού
    public String toString() {
        return id + " | " + doctorName + " | " + dateTime + " | " + cost + "€";
    }
}