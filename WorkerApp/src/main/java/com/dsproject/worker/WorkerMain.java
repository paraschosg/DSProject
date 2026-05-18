package com.dsproject.worker;

import com.dsproject.worker.server.WorkerServer;

public class WorkerMain { //η κλάση αυτή είναι το entry point για την εφαρμογή του worker. Δημιουργεί και ξεκινάει τον WorkerServer, ο οποίος θα χειρίζεται τις αιτήσεις από τον server και θα εκτελεί τις εργασίες

    public static void main(String[] args) {
        WorkerServer server = new WorkerServer();
        server.start();
    }
}