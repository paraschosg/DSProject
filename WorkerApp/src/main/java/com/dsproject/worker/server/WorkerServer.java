package com.dsproject.worker.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WorkerServer { //η κλάση αυτή είναι υπεύθυνη για τη δημιουργία ενός server socket που θα ακούει σε μια συγκεκριμένη θύρα για εισερχόμενες συνδέσεις από τον server. Κάθε φορά που ένας client συνδέεται, δημιουργείται ένα νέο thread με έναν RequestHandler για να επεξεργαστεί την αίτηση του client

    private static final int PORT = 5000; //Η θύρα στην οποία θα ακούει ο worker για εισερχόμενες συνδέσεις από τον server

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT); //Δημιουργία ενός server socket που θα ακούει στην καθορισμένη θύρα
            System.out.println("Worker running on port " + PORT);

            while (true) {
                Socket client = serverSocket.accept(); //Αποδοχή μιας εισερχόμενης σύνδεσης από τον server. Η μέθοδος accept() μπλοκάρει μέχρι να συνδεθεί ένας client
                System.out.println("Client connected");

                new Thread(new RequestHandler(client)).start(); //Δημιουργία ενός νέου thread με έναν RequestHandler για να επεξεργαστεί την αίτηση του client. Ο RequestHandler θα διαβάσει την αίτηση, θα την αναλύσει και θα απαντήσει ανάλογα με το περιεχόμενο της αίτησης
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}