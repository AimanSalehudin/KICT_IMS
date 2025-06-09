package Test2.Java.model;

import Test2.Java.util.FileUtil;

import java.time.LocalDate;

public abstract class InventoryItem {
    private String id;
    private String name;
    private String description;
    private int quantity;
    private LocalDate lastMaintenanceDate;

    public InventoryItem(String id, String name, String description, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public LocalDate getLastMaintenanceDate() { return lastMaintenanceDate; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setLastMaintenanceDate(LocalDate date) { this.lastMaintenanceDate = date; }

    public abstract String getCategory();

    public boolean needsMaintenance() {
        if (lastMaintenanceDate == null) return true;
        return LocalDate.now().isAfter(lastMaintenanceDate.plusYears(1));
    }

    @Override
    public String toString() {
        return String.format("%s: %s (Qty: %d)", id, name, quantity);
    }

    public String toDataString() {
        return String.join(",", id, name, description, String.valueOf(quantity),
                lastMaintenanceDate != null ? lastMaintenanceDate.toString() : "");
    }

    public static InventoryItem fromDataString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) {
            return null;
        }

        try {
            String id = parts[0];
            String name = parts[1];
            String desc = parts[2];
            int qty = Integer.parseInt(parts[3]);
            LocalDate lastMaintenance = parts.length > 4 && !parts[4].isEmpty() ?
                    LocalDate.parse(parts[4]) : null;

            // Determine the concrete type based on ID prefix or other logic
            InventoryItem item;
            if (id.startsWith("ELEC")) {
                item = new ElectricalItem(id, name, desc, qty);
            } else if (id.startsWith("FURN")) {
                item = new FurnitureItem(id, name, desc, qty);
            } else {
                item = new GeneralItem(id, name, desc, qty);
            }

            item.setLastMaintenanceDate(lastMaintenance);
            return item;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}