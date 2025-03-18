package jv.chat.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jv.chat.network.ChatClient;

import java.io.IOException;

import static jv.chat.network.ChatServer.PORT;

public class ChatController {
    @FXML
    private Label chatHeader;

    @FXML
    private VBox chatHistory;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField messageInput;

    @FXML
    private Button sendButton;

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static ChatClient client;

    @FXML
    public void initialize() {
        System.out.println("do i work");
        System.out.flush();
        new Thread(() -> {
            client = new ChatClient(SERVER_ADDRESS, PORT);
        }).start();
        sendButton.setOnAction(event -> sendMessageUI());  // Button click sends message

        messageInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                sendMessageUI();
                event.consume(); // Prevent new line in TextField
            }
        });

        new Thread(() -> {
            while (true) {
                try {
                    String received = ChatClient.receiveMessage();
                    Platform.runLater(() -> displayReceivedMessage(received));
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    private void sendMessageUI() {
        System.out.println("if i work i will be displayed");
        String message = messageInput.getText().trim();

        if (!message.isEmpty()) {
            // Ensure UI updates run on JavaFX thread
            Platform.runLater(() -> {
                displaySentMessage(message);  // Display message safely
                messageInput.clear();         // Clear input safely
                System.out.println("message sent in Platrform.runLater");
            });


            // Send message in a separate thread
            new Thread(() -> {
                try{
                ChatClient.sendMessage(message);
                System.out.println("message sent in Thread");}
                catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }
    }


    private void displaySentMessage(String message) {
        HBox messageBox = new HBox();
        messageBox.setPadding(new Insets(5));

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5px; -fx-background-radius: 5px;");

        messageBox.setAlignment(Pos.CENTER_RIGHT);
        messageBox.getChildren().add(messageLabel);

        chatHistory.getChildren().add(messageBox);

        // Scroll to the bottom
        chatHistory.layout();

        // Clear input field
        messageInput.clear();
    }

    private void displayReceivedMessage(String message) {
        if (message != null && !message.isEmpty()) {
            HBox messageBox = new HBox();
            messageBox.setPadding(new Insets(5));

            Label messageLabel = new Label();
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(400);
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 5px; -fx-background-radius: 5px;");

            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageBox.getChildren().add(messageLabel);

            chatHistory.getChildren().add(messageBox);
        }
    }
}