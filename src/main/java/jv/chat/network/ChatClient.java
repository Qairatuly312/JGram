package jv.chat.network;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
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

            // Читаем сообщения от сервера
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

            // Ввод пользователя
            while (true) {
                String message = scanner.nextLine();
                out.println(message);

                if (message.equalsIgnoreCase("/exit")) {
                    break;
                } else if (message.equalsIgnoreCase("/history")) {
                    System.out.println("📜 Loading chat history...");
                }
            }

            disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ChatClient().start();
    }
}
