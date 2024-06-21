public class Laptop extends  StoreItems {
    private int memorySize;
    private int ssdCapacity;
    private double screenSize;

    public Laptop(String category, String type, String id, String brand, String cpuFamily, double price, int memorySize, int ssdCapacity,  double screenSize){
        super(category, type, id, brand, cpuFamily, price);
        this.memorySize = memorySize;
        this.ssdCapacity = ssdCapacity;
        this.screenSize = screenSize;
    }

    public int getMemorySize(){
        return memorySize;
    }

    public int getSsdCapacity(){
        return ssdCapacity;
    }

    public double getScreenSize(){
        return screenSize;
    }

    @Override
    public String toString() {
        return super.toString() + ", Memory Size: " + memorySize + "GB, SSD Capacity: " + ssdCapacity + "GB, Screen Size: " + screenSize + " inches";
    }


}
