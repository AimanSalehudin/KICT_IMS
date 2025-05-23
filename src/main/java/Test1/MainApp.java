package Test1;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
