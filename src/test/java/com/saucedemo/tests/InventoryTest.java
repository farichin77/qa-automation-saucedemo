package com.saucedemo.tests;

import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.CartPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InventoryTest extends BaseTest {

    private InventoryPage loginAsStandardUser() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateTo();
        return loginPage.login("standard_user", "secret_sauce");
    }

    @Test
    public void testProductCount() {
        InventoryPage inventoryPage = loginAsStandardUser();
        int productCount = inventoryPage.getNumberOfProducts();
        Assert.assertEquals(productCount, 6, "Expected 6 products, but found " + productCount);
    }

    @Test
    public void testAddProductToCart() {
        InventoryPage inventoryPage = loginAsStandardUser();

        String productName = inventoryPage.getProductName(0);
        double productPrice = inventoryPage.getProductPrice(0);

        inventoryPage.addProductToCart(0);

        // Verify button changed to "Remove"
        Assert.assertTrue(inventoryPage.isProductInCart(0), "Product should show as 'Remove' after adding");

        CartPage cartPage = inventoryPage.navigateToCart();
        Assert.assertEquals(cartPage.getNumberOfItems(), 1, "Cart should contain 1 product");
        Assert.assertEquals(cartPage.getItemName(0), productName, "Product name mismatch in cart");
        Assert.assertEquals(cartPage.getItemPrice(0), productPrice, "Product price mismatch in cart");
        Assert.assertEquals(cartPage.getItemQuantity(0), 1, "Product quantity should be 1");
    }

    @Test
    public void testAddMultipleProductsToCart() {
        InventoryPage inventoryPage = loginAsStandardUser();

        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(1);
        inventoryPage.addProductToCart(2);

        CartPage cartPage = inventoryPage.navigateToCart();
        Assert.assertEquals(cartPage.getNumberOfItems(), 3, "Cart should contain 3 products");
    }

    @Test
    public void testProductDetails() {
        InventoryPage inventoryPage = loginAsStandardUser();

        String productName = inventoryPage.getProductName(0);
        double productPrice = inventoryPage.getProductPrice(0);

        Assert.assertNotNull(productName, "Product name should not be null");
        Assert.assertFalse(productName.isEmpty(), "Product name should not be empty");
        Assert.assertTrue(productPrice > 0, "Product price should be greater than 0");
    }

    @Test
    public void testNavigateToCart() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = inventoryPage.navigateToCart();

        Assert.assertTrue(
                getDriver().getCurrentUrl().contains("cart.html"),
                "Expected to be on cart page but URL is: " + getDriver().getCurrentUrl()
        );
    }
}
