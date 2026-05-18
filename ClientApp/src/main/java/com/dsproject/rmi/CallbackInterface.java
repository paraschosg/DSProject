package com.dsproject.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallbackInterface extends Remote { //Αυτή η διεπαφή ορίζει τη μέθοδο που θα καλείται από τον server για να ενημερώνει τον client για νέα διαθέσιμα ραντεβού ή άλλες σημαντικές πληροφορίες. Ο client θα υλοποιεί αυτή τη διεπαφή και θα εγγράφεται στον server για να λαμβάνει αυτές τις ενημερώσεις

    void notifyUser(String message) throws RemoteException;
}