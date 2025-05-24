package Test1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TechnicianDashboard {
    ObservableList<MaintenanceRecord> records = FXCollections.observableArrayList(FileUtil.loadMaintenance());
    ObservableList<InventoryItem> inventoryItems = FXCollections.observableArrayList(FileUtil.loadInventory());

    public void start(Stage stage) {
        ListView<MaintenanceRecord> listView = new ListView<>(records);
        listView.setCellFactory(lv -> new ListCell<MaintenanceRecord>() {
            @Override
            protected void updateItem(MaintenanceRecord item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString() + "\nRemarks: " + item.getRemarks());

                    if (FileUtil.isInventoryDueForMaintenance(item.getItemId())) {
                        setStyle("-fx-background-color: #ffcccc; -fx-border-color: #ff0000;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        Button addBtn = new Button("Add Maintenance");
        Button updateBtn = new Button("Update Maintenance");
        Button backBtn = new Button("Logout");

        // Styling buttons
        String buttonStyle = "-fx-background-color: #2a9df4; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;";
        addBtn.setStyle(buttonStyle);
        updateBtn.setStyle(buttonStyle);
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");

        addBtn.setOnAction(e -> FileUtil.addMaintenanceRecord(stage, records, inventoryItems));

        updateBtn.setOnAction(e -> {
            MaintenanceRecord selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                FileUtil.updateMaintenanceRecord(stage, selected, records, inventoryItems);
            }
        });
        backBtn.setOnAction(e -> new LoginScreen().start(stage));

        HBox buttonBox = new HBox(10, addBtn, updateBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15, listView, buttonBox, backBtn);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #f5f5f5;");

        stage.setScene(new Scene(box, 500, 500)); // Larger size for better display
        stage.setTitle("Technician Dashboard");
        stage.show();
    }
}
