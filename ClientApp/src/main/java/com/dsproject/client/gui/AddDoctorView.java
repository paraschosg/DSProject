package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.Doctor;

import javax.swing.*;
import java.awt.*;

public class AddDoctorView {

    public AddDoctorView(ClientController controller) {

        JFrame frame = new JFrame("Add Doctor");

        JTextField nameField = new JTextField(15);
        JTextField specialtyField = new JTextField(15);

        JButton addBtn = new JButton("Add");

        JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Specialty:"));
        panel.add(specialtyField);

        panel.add(addBtn);

        frame.add(panel);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        addBtn.addActionListener(e -> {
            String name = nameField.getText();
            String specialty = specialtyField.getText();

            Doctor doctor = new Doctor(name, specialty, "General", "1234567890", name.toLowerCase() + "@hospital.com", 100.0);

            boolean ok = controller.addDoctor(doctor, "admin");

            if (ok) {
                JOptionPane.showMessageDialog(frame, "Doctor added!");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed!");
            }
        });
    }
}