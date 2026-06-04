package com.dsproject.server.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String doctorName;
    private LocalDateTime dateTime;
    private int duration;
    private double cost;
    private boolean available;
    private String bookedBy;

    public Appointment(int id, String doctorName, LocalDateTime dateTime, int duration, double cost) {
        this.id = id;
        this.doctorName = doctorName;
        this.dateTime = dateTime;
        this.duration = duration;
        this.cost = cost;
        this.available = true;
    }

    public int getId()               { return id; }
    public String getDoctorName()    { return doctorName; }
    public LocalDateTime getDateTime(){ return dateTime; }
    public int getDuration()         { return duration; }
    public double getCost()          { return cost; }
    public boolean isAvailable()     { return available; }
    public String getBookedBy()      { return bookedBy; }

    public void setAvailable(boolean available) { this.available = available; }
    public void setBookedBy(String bookedBy)    { this.bookedBy = bookedBy; }
    public void setDateTime(LocalDateTime dt)   { this.dateTime = dt; }
    public void setCost(double cost)            { this.cost = cost; }

    @Override
    public String toString() {
        return id + " | " + doctorName + " | " + dateTime + " | " + duration + "min | " + cost + "€";
    }
}
