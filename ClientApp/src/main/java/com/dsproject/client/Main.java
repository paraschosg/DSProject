package com.dsproject.client;

import com.dsproject.client.controller.ClientController;
import com.dsproject.client.gui.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        ClientController controller = new ClientController();

        new LoginView(stage, controller);
    }

    public static void main(String[] args) {
        launch();
    }
}