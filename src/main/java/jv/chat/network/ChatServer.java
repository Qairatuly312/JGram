package jv.chat.network;

import jv.chat.database.UserDAO;
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


//    public synchronized void broadcast(String message, PrintWriter excludeWriter) {
//        for (PrintWriter writer : clients) {
//            if (writer != excludeWriter) { // Avoid sending to the sender
//                writer.println(message);
//            }
//        }
//    }

    public synchronized void addUser(int userID, ClientHandler clientHandler) {
        clients.put(userID, clientHandler);
    }

    public synchronized void removeUser(int userID) {
        clients.remove(userID);
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
