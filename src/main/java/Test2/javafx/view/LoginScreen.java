package Test2.javafx.view;

import Test2.Java.model.AdminUser;
import Test2.Java.model.TechnicianUser;
import Test2.Java.model.User;
import Test2.Java.util.FileUtil;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

public class LoginScreen {
    public void start(Stage stage) {
        // Insert logo
        ImageView logo = new ImageView(new Image("https://kulliyyah.iium.edu.my/kict/wp-content/uploads/sites/3/2024/01/iium-kict-logo-simple.png"));
        logo.setFitWidth(250);
        logo.setPreserveRatio(true);

        // Welcome text
        Label welcomeLabel = new Label("Welcome to KICT Inventory Management System");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
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
        message.setStyle("-fx-text-fill: red;");

        loginBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                User user = FileUtil.validateLogin(username, password);

                if (user != null) {
                    stage.close();
                    if (user.getRole().equals("admin")) {
                        new AdminDashboard(user).start(new Stage());
                    } else {
                        new TechnicianDashboard(user).start(new Stage());
                    }
                } else {
                    message.setText("Invalid username or password");
                }
            }
        });

        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                stage.close();
            }
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(15);
        grid.addRow(0, userLabel, usernameField);
        grid.addRow(1, passLabel, passwordField);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(exitBtn, loginBtn);
        grid.add(buttonBox, 1, 2);


        grid.add(message, 1, 3);

        VBox root = new VBox(25, logo, welcomeLabel, grid);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        stage.setScene(new Scene(root));
        stage.setTitle("Login - KICT Inventory System");
        stage.setWidth(400);
        stage.setHeight(400);
        stage.setMaximized(true);
        stage.show();
    }

    // Helper method to show alerts
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
