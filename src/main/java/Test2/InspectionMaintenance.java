package Test2;

import java.time.LocalDate;

public class InspectionMaintenance extends MaintenanceRecord {
    private String inspector;

    public InspectionMaintenance(String itemId, LocalDate date, String remarks) {
        super(itemId, date, remarks);
    }

    @Override
    public String getMaintenanceType() {
        return "Inspection";
    }

    public String getInspector() { return inspector; }
    public void setInspector(String inspector) { this.inspector = inspector; }

    @Override
    public String toData() {
        return super.toData() + "," + (inspector != null ? inspector : "");
    }
}