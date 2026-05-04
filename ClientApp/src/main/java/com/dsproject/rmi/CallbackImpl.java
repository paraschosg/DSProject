package com.dsproject.rmi;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CallbackImpl extends UnicastRemoteObject implements CallbackInterface {

    public CallbackImpl() throws RemoteException {
        super();
    }

    @Override
    public void notifyUser(String message) throws RemoteException {
        System.out.println("Callback received: " + message);
        JOptionPane.showMessageDialog(null, message);
    }
}