public class InventoryImport {
    private String importId;
    private String productId;
    private int quantity;
    private String supplier;
    private String importDate;

    public InventoryImport(String importId, String productId, int quantity, String supplier, String importDate) {
        this.importId = importId;
        this.productId = productId;
        this.quantity = quantity;
        this.supplier = supplier;
        this.importDate = importDate;
    }

    // Getter
    public String getImportId() { return importId; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getSupplier() { return supplier; }
    public String getImportDate() { return importDate; }

    @Override
    public String toString() {
        return String.format("%s | %s | %d | %s | %s", importId, productId, quantity, supplier, importDate);
    }
}