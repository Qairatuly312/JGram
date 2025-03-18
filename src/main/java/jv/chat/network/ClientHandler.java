package jv.chat.network;

import jv.chat.database.MessageDAO;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private BufferedReader reader;
    private PrintWriter writer;
    private int userId;
    private int receiverId = 1; // Фиксированный ID получателя (можно заменить)

    public ClientHandler(Socket socket, ChatServer server) { // Передаем userId сразу
        this.socket = socket;
        this.server = server;
        this.userId = userId;

        try {
            System.out.println("ClientHandler started " + socket.getInetAddress());

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Clienthandler started client reader/writer: " + reader + " / " + writer);

            System.out.println("User logged in with ID: " + userId);

            server.addUser(userId, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received: " + message);

                // Сохраняем сообщение в БД
                MessageDAO.saveMessage(userId, receiverId, message);

                // Отправляем его получателю
                ClientHandler receiverHandler = server.getClient(receiverId);
                if (receiverHandler != null) {
                    receiverHandler.sendMessage("From " + userId + ": " + message);
                } else {
                    sendMessage("User " + receiverId + " is offline.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Client disconnected");
        } finally {
            server.removeUser(userId);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
