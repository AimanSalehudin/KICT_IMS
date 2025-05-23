package Test1;

public class InventoryItem {
    private String id;
    private String name;
    private String description;
    private int quantity;

    // Constructor with all fields
    public InventoryItem(String id, String name, String description, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
    }

    // Original constructor for backward compatibility
    public InventoryItem(String id, String name, String description) {
        this(id, name, description, 0); // Default quantity to 0
    }

    @Override
    public String toString() {
        return String.format("%s: %s (Qty: %d)", id, name, quantity);
    }

    public String toData() {
        return String.join(",", id, name, description, String.valueOf(quantity));
    }

    public String toDataString() {
        return toData(); // Alias to maintain both versions
    }

    public static InventoryItem fromDataString(String line) {  // Renamed from fromData()
        String[] parts = line.split(",");
        if (parts.length < 4) {
            return new InventoryItem(parts[0], parts[1], parts[2]);
        }
        try {
            return new InventoryItem(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
        } catch (NumberFormatException e) {
            return new InventoryItem(parts[0], parts[1], parts[2]);
        }
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
}