package com.dsproject.server.models;

import java.io.Serializable;

public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    private int bookingId;
    private String doctorName;
    private int rating;
    private String comment;
    private String patientUsername;

    public Review(int bookingId, String doctorName, int rating, String comment, String patientUsername) {
        this.bookingId = bookingId;
        this.doctorName = doctorName;
        this.rating = rating;
        this.comment = comment;
        this.patientUsername = patientUsername;
    }

    public int    getBookingId()         { return bookingId; }
    public String getDoctorName()        { return doctorName; }
    public int    getRating()            { return rating; }
    public String getComment()           { return comment; }
    public String getPatientUsername()   { return patientUsername; }

    @Override
    public String toString() {
        return "★".repeat(rating) + " | " + patientUsername + ": " + comment;
    }
}
