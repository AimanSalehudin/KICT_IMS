package Test2;

public class InventoryItem {
    String id, name, description;

    public InventoryItem(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static InventoryItem fromString(String line) {
        String[] parts = line.split(",");
        return new InventoryItem(parts[0], parts[1], parts[2]);
    }

    @Override
    public String toString() {
        return id + "," + name + "," + description;
    }
}

