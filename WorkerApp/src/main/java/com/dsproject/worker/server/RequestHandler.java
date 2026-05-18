package com.dsproject.worker.server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class RequestHandler implements Runnable { //η κλάση αυτή είναι υπεύθυνη για την επεξεργασία των αιτήσεων που λαμβάνει ο worker από τον server. Κάθε φορά που ο worker λαμβάνει μια αίτηση, δημιουργείται ένα νέο thread με αυτόν τον handler για να επεξεργαστεί την αίτηση και να απαντήσει στον server

    private Socket socket;

    private static HashMap<String, String> users = new HashMap<>(); //Hashmap που αποθηκεύει τα username και password των χρηστών. Το κλειδί είναι το username και η τιμή είναι το password

    private static final String FILE_NAME = "users.txt"; //Το όνομα του αρχείου που θα χρησιμοποιείται για την αποθήκευση των χρηστών. Κάθε γραμμή στο αρχείο έχει τη μορφή "username;password"

    static {
        loadUsersFromFile();

        if (!users.containsKey("admin")) { //Προσθέτουμε έναν default admin χρήστη αν δεν υπάρχει ήδη
            users.put("admin", "1234");
            saveUserToFile("admin", "1234");
            System.out.println("Admin user added!");
        }
    }

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() { //Εδώ γίνεται η επεξεργασία της αίτησης που λαμβάνει ο worker από τον server. Διαβάζει την αίτηση, την αναλύει και απαντάει ανάλογα με το περιεχόμενο της αίτησης (register, login, delete)

        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {

            String request = in.readLine();

            if (request == null || request.isEmpty()) { //Αν η αίτηση είναι κενή ή null, απαντάμε με "FAIL" και τερματίζουμε την επεξεργασία
                out.println("FAIL");
                return;
            }

            System.out.println("Worker received: " + request);

            String[] parts = request.split(";"); //Η αίτηση χωρίζεται σε μέρη με βάση το ";" ως διαχωριστικό. Το πρώτο μέρος είναι η εντολή (register, login, delete) και τα επόμενα μέρη είναι τα δεδομένα που απαιτούνται για την εκτέλεση της εντολής

            String command = parts[0]; //Η εντολή που ζητάει ο server (register, login, delete)

            switch (command) {

                case "register": { //Αν η εντολή είναι register, ελέγχουμε αν υπάρχουν τα απαραίτητα δεδομένα. Αν όχι, απαντάμε με FAIL. Αν ναι, ελέγχουμε αν το username υπάρχει ήδη. Αν υπάρχει, απαντάμε με FAIL. Αν δεν υπάρχει, προσθέτουμε τον χρήστη στο HashMap και στο αρχείο και απαντάμε με OK
                    if (parts.length < 3) {
                        out.println("FAIL");
                        break;
                    }

                    String username = parts[1].trim(); //Το username που θέλει να καταχωρήσει ο χρήστης με trim() για να αφαιρέσουμε τυχόν κενά πριν ή μετά το username
                    String password = parts[2].trim();

                    if (users.containsKey(username)) {
                        out.println("FAIL");
                    } else {
                        users.put(username, password);
                        saveUserToFile(username, password);
                        out.println("OK");
                    }
                    break;
                }

                case "login": {
                    if (parts.length < 3) {
                        out.println("FAIL");
                        break;
                    }

                    String username = parts[1].trim();
                    String password = parts[2].trim();

                    System.out.println("Trying login: " + username);

                    if (users.containsKey(username)
                            && users.get(username).equals(password)) {
                        out.println("OK");
                    } else {
                        out.println("FAIL");
                    }
                    break;
                }

                case "delete":
                    String username = parts[1];

                    if (users.containsKey(username)) {
                        users.remove(username);
                        rewriteFile();
                        out.println("OK");
                    } else {
                        out.println("FAIL");
                    }
                    break;

                default:
                    out.println("UNKNOWN");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception ignored) {}
        }
    }


    private static void saveUserToFile(String username, String password) { //Αυτή η μέθοδος προσθέτει έναν νέο χρήστη στο αρχείο users.txt. Χρησιμοποιεί BufferedWriter για να γράψει το username και το password σε μια νέα γραμμή στο αρχείο. Το true στο FileWriter σημαίνει ότι θα προσθέσει στο τέλος του αρχείου αντί να το αντικαταστήσει
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_NAME, true))) {

            writer.write(username + ";" + password); //Γράφει το username και το password στο αρχείο με διαχωριστικό ";"
            writer.newLine();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadUsersFromFile() {
        try {
            File file = new File(FILE_NAME);

            if (!file.exists()) {
                System.out.println("users.txt not found");
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");

                if (parts.length >= 2) { //Κάθε γραμμή πρέπει να έχει τουλάχιστον δύο μέρη (username και password) για να θεωρηθεί έγκυρη. Αν η γραμμή είναι έγκυρη, προσθέτουμε το username και το password στο HashMap users
                    users.put(parts[0].trim(), parts[1].trim()); //Το trim() αφαιρεί τυχόν κενά πριν ή μετά το username και το password για να διασφαλίσουμε ότι δεν υπάρχουν περιττά κενά που θα μπορούσαν να προκαλέσουν προβλήματα κατά την επαλήθευση των στοιχείων του χρήστη
                }
            }

            reader.close();

            System.out.println("Loaded users: " + users.keySet()); //Εκτυπώνει τα usernames που φορτώθηκαν από το αρχείο για να έχουμε μια εικόνα των χρηστών που υπάρχουν ήδη

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rewriteFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"));

            for (String user : users.keySet()) {
                writer.write(user + ";" + users.get(user));
                writer.newLine();
            }

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}