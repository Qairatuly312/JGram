package jv.chat.network;

import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket socket;
    private static PrintWriter writer;
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


    public ChatClient(String serverAddress, int port) {
        System.out.println("i'm chat client constructor");
        try {
            socket = new Socket(serverAddress, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(this::receiveMessages).start();

//            sendMessages();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void sendMessages() {
//        try {
//            String message;
//            while ((message = reader.readLine()) != null) {
//                writer.println(message);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Server: " + message);
                System.out.flush();
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server.");
            System.out.flush();
        }
    }

    public static void sendMessage(String message) {
        try {
            writer.println(message); // Send the actual message
            writer.flush(); // Ensure it's sent immediately
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String receiveMessage() throws IOException {
        return reader.readLine();
    }
}
