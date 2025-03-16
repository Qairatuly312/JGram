package jv.chat.network;

import jv.chat.database.DatabaseConnection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private static final int PORT = 12345;
    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🔥 Chat server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("✅ New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public void broadcastMessage(String message, ClientHandler sender) {
        // Сохраняем сообщение в базе данных
        if (sender != null) {
            DatabaseConnection.sendMessage(sender.getUsername(), message);
        }

        // Отправляем всем клиентам
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }
}
