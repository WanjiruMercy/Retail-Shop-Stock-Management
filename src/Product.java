public class Product {
    private int id;
    private String name;
    private double price;
    private int quantityInStock;

    public Product(int id, String name, double price, int quantityInStock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantityInStock = quantityInStock;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantityInStock() { return quantityInStock; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantityInStock(int q) { this.quantityInStock = q; }
    
    // This override is needed so the dropdown box shows the product name
    // instead of a memory address like "Product@1b6d3586"
    @Override
    public String toString() {
        return this.name; 
    }
}