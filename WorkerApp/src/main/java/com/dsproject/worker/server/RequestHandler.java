package com.dsproject.worker.server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class RequestHandler implements Runnable {

    private Socket socket;

    private static HashMap<String, String> users = new HashMap<>();

    private static final String FILE_NAME = "users.txt";

    static {
        loadUsersFromFile();

        if (!users.containsKey("admin")) {
            users.put("admin", "1234");
            saveUserToFile("admin", "1234");
            System.out.println("Admin user added!");
        }
    }

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {

            String request = in.readLine();

            if (request == null || request.isEmpty()) {
                out.println("FAIL");
                return;
            }

            System.out.println("Worker received: " + request);

            String[] parts = request.split(";");

            String command = parts[0];

            switch (command) {

                case "register": {
                    if (parts.length < 3) {
                        out.println("FAIL");
                        break;
                    }

                    String username = parts[1].trim();
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


    private static void saveUserToFile(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_NAME, true))) {

            writer.write(username + ";" + password);
            writer.newLine();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadUsersFromFile() {
        try {
            File file = new File(FILE_NAME);

            if (!file.exists()) {
                System.out.println("users.txt not found, starting fresh...");
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");

                if (parts.length >= 2) {
                    users.put(parts[0].trim(), parts[1].trim());
                }
            }

            reader.close();

            System.out.println("Loaded users: " + users.keySet());

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