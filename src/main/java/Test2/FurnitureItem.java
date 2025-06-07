package Test2;

public class FurnitureItem extends InventoryItem {
    private String material;
    private String dimensions;

    public FurnitureItem(String id, String name, String description, int quantity) {
        super(id, name, description, quantity);
    }

    @Override
    public String getCategory() {
        return "Furniture";
    }

    public String getMaterial() { return material; }
    public String getDimensions() { return dimensions; }

    public void setMaterial(String material) { this.material = material; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    @Override
    public String toDataString() {
        return super.toDataString() + "," + (material != null ? material : "") + "," +
                (dimensions != null ? dimensions : "");
    }
}