package com.saucedemo.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
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
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        driver.set(new ChromeDriver(options));
        getDriver().manage().window().maximize();
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));

        getDriver().get(BASE_URL);
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
