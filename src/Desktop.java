public class Desktop extends StoreItems{
    private int memorySize;
    private int ssdCapacity;

    public Desktop(String category,String type, String id, String brand, String cpuFamily, double price, int memorySize, int ssdCapacity){
        super(category, type, id, brand, cpuFamily, price);
        this.memorySize = memorySize;
        this.ssdCapacity = ssdCapacity;
    }

    public int getMemorySize(){
        return memorySize;
    }

    public int getSsdCapacity(){
        return ssdCapacity;
    }

    @Override
    public String toString() {
        return super.toString() + ", Memory Size: " + memorySize + "GB, SSD Capacity: " + ssdCapacity + "GB";
    }
}
