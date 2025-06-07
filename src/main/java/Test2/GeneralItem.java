package Test2;

public class GeneralItem extends InventoryItem {
    public GeneralItem(String id, String name, String description, int quantity) {
        super(id, name, description, quantity);
    }

    @Override
    public String getCategory() {
        return "General";
    }
}