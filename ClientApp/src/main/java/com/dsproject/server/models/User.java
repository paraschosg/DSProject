package com.dsproject.server.models;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fullName;
    private String amka;
    private String phone;
    private String email;
    private String username;
    private String password;
    private String role;

    public User(String fullName, String amka, String phone, String email,
                String username, String password, String role) {

        this.fullName = fullName;
        this.amka = amka;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAmka() {
        return amka;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAmka(String amka) {
        this.amka = amka;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", amka='" + amka + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}