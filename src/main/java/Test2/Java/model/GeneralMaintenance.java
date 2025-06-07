package Test2.Java.model;

import java.time.LocalDate;

public class GeneralMaintenance extends MaintenanceRecord {
    public GeneralMaintenance(String itemId, LocalDate date, String remarks) {
        super(itemId, date, remarks);
    }

    @Override
    public String getMaintenanceType() {
        return "General";
    }
}