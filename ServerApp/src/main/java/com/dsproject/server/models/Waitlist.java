package com.dsproject.server.models;

import java.util.LinkedList;
import java.util.Queue;

public class Waitlist {

    private final int appointmentId;
    private final Queue<String> users = new LinkedList<>();

    public Waitlist(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void addUser(String username) { users.add(username); }

    /** Removes and returns the next user in the FIFO queue, or null if empty. */
    public String getNextUser()          { return users.poll(); }

    /** Peeks at the next user without removing them. */
    public String peekNextUser()         { return users.peek(); }

    public boolean isEmpty()             { return users.isEmpty(); }

    public int getAppointmentId()        { return appointmentId; }
}
