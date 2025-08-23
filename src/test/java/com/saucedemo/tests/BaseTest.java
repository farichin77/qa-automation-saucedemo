package com.saucedemo.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    protected WebDriverWait wait;
    protected final String BASE_URL = "https://www.saucedemo.com";

    public WebDriver getDriver() {
        return driver.get();
    }

    @BeforeSuite
    public void setUpSuite() {
        System.out.println("Setting up specific ChromeDriver version 139...");
        WebDriverManager.chromedriver().driverVersion("139").setup();
        System.out.println("WebDriver setup complete.");
    }

    /**
     * Handles any unexpected popups that might appear during test execution
     */
    protected void handleUnexpectedPopups() {
        try {
            // Handle common popups like "Change Password"
            JavascriptExecutor js = (JavascriptExecutor) driver.get();
            // Try to close any open modals or popups
            js.executeScript("var modals = document.querySelectorAll('.modal, .popup, [role=dialog]'); " +
                    "for (var i = 0; i < modals.length; i++) { " +
                    "   var modal = modals[i]; " +
                    "   var closeButton = modal.querySelector('[data-dismiss=modal], .close, [aria-label=Close]'); " +
                    "   if (closeButton) closeButton.click(); " +
                    "   else modal.style.display = 'none'; " +
                    "}");
            
            // Handle browser native alerts/prompts
            try {
                driver.get().switchTo().alert().dismiss();
            } catch (Exception e) {
                // No alert present, continue
            }
        } catch (Exception e) {
            System.out.println("No popups to handle or error handling popups: " + e.getMessage());
        }
    }

    @BeforeMethod
    public void setUp() throws InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.setCapability("se:bidi", false);

        // Disable password manager, autofill, and other popups
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("password_manager_enabled", false);
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("profile.password_manager_autofill_available", false);
        prefs.put("profile.password_manager_allow_show_passwords", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("autofill.profile_enabled", false);
        prefs.put("autofill.credit_card_enabled", false);
        prefs.put("autofill.password_manager_enabled", false);
        prefs.put("password_manager_enabled", false);
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.default_content_setting_values.credentials_enable_service", false);
        prefs.put("profile.default_content_setting_values.autofill.profile_enabled", false);
        prefs.put("profile.default_content_setting_values.autofill.credit_card_enabled", false);
        
        // Additional Chrome arguments to prevent password prompts and popups
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-autofill-keyboard-accessory-view");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-password-manager");
        options.addArguments("--disable-password-manager-reauthentication");
        options.addArguments("--disable-single-click-autofill");
        options.addArguments("--disable-blink-features=PasswordReveal");
        options.addArguments("--disable-blink-features=PasswordGeneration");
        options.addArguments("--disable-features=PasswordGeneration");
        options.addArguments("--disable-features=PasswordManager");
        options.addArguments("--disable-features=PasswordManagerOnboarding");
        options.addArguments("--disable-features=PasswordChange");
        options.addArguments("--disable-features=PasswordLeakDetection");
        options.addArguments("--disable-features=PasswordCheck");
        
        // Set experimental options
        options.setExperimentalOption("prefs", prefs);
        options.setExperimentalOption("excludeSwitches", 
            new String[]{"enable-automation", "enable-logging", "password-manager"});
        options.setExperimentalOption("useAutomationExtension", false);

        // Try to initialize ChromeDriver
        WebDriver webDriver = null;
        int attempts = 0;
        while (attempts < 3 && webDriver == null) {
            try {
                webDriver = new ChromeDriver(options);
                driver.set(webDriver);
                
                // Set timeouts
                webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
                webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
                
                // Try to navigate to base URL
                webDriver.get(BASE_URL);
                
                // Verify page loaded
                if (webDriver.getTitle().contains("Swag Labs")) {
                    System.out.println("Successfully loaded: " + BASE_URL);
                    break;
                }
                
            } catch (Exception e) {
                System.err.println("Attempt " + (attempts + 1) + " failed: " + e.getMessage());
                attempts++;
                if (webDriver != null) {
                    webDriver.quit();
                    webDriver = null;
                }
                if (attempts < 3) {
                    Thread.sleep(2000); // Wait before retry
                }
            }
        }
        
        if (webDriver == null) {
            throw new RuntimeException("Failed to initialize WebDriver after 3 attempts");
        }

        // Initialize explicit wait
        wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
        
        // Add a small delay to ensure page is fully loaded
        Thread.sleep(1000);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Take screenshot if test failed
        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot(result.getName());
        }
        
        // Add delay before closing browser
        try {
            Thread.sleep(2000); // 2 seconds delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Close browser
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
    }
    
    @Attachment(value = "Screenshot on failure", type = "image/png")
    public byte[] takeScreenshot(String testName) {
        return ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
    }

    protected void performLogin(String username, String password) {
        getDriver().findElement(org.openqa.selenium.By.id("user-name")).clear();
        getDriver().findElement(org.openqa.selenium.By.id("user-name")).sendKeys(username);
        getDriver().findElement(org.openqa.selenium.By.id("password")).clear();
        getDriver().findElement(org.openqa.selenium.By.id("password")).sendKeys(password);
        getDriver().findElement(org.openqa.selenium.By.id("login-button")).click();
    }
}
