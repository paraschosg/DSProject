package com.dsproject.server.service;

import java.io.*;
import java.net.Socket;

public class WorkerClient {

    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public String sendRequest(String request) {
        try (
                Socket socket = new Socket(HOST, PORT);
                BufferedReader in = new BufferedReader(
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
            Socket socket = new Socket("localhost", 5000);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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