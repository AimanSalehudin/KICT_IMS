package Test2.Java.util;

import Test2.Java.model.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

    // Save users to file
    public static void saveUsers(List<User> users) {
        try (PrintWriter pw = new PrintWriter(USERS_FILE)) {
            for (User user : users) {
                pw.println(user.toDataString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}