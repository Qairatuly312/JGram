package jv.chat.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jv.chat.controllers.ChatController;

import java.io.IOException;

public class UIManager {
    private static Stage primaryStage = new Stage();
    private static String currentUsername; // Store the logged-in username
    private static final boolean resizable = primaryStage.isResizable();

    public static void setPrimaryStage(Stage stage) {
        System.out.println("primary stage set");
        primaryStage = stage;
//        primaryStage.setMaximized(true);
    }

    public static void switchScene(String fxmlFile) {
        System.out.println("switch scene");
        try {
            FXMLLoader loader = new FXMLLoader(UIManager.class.getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();
            primaryStage.setResizable(!resizable);
            primaryStage.setResizable(resizable);
            primaryStage.setMaximized(true);
            primaryStage.setTitle(fxmlFile.replace(".fxml", ""));
            primaryStage.setMinWidth(1710);
            primaryStage.setMinHeight(1068);
            primaryStage.centerOnScreen();
//            System.out.println("Before switch: " + primaryStage.getWidth() + "x" + primaryStage.getHeight());
//            System.out.flush();

            primaryStage.setScene(new Scene(root));


            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }
}
