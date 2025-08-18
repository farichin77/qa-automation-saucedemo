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

    @BeforeMethod
    public void setUp() throws InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.setCapability("se:bidi", false);

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
        if (result.getStatus() == ITestResult.FAILURE) {
            saveScreenshot();
        }
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] saveScreenshot() {
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
