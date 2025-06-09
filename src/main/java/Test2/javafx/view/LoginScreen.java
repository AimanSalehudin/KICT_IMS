package Test2.javafx.view;

import Test2.Java.model.AdminUser;
import Test2.Java.model.GeneralUser;
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
        grid.addRow(2, loginBtn, exitBtn);
        grid.add(message, 1, 3);

        VBox root = new VBox(15, logo, welcomeLabel, grid);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        stage.setScene(new Scene(root));
        stage.setTitle("Login - KICT Inventory System");
        stage.setWidth(400);
        stage.setHeight(400);
        stage.show();
    }
    // Add new user
    public static void addUser(Stage stage, ObservableList<User> users) {
        Stage form = new Stage();
        form.initOwner(stage);
        GridPane pane = new GridPane();

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("admin", "technician", "general");
        roleCombo.setValue("general");

        TextField username = new TextField();
        PasswordField password = new PasswordField();
        TextField name = new TextField();
        TextField email = new TextField();

        // Technician-specific fields
        GridPane techFields = new GridPane();
        TextField specialization = new TextField();
        techFields.addRow(0, new Label("Specialization:"), specialization);
        techFields.setVisible(false);

        roleCombo.setOnAction(e -> {
            techFields.setVisible("technician".equals(roleCombo.getValue()));
        });

        Button save = new Button("Save");
        Button cancel = new Button("Cancel");

        HBox btnBox = new HBox(10, save, cancel);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10));

        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("Role:"), roleCombo);
        pane.addRow(1, new Label("Username:"), username);
        pane.addRow(2, new Label("Password:"), password);
        pane.addRow(3, new Label("Name:"), name);
        pane.addRow(4, new Label("Email:"), email);
        pane.add(techFields, 0, 5, 2, 1);
        pane.add(btnBox, 1, 6);

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                User user = createUserBasedOnRole(
                        roleCombo.getValue(),
                        username.getText(),
                        password.getText(),
                        name.getText(),
                        email.getText(),
                        specialization.getText()
                );

                if (user != null) {
                    users.add(user);
                    FileUtil.saveUsers(users);
                    form.close();
                }
            }
        });

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                form.close();
            }
        });

        Scene scene = new Scene(pane, 500, 350);
        form.setScene(scene);
        form.setTitle("Add New User");
        form.showAndWait();
    }

    private static User createUserBasedOnRole(String role, String username, String password,
                                              String name, String email, String specialization) {
        switch (role) {
            case "admin":
                return new AdminUser(username, password, name, email);
            case "technician":
                TechnicianUser techUser = new TechnicianUser(username, password, name, email);
                techUser.setSpecialization(specialization);
                return techUser;
            default:
                return new GeneralUser(username, password, name, email);
        }
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
