package com.dsproject.client.gui;

import javax.swing.*;
import com.dsproject.client.controller.ClientController;

public class MenuView {

    public MenuView(ClientController controller, String username) {

        JFrame frame = new JFrame("Menu");

        JLabel label = new JLabel("Welcome " + username);

        JButton appointmentsBtn = new JButton("View Appointments");
        JButton deleteBtn = new JButton("Delete Account");
        JButton logoutBtn = new JButton("Logout");

        JPanel panel = new JPanel();

        panel.add(label);
        panel.add(appointmentsBtn);
        panel.add(deleteBtn);
        panel.add(logoutBtn);

        if (username.equals("admin")) {

            JButton addDoctorBtn = new JButton("Add Doctor");
            JButton addAppointmentBtn = new JButton("Add Appointment");

            panel.add(addDoctorBtn);
            panel.add(addAppointmentBtn);

            addDoctorBtn.addActionListener(e -> {
                new AddDoctorView(controller);
            });

            addAppointmentBtn.addActionListener(e -> {
                new AddAppointmentView(controller);
            });
        }

        frame.add(panel);
        frame.setSize(450, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        appointmentsBtn.addActionListener(e -> {
            new AppointmentView(controller, username);
        });

        deleteBtn.addActionListener(e -> {

            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure?");

            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = controller.deleteUser(username);

                if (ok) {
                    JOptionPane.showMessageDialog(frame, "Account deleted");
                    frame.dispose();
                    new LoginView(controller);
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed");
                }
            }
        });

        logoutBtn.addActionListener(e -> {
            frame.dispose();
            new LoginView(controller);
        });
    }
}