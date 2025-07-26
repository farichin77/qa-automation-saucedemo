package com.saucedemo.tests;

import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.CartPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class InventoryTest extends BaseTest {
    private InventoryPage inventoryPage;

    @BeforeMethod
    public void loginAndNavigateToInventory() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo();
        inventoryPage = loginPage.login("standard_user", "secret_sauce");
    }

    @Test
    public void testProductCount() {
        Assert.assertEquals(inventoryPage.getNumberOfProducts(), 6, "Incorrect number of products displayed");
    }

    @Test
    public void testAddProductToCart() {
        // Store initial product details
        String productName = inventoryPage.getProductName(0);
        double productPrice = inventoryPage.getProductPrice(0);
        
        // Add to cart
        inventoryPage.addProductToCart(0);
        
        // Verify button state changed
        Assert.assertTrue(driver.findElement(By.cssSelector("button[data-test^='remove']")).isDisplayed(), 
            "Remove button should be visible after adding to cart");
        
        // Navigate to cart and verify
        CartPage cartPage = inventoryPage.navigateToCart();
        Assert.assertEquals(cartPage.getNumberOfItems(), 1, "Product not added to cart");
        Assert.assertEquals(cartPage.getItemName(0), productName, "Wrong product added to cart");
        Assert.assertEquals(cartPage.getItemPrice(0), productPrice, "Product price mismatch in cart");
        Assert.assertEquals(cartPage.getItemQuantity(0), 1, "Product quantity should be 1");
    }

    @Test
    public void testAddMultipleProductsToCart() {
        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(1);
        inventoryPage.addProductToCart(2);
        CartPage cartPage = inventoryPage.navigateToCart();
        Assert.assertEquals(cartPage.getNumberOfItems(), 3, "Incorrect number of products in cart");
    }

    @Test
    public void testProductDetails() {
        String productName = inventoryPage.getProductName(0);
        double productPrice = inventoryPage.getProductPrice(0);
        Assert.assertNotNull(productName, "Product name should not be null");
        Assert.assertTrue(productPrice > 0, "Product price should be greater than 0");
    }

    @Test
    public void testNavigateToCart() {
        CartPage cartPage = inventoryPage.navigateToCart();
        Assert.assertTrue(driver.getCurrentUrl().contains("cart.html"), "Not navigated to cart page");
    }
}
