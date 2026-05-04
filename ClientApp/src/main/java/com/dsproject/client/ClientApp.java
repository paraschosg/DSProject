package com.dsproject.client;

import com.dsproject.client.controller.ClientController;
import com.dsproject.client.gui.LoginView;

public class ClientApp {

    public void start() {
        System.out.println("Client started...");

        ClientController controller = new ClientController();
        
        new LoginView(controller);
    }
}