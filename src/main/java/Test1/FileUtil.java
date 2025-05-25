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
        Stage form = new Stage();
        GridPane pane = new GridPane();

        TextField id = new TextField();
        TextField name = new TextField();
        TextArea desc = new TextArea();
        Spinner<Integer> quantity = new Spinner<>(0, Integer.MAX_VALUE, 1);
        desc.setPrefRowCount(3);

        Button save = new Button("Save");
        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("ID:"), id);
        pane.addRow(1, new Label("Name:"), name);
        pane.addRow(2, new Label("Description:"), desc);
        pane.addRow(3, new Label("Quantity:"), quantity);
        pane.add(save, 1, 4);

        save.setOnAction(e -> {
            InventoryItem item = new InventoryItem(id.getText(), name.getText(), desc.getText(), quantity.getValue());
            list.add(item);
            saveInventory(list);
            form.close();
        });

        form.setScene(new Scene(pane, 750, 300));
        form.setTitle("Add Inventory");
        form.show();
    }

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
        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("ID:"), id);
        pane.addRow(1, new Label("Name:"), name);
        pane.addRow(2, new Label("Description:"), desc);
        pane.addRow(3, new Label("Quantity:"), quantity);
        pane.add(save, 1, 4);

        save.setOnAction(e -> {
            list.remove(item);
            list.add(new InventoryItem(id.getText(), name.getText(), desc.getText(), quantity.getValue()));
            saveInventory(list);
            form.close();
        });

        form.setScene(new Scene(pane, 750, 300));
        form.setTitle("Update Inventory");
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
        Stage form = new Stage();
        GridPane pane = new GridPane();

        ComboBox<String> itemComboBox = new ComboBox<>();
        TextField itemNameField = new TextField();
        itemNameField.setEditable(false);

        for (InventoryItem item : inventoryItems) itemComboBox.getItems().add(item.getId());

        itemComboBox.setOnAction(e -> {
            String selectedId = itemComboBox.getValue();
            for (InventoryItem item : inventoryItems) {
                if (item.getId().equals(selectedId)) {
                    itemNameField.setText(item.getName());
                    break;
                }
            }
        });

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextArea remarks = new TextArea();
        remarks.setPrefRowCount(3);

        Button save = new Button("Save");
        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("Item ID:"), itemComboBox);
        pane.addRow(1, new Label("Item Name:"), itemNameField);
        pane.addRow(2, new Label("Date:"), datePicker);
        pane.addRow(3, new Label("Remarks:"), remarks);
        pane.add(save, 1, 4);

        save.setOnAction(e -> {
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
        });

        form.setScene(new Scene(pane, 750, 300));
        form.setTitle("Add Maintenance Record");
        form.show();
    }

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
