package com.dsproject.client;

import com.dsproject.client.controller.ClientController;
import com.dsproject.client.gui.LoginView;
import javafx.stage.Stage;

public class ClientApp {

    public void start() {
        System.out.println("Client started...");

        ClientController controller = new ClientController();

        Stage stage = new Stage();
        new LoginView(stage, controller);
    }
}