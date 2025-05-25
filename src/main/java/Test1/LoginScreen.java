package Test1;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.*;

public class LoginScreen {
    public void start(Stage stage) {
        // Create logo
        ImageView logo = new ImageView(new Image("https://kulliyyah.iium.edu.my/kict/wp-content/uploads/sites/3/2024/01/iium-kict-logo-simple.png"));
        logo.setFitWidth(200);
        logo.setPreserveRatio(true);

        // Welcome text
        Label welcomeLabel = new Label("Welcome to KICT Inventory Management System");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        welcomeLabel.setStyle("-fx-text-fill: #2a9df4;");

        Label userLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginBtn = new Button("Login");
        Button exitBtn = new Button("Exit");
        Label message = new Label();

        // Styling
        loginBtn.setStyle("-fx-background-color: #2a9df4; -fx-text-fill: white; -fx-font-weight: bold;");
        exitBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        usernameField.setStyle("-fx-padding: 8px;");
        passwordField.setStyle("-fx-padding: 8px;");
        message.setStyle("-fx-text-fill: red;");

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = FileUtil.validateLogin(username, password);
            if (role != null) {
                if (role.equals("admin")) new AdminDashboard().start(stage);
                else new TechnicianDashboard().start(stage);
            } else {
                message.setText("Invalid login.");
            }
        });

        exitBtn.setOnAction(e -> stage.close() );

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));
        grid.setVgap(15); grid.setHgap(15);
        grid.add(userLabel, 0, 1); grid.add(usernameField, 1, 1);
        grid.add(passLabel, 0, 2); grid.add(passwordField, 1, 2);
        grid.add(loginBtn, 1, 3); grid.add(exitBtn, 2, 3);
        grid.add(message, 1, 4);

        VBox root = new VBox(15, logo, welcomeLabel, grid);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        Scene scene = new Scene(root, 500, 350); // Increased height
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}