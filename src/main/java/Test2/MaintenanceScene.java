package Test2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MaintenanceScene {
    private Scene scene;

    public MaintenanceScene(Stage stage, Scene dashboardScene) {
        Button viewButton = new Button("View Maintenance");
        Button addButton = new Button("Add Maintenance");
        Button updateButton = new Button("Update Maintenance");
        Button deleteButton = new Button("Delete Maintenance");
        Button backButton = new Button("Back");

        backButton.setOnAction(e -> stage.setScene(dashboardScene));

        VBox layout = new VBox(15, viewButton, addButton, updateButton, deleteButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #fdfdfd;");

        this.scene = new Scene(layout, 600, 400);
    }

    public Scene getScene() {
        return scene;
    }
}
