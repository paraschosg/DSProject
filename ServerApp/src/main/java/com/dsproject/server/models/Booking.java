package com.dsproject.server.models;

import java.io.Serializable;

public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private int appointmentId;

    public Booking(int id, String username, int appointmentId) {
        this.id = id;
        this.username = username;
        this.appointmentId = appointmentId;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", appointmentId=" + appointmentId +
                '}';
    }
}