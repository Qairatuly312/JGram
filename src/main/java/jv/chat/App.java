//package jv.chat;
//
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.stage.Stage;
//import jv.chat.util.UIManager;
//
//public class App extends Application {
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        UIManager.setPrimaryStage(primaryStage);
//        primaryStage.setTitle("JGram");
//        primaryStage.setResizable(false);
//        primaryStage.show();
//
//        primaryStage.setOnCloseRequest(event -> {
//            try {
//                stop();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    public void stop() throws Exception {
//        Platform.exit();
//        System.exit(0);
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
