package com.techelevator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductInventoryTest {
	ProductInventory productInventory = new ProductInventory();

	//Validates maps are populated.
	@Test
	public void mapTest() {
		productInventory.populatingFile();
		Assert.assertEquals("Potato Crisps", productInventory.getItemName("A1"));
		Assert.assertEquals(Double.valueOf("3.05"), productInventory.getPrice("A1"));
		Assert.assertEquals("Chip", productInventory.getItemType("A1"));
		Assert.assertEquals(5, productInventory.getStock("A1"));
		Assert.assertEquals(0, productInventory.getItemSold("A1"));
	}

	//Tests if stock-checking method operates as designed.
	@Test
	public void isInStockTest(){
		String value = "A1";
		productInventory.setStock(value, 5);
		productInventory.isInStock(value);
        Assert.assertTrue(productInventory.isStocked());
		productInventory.setStock(value, 0);
		productInventory.isInStock(value);
        Assert.assertFalse(productInventory.isStocked());
	}

	//Tests if stock and sales counter method operates as designed.
	@Test
	public void updatingStockTest(){
		String value = "A2";
		productInventory.setStock(value, 5);
		productInventory.setItemSold(value, 0);
		productInventory.updatingStock(value);
		Assert.assertEquals(4, productInventory.getStock(value));
		Assert.assertEquals(1, productInventory.getItemSold(value));
	}

	//Tests if user selection validator method operates as intended.
	@Test
	public void validateSelectionTest(){
		String value = "B1";
		productInventory.setItemName(value, "Moonpie");
		productInventory.validateSelection(value);
		Assert.assertTrue(productInventory.isValid());
		String fakevalue = "E1";
		productInventory.validateSelection(fakevalue);
        Assert.assertFalse(productInventory.isValid());
	}

}
