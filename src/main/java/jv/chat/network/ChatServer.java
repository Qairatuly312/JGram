package jv.chat.network;

import jv.chat.database.UserDAO;
import jv.chat.models.Message;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class ChatServer {
    public static final int PORT = 12346;
    private Map<Integer, ClientHandler> clients = new ConcurrentHashMap<>();


    public ChatServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                new Thread(new ClientHandler(clientSocket, this)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void broadcast(Message message, ObjectOutputStream excludeStream) throws IOException {
        for (ClientHandler handler : clients.values()) {
            if (handler.getOut() != excludeStream) { // Avoid sending to the sender
                handler.sendMessage(message);
            }
        }
    }


    public synchronized void addUser(int userID, ClientHandler clientHandler) {
        clients.put(userID, clientHandler);
    }

    public synchronized void removeUser(int userID) {
        ClientHandler handler = clients.remove(userID);
        if (handler != null) {
            handler.closeConnection();
        }
    }

    public synchronized ClientHandler getClient(int userId) {
        return clients.get(userId);
    }

    public static void main(String[] args) {
        new ChatServer(PORT);
    }

    public static void stop() {
        System.exit(0);
    }
}
