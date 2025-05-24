package Test1;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.io.*;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class FileUtil {
    public static String validateLogin(String user, String pass) {
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(user) && parts[1].equals(pass)) {
                    return parts[2];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<InventoryItem> loadInventory() {
        List<InventoryItem> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("inventory.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(InventoryItem.fromDataString(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveInventory(List<InventoryItem> items) {
        try (PrintWriter pw = new PrintWriter("inventory.txt")) {
            for (InventoryItem item : items) {
                pw.println(item.toDataString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addInventoryItem(Stage stage, ObservableList<InventoryItem> list) {
        final Stage form = new Stage();
        final GridPane pane = new GridPane();

        final TextField id = new TextField();
        final TextField name = new TextField();
        final TextArea desc = new TextArea();
        Spinner<Integer> quantity = new Spinner<>(0, Integer.MAX_VALUE, 1);
        desc.setPrefRowCount(3);

        final Button save = new Button("Save");
        pane.setVgap(10);
        pane.setHgap(10);
        pane.addRow(0, new Label("ID:"), id);
        pane.addRow(1, new Label("Name:"), name);
        pane.addRow(2, new Label("Description:"), desc);
        pane.addRow(3, new Label("Quantity:"), quantity);
        pane.add(save, 1, 4);

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                InventoryItem item = new InventoryItem(
                        id.getText(),
                        name.getText(),
                        desc.getText(),
                        quantity.getValue()
                );
                list.add(item);
                saveInventory(list);
                form.close();
            }
        });

        form.setScene(new Scene(pane, 700, 300));
        form.setTitle("Add Inventory");
        form.show();
    }

    public static void updateInventoryItem(Stage stage, InventoryItem item, ObservableList<InventoryItem> list) {
        if (item == null) {
            return;
        }

        Stage form = new Stage();
        GridPane pane = new GridPane();


        String[] parts = item.toData().split(",");
        final TextField id = new TextField(parts[0]);
        final TextField name = new TextField(parts[1]);
        final TextArea desc = new TextArea(parts[2]);
        Spinner<Integer> quantity = new Spinner<>(0, Integer.MAX_VALUE, Integer.parseInt(parts[3]));
        desc.setPrefRowCount(3);

        final Button save = new Button("Update");
        pane.setVgap(10);
        pane.setHgap(10);
        pane.addRow(0, new Label("ID:"), id);
        pane.addRow(1, new Label("Name:"), name);
        pane.addRow(2, new Label("Description:"), desc);
        pane.addRow(3, new Label("Quantity:"), quantity);
        pane.add(save, 1, 4);

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                list.remove(item);
                list.add(new InventoryItem(
                        id.getText(),
                        name.getText(),
                        desc.getText(),
                        quantity.getValue()
                ));
                saveInventory(list);
                form.close();
            }
        });

        form.setScene(new Scene(pane, 700, 300));
        form.setTitle("Update Inventory");
        form.show();
    }

    // Remove the duplicate method and keep this one:
    public static void updateMaintenanceRecord(Stage stage, MaintenanceRecord record,
                                               ObservableList<MaintenanceRecord> records) {
        if (record == null) {
            return;
        }

        final Stage form = new Stage();
        final GridPane pane = new GridPane();

        // Use DatePicker instead of TextField for date
        final DatePicker datePicker = new DatePicker();
        try {
            datePicker.setValue(java.time.LocalDate.parse(record.toData().split(",")[1]));
        } catch (Exception e) {
            datePicker.setValue(java.time.LocalDate.now());
        }

        final TextField id = new TextField(record.toData().split(",")[0]);
        final TextArea remarks = new TextArea(record.toData().split(",")[2]);
        remarks.setPrefRowCount(3);

        final Button save = new Button("Update");

        pane.setVgap(10);
        pane.setHgap(10);
        pane.addRow(0, new Label("Item ID:"), id);
        pane.addRow(1, new Label("Date:"), datePicker);
        pane.addRow(2, new Label("Remarks:"), remarks);
        pane.add(save, 1, 3);

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                records.remove(record);
                records.add(new MaintenanceRecord(
                        id.getText(),
                        datePicker.getValue().toString(),
                        remarks.getText()
                ));
                saveMaintenance(records);
                form.close();
            }
        });

        // Styling
        pane.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 20;");
        save.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        id.setStyle("-fx-padding: 8px;");
        remarks.setStyle("-fx-padding: 8px;");

        form.setScene(new Scene(pane, 550, 250));
        form.setTitle("Update Maintenance Record");
        form.show();
    }

    public static List<MaintenanceRecord> loadMaintenance() {
        List<MaintenanceRecord> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("maintenance.txt"))) {
            String line;
            while ((line = br.readLine()) != null) list.add(MaintenanceRecord.fromData(line));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveMaintenance(List<MaintenanceRecord> records) {
        try (PrintWriter pw = new PrintWriter("maintenance.txt")) {
            for (MaintenanceRecord record : records) pw.println(record.toData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addMaintenanceRecord(Stage stage, ObservableList<MaintenanceRecord> records, ObservableList<InventoryItem> inventoryItems) {
        final Stage form = new Stage();
        final GridPane pane = new GridPane();

        // Create ComboBox for item selection
        final ComboBox<String> itemComboBox = new ComboBox<>();
        final TextField itemNameField = new TextField();
        itemNameField.setEditable(false);

        // Populate ComboBox with inventory item IDs
        for (InventoryItem item : inventoryItems) {
            itemComboBox.getItems().add(item.getId());
        }

        // Auto-fill item name when ID is selected
        itemComboBox.setOnAction(e -> {
            String selectedId = itemComboBox.getValue();
            for (InventoryItem item : inventoryItems) {
                if (item.getId().equals(selectedId)) {
                    itemNameField.setText(item.getName());
                    break;
                }
            }
        });

        final DatePicker datePicker = new DatePicker(java.time.LocalDate.now());
        TextArea remarks = new TextArea();
        remarks.setPrefRowCount(3);

        final Button save = new Button("Save");
        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("Item ID:"), itemComboBox);
        pane.addRow(1, new Label("Item Name:"), itemNameField);
        pane.addRow(2, new Label("Date:"), datePicker);
        pane.addRow(3, new Label("Remarks:"), remarks);
        pane.add(save, 1, 4);

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

                MaintenanceRecord record = new MaintenanceRecord(
                        itemComboBox.getValue(),
                        datePicker.getValue().toString(),
                        remarks.getText()
                );
                records.add(record);

                // Auto-schedule next maintenance (1 year later)
                MaintenanceRecord nextRecord = new MaintenanceRecord(
                        record.getItemId(),
                        datePicker.getValue().plusYears(1).toString(),
                        "Auto-scheduled yearly maintenance"
                );

                records.add(nextRecord);
                saveMaintenance(records);
                form.close();
            }
        });

        form.setScene(new Scene(pane, 750, 300));
        form.setTitle("Add Maintenance Record");
        form.show();
    }

    public static void updateMaintenanceRecord(Stage stage, MaintenanceRecord record,
                                               ObservableList<MaintenanceRecord> records,
                                               ObservableList<InventoryItem> inventoryItems) {
        if (record == null) {
            return;
        }

        final Stage form = new Stage();
        final GridPane pane = new GridPane();

        // Create ComboBox for item selection
        final ComboBox<String> itemComboBox = new ComboBox<>();
        final TextField itemNameField = new TextField();
        itemNameField.setEditable(false);

        // Populate ComboBox with inventory item IDs
        for (InventoryItem item : inventoryItems) {
            itemComboBox.getItems().add(item.getId());
        }

        // Set current value and find corresponding name
        itemComboBox.setValue(record.getItemId());
        for (InventoryItem item : inventoryItems) {
            if (item.getId().equals(record.getItemId())) {
                itemNameField.setText(item.getName());
                break;
            }
        }

        // Auto-fill item name when ID is selected
        itemComboBox.setOnAction(e -> {
            String selectedId = itemComboBox.getValue();
            for (InventoryItem item : inventoryItems) {
                if (item.getId().equals(selectedId)) {
                    itemNameField.setText(item.getName());
                    break;
                }
            }
        });

        final DatePicker datePicker = new DatePicker();
        try {
            datePicker.setValue(java.time.LocalDate.parse(record.getDate()));
        } catch (Exception e) {
            datePicker.setValue(java.time.LocalDate.now());
        }

        final TextArea remarks = new TextArea(record.getRemarks());
        remarks.setPrefRowCount(3);

        final Button save = new Button("Update");
        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("Item ID:"), itemComboBox);
        pane.addRow(1, new Label("Item Name:"), itemNameField);
        pane.addRow(2, new Label("Date:"), datePicker);
        pane.addRow(3, new Label("Remarks:"), remarks);
        pane.add(save, 1, 4);

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

                records.remove(record);
                records.add(new MaintenanceRecord(
                        itemComboBox.getValue(),
                        datePicker.getValue().toString(),
                        remarks.getText()
                ));
                saveMaintenance(records);
                form.close();
            }
        });

        form.setScene(new Scene(pane, 750, 300));
        form.setTitle("Update Maintenance Record");
        form.show();
    }

    public static boolean isInventoryDueForMaintenance(String itemId) {
        List<MaintenanceRecord> records = loadMaintenance();
        Optional<MaintenanceRecord> latestRecord = records.stream()
                .filter(r -> r.getItemId().equals(itemId))
                .max(Comparator.comparing(r -> LocalDate.parse(r.getDate())));

        return latestRecord.map(record -> {
            LocalDate dueDate = LocalDate.parse(record.getDate()).plusYears(1);
            return LocalDate.now().isAfter(dueDate) || LocalDate.now().equals(dueDate);
        }).orElse(false);
    }
}