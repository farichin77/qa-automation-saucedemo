package com.saucedemo.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.time.Duration;


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
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOf(usernameInput));
            element.clear();
            element.sendKeys(username);
            // Small delay to ensure input is processed
            Thread.sleep(200);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter username: " + e.getMessage(), e);
        }
    }

    public void enterPassword(String password) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOf(passwordInput));
            element.clear();
            element.sendKeys(password);
            // Small delay to ensure input is processed
            Thread.sleep(200);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter password: " + e.getMessage(), e);
        }
    }

    public void clickLogin() {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
            // Scroll into view if needed
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
            // Click using JavaScript as a fallback
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            throw new RuntimeException("Failed to click login button: " + e.getMessage(), e);
        }
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
        try {
            // Clear any existing data and enter credentials
            enterUsername(username);
            enterPassword(password);
            
            // Wait for button to be clickable and click
            clickLogin();
            
            // Wait for either success or error condition
            WebDriverWait shortWait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
            
            try {
                // First check for error message
                if (shortWait.until(ExpectedConditions.alertIsPresent()) != null) {
                    throw new RuntimeException("Browser alert present: " + getDriver().switchTo().alert().getText());
                }
                
                // Check for error message
                if (isErrorMessageDisplayed()) {
                    throw new RuntimeException("Login failed: " + getErrorMessage());
                }
                
                // Wait for inventory page to load (shopping cart should be present)
                wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("inventory_container")
                ));
                
                // Additional wait for shopping cart to be visible
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("shopping_cart_container")
                ));
                
                // Small delay to ensure page is fully loaded
                Thread.sleep(500);
                
                return new InventoryPage(getDriver());
                
            } catch (org.openqa.selenium.TimeoutException te) {
                // If we got here, the page might have already navigated
                if (getDriver().getCurrentUrl().contains("inventory")) {
                    return new InventoryPage(getDriver());
                }
                throw new RuntimeException("Timeout during login: " + te.getMessage(), te);
            }
            
        } catch (Exception e) {
            // Take screenshot on failure
            takeScreenshot("login_failed_" + System.currentTimeMillis());
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }

    public void loginExpectingFailure(String username, String password) {
        try {
            enterUsername(username);
            enterPassword(password);
            
            // Wait for button to be visible and click
            WebElement button = wait.until(ExpectedConditions.visibilityOf(loginButton));
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", button);
            
            // Wait for error message with a short timeout
            WebDriverWait shortWait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.visibilityOf(errorMessage));
            
        } catch (Exception e) {
            takeScreenshot("login_failure_expected_" + System.currentTimeMillis());
            throw new RuntimeException("Expected login failure did not occur: " + e.getMessage(), e);
        }
    }
    
    private void takeScreenshot(String fileName) {
        try {
            File screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("screenshots/" + fileName + ".png"));
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }
}
