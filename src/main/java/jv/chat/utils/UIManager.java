package jv.chat.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jv.chat.controllers.ChatController;

import java.io.IOException;

public class UIManager {
    private static Stage primaryStage = new Stage();

    public static void setPrimaryStage(Stage stage) {
        System.out.println("primary stage set");
        primaryStage = stage;
//        primaryStage.setMaximized(true);
    }

    public static void switchScene(String fxmlFile) {
        System.out.println("switch scene");
        System.out.println("switch scene inside of try");
        try {
            System.out.println("switch scene inside of try");
            FXMLLoader loader = new FXMLLoader(UIManager.class.getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.setMaximized(true);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(900);
            primaryStage.show();
            System.out.println("switch scene inside of try");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
