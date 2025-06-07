package Test2.Java.model;

import java.time.LocalDate;

public abstract class MaintenanceRecord {
    private String itemId;
    private LocalDate date;
    private String remarks;

    public MaintenanceRecord(String itemId, LocalDate date, String remarks) {
        this.itemId = itemId;
        this.date = date;
        this.remarks = remarks;
    }

    // Getters
    public String getItemId() { return itemId; }
    public LocalDate getDate() { return date; }
    public String getRemarks() { return remarks; }

    // Setters
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public abstract String getMaintenanceType();

    public String toData() {
        return String.join(",", itemId, date.toString(), remarks, getMaintenanceType());
    }

    public static MaintenanceRecord fromData(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) return null;

        String itemId = parts[0];
        LocalDate date = LocalDate.parse(parts[1]);
        String remarks = parts[2];
        String type = parts[3];

        switch (type) {
            case "Routine":
                return new RoutineMaintenance(itemId, date, remarks);
            case "Repair":
                return new RepairMaintenance(itemId, date, remarks);
            case "Inspection":
                return new InspectionMaintenance(itemId, date, remarks);
            default:
                return new GeneralMaintenance(itemId, date, remarks);
        }
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", itemId, date, getMaintenanceType());
    }
}