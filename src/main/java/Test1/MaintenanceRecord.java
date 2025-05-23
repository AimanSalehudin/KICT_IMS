package Test1;

public class MaintenanceRecord {
    private String itemId, date, remarks;

    public MaintenanceRecord(String itemId, String date, String remarks) {
        this.itemId = itemId;//ID User
        this.date = date;// Tarikh login
        this.remarks = remarks;// comment
    }

    public String toData() {
        return String.join(",", itemId, date, remarks);
    }

    public static MaintenanceRecord fromData(String line) {
        String[] parts = line.split(",");
        if (parts.length < 3) {
            return new MaintenanceRecord("N/A", "N/A", "N/A");
        }
        return new MaintenanceRecord(parts[0], parts[1], parts[2]);
    }

    @Override
    public String toString() {
        return itemId + " - " + date;
    }

    // Getters
    public String getItemId() { return itemId; }
    public String getDate() { return date; }
    public String getRemarks() { return remarks; }
}