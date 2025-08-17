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
    public void setUp() {
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--ignore-certificate-errors");
            options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
            options.setCapability("se:bidi", false); // Disable BiDi protocol

            WebDriver webDriver = new ChromeDriver(options);
            driver.set(webDriver);

            // Set timeouts
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

            // Explicit wait
            wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));

            // Langsung buka base URL
            webDriver.get(BASE_URL);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to initialize WebDriver: " + e.getMessage());
            throw e;
        }
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
