package Test2.Java.model;

import java.time.LocalDate;

public class RoutineMaintenance extends MaintenanceRecord {
    public RoutineMaintenance(String itemId, LocalDate date, String remarks) {
        super(itemId, date, remarks);
    }

    @Override
    public String getMaintenanceType() {
        return "Routine";
    }
}