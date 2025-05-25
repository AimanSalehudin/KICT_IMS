package Test1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class TechnicianDashboard {

    private ObservableList<MaintenanceRecord> records;
    private ObservableList<InventoryItem> inventoryItems;

    public void start(Stage stage) {
        // Load data
        records = FXCollections.observableArrayList(FileUtil.loadMaintenance());
        inventoryItems = FXCollections.observableArrayList(FileUtil.loadInventory());

        // Create ListView
        ListView<MaintenanceRecord> listView = new ListView<>(records);

        // Create Buttons
        Button addBtn = new Button("Add Maintenance");
        Button updateBtn = new Button("Update Maintenance");
        Button backBtn = new Button("Logout");

        addBtn.setPrefWidth(150);
        updateBtn.setPrefWidth(150);
        backBtn.setPrefWidth(150);

        // Event handling using anonymous inner classes
        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileUtil.addMaintenanceRecord(stage, records, inventoryItems);
            }
        });

        updateBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MaintenanceRecord selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    FileUtil.addMaintenanceRecord(stage, records, inventoryItems);

                }
            }
        });

        backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LoginScreen login = new LoginScreen();
                login.start(stage);
            }
        });

        // Layout
        HBox buttonBox = new HBox(10, addBtn, updateBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(listView, buttonBox, backBtn);

        // Set Scene and Stage
        Scene scene = new Scene(root, 400, 500);
        stage.setTitle("Technician Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
