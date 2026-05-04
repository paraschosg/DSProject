package com.dsproject.server;

import com.dsproject.server.service.RemoteServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {

    public static void main(String[] args) {

        try {
            RemoteServiceImpl service = new RemoteServiceImpl();

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("ServerService", service);

            System.out.println("Server is running...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}