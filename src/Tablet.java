public class Tablet extends StoreItems{
    private double screenSize;


    //Constructor
    public Tablet(String category, String type, String id, String brand, String cpuFamily, double price, double screenSize){
        super(category, type, id, brand, cpuFamily, price);
        this.screenSize = screenSize;
    }


    //Getter
    public double getScreenSize(){
        return screenSize;
    }

    @Override
    public String toString() {
        return super.toString() + ", Screen Size: " + screenSize + " inches";
    }
}
