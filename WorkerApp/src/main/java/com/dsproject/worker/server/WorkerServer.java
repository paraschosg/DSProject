package com.dsproject.worker.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WorkerServer {

    private static final int PORT = 5000;

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Worker running on port " + PORT);

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected");

                new Thread(new RequestHandler(client)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}