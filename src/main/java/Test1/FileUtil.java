package Test1;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.io.*;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class FileUtil {
    private static final String INVENTORY_FILE = "inventory.txt";
    private static final String MAINTENANCE_FILE = "maintenance.txt";
    private static final String USERS_FILE = "users.txt";

    // Validate Users Login
    public static String validateLogin(String user, String pass) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            System.out.println("File user is being read.");
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(user) && parts[1].equals(pass)) {
                    return parts[2];
                }
            }
        } catch (IOException e) {
            System.out.println("Role not found...");
            e.printStackTrace();
        }
        return null;
    }

    // Load Inventory from File
    public static List<InventoryItem> loadInventory() {
        List<InventoryItem> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;
            System.out.println("File inventory is being read.");
            while ((line = br.readLine()) != null) {
                list.add(InventoryItem.fromDataString(line));
            }
        } catch (IOException e) {
            System.out.println("File not found...");
            e.printStackTrace();
        }
        return list;
    }

    // Save Inventory to File
    public static void saveInventory(List<InventoryItem> items) {
        try (PrintWriter pw = new PrintWriter(INVENTORY_FILE)) {
            for (InventoryItem item : items) {
                pw.println(item.toDataString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add Inventory Item
    public static void addInventoryItem(Stage stage, ObservableList<InventoryItem> list) {
        Stage form = new Stage();
        GridPane pane = new GridPane();

        TextField id = new TextField();
        TextField name = new TextField();
        TextArea desc = new TextArea();
        Spinner<Integer> quantity = new Spinner<>(0, Integer.MAX_VALUE, 1);
        desc.setPrefRowCount(3);

        Button save = new Button("Save");
        Button cancel = new Button("Cancel");

        HBox btnBox = new HBox(5);
        btnBox.getChildren().addAll(save, cancel);
        btnBox.setAlignment(Pos.TOP_LEFT);
        btnBox.setPadding(new Insets(10));

        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("ID:"), id);
        pane.addRow(1, new Label("Name:"), name);
        pane.addRow(2, new Label("Description:"), desc);
        pane.addRow(3, new Label("Quantity:"), quantity);
        pane.add(btnBox, 1, 4);

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                InventoryItem item = new InventoryItem(id.getText(), name.getText(), desc.getText(), quantity.getValue());
                list.add(item);
                saveInventory(list);
                form.close();
            }
        });
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                form.close();
            }
        });

        form.setScene(new Scene(pane, 750, 300));
        form.setTitle("Add Inventory");
        form.show();
    }

    // Update Inventory Item
    public static void updateInventoryItem(Stage stage, InventoryItem item, ObservableList<InventoryItem> list) {
        if (item == null) return;

        Stage form = new Stage();
        GridPane pane = new GridPane();

        TextField id = new TextField(item.getId());
        TextField name = new TextField(item.getName());
        TextArea desc = new TextArea(item.getDescription());
        Spinner<Integer> quantity = new Spinner<>(0, Integer.MAX_VALUE, item.getQuantity());
        desc.setPrefRowCount(3);

        Button save = new Button("Update");
        Button cancel = new Button("Cancel");

        HBox btnBox = new HBox(5);
        btnBox.getChildren().addAll(save, cancel);
        btnBox.setAlignment(Pos.TOP_LEFT);
        btnBox.setPadding(new Insets(10));

        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("ID:"), id);
        pane.addRow(1, new Label("Name:"), name);
        pane.addRow(2, new Label("Description:"), desc);
        pane.addRow(3, new Label("Quantity:"), quantity);
        pane.add(btnBox, 1, 4);

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                list.remove(item);
                list.add(new InventoryItem(id.getText(), name.getText(), desc.getText(), quantity.getValue()));
                saveInventory(list);
                form.close();
            }
        });
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                form.close();
            }
        });

        form.setScene(new Scene(pane, 750, 300));
        form.setTitle("Update Inventory");
        form.show();
    }

    // Load Maintenance from File
    public static List<MaintenanceRecord> loadMaintenance() {
        List<MaintenanceRecord> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MAINTENANCE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(MaintenanceRecord.fromData(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Save Maintenance to File
    public static void saveMaintenance(List<MaintenanceRecord> records) {
        try (PrintWriter pw = new PrintWriter(MAINTENANCE_FILE)) {
            for (MaintenanceRecord record : records) {
                pw.println(record.toData());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add Maintenance Record
    public static void addMaintenanceRecord(Stage stage, ObservableList<MaintenanceRecord> records, ObservableList<InventoryItem> inventoryItems) {
        Stage form = new Stage();
        GridPane pane = new GridPane();

        ComboBox<String> itemComboBox = new ComboBox<>();
        for (InventoryItem item : inventoryItems) {
            itemComboBox.getItems().add(item.getId());
        }

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextArea remarks = new TextArea();
        remarks.setPrefRowCount(3);

        Button save = new Button("Save");
        Button cancel = new Button("Cancel");

        HBox btnBox = new HBox(5);
        btnBox.getChildren().addAll(save, cancel);
        btnBox.setAlignment(Pos.TOP_LEFT);
        btnBox.setPadding(new Insets(10));

        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("Item ID:"), itemComboBox);
        pane.addRow(1, new Label("Date:"), datePicker);
        pane.addRow(2, new Label("Remarks:"), remarks);
        pane.add(btnBox, 1, 3);

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (itemComboBox.getValue() == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Selection Required");
                    alert.setHeaderText(null);
                    alert.setContentText("Please select an item ID");
                    alert.showAndWait();
                    return;
                }

                MaintenanceRecord record = new MaintenanceRecord(itemComboBox.getValue(), datePicker.getValue().toString(), remarks.getText());
                records.add(record);
                saveMaintenance(records);
                form.close();
            }
        });
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                form.close();
            }
        });

        form.setScene(new Scene(pane, 750, 300));
        form.setTitle("Add Maintenance Record");
        form.show();
    }

    // Update Maintenance record
    public static void updateMaintenanceRecord(Stage stage, MaintenanceRecord record, ObservableList<MaintenanceRecord> list) {
        if (record == null) return;

        Stage form = new Stage();
        GridPane pane = new GridPane();

        TextField itemIdField = new TextField(record.getItemId());
        itemIdField.setEditable(false);
        DatePicker datePicker = new DatePicker(LocalDate.parse(record.getDate()));
        TextArea remarks = new TextArea(record.getRemarks());
        remarks.setPrefRowCount(3);

        Button save = new Button("Update");
        Button cancel = new Button("Cancel");

        HBox btnBox = new HBox(5);
        btnBox.getChildren().addAll(save, cancel);
        btnBox.setAlignment(Pos.TOP_LEFT);
        btnBox.setPadding(new Insets(10));

        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("Item ID:"), itemIdField);
        pane.addRow(1, new Label("Date:"), datePicker);
        pane.addRow(2, new Label("Remarks:"), remarks);
        pane.add(btnBox, 1, 3);

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                list.remove(record);
                list.add(new MaintenanceRecord(itemIdField.getText(), datePicker.getValue().toString(), remarks.getText()));
                saveMaintenance(list);
                form.close();
            }
        });
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                form.close();
            }
        });

        form.setScene(new Scene(pane, 750, 300));
        form.setTitle("Update Maintenance Record");
        form.show();
    }

    // Check if Inventory is due
    public static boolean isInventoryDueForMaintenance(String itemId) {
        List<MaintenanceRecord> records = loadMaintenance();
        MaintenanceRecord latestRecord = null;

        for (MaintenanceRecord record : records) {
            if (record.getItemId().equals(itemId)) {
                if (latestRecord == null || LocalDate.parse(record.getDate()).isAfter(LocalDate.parse(latestRecord.getDate()))) {
                    latestRecord = record;
                }
            }
        }
        if (latestRecord != null) {
            LocalDate dueDate = LocalDate.parse(latestRecord.getDate()).plusYears(1);
            return LocalDate.now().isAfter(dueDate) || LocalDate.now().equals(dueDate);
        }
        return false;
    }
}