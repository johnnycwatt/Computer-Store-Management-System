import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class Main {

    private static HashMap<String, StoreItems> computers = new HashMap<>();
    private static HashMap<String, Staff> staffMap = new HashMap<>();

    public static void main(String[] args) {
        try {
            loadComputers("computers.txt");
        } catch (IOException e) {
            System.out.println("There was an error loading the data: " + e.getMessage());
        }

        initializeStaff();

        SwingUtilities.invokeLater(() -> new LoginFrame(staffMap, computers));

    }

    private static void loadComputers(String filename) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        String line;
        while((line = reader.readLine()) != null){
          String[] parts = line.split(",");
          String category = parts[0];
          String type = parts[1];
          String id = parts[2];
          String brand = parts[3];
          String cpu = parts[4];
          //depends on category
          int memorySize;
          int ssdCapacity;
          double screenSize;
          double price = Double.parseDouble(parts[parts.length-1]); //price is always last regardles off category


          switch(category.toLowerCase()){
              case "desktop pc":
                  memorySize = Integer.parseInt(parts[5]);
                  ssdCapacity = Integer.parseInt(parts[6]);
                  Desktop desktop = new Desktop(category, type, id, brand, cpu, price, memorySize, ssdCapacity);
                  computers.put(desktop.getId(), desktop);
                  break;

              case "laptop":
                  memorySize = Integer.parseInt(parts[5]);
                  ssdCapacity = Integer.parseInt(parts[6]);
                  screenSize = Double.parseDouble(parts[7]);
                  Laptop laptop = new Laptop(category, type, id, brand, cpu, price, memorySize, ssdCapacity, screenSize);
                  computers.put(laptop.getId(),laptop);
                  break;

              case "tablet":
                  screenSize = Double.parseDouble(parts[5]);
                  Tablet tablet = new Tablet(category, type, id, brand, cpu, price, screenSize);
                  computers.put(tablet.getId(), tablet);
                  break;
          }
        }
        reader.close();


    }

    //Test Data
    private static void initializeStaff(){
        staffMap.put("p1", new Salesperson("p1", "p1"));
        staffMap.put("p2", new Salesperson("p2", "p2"));
        staffMap.put("p3", new Salesperson("p3", "p3"));
        staffMap.put("m1", new Manager("m1", "m1"));
        staffMap.put("m2", new Manager("m2", "m2"));
    }

}
