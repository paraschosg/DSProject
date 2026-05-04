package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.Doctor;

import javax.swing.*;
import java.awt.*; 

public class AdminView {

    public AdminView(ClientController controller, String username) {

        JFrame frame = new JFrame("Admin Panel");

        JLabel title = new JLabel("Welcome Admin: " + username);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JButton logoutBtn = new JButton("Logout");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(logoutBtn, BorderLayout.EAST);

        JTextField nameField = new JTextField(15);
        JTextField specialtyField = new JTextField(15);
        JTextField departmentField = new JTextField(15);
        JTextField phoneField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JTextField costField = new JTextField(15);

        JButton addButton = new JButton("Add Doctor");

        JButton viewAppointmentsBtn = new JButton("View Appointments");
        JButton addAppointmentBtn = new JButton("Add Appointment");

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(viewAppointmentsBtn);
        buttonsPanel.add(addAppointmentBtn);

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));

        form.add(new JLabel("Doctor Name:"));
        form.add(nameField);

        form.add(new JLabel("Specialty:"));
        form.add(specialtyField);

        form.add(new JLabel("Department:"));
        form.add(departmentField);

        form.add(new JLabel("Phone:"));
        form.add(phoneField);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Visit Cost:"));
        form.add(costField);

        form.add(new JLabel(""));
        form.add(addButton);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        main.add(topPanel, BorderLayout.NORTH);
        main.add(buttonsPanel, BorderLayout.CENTER);
        main.add(form, BorderLayout.SOUTH);

        frame.add(main);
        frame.setSize(500, 450);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        logoutBtn.addActionListener(e -> {
            frame.dispose();
            new LoginView(controller);
        });

        viewAppointmentsBtn.addActionListener(e -> {
            new AppointmentView(controller, username);
        });

        addAppointmentBtn.addActionListener(e -> {
            new AddAppointmentView(controller);
        });

        addButton.addActionListener(e -> {

            String name = nameField.getText();
            String specialty = specialtyField.getText();
            String department = departmentField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String costText = costField.getText();

            if (name.isEmpty() || specialty.isEmpty() || department.isEmpty()
                    || phone.isEmpty() || email.isEmpty() || costText.isEmpty()) {

                JOptionPane.showMessageDialog(frame, "Fill all fields!");
                return;
            }

            double cost;

            try {
                cost = Double.parseDouble(costText);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Cost must be a number!");
                return;
            }

            Doctor doctor = new Doctor(
                    name,
                    specialty,
                    department,
                    phone,
                    email,
                    cost
            );

            boolean result = controller.addDoctor(doctor, "admin");

            if (result) {
                JOptionPane.showMessageDialog(frame, "Doctor added!");

                nameField.setText("");
                specialtyField.setText("");
                departmentField.setText("");
                phoneField.setText("");
                emailField.setText("");
                costField.setText("");

            } else {
                JOptionPane.showMessageDialog(frame, "Failed!");
            }
        });
    }
}