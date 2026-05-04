package com.dsproject.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallbackInterface extends Remote {

    void notifyUser(String message) throws RemoteException;
}