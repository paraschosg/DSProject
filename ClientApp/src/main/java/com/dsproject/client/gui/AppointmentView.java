package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.Appointment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AppointmentView {

    public AppointmentView(ClientController controller, String username) {

        JFrame frame = new JFrame("Appointments");

        DefaultListModel<Appointment> model = new DefaultListModel<>();
        JList<Appointment> list = new JList<>(model);

        JButton refresh = new JButton("Refresh");
        JButton book = new JButton("Book");
        JButton cancel = new JButton("Cancel");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(refresh);
        bottom.add(book);
        bottom.add(cancel);

        panel.add(bottom, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setSize(450, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        refresh.addActionListener(e -> {
            model.clear();

            System.out.println("Refreshing appointments...");

            List<Appointment> apps = controller.getAvailableAppointments();

            System.out.println("Apps received: " + apps);

            if (apps != null) {
                for (Appointment a : apps) {
                    System.out.println("Adding: " + a);
                    model.addElement(a);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No data from server");
            }
        });

        book.addActionListener(e -> {
            Appointment selected = list.getSelectedValue();

            if (selected == null) {
                JOptionPane.showMessageDialog(frame, "Select an appointment first");
                return;
            }

            boolean result = controller.bookAppointment(username, selected.getId());

            if (result) {
                JOptionPane.showMessageDialog(frame, "Booked successfully!");
                refresh.doClick();
            } else {
                JOptionPane.showMessageDialog(frame, "Booking failed!");
            }
        });
        
        cancel.addActionListener(e -> {
            Appointment selected = list.getSelectedValue();

            if (selected == null) {
                JOptionPane.showMessageDialog(frame, "Select an appointment first");
                return;
            }

            boolean result = controller.cancelBooking(selected.getId());

            if (result) {
                JOptionPane.showMessageDialog(frame, "Cancelled successfully!");
                refresh.doClick(); // auto refresh
            } else {
                JOptionPane.showMessageDialog(frame, "Cancel failed!");
            }
        });

        refresh.doClick();
    }
}