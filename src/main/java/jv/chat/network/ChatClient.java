package jv.chat.network;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private final String SERVER_ADDRESS = "127.0.0.1";  // Change if needed
    private final int SERVER_PORT = 12345;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void start() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("✅ Connected to chat server.");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            // Handle incoming messages from the server
            Thread listenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("❌ Disconnected from server.");
                }
            });
            listenerThread.setDaemon(true);
            listenerThread.start();

            // Send messages to the server
            while (true) {
                String message = scanner.nextLine();
                out.println(message);
                if (message.equalsIgnoreCase("/exit")) {
                    break;
                }
            }

            disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            if (socket != null) socket.close();
            System.out.println("🔌 Disconnected from server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ChatClient().start();
    }
}
