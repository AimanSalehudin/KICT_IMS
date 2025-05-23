package Test2;

public class MaintenanceRecord {
    String itemId, date, notes;

    public MaintenanceRecord(String itemId, String date, String notes) {
        this.itemId = itemId;
        this.date = date;
        this.notes = notes;
    }

    public static MaintenanceRecord fromString(String line) {
        String[] parts = line.split(",", 3);
        return new MaintenanceRecord(parts[0], parts[1], parts[2]);
    }

    @Override
    public String toString() {
        return itemId + "," + date + "," + notes;
    }
}

