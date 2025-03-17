package jv.chat.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UIManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        primaryStage.setMaximized(true);
    }

    public static void switchScene(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(UIManager.class.getResource("/fxml/" + fxmlFile));
            primaryStage.setScene(new Scene(root));
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
