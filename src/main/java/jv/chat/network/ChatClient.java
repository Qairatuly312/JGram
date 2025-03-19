package jv.chat.network;

import jv.chat.models.Message;

import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;


    public ChatClient(String serverAddress, int port, String username) {
        System.out.println("i'm chat client constructor");
        try {
            socket = new Socket(serverAddress, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("BEFORE IN CHATCLIENT");
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("After IN CHATCLIENT");

            out.writeObject(username);

//            new Thread(this::receiveMessages).start();

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
            Message message;
            while ((message = (Message) in.readObject()) != null) {
                System.out.println("Server: " + message.getContent());
            }
        } catch (SocketException e) {
            System.out.println("Connection closed by server.");
        } catch (EOFException e) {
            System.out.println("Server shut down.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(Message message) {
        try {
            synchronized (out) {
                out.writeObject(message);
                out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Message receiveMessage() throws IOException, InterruptedException {
        try {
            return (Message) in.readObject();
        } catch (EOFException e) {
            System.out.println("Connection closed.");
            return null; // Stop processing
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Thread.sleep(100); // Reduce spam (wait 100ms before retrying)
        }
        return null;
    }

    public Socket getSocket() {
        return socket;
    }
}
