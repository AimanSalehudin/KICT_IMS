package Test1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.Optional;

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
        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        Button backBtn = new Button("Logout");

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
                    FileUtil.updateMaintenanceRecord(stage, selected, records);
                } else {
                    // Show alert if no item is selected
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("No Selection");
                    alert.setHeaderText(null);
                    alert.setContentText("Please select an item to update");
                    alert.showAndWait();
                }
            }
        });
        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                MaintenanceRecord record = listView.getSelectionModel().getSelectedItem();
                if (record != null) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirm Deletion");
                    confirm.setHeaderText("Delete Item: " + record.getItemId());
                    confirm.setContentText("Are you sure you want to delete this item?");

                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        records.remove(record);
                        FileUtil.saveMaintenance(records);
                    }
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
        HBox buttonBox = new HBox(10, addBtn, updateBtn, deleteBtn);
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
