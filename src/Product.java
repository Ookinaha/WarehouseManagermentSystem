public class Product {
    private String productId;
    private String name;
    private String origin;
    private String manufacturer;
    private int warrantyPeriod;
    private String dimensions;
    private String color;
    private double price;
    private int quantity;
    private String imagePath; // Thêm thuộc tính lưu đường dẫn ảnh

    public Product(String productId, String name, String origin, String manufacturer, int warrantyPeriod,
                   String dimensions, String color, double price, int quantity, String imagePath) {
        this.productId = productId;
        this.name = name;
        this.origin = origin;
        this.manufacturer = manufacturer;
        this.warrantyPeriod = warrantyPeriod;
        this.dimensions = dimensions;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
    }

    public String getStatus() {
        if (quantity == 0) return "Hết hàng";
        if (quantity <= 10) return "Sắp hết hàng";
        return "Còn hàng";
    }

    // Getter và Setter
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getOrigin() { return origin; }
    public String getManufacturer() { return manufacturer; }
    public int getWarrantyPeriod() { return warrantyPeriod; }
    public String getDimensions() { return dimensions; }
    public String getColor() { return color; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String toString() {
        return String.format("%s | %s | %d | %s | Ảnh: %s", productId, name, quantity, getStatus(), imagePath);
    }
}