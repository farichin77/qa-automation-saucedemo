package com.saucedemo.tests;

import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.List;

public class CheckoutTest extends BaseTest {

    @Test
    public void testSuccessfulCheckout() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateTo();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        inventoryPage.addProductToCart(0);
        CartPage cartPage = inventoryPage.navigateToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        Assert.assertTrue(getDriver().getCurrentUrl().contains("checkout-step-one.html"), "Should be on checkout step one");
        
        checkoutPage.fillCheckoutInfo("John", "Doe", "12345");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("checkout-step-two.html"), "Should proceed to checkout step two");
        
        checkoutPage.completeCheckout();
        Assert.assertTrue(checkoutPage.getConfirmationMessage().contains("Thank you for your order"), "Should show thank you message");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("checkout-complete.html"), "Should be on complete page");
        Assert.assertTrue(getDriver().findElement(By.className("complete-header")).isDisplayed(), "Completion header should be visible");
    }

    @Test
    public void testEmptyFirstName() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateTo();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        inventoryPage.addProductToCart(0);
        CartPage cartPage = inventoryPage.navigateToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.fillCheckoutInfo("", "Doe", "12345");
        String errorMessage = checkoutPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("First Name is required"), "Wrong error message for empty first name");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("checkout-step-one.html"), "Should stay on checkout step one");
        Assert.assertTrue(getDriver().findElement(By.cssSelector("[data-test='error']")).isDisplayed(), "Error message should be visible");
    }

    @Test
    public void testCheckoutWithEmptyCart() {
        // Handle any unexpected popups first
        handleUnexpectedPopups();
        // Login
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateTo();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        
        // Navigate to cart without adding any products
        CartPage cartPage = inventoryPage.navigateToCart();
        
        // Verify cart is empty
        int itemCount = cartPage.getNumberOfItems();
        System.out.println("Number of items in cart: " + itemCount);
        Assert.assertEquals(itemCount, 0, "Cart should be empty");
        
        // Verify checkout button is disabled when cart is empty
        boolean isCheckoutEnabled = cartPage.isCheckoutButtonEnabled();
        System.out.println("Is checkout button enabled? " + isCheckoutEnabled);
        
        // Check if the application behaves as expected
        if (isCheckoutEnabled) {
            // Actual behavior (incorrect): Button is enabled
            System.out.println("ISSUE DITEMUKAN: Tombol checkout seharusnya dinonaktifkan saat keranjang kosong");
            
            // Try to proceed to checkout (should not be possible)
            try {
                cartPage.proceedToCheckout();
                System.out.println("PERINGATAN: Aplikasi mengizinkan checkout dengan keranjang kosong");
                
                // Verify we're on the checkout page (should not happen)
                Assert.assertFalse(
                    getDriver().getCurrentUrl().contains("checkout-step-one.html"),
                    "Seharusnya tidak bisa masuk ke halaman checkout dengan keranjang kosong"
                );
                
            } catch (Exception e) {
                // Expected: Should throw exception when trying to proceed with empty cart
                System.out.println("Aplikasi berhasil mencegah checkout dengan keranjang kosong: " + e.getMessage());
            }
        } else {
            // Expected behavior: Button is disabled
            System.out.println("PERILAKU YANG DIHARAPKAN: Tombol checkout dinonaktifkan saat keranjang kosong");
        }
        
        // Verify the test result
        Assert.assertFalse(isCheckoutEnabled, "Tombol checkout harus dinonaktifkan saat keranjang kosong");
    }

    @Test
    public void testEmptyLastName() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateTo();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        inventoryPage.addProductToCart(0);
        CartPage cartPage = inventoryPage.navigateToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.fillCheckoutInfo("John", "", "12345");
        String errorMessage = checkoutPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("Last Name is required"), "Wrong error message for empty last name");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("checkout-step-one.html"), "Should stay on checkout step one");
        Assert.assertEquals(getDriver().findElement(By.id("first-name")).getAttribute("value"), "John", "First name should be preserved");
    }

    @Test
    public void testEmptyPostalCode() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateTo();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        inventoryPage.addProductToCart(0);
        CartPage cartPage = inventoryPage.navigateToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.fillCheckoutInfo("John", "Doe", "");
        String errorMessage = checkoutPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("Postal Code is required"), "Wrong error message for empty postal code");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("checkout-step-one.html"), "Should stay on checkout step one");
        Assert.assertEquals(getDriver().findElement(By.id("first-name")).getAttribute("value"), "John", "First name should be preserved");
        Assert.assertEquals(getDriver().findElement(By.id("last-name")).getAttribute("value"), "Doe", "Last name should be preserved");
    }

    @Test
    public void testCompleteCheckoutFlow() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateTo();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        inventoryPage.addProductToCart(0);
        CartPage cartPage = inventoryPage.navigateToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.fillCheckoutInfo("John", "Doe", "12345");
        checkoutPage.completeCheckout();
        Assert.assertTrue(getDriver().getCurrentUrl().contains("checkout-complete.html"));
        Assert.assertTrue(checkoutPage.getConfirmationMessage().contains("Thank you for your order"));
    }
}
