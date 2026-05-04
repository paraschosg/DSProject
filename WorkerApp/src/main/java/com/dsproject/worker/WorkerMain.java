package com.dsproject.worker;

import com.dsproject.worker.server.WorkerServer;

public class WorkerMain {

    public static void main(String[] args) {
        WorkerServer server = new WorkerServer();
        server.start();
    }
}