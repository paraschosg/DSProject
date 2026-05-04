package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterView {

    public RegisterView(ClientController controller) {

        JFrame frame = new JFrame("Register");
        frame.setSize(400, 350);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Create Account", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField amkaField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JComboBox<String> roleBox = new JComboBox<>(new String[]{"patient", "admin"});

        form.add(new JLabel("Full Name:"));
        form.add(nameField);

        form.add(new JLabel("AMKA:"));
        form.add(amkaField);

        form.add(new JLabel("Phone:"));
        form.add(phoneField);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Username:"));
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        form.add(passwordField);

        form.add(new JLabel("Role:"));
        form.add(roleBox);

        mainPanel.add(form, BorderLayout.CENTER);

        JButton registerBtn = new JButton("Register");
        registerBtn.setFocusPainted(false);
        registerBtn.setBackground(new Color(70, 130, 180));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel btnPanel = new JPanel();
        btnPanel.add(registerBtn);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);

        registerBtn.addActionListener(e -> {

            String fullName = nameField.getText();
            String amka = amkaField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleBox.getSelectedItem();

            if (fullName.isEmpty() || amka.isEmpty() || phone.isEmpty()
                    || email.isEmpty() || username.isEmpty() || password.isEmpty()) {

                JOptionPane.showMessageDialog(frame, "Fill all fields!");
                return;
            }

            User user = new User(fullName, amka, phone, email, username, password, role);

            boolean success = controller.register(user);

            if (success) {
                JOptionPane.showMessageDialog(frame, "Registered successfully!");
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "User already exists!");
            }
        });
    }
}