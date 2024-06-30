package com.techelevator;

public class Transaction {

    Application application = new Application();
    ProductInventory productInventory = new ProductInventory();

 public void reduceBalance(String userInput){
     productInventory.populatingFile();
     application.setCurrentMoneyProvided(application.getCurrentMoneyProvided() - productInventory.getPrice(userInput));
 }


















}
