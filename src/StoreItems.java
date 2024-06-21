public class StoreItems {

    private String category;
    private String type;
    private String id;
    private String brand;
    private String cpuFamily;
    private double price;

    //constructor
    public StoreItems(String category, String type, String id, String brand, String cpuFamily, double price){
        this.category = category;
        this.type = type;
        this.id = id;
        this.brand = brand;
        this.cpuFamily = cpuFamily;
        this.price = price;
    }

    //Getters

    public String getCategory() {
        return category;
    }

    public String getType(){
        return type;
    }
    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getCpuFamily() {
        return cpuFamily;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Brand: " + brand + ", CPU: " + cpuFamily + ", Price: $" + price;
    }
}

