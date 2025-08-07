package com.saucedemo.tests;

import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.InventoryPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.*;
import io.qameta.allure.Description;

@Epic("Authentication Tests")
@Feature("Login")
public class LoginTest extends BaseTest {

    @Test
    @Story("Successful Login")
    @Description("Verify that user can successfully login with valid credentials")
    @Severity(SeverityLevel.BLOCKER)
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateTo();
        Assert.assertEquals(getDriver().getCurrentUrl(), "https://www.saucedemo.com/", "Login page URL is incorrect");

        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("inventory.html"), "Not redirected to inventory page");
        Assert.assertEquals(inventoryPage.getNumberOfProducts(), 6, "Incorrect number of products displayed");
        Assert.assertTrue(getDriver().findElement(By.className("shopping_cart_link")).isDisplayed(), "Shopping cart is not visible");
    }

    @Test
    @Story("Locked Out User")
    @Description("Verify that locked out users cannot login")
    @Severity(SeverityLevel.CRITICAL)
    public void testLockedOutUser() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateTo();
        loginPage.login("locked_out_user", "secret_sauce");

        String errorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("locked out"), "Incorrect error message for locked out user");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("saucedemo.com"), "URL should not change for failed login");
        Assert.assertFalse(getDriver().findElements(By.className("inventory_list")).size() > 0, "Inventory should not be accessible");
    }

    @Test
    public void testInvalidCredentials() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateTo();
        loginPage.login("invalid_user", "invalid_password");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username and password do not match"));
    }

    @Test
    public void testEmptyCredentials() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateTo();
        loginPage.clickLogin();
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username is required"));
    }

    @Test
    public void testLogout() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateTo();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        loginPage = inventoryPage.logout();
        Assert.assertTrue(getDriver().getCurrentUrl().contains("saucedemo.com"));
    }
}
