package Test1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


public class AdminDashboard {
    ObservableList<InventoryItem> items = FXCollections.observableArrayList(FileUtil.loadInventory());

    public void start(Stage stage) {
        ListView<InventoryItem> listView = new ListView<>(items);
        listView.setCellFactory(lv -> new ListCell<InventoryItem>() {
            @Override
            protected void updateItem(InventoryItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    boolean isOverdue = FileUtil.isInventoryDueForMaintenance(item.getId());
                    setText(String.format("%s: %s\nDescription: %s\nQuantity: %d",
                            item.getId(), item.getName(),
                            item.getDescription(), item.getQuantity()));

                    // Apply red background if overdue
                    if (isOverdue) {
                        setStyle("-fx-background-color: #ffcccc; -fx-border-color: #ff0000;"); // Light red with red border
                    } else {
                        setStyle(""); // Reset to default
                    }
                }
            }
        });

        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        Button backBtn = new Button("Logout");

        // Styling buttons
        String buttonStyle = "-fx-background-color: #2a9df4; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;";
        addBtn.setStyle(buttonStyle);
        updateBtn.setStyle(buttonStyle);
        deleteBtn.setStyle(buttonStyle);
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");

        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                FileUtil.addInventoryItem(stage, items);
            }
        });
        
        // In AdminDashboard.java - update the updateBtn action handler
        updateBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                InventoryItem selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    FileUtil.updateInventoryItem(stage, selected, items);
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
                InventoryItem item = listView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirm Deletion");
                    confirm.setHeaderText("Delete Item: " + item.getName());
                    confirm.setContentText("Are you sure you want to delete this item?");

                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        items.remove(item);
                        FileUtil.saveInventory(items);
                    }
                }
            }
        });
        
        backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                new LoginScreen().start(stage);
            }
        });

        HBox buttonBox = new HBox(10, addBtn, updateBtn, deleteBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15, listView, buttonBox, backBtn);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #f5f5f5;");

        stage.setScene(new Scene(box, 450, 450));
        stage.setTitle("Admin Dashboard");
        stage.show();
    }
}