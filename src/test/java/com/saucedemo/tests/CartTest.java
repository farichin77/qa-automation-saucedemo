package com.saucedemo.tests;

import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.CartPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {
    private InventoryPage inventoryPage;
    private CartPage cartPage;

    @BeforeMethod
    public void loginAndAddToCart() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo();
        inventoryPage = loginPage.login("standard_user", "secret_sauce");
        inventoryPage.addProductToCart(0);
        cartPage = inventoryPage.navigateToCart();
    }

    @Test
    public void testCartItemCount() {
        // Verify initial state
        Assert.assertEquals(cartPage.getNumberOfItems(), 1, "Incorrect number of items in cart");
        Assert.assertTrue(driver.findElement(By.id("checkout")).isDisplayed(), "Checkout button should be visible");
        Assert.assertTrue(driver.getCurrentUrl().contains("cart.html"), "Should be on cart page");
    }

    @Test
    public void testRemoveFromCart() {
        // Store item details before removal
        String itemName = cartPage.getItemName(0);
        Assert.assertNotNull(itemName, "Item name should be present before removal");
        
        // Remove item
        cartPage.removeItem(0);
        
        // Verify item removed
        Assert.assertEquals(cartPage.getNumberOfItems(), 0, "Item not removed from cart");
        Assert.assertTrue(driver.findElements(By.cssSelector(".cart_item")).isEmpty(), "Cart should be empty");
        Assert.assertTrue(driver.findElement(By.className("cart_list")).isDisplayed(), "Cart list should still be visible");
    }

    @Test
    public void testCartItemDetails() {
        // Get all item details
        String itemName = cartPage.getItemName(0);
        double itemPrice = cartPage.getItemPrice(0);
        int quantity = cartPage.getItemQuantity(0);

        // Verify item details
        Assert.assertNotNull(itemName, "Item name should not be null");
        Assert.assertTrue(itemPrice > 0, "Item price should be greater than 0");
        Assert.assertEquals(quantity, 1, "Quantity should be 1");
        
        // Verify presence of all required elements
        Assert.assertTrue(driver.findElement(By.className("inventory_item_name")).isDisplayed(), "Item name should be visible");
        Assert.assertTrue(driver.findElement(By.className("inventory_item_price")).isDisplayed(), "Price should be visible");
        Assert.assertTrue(driver.findElement(By.className("cart_quantity")).isDisplayed(), "Quantity should be visible");
    }

    @Test
    public void testProceedToCheckout() {
        cartPage.proceedToCheckout();
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"), 
            "Not navigated to checkout page");
    }
}
