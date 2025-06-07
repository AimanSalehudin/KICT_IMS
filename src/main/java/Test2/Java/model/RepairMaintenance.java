package Test2.Java.model;

import java.time.LocalDate;

public class RepairMaintenance extends MaintenanceRecord {
    private String severity;

    public RepairMaintenance(String itemId, LocalDate date, String remarks) {
        super(itemId, date, remarks);
    }

    @Override
    public String getMaintenanceType() {
        return "Repair";
    }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    @Override
    public String toData() {
        return super.toData() + "," + (severity != null ? severity : "");
    }
}