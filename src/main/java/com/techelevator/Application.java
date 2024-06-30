package com.techelevator;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Application {
	static double currentMoneyProvided = 0.0;
	int nickels;
	int dimes;
	int quarters;
	PrintWriter printWriter = null;
	String transactionDate = "";
	static Application application = new Application();

	public static void main(String[] args) throws FileNotFoundException {

		//Initializes variables, scanners, print statements to start-up the vending machine.
		ProductInventory productInventory = new ProductInventory();
		Scanner userInput = new Scanner(System.in);
		Transaction transaction = new Transaction();
		System.out.println("Please Enter A Valid Code From The List Of Purchasable Items.");
		productInventory.populatingFile();
		boolean mainLoop = true;

		//Displays menu with options to user
		while (mainLoop) {
			boolean caseTwoLoop = true;
			System.out.println("MAIN MENU");
			String[] mainMenu = {"(1) Display Vending Machine Items", "(2) Purchase", "(3) Exit"};
			for (String menuOptions : mainMenu) {
				System.out.println(menuOptions);
			}
			System.out.println("Choose Option Number");
			int userChoice = Integer.parseInt(userInput.nextLine());
			switch (userChoice) {
				case 1:
					File productFile = new File("vendingmachine.csv");
					Scanner scanner = new Scanner(productFile);
					while (scanner.hasNextLine()) {
						String line = scanner.nextLine();
						System.out.println(line + " " + 5); //Prints out an item listing with an initial stock of 5.
					}
					break;
				case 2:
					while (caseTwoLoop) {
						//Displays a $0.00 balance to user and Purchasing Menu options.
						System.out.printf("%s $%.2f %s","Current Money Provided:", currentMoneyProvided,"\n");
						String[] secondaryMenu = {"(1) Feed Money", "(2) Select Product", "(3) Finish Transaction"};
						for (String menuOptions : secondaryMenu) {
							System.out.println(menuOptions);
						}
						//Prompts user to input/choose an option number.
						System.out.println("Choose Option Number");
						int transactionChoice = Integer.parseInt(userInput.nextLine());
						//Restricts progress through the options menu validating the user input is between 1 and 3, inclusive.
						if (transactionChoice < 1 || transactionChoice > 3) {
							System.out.println("Not A Valid Selection: Choose An Option Number From 1 to 3");
						}
						switch (transactionChoice) {
							case 1:
								//Prompts user to feed money into the vending machine.
								System.out.println("Please Feed Money In Full Dollar Amounts.");
								double dollarAmount = Double.parseDouble(userInput.nextLine());
								currentMoneyProvided += dollarAmount;
								String feedData = String.format("%s $%.2f $%.2f","Feed Money: ", dollarAmount, currentMoneyProvided);
								application.log(feedData); //logs current balance increase in Log.txt
								break;
							case 2:
								//Propmts user to select a product.
								productInventory.addProductsAvailable(); //generates and displays a list of products for the user to choose from.
								System.out.println("Please Make Your Selection From The List Provided.");
								String selectedItems = userInput.nextLine().toUpperCase();

								productInventory.validateSelection(selectedItems); //validates user selection against available products.
								if (!productInventory.isValid()) {
									System.out.println("Invalid Selection. Returning To Purchase Menu");
									break;
								}
								//Informs user if a product is SOLD OUT and allows for reselection.
								productInventory.isInStock(selectedItems);
								if (!productInventory.isStocked()) {
									System.out.println("SOLD OUT");
									break;
								}
								//Informs the user of insufficient funds when the selection price exceeds money provided to the vending machine.
								//Allows user to enter more money or reselect.
								if (currentMoneyProvided<productInventory.getPrice(selectedItems)){
									System.out.println("Please Enter More Money");
									break;
								}

								productInventory.updatingStock(selectedItems); //Reduces stock of selected item by 1 and adds the item to a sales counter (+1).

								transaction.reduceBalance(selectedItems); //Reduces the remaining balance by the price of the item selected.
								//Logs transaction using item details in Log.txt
								String formattedBalance = String.format("$%.2f", currentMoneyProvided);
								String transactionData = productInventory.getItemName(selectedItems) + " " + selectedItems + " $" + productInventory.getPrice(selectedItems) + " " + formattedBalance;

								application.log(transactionData);

								//Creates sounds based on items selected when dispensed.
								switch (productInventory.getItemType(selectedItems)) {
									case "Chip":
										System.out.println("Crunch Crunch, Yum!");
										break;
									case "Candy":
										System.out.println("Munch Munch, Yum!");
										break;
									case "Drink":
										System.out.println("Glug Glug, Yum!");
										break;
									case "Gum":
										System.out.println("Chew Chew, Yum!");
										break;
								}
								break;

								//Dispense money back to customer in coin-change form using the smallest combination of coins.
							case 3:
								int remainingChange = (int) (currentMoneyProvided * 100);
								int quarters = remainingChange / 25;
								remainingChange %= 25;
								int dimes = remainingChange / 10;
								remainingChange %= 10;
								int nickels = remainingChange / 5;
								StringBuilder changeBuilder = new StringBuilder("Dispensing Remaining Change: ");
								if (quarters > 0) {
									changeBuilder.append(quarters + ":Quarters ");
								}
								if (dimes > 0) {
									changeBuilder.append(dimes + ":Dimes ");
								}
								if (nickels > 0) {
									changeBuilder.append(nickels + ":Nickels");
								}
								System.out.println(changeBuilder);
								caseTwoLoop = false;
								//Logs reduction of remaining balance to $0.00 and change amount provided to user in Log.txt
								String changeData = String.format("%s $%.2f $%.2f ","Give Change: ", currentMoneyProvided, (double)(remainingChange));
								application.log(changeData);
								currentMoneyProvided = 0.00;
								break;
						}
					}
					break;

				case 3:
					exit();
				case 4:
					//Secret menu option which generates a sales report calculating total revenue for the items vended.
					productInventory.generateSalesReport();
					break;
			}
		}
	}
	public double getCurrentMoneyProvided() {
		return currentMoneyProvided;
	}

	public void setCurrentMoneyProvided(double balance) {
		this.currentMoneyProvided = balance;
	}

	public static void exit() {
		// Cleans-up and exits vending machine application.
		System.out.println("Thanks For Using Vendo - Matic 800");
		System.exit(0);
	}

    //Creats and formats Log.txt inputs local times, dates, and changes in vending machine balances.
	public void log(String transactionData) {
		LocalDateTime now = LocalDateTime.now();
		final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
		String timestamp = now.format(DATE_TIME_FORMATTER);
		try {
			String log = timestamp + " " + transactionData;
			printWriter = new PrintWriter(new FileOutputStream(new File("Log.txt"),true));
			printWriter.println(log);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}finally {
			if (printWriter != null){
				printWriter.close();
			}
		}
	}
}
