package jv.chat;

import javafx.application.Application;
import javafx.stage.Stage;
import jv.chat.utils.UIManager;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        UIManager.setPrimaryStage(primaryStage);
        UIManager.switchScene("login.fxml"); // Запускаем экран входа
    }
    public static void main(String[] args) {
        launch(args);
    }
}
