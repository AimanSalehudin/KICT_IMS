package Test2;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Start with the login screen
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}