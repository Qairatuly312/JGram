package jv.chat.network;

import jv.chat.database.DatabaseConnection;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

            // Запрашиваем имя пользователя
            out.println("Enter your username:");
            username = in.readLine();

            if (username == null || username.trim().isEmpty()) {
                disconnect();
                return;
            }

            System.out.println("👤 User logged in: " + username);
            server.broadcastMessage("📢 " + username + " joined the chat!", this);

            // Загружаем историю сообщений при входе
            sendChatHistory();

            // Читаем входящие сообщения
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

    public String getUsername() {
        return username;
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

    private void sendChatHistory() {
        String query = "SELECT sender, content FROM messages ORDER BY timestamp ASC LIMIT 50";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                sendMessage(rs.getString("sender") + ": " + rs.getString("content"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
