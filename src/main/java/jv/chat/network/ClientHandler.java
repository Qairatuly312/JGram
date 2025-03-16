package jv.chat.network;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ChatServer server;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Ask for username
            out.println("Enter your username:");
            username = in.readLine();
            System.out.println("👤 User logged in: " + username);
            server.broadcastMessage("📢 " + username + " joined the chat!", this);

            // Read messages from client
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/exit")) {
                    break;
                }
                System.out.println(username + ": " + message);
                server.broadcastMessage(username + ": " + message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void disconnect() {
        try {
            System.out.println("❌ " + username + " disconnected.");
            server.broadcastMessage("📢 " + username + " left the chat!", this);
            server.removeClient(this);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
