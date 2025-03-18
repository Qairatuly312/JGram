package jv.chat.network;

import jv.chat.database.MessageDAO;
import jv.chat.database.UserDAO;
import jv.chat.models.User;

import java.io.*;
import java.net.*;
import java.sql.SQLException;

import static jv.chat.database.UserDAO.extractReceiverId;
import static jv.chat.network.ChatServer.PORT;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private BufferedReader reader;
    private PrintWriter writer;
    private int userId;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        try {
            System.out.println("ClientHandler started " + socket.getInetAddress());

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Clienthandler started client reader/writer: " + reader + " / " + writer);

            userId = UserDAO.getUserIdByUsername(reader.readLine());
            System.out.println(userId + ": " + reader.readLine());

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

                int receiverId = extractReceiverId(message);
                System.out.println("receiverId : " + receiverId);

                if (receiverId != -1) {
                    // Save message to the database
                    MessageDAO.saveMessage(userId, receiverId, message);

                    // Find receiver
                    ClientHandler receiverHandler = server.getClient(receiverId);

                    if (receiverHandler != null) {
                        receiverHandler.sendMessage("Private message from " + userId + ": " + message);
                    } else {
                        sendMessage("User " + receiverId + " is offline.");
                    }
                } else {
                    // Broadcast to all clients
//                    server.broadcast("Client " + userId + ": " + message, writer);
                }

                writer.println("Server received: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Client " + e.getMessage() + " caught " + e + "\nClient disconnected");
        } finally {
            server.removeUser(userId); // Remove client when they disconnect
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
