package com.techelevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProductInventory {
    File productFile = new File("vendingmachine.csv");
    private Map<String, Integer> stock = new HashMap<>();
    private Map<String, String> inventory = new HashMap<>();
    private Map<String, Double> priceMap = new HashMap<>();
    private Map<String, String> slotIdentifier = new HashMap<>();
    private Map<String, Integer> itemsSold = new HashMap<>(); //this is a sales counter for items sold.
    private String itemType = "";
    private Scanner fileScan = null;
    private boolean validator;
    private boolean stocked;

    public void addProductsAvailable() {
        for (Map.Entry<String, Integer> s : stock.entrySet()) {
            if (s.getValue() > 0){
                inventory.put(s.getKey(), inventory.get(s.getKey())); //adding only. not updating map.
                System.out.println(s.getKey() + ": " + inventory.get(s.getKey()) + " - " + s.getValue() + " available");
            }
        }
    }

    // Getters and Setters for Maps and Variables.
    public String getItemName(String itemCode){
       return inventory.get(itemCode);
    }

    public void setItemName(String key, String value){
        inventory.put(key, value);
    }

    public Double getPrice(String itemCode){
        return priceMap.get(itemCode);
    }

    public void setPrice(String key, Double value){
        priceMap.put(key, value);
    }
   
    public String getItemType(String userInput){
       return itemType = slotIdentifier.get(userInput);
    }

    public void setItemType(String key, String value){
        slotIdentifier.put(key, value);
    }

    public boolean isValid(){
        return validator;
    }

    public void validateSelection(String userInput){
       validator = inventory.containsKey(userInput);
    }

    public int getStock(String userInput){
        return stock.get(userInput);
    }

    public void setStock(String key, Integer value){
        stock.put(key, value);
    }

    public boolean isStocked(){
        return stocked;
    }

    public int getItemSold(String userInput){
        return itemsSold.get(userInput);
    }

    public void setItemSold(String key, Integer value){
        itemsSold.put(key, value);
    }

    public void updatingStock(String userInput){
        stock.put(userInput, stock.get(userInput) - 1);
        itemsSold.put(userInput, itemsSold.get(userInput) + 1);
    }

    //Identifies if item is in stock or not.
    public void isInStock(String userInput){
      if(stock.get(userInput) > 0){
          stocked = true;
      }else{
          stocked = false;
      }
    }

    //Populates maps using itemCode as the unique identifier/key bewteen each.
    public void populatingFile() {
            try {
                fileScan = new Scanner(productFile);
                while (fileScan.hasNextLine()) {
                    String[] detailsArray = fileScan.nextLine().split("\\|");
                    inventory.put(detailsArray[0], detailsArray[1]);
                    stock.put(detailsArray[0], 5);
                    priceMap.put(detailsArray[0], Double.valueOf(detailsArray[2]));
                    slotIdentifier.put(detailsArray[0], detailsArray[3]);
                    itemsSold.put(detailsArray[0], 0);
                }
                } catch(FileNotFoundException e){
                    throw new RuntimeException(e);
                } finally{
                    if (fileScan != null) {
                        fileScan.close();
                    }
                }
    }
        //Generates Sales Report including local date & time as part of the file naming convention.
        public void generateSalesReport(){
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            String timeStamp = now.format(formatter);
            String fileName = "Sales_Report_" + timeStamp + ".txt";
            try (PrintWriter dataOutput = new PrintWriter(fileName)){
                double revenue = 0.00;
                for (Map.Entry<String, Integer> lineItem : itemsSold.entrySet()){
                    dataOutput.println(inventory.get(lineItem.getKey()) + "|" + lineItem.getValue());
                    revenue += priceMap.get(lineItem.getKey()) * lineItem.getValue(); //increments revenue
                }
                dataOutput.printf("%s $%.2f","\n **TOTAL SALES** " , revenue);
            }catch (FileNotFoundException e) {
                System.err.println("Error Generating Sales Report: " + e.getMessage());
            }
            System.out.println("Sales Report Generated: " + fileName);
        }

}








