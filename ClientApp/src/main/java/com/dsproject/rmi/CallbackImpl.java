package com.dsproject.rmi;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CallbackImpl extends UnicastRemoteObject implements CallbackInterface { //η υλοποίηση της διεπαφής CallbackInterface

    public CallbackImpl() throws RemoteException { //κατασκευαστής που καλεί τον κατασκευαστή της UnicastRemoteObject
        super();
    }

    @Override //η μέθοδος που θα καλείται από τον server για να ενημερώσει τον client
    public void notifyUser(String message) throws RemoteException {
        System.out.println("Callback received: " + message);
        JOptionPane.showMessageDialog(null, message);
    }

}