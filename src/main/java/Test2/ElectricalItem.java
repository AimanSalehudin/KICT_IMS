package Test2;

public class ElectricalItem extends InventoryItem {
    private String voltage;
    private String powerSource;

    public ElectricalItem(String id, String name, String description, int quantity) {
        super(id, name, description, quantity);
    }

    @Override
    public String getCategory() {
        return "Electrical";
    }

    public String getVoltage() { return voltage; }
    public String getPowerSource() { return powerSource; }

    public void setVoltage(String voltage) { this.voltage = voltage; }
    public void setPowerSource(String powerSource) { this.powerSource = powerSource; }

    @Override
    public String toDataString() {
        return super.toDataString() + "," + (voltage != null ? voltage : "") + "," +
                (powerSource != null ? powerSource : "");
    }
}