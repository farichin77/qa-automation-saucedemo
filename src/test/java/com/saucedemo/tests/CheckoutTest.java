package com.saucedemo.tests;

import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
