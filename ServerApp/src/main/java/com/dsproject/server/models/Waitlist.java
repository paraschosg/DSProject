package com.dsproject.server.models;

import java.util.LinkedList;
import java.util.Queue;

public class Waitlist {

    private int appointmentId;
    private Queue<String> users;

    public Waitlist(int appointmentId) {
        this.appointmentId = appointmentId;
        this.users = new LinkedList<>();
    }

    public void addUser(String username) {
        users.add(username);
    }

    public String getNextUser() {
        return users.poll();
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }
}