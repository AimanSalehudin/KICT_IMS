package Test1;

public class MaintenanceRecord {
    private String itemId;
    private String date;
    private String remarks;

    public MaintenanceRecord(String itemId, String date, String remarks) {
        this.itemId = itemId;
        this.date = date;
        this.remarks = remarks;
    }

    // Getters
    public String getItemId() {
        return itemId;
    }

    public String getDate() {
        return date;
    }

    public String getRemarks() {
        return remarks;
    }

    // Setters
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    // Convert object to data string for saving
    public String toData() {
        return itemId + "," + date + "," + remarks;
    }

    // Create object from saved string
    public static MaintenanceRecord fromData(String line) {
        String[] parts = line.split(",");
        if (parts.length == 3) {
            return new MaintenanceRecord(parts[0], parts[1], parts[2]);
        } else {
            return new MaintenanceRecord("", "", "");
        }
    }

    // Display format for list view or combo box
    @Override
    public String toString() {
        return itemId + " - " + date;
    }
}
