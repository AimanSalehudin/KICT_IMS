package Test2.Java.util;

import Test2.Java.model.*;
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

public class FileUtil {
    private static final String INVENTORY_FILE = "inventory2.txt";
    private static final String MAINTENANCE_FILE = "maintenance2.txt";
    private static final String USERS_FILE = "users2.txt";

    // Validate Users Login
    public static User validateLogin(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                User user = User.fromDataString(line);
                if (user != null && user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    return user;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Load Inventory from File
    public static List<InventoryItem> loadInventory() {
        List<InventoryItem> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                InventoryItem item = InventoryItem.fromDataString(line);
                if (item != null) {
                    list.add(item);
                }
            }
        } catch (IOException e) {
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
        form.initOwner(stage);
        GridPane pane = new GridPane();

        // Common fields
        TextField id = new TextField();
        TextField name = new TextField();
        TextArea desc = new TextArea();
        Spinner<Integer> quantity = new Spinner<>(0, Integer.MAX_VALUE, 1);
        desc.setPrefRowCount(3);

        // Item type selection
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("General", "Electrical", "Furniture");
        typeCombo.setValue("General");

        // Type-specific fields
        GridPane electricalFields = createElectricalFields();
        GridPane furnitureFields = createFurnitureFields();

        // Show/hide fields based on type selection
        typeCombo.setOnAction(e -> {
            electricalFields.setVisible("Electrical".equals(typeCombo.getValue()));
            furnitureFields.setVisible("Furniture".equals(typeCombo.getValue()));
        });

        Button save = new Button("Save");
        Button cancel = new Button("Cancel");

        HBox btnBox = new HBox(10, save, cancel);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10));

        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("Type:"), typeCombo);
        pane.addRow(1, new Label("ID:"), id);
        pane.addRow(2, new Label("Name:"), name);
        pane.addRow(3, new Label("Description:"), desc);
        pane.addRow(4, new Label("Quantity:"), quantity);
        pane.add(electricalFields, 0, 5, 2, 1);
        pane.add(furnitureFields, 0, 5, 2, 1);
        pane.add(btnBox, 1, 6);

        save.setOnAction(e -> {
            InventoryItem item = createItemBasedOnType(
                    typeCombo.getValue(),
                    id.getText(),
                    name.getText(),
                    desc.getText(),
                    quantity.getValue(),
                    electricalFields,
                    furnitureFields
            );

            if (item != null) {
                list.add(item);
                saveInventory(list);
                form.close();
            }
        });

        cancel.setOnAction(e -> form.close());

        Scene scene = new Scene(pane, 600, 400);
        form.setScene(scene);
        form.setTitle("Add Inventory Item");
        form.showAndWait();
    }

    private static GridPane createElectricalFields() {
        GridPane pane = new GridPane();
        TextField voltage = new TextField();
        TextField powerSource = new TextField();
        pane.addRow(0, new Label("Voltage:"), voltage);
        pane.addRow(1, new Label("Power Source:"), powerSource);
        pane.setVgap(5);
        pane.setHgap(5);
        pane.setVisible(false);
        return pane;
    }

    private static GridPane createFurnitureFields() {
        GridPane pane = new GridPane();
        TextField material = new TextField();
        TextField dimensions = new TextField();
        pane.addRow(0, new Label("Material:"), material);
        pane.addRow(1, new Label("Dimensions:"), dimensions);
        pane.setVgap(5);
        pane.setHgap(5);
        pane.setVisible(false);
        return pane;
    }

    private static InventoryItem createItemBasedOnType(String type, String id, String name,
                                                       String desc, int quantity, GridPane electricalFields, GridPane furnitureFields) {
        switch (type) {
            case "Electrical":
                ElectricalItem elecItem = new ElectricalItem(id, name, desc, quantity);
                TextField voltage = (TextField) electricalFields.getChildren().get(1);
                TextField powerSource = (TextField) electricalFields.getChildren().get(3);
                elecItem.setVoltage(voltage.getText());
                elecItem.setPowerSource(powerSource.getText());
                return elecItem;
            case "Furniture":
                FurnitureItem furnItem = new FurnitureItem(id, name, desc, quantity);
                TextField material = (TextField) furnitureFields.getChildren().get(1);
                TextField dimensions = (TextField) furnitureFields.getChildren().get(3);
                furnItem.setMaterial(material.getText());
                furnItem.setDimensions(dimensions.getText());
                return furnItem;
            default:
                return new GeneralItem(id, name, desc, quantity);
        }
    }

    // Update Inventory Item
    public static void updateInventoryItem(Stage stage, InventoryItem item, ObservableList<InventoryItem> list) {
        if (item == null) return;

        Stage form = new Stage();
        form.initOwner(stage);
        GridPane pane = new GridPane();

        // Common fields
        TextField id = new TextField(item.getId());
        TextField name = new TextField(item.getName());
        TextArea desc = new TextArea(item.getDescription());
        Spinner<Integer> quantity = new Spinner<>(0, Integer.MAX_VALUE, item.getQuantity());
        desc.setPrefRowCount(3);

        // Different item, different specs
        GridPane specificFields = new GridPane();
        if (item instanceof ElectricalItem) {
            ElectricalItem elecItem = (ElectricalItem) item;
            TextField voltage = new TextField(elecItem.getVoltage());
            TextField powerSource = new TextField(elecItem.getPowerSource());
            specificFields.addRow(0, new Label("Voltage:"), voltage);
            specificFields.addRow(1, new Label("Power Source:"), powerSource);
        } else if (item instanceof FurnitureItem) {
            FurnitureItem furnItem = (FurnitureItem) item;
            TextField material = new TextField(furnItem.getMaterial());
            TextField dimensions = new TextField(furnItem.getDimensions());
            specificFields.addRow(0, new Label("Material:"), material);
            specificFields.addRow(1, new Label("Dimensions:"), dimensions);
        }

        Button save = new Button("Update");
        Button cancel = new Button("Cancel");

        HBox btnBox = new HBox(10, save, cancel);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10));

        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("ID:"), id);
        pane.addRow(1, new Label("Name:"), name);
        pane.addRow(2, new Label("Description:"), desc);
        pane.addRow(3, new Label("Quantity:"), quantity);
        pane.add(specificFields, 0, 4, 2, 1);
        pane.add(btnBox, 1, 5);

        save.setOnAction(e -> {
            list.remove(item);
            InventoryItem updatedItem = createUpdatedItem(item, id.getText(), name.getText(),
                    desc.getText(), quantity.getValue(), specificFields);

            if (updatedItem != null) {
                list.add(updatedItem);
                saveInventory(list);
                form.close();
            }
        });

        cancel.setOnAction(e -> form.close());

        Scene scene = new Scene(pane, 600, 400);
        form.setScene(scene);
        form.setTitle("Update Inventory Item");
        form.showAndWait();
    }

    private static InventoryItem createUpdatedItem(InventoryItem original, String id, String name,
                                                   String desc, int quantity, GridPane specificFields) {
        if (original instanceof ElectricalItem) {
            ElectricalItem item = new ElectricalItem(id, name, desc, quantity);
            TextField voltage = (TextField) specificFields.getChildren().get(1);
            TextField powerSource = (TextField) specificFields.getChildren().get(3);
            item.setVoltage(voltage.getText());
            item.setPowerSource(powerSource.getText());
            return item;
        } else if (original instanceof FurnitureItem) {
            FurnitureItem item = new FurnitureItem(id, name, desc, quantity);
            TextField material = (TextField) specificFields.getChildren().get(1);
            TextField dimensions = (TextField) specificFields.getChildren().get(3);
            item.setMaterial(material.getText());
            item.setDimensions(dimensions.getText());
            return item;
        } else {
            return new GeneralItem(id, name, desc, quantity);
        }
    }

    // Load Maintenance from File
    public static List<MaintenanceRecord> loadMaintenance() {
        List<MaintenanceRecord> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MAINTENANCE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                MaintenanceRecord record = MaintenanceRecord.fromData(line);
                if (record != null) {
                    list.add(record);
                }
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
    public static void addMaintenanceRecord(Stage stage, ObservableList<MaintenanceRecord> records,
                                            ObservableList<InventoryItem> inventoryItems) {
        Stage form = new Stage();
        form.initOwner(stage);
        GridPane pane = new GridPane();

        ComboBox<String> itemComboBox = new ComboBox<>();
        for (InventoryItem item : inventoryItems) {
            itemComboBox.getItems().add(item.getId());
        }

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Routine", "Repair", "Inspection", "General");
        typeCombo.setValue("General");

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextArea remarks = new TextArea();
        remarks.setPrefRowCount(3);

        // Type-specific fields
        GridPane specificFields = new GridPane();
        specificFields.setVgap(5);
        specificFields.setHgap(5);

        // to show or hide fields based on type selection
        typeCombo.setOnAction(e -> updateMaintenanceFields(typeCombo.getValue(), specificFields));

        Button save = new Button("Save");
        Button cancel = new Button("Cancel");

        HBox btnBox = new HBox(10, save, cancel);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10));

        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("Item ID:"), itemComboBox);
        pane.addRow(1, new Label("Type:"), typeCombo);
        pane.addRow(2, new Label("Date:"), datePicker);
        pane.addRow(3, new Label("Remarks:"), remarks);
        pane.add(specificFields, 0, 4, 2, 1);
        pane.add(btnBox, 1, 5);

        save.setOnAction(e -> {
            if (itemComboBox.getValue() == null) {
                showAlert("Selection Required", "Please select an item ID");
                return;
            }

            MaintenanceRecord record = createMaintenanceRecord(
                    itemComboBox.getValue(),
                    typeCombo.getValue(),
                    datePicker.getValue(),
                    remarks.getText(),
                    specificFields
            );

            if (record != null) {
                records.add(record);
                saveMaintenance(records);
                form.close();
            }
        });

        cancel.setOnAction(e -> form.close());

        Scene scene = new Scene(pane, 600, 400);
        form.setScene(scene);
        form.setTitle("Add Maintenance Record");
        form.showAndWait();
    }

    private static void updateMaintenanceFields(String type, GridPane pane) {
        pane.getChildren().clear();

        switch (type) {
            case "Repair":
                TextField severity = new TextField();
                pane.addRow(0, new Label("Severity:"), severity);
                break;
            case "Inspection":
                TextField inspector = new TextField();
                pane.addRow(0, new Label("Inspector:"), inspector);
                break;
        }
    }

    private static MaintenanceRecord createMaintenanceRecord(String itemId, String type,
                                                             LocalDate date, String remarks, GridPane specificFields) {
        switch (type) {
            case "Routine":
                return new RoutineMaintenance(itemId, date, remarks);
            case "Repair":
                RepairMaintenance repair = new RepairMaintenance(itemId, date, remarks);
                TextField severity = (TextField) specificFields.getChildren().get(1);
                repair.setSeverity(severity.getText());
                return repair;
            case "Inspection":
                InspectionMaintenance inspection = new InspectionMaintenance(itemId, date, remarks);
                TextField inspector = (TextField) specificFields.getChildren().get(1);
                inspection.setInspector(inspector.getText());
                return inspection;
            default:
                return new GeneralMaintenance(itemId, date, remarks);
        }
    }

    // Update Maintenance record
    public static void updateMaintenanceRecord(Stage stage, MaintenanceRecord record,
                                               ObservableList<MaintenanceRecord> list) {
        if (record == null) return;

        Stage form = new Stage();
        form.initOwner(stage);
        GridPane pane = new GridPane();

        TextField itemIdField = new TextField(record.getItemId());
        itemIdField.setEditable(false);

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Routine", "Repair", "Inspection", "General");
        typeCombo.setValue(record.getMaintenanceType());

        DatePicker datePicker = new DatePicker(record.getDate());
        TextArea remarks = new TextArea(record.getRemarks());
        remarks.setPrefRowCount(3);

        // Type-specific fields
        GridPane specificFields = new GridPane();
        specificFields.setVgap(5);
        specificFields.setHgap(5);

        // Initialize fields based on record type
        if (record instanceof RepairMaintenance) {
            RepairMaintenance repair = (RepairMaintenance) record;
            TextField severity = new TextField(repair.getSeverity());
            specificFields.addRow(0, new Label("Severity:"), severity);
        } else if (record instanceof InspectionMaintenance) {
            InspectionMaintenance inspection = (InspectionMaintenance) record;
            TextField inspector = new TextField(inspection.getInspector());
            specificFields.addRow(0, new Label("Inspector:"), inspector);
        }

        // Update fields when type changes
        typeCombo.setOnAction(e -> updateMaintenanceFields(typeCombo.getValue(), specificFields));

        Button save = new Button("Update");
        Button cancel = new Button("Cancel");

        HBox btnBox = new HBox(10, save, cancel);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10));

        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("Item ID:"), itemIdField);
        pane.addRow(1, new Label("Type:"), typeCombo);
        pane.addRow(2, new Label("Date:"), datePicker);
        pane.addRow(3, new Label("Remarks:"), remarks);
        pane.add(specificFields, 0, 4, 2, 1);
        pane.add(btnBox, 1, 5);

        save.setOnAction(e -> {
            list.remove(record);
            MaintenanceRecord updatedRecord = createMaintenanceRecord(
                    itemIdField.getText(),
                    typeCombo.getValue(),
                    datePicker.getValue(),
                    remarks.getText(),
                    specificFields
            );

            if (updatedRecord != null) {
                list.add(updatedRecord);
                saveMaintenance(list);
                form.close();
            }
        });

        cancel.setOnAction(e -> form.close());

        Scene scene = new Scene(pane, 600, 400);
        form.setScene(scene);
        form.setTitle("Update Maintenance Record");
        form.showAndWait();
    }

// Check if Inventory is due for maintenance
// Check if Inventory is due for maintenance
public static boolean isInventoryDueForMaintenance(String itemId) {
    List<MaintenanceRecord> records = loadMaintenance();
    MaintenanceRecord latestRecord = null;

    // Find the latest maintenance record for this item
    for (MaintenanceRecord record : records) {
        if (record.getItemId().equals(itemId)) {
            if (latestRecord == null || record.getDate().isAfter(latestRecord.getDate())) {
                latestRecord = record;
            }
        }
    }

    // If no maintenance record exists, it's overdue
    if (latestRecord == null) {
        return true;
    }

    // Check if maintenance is due based on the type of maintenance
    LocalDate nextDueDate = calculateNextDueDate(latestRecord);
    return LocalDate.now().isAfter(nextDueDate) || LocalDate.now().equals(nextDueDate);
}

    private static LocalDate calculateNextDueDate(MaintenanceRecord record) {
        switch (record.getMaintenanceType()) {
            case "Routine":
                // Routine maintenance every 6 months
                return record.getDate().plusMonths(6);
            case "Inspection":
                // Inspection every year
                return record.getDate().plusYears(1);
            case "Repair":
                // Repairs should be followed up in 3 months
                return record.getDate().plusMonths(3);
            default:
                // General maintenance every year
                return record.getDate().plusYears(1);
        }
    }

    // Load users from file
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                User user = User.fromDataString(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
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