package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        // Increase wait time to 20 seconds for better stability
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public WebDriver getDriver() {
        return driver;
    }
    
    protected void waitForPageLoad() {
        try {
            // Wait for document.readyState to be complete
            wait.until(webDriver -> {
                String state = ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").toString();
                return state.equals("complete");
            });
            // Additional wait for jQuery if it exists
            if ((Boolean) ((JavascriptExecutor) driver).executeScript("return window.jQuery != undefined")) {
                wait.until(webDriver -> (Boolean) ((JavascriptExecutor) webDriver)
                    .executeScript("return jQuery.active == 0"));
            }
        } catch (Exception e) {
            System.out.println("Page load wait interrupted: " + e.getMessage());
        }
    }
    
    protected void shortWait() {
    // shortWait dihapus agar tidak memperlambat eksekusi
    }
    
    protected void waitForUrlChange(String urlFragment) {
        wait.until(ExpectedConditions.urlContains(urlFragment));
        waitForPageLoad();
        shortWait(); // Give a short pause after page load
    }

    protected void waitForClickable(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected void scrollIntoView(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        } catch (Exception ignored) {}
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void clickWithRetry(WebElement element) {
        waitForClickable(element);
        try {
            element.click();
        } catch (Exception e1) {
            scrollIntoView(element);
            try {
                element.click();
            } catch (Exception e2) {
                jsClick(element);
            }
        }
    }
}
