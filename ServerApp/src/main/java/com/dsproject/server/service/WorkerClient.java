package com.dsproject.server.service;

import java.io.*;
import java.net.Socket;

public class WorkerClient {

    private static final String HOST = "localhost";
    private static final int PORT = 5000; //Ο port που χρησιμοποιεί ο worker server

    public String sendRequest(String request) {
        try (
                Socket socket = new Socket(HOST, PORT); //Σύνδεση με τον worker server
                BufferedReader in = new BufferedReader( //Δημιουργία BufferedReader για ανάγνωση της απάντησης από τον worker server
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {

            out.println(request);
            return in.readLine();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public boolean deleteUser(String username) {
        try {
            Socket socket = new Socket("localhost", 5000); //Σύνδεση με τον worker server

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Δημιουργία PrintWriter για αποστολή της εντολής διαγραφής και BufferedReader για ανάγνωση της απάντησης

            out.println("delete;" + username);

            String response = in.readLine();
            socket.close();

            return response.equals("OK");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}