package Test2;

import javafx.scene.control.TextInputDialog;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileUtil {
    private static final String USERS_FILE = "users.txt";
    private static final String INVENTORY_FILE = "inventory.txt";
    private static final String MAINTENANCE_FILE = "maintenance.txt";

    public static String[] validateUser(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    return parts;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<InventoryItem> readInventory() {
        List<InventoryItem> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(InventoryItem.fromString(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void addInventory(InventoryItem item) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(INVENTORY_FILE, true))) {
            bw.write(item.toString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateInventory(InventoryItem oldItem, InventoryItem newItem) {
        List<InventoryItem> list = readInventory();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(INVENTORY_FILE))) {
            for (InventoryItem item : list) {
                if (item.toString().equals(oldItem.toString())) {
                    bw.write(newItem.toString());
                } else {
                    bw.write(item.toString());
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteInventory(InventoryItem item) {
        List<InventoryItem> list = readInventory();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(INVENTORY_FILE))) {
            for (InventoryItem i : list) {
                if (!i.toString().equals(item.toString())) {
                    bw.write(i.toString());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static InventoryItem showInventoryForm(InventoryItem current) {
        TextInputDialog dialog = new TextInputDialog(current != null ? current.id : "");
        dialog.setHeaderText("Enter Item ID:");
        Optional<String> id = dialog.showAndWait();
        if (id.isEmpty()) return null;

        dialog = new TextInputDialog(current != null ? current.name : "");
        dialog.setHeaderText("Enter Item Name:");
        Optional<String> name = dialog.showAndWait();
        if (name.isEmpty()) return null;

        dialog = new TextInputDialog(current != null ? current.description : "");
        dialog.setHeaderText("Enter Description:");
        Optional<String> desc = dialog.showAndWait();
        if (desc.isEmpty()) return null;

        return new InventoryItem(id.get(), name.get(), desc.get());
    }

    public static List<MaintenanceRecord> readMaintenance() {
        List<MaintenanceRecord> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MAINTENANCE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(MaintenanceRecord.fromString(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void updateMaintenance(MaintenanceRecord oldR, MaintenanceRecord newR) {
        List<MaintenanceRecord> list = readMaintenance();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MAINTENANCE_FILE))) {
            for (MaintenanceRecord r : list) {
                if (r.toString().equals(oldR.toString())) {
                    bw.write(newR.toString());
                } else {
                    bw.write(r.toString());
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MaintenanceRecord showMaintenanceForm(MaintenanceRecord current) {
        TextInputDialog dialog = new TextInputDialog(current != null ? current.date : "");
        dialog.setHeaderText("Enter Maintenance Date (YYYY-MM-DD):");
        Optional<String> date = dialog.showAndWait();
        if (date.isEmpty()) return null;

        dialog = new TextInputDialog(current != null ? current.notes : "");
        dialog.setHeaderText("Enter Notes:");
        Optional<String> notes = dialog.showAndWait();
        if (notes.isEmpty()) return null;

        return new MaintenanceRecord(current.itemId, date.get(), notes.get());
    }
}

