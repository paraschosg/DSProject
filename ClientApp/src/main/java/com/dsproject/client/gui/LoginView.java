package com.dsproject.client.gui;

import com.dsproject.client.controller.ClientController;
import com.dsproject.server.models.User;

import javax.swing.*;
import java.awt.*;

public class LoginView {

    public LoginView(ClientController controller) {

        JFrame frame = new JFrame("Login");

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3,2));

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        panel.add(loginButton);
        panel.add(registerButton);

        frame.add(panel);
        frame.setSize(300,200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        loginButton.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());

            User loggedUser = controller.login(user, pass);

            if (loggedUser != null) {

                JOptionPane.showMessageDialog(frame, "Login Successful");

                controller.registerCallback(user);

                frame.dispose();

                if (loggedUser.getRole().equals("admin")) {
                    new AdminView(controller, user);
                } else {
                    new MenuView(controller, user);
                }

            } else {
                JOptionPane.showMessageDialog(frame, "Login Failed");
            }
        });

        registerButton.addActionListener(e -> {
            new RegisterView(controller);
        });
    }
}