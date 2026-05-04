package com.dsproject.server.models;

public class Review {

    private int bookingId;
    private String doctorName;
    private int rating;
    private String comment;

    public Review(int bookingId, String doctorName, int rating, String comment) {
        this.bookingId = bookingId;
        this.doctorName = doctorName;
        this.rating = rating;
        this.comment = comment;
    }
}