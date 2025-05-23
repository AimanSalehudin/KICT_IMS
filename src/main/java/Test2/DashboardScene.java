package Test2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardScene {
    private Scene scene;

    public DashboardScene(Stage stage, Scene loginScene) {
        Button inventoryButton = new Button("Inventory Management");
        Button maintenanceButton = new Button("Maintenance Management");
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.setScene(loginScene));

        inventoryButton.setOnAction(e -> {
            InventoryScene inv = new InventoryScene(stage, this.getScene());
            stage.setScene(inv.getScene());
        });

        maintenanceButton.setOnAction(e -> {
            MaintenanceScene main = new MaintenanceScene(stage, this.getScene());
            stage.setScene(main.getScene());
        });

        VBox layout = new VBox(15, inventoryButton, maintenanceButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #eef2f7;");

        this.scene = new Scene(layout, 600, 400);
    }

    public Scene getScene() {
        return scene;
    }
}