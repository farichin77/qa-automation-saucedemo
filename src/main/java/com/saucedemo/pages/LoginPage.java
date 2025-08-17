package com.saucedemo.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;


public class LoginPage extends BasePage {
    @FindBy(id = "user-name")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void navigateTo() {
        getDriver().get("https://www.saucedemo.com/");
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameInput)).clear();
        usernameInput.sendKeys(username);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordInput)).clear();
        passwordInput.sendKeys(password);
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOf(errorMessage)).getText();
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(errorMessage)).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public InventoryPage login(String username, String password) {
    enterUsername(username);
    enterPassword(password);

    // Tunggu button ready lalu klik
    wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();

    // Kalau ada error, berarti login gagal
    if (isErrorMessageDisplayed()) {
        throw new RuntimeException("Login failed: " + getErrorMessage());
    }

    // Tunggu indikator sukses (shopping cart muncul)
    wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.id("shopping_cart_container")
    ));

    return new InventoryPage(getDriver());
}

    public void loginExpectingFailure(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        wait.until(ExpectedConditions.visibilityOf(loginButton));
        clickWithRetry(loginButton);
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
    }
}
