package jv.chat.network;

import jv.chat.database.MessageDAO;
import jv.chat.database.UserDAO;
import jv.chat.models.Message;

import java.io.*;
import java.net.*;

import static jv.chat.database.UserDAO.extractReceiverId;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int userId;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        try {
            System.out.println("ClientHandler started " + socket.getInetAddress());

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Clienthandler started client out/writer: " + in + " / " + out);

            userId = 1;

            server.addUser(userId, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
    }


    @Override
    public void run() {
        try {
            // Read username first before processing messages
            String username = (String) in.readObject();
            System.out.println("User connected: " + username);

//            Object obj = in.readObject();
//            System.out.println("Received object type: " + obj.getClass().getName());  // Debugging
//
//            if (obj instanceof Message) {
//                Message message = (Message) obj;
//                System.out.println("Received Message: " + message.getContent());
//            } else {
//                System.out.println("Unexpected data received: " + obj);
//            }

            Message message;
            while ((message = (Message) in.readObject()) != null) {
                System.out.println("Received: " + message);

                int receiverId = extractReceiverId(message);
                System.out.println("receiverId: " + receiverId);

                if (receiverId != -1) {
                    // Save message to the database
                    MessageDAO.saveMessage(userId, receiverId, message.getContent());

                    // Find receiver
                    ClientHandler receiverHandler = server.getClient(receiverId);

                    if (receiverHandler != null) {
                        receiverHandler.sendMessage(message);  // ✅ Send to the receiver
                    } else {
                        System.out.println("User " + receiverId + " is offline.");
                        // You could queue the message or notify the sender
                        sendMessage(new Message(userId, receiverId, "User is offline. Message will be saved."));
                    }
                } else {
                    server.broadcast(message, out);  // ✅ Broadcast to all
                }

                // ✅ Send proper acknowledgment as a Message object (not a String)
                sendMessage(new Message(0, userId, "Server received your message."));
            }
        } catch (EOFException e) {
            System.out.println("Client disconnected.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // ✅ Remove the user and close resources properly
            server.removeUser(userId);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }
}
