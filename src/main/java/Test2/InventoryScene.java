package Test2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InventoryScene {
    private Scene scene;

    public InventoryScene(Stage stage, Scene dashboardScene) {
        Button viewButton = new Button("View Inventory");
        Button addButton = new Button("Add Inventory");
        Button updateButton = new Button("Update Inventory");
        Button deleteButton = new Button("Delete Inventory");
        Button backButton = new Button("Back");

        backButton.setOnAction(e -> stage.setScene(dashboardScene));

        VBox layout = new VBox(15, viewButton, addButton, updateButton, deleteButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f8f9fa;");

        this.scene = new Scene(layout, 600, 400);
    }

    public Scene getScene() {
        return scene;
    }
}

