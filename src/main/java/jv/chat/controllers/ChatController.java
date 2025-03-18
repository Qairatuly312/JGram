package jv.chat.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import jv.chat.database.MessageDAO;
import jv.chat.database.UserDAO;
import jv.chat.models.Message;
import jv.chat.network.ChatClient;
import jv.chat.utils.UIManager;

import java.io.IOException;
import java.util.List;

import static jv.chat.database.UserDAO.getAllUsernames;
import static jv.chat.network.ChatServer.PORT;

public class ChatController {
    @FXML
    private ListView<String> contactsList;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button profileButton;

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

    @FXML
    private Label accountName = new Label();

    @FXML
    private HBox messageBox;

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static ChatClient client;
    private static String username;

    @FXML
    public void initialize() {

        username = UIManager.getCurrentUsername();
        accountName.setText(username);

        new Thread(() -> client = new ChatClient(SERVER_ADDRESS, PORT, username)).start();

        sendButton.setOnAction(event -> sendMessageUI());
        messageInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                sendMessageUI();
                event.consume();
            }
        });

        contactsList.getSelectionModel().selectedItemProperty().addListener((obs, oldContact, newContact) -> {
            if (newContact != null) {
                loadChatHistory(newContact);
            }
        });


        new Thread(() -> {
            while (true) {
                try {
                    Message received = ChatClient.receiveMessage();
                    Platform.runLater(() -> displayReceivedMessage(received));
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();

        loadContacts();
        chatHeader.setText("Chat Header");
    }

    private void loadContacts() {
        List<String> contacts = getAllUsernames(UserDAO.getUserIdByUsername(username));
        Platform.runLater(() -> contactsList.getItems().setAll(contacts));
    }

    private void sendMessageUI() {
        String selectedContact = contactsList.getSelectionModel().getSelectedItem();
        if (selectedContact == null) {
            System.out.println("choose contact before sending message");
            return;
        }

        int receiverId = UserDAO.getUserIdByUsername(selectedContact);
        int senderId = UserDAO.getUserIdByUsername(username);

        Message message = new Message(senderId, receiverId, messageInput.getText().trim());
        if (message.getContent().trim().isEmpty()) return;


        MessageDAO.saveMessage(senderId, receiverId, messageInput.getText().trim());

        Platform.runLater(() -> {
            displaySentMessage(message.getContent());
            messageInput.clear();
        });

        new Thread(() -> {
            try {
                ChatClient.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

private void displaySentMessage(String message) {
    HBox messageBox = createMessageBubble(message, "#3498db", Pos.CENTER_RIGHT);
    chatHistory.getChildren().add(messageBox);
    chatHistory.layout();
}

private void displayReceivedMessage(Message message) {
    if (message == null || message.getContent().isEmpty())
        return;
    if(message.getSenderId() == UserDAO.getUserIdByUsername(username)) {
        HBox messageBox = createMessageBubble(message.getContent(), "#2ecc71", Pos.CENTER_LEFT);
        chatHistory.getChildren().add(messageBox);
    }
}

private HBox createMessageBubble(String text, String color, Pos alignment) {
    HBox messageBox = new HBox();
    messageBox.setPadding(new Insets(5));
    messageBox.setAlignment(alignment);

    Label messageLabel = new Label(text);
    messageLabel.setWrapText(true);
    messageLabel.setMaxWidth(400);
    messageLabel.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 5px; -fx-background-radius: 5px;");

    messageBox.getChildren().add(messageLabel);
    return messageBox;
}

private void loadChatHistory(String selectedContact) {
    chatHistory.getChildren().clear(); // Clear previous chat
    int senderId = UserDAO.getUserIdByUsername(username);
    int receiverId = UserDAO.getUserIdByUsername(selectedContact);

    List<Message> messages = MessageDAO.getChatHistory(senderId, receiverId);

    for (Message msg : messages) {
        if (msg.getSenderId() == senderId) {
            displaySentMessage(msg.getContent());
        } else {
            displayReceivedMessage(msg);
        }
    }
}

}
