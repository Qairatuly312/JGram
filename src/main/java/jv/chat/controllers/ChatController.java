package jv.chat.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    public void initialize() {
        sendButton.setOnAction(event -> sendMessage());
    }

    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty()) {
            HBox messageBox = new HBox();
            messageBox.setPadding(new Insets(5));

            Label messageLabel = new Label();
            messageLabel.setStyle("-fx-padding: 5px; -fx-background-radius: 5px;");
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(400);

            // Подсчет строк
            int maxLines = 15;
            String[] lines = message.split("\n");
            if (lines.length > maxLines) {
                StringBuilder previewText = new StringBuilder();
                for (int i = 0; i < maxLines; i++) {
                    previewText.append(lines[i]).append("\n");
                }
                previewText.append("...");

                messageLabel.setText(previewText.toString());

                Button expandButton = new Button("Expand");
                expandButton.setOnAction(ev -> {
                    messageLabel.setText(message);
                    messageBox.getChildren().remove(expandButton);
                });

                messageBox.getChildren().addAll(messageLabel, expandButton);
            } else {
                messageLabel.setText(message);
                messageBox.getChildren().add(messageLabel);
            }

            messageLabel.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5px; -fx-background-radius: 5px;");
            messageBox.setAlignment(Pos.CENTER_RIGHT);

            chatHistory.getChildren().add(messageBox);
            messageInput.clear();
        }
    }

    @FXML
    private void handleClose() {
        Platform.exit();
        System.exit(0);
    }
}
