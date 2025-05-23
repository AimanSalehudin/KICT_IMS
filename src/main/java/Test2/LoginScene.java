package Test2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginScene {
    private Scene scene;

    public LoginScene(Stage stage) {
        Label welcome = new Label("Welcome to KICT Inventory Management System");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcome.setTextFill(Color.DARKBLUE);

        Image logoImg = new Image("https://kulliyyah.iium.edu.my/kict/wp-content/uploads/sites/3/2024/01/iium-kict-logo-simple.png");
        ImageView logo = new ImageView(logoImg);
        logo.setFitHeight(100);
        logo.setPreserveRatio(true);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            DashboardScene dashboard = new DashboardScene(stage, this.getScene());
            stage.setScene(dashboard.getScene());
        });

        VBox layout = new VBox(20, logo, welcome, usernameField, passwordField, loginButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f8ff, #e6f0ff);");

        this.scene = new Scene(layout, 600, 400);
    }

    public Scene getScene() {
        return scene;
    }
}

