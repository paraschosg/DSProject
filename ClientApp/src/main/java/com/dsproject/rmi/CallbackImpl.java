package com.dsproject.rmi;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Client-side RMI callback implementation.
 * Uses Platform.runLater to show JavaFX alerts safely from RMI threads.
 */
public class CallbackImpl extends UnicastRemoteObject implements CallbackInterface {

    public CallbackImpl() throws RemoteException {
        super();
    }

    @Override
    public void notifyUser(String message) throws RemoteException {
        System.out.println("[Callback] " + message);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notification");
            alert.setHeaderText("Server Notification");
            alert.setContentText(message);
            alert.show();
        });
    }
}
