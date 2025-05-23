package Test2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        LoginScene loginScene = new LoginScene(primaryStage);
        primaryStage.setTitle("KICT Inventory Management System");
        primaryStage.setScene(loginScene.getScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
