package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class AddAppointmentView {

    public AddAppointmentView(ClientController controller) {

        JFrame frame = new JFrame("Add Appointment");

        JTextField doctorField = new JTextField(15);
        JTextField dateField = new JTextField(15); // format: 2026-05-10T10:00
        JTextField durationField = new JTextField(15);
        JTextField costField = new JTextField(15);

        JButton addBtn = new JButton("Add");

        JPanel panel = new JPanel(new GridLayout(5, 2));

        panel.add(new JLabel("Doctor Name:"));
        panel.add(doctorField);

        panel.add(new JLabel("DateTime (yyyy-MM-ddTHH:mm):"));
        panel.add(dateField);

        panel.add(new JLabel("Duration:"));
        panel.add(durationField);

        panel.add(new JLabel("Cost:"));
        panel.add(costField);

        panel.add(addBtn);

        frame.add(panel);
        frame.setSize(400, 250);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        addBtn.addActionListener(e -> {
            try {
                String doctor = doctorField.getText();
                LocalDateTime date = LocalDateTime.parse(dateField.getText());
                int duration = Integer.parseInt(durationField.getText());
                double cost = Double.parseDouble(costField.getText());

                int id = controller.addAppointment(doctor, date, duration, cost);

                if (id != -1) {
                    JOptionPane.showMessageDialog(frame, "Appointment added!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input!");
            }
        });
    }
}