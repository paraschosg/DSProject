package com.dsproject.server.models;

import java.io.Serializable;

public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fullName;
    private String specialty;
    private String department;
    private String phone;
    private String email;
    private double cost;

    public Doctor(String fullName, String specialty, String department,
                  String phone, String email, double cost) {

        this.fullName = fullName;
        this.specialty = specialty;
        this.department = department;
        this.phone = phone;
        this.email = email;
        this.cost = cost;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getDepartment() {
        return department;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public double getCost() {
        return cost;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    
    @Override
    public String toString() {
        return "Doctor{" +
                "fullName='" + fullName + '\'' +
                ", specialty='" + specialty + '\'' +
                ", department='" + department + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", cost=" + cost +
                '}';
    }
}