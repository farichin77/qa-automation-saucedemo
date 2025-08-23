package com.saucedemo.tests.listeners;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.saucedemo.tests.BaseTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener {
    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
    private static final String SCREENSHOT_DIR = "screenshots";

    static {
        // Create screenshots directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));
        } catch (IOException e) {
            logger.error("Failed to create screenshots directory", e);
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Starting test: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test passed: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.error("Test failed: {}", testName);
        
        try {
            // Get the test class instance
            Object testInstance = result.getInstance();
            if (testInstance == null) {
                logger.error("Test instance is null, cannot capture screenshot");
                return;
            }

            // Get WebDriver from test instance
            WebDriver driver = null;
            if (testInstance instanceof BaseTest) {
                driver = ((BaseTest) testInstance).getDriver();
            }

            if (driver == null) {
                logger.error("WebDriver is null, cannot capture screenshot");
                return;
            }

            logger.info("Attempting to capture screenshot for failed test: {}", testName);
            
            try {
                // Create screenshots directory if it doesn't exist
                Path screenshotDir = Paths.get(SCREENSHOT_DIR);
                if (!Files.exists(screenshotDir)) {
                    Files.createDirectories(screenshotDir);
                }
                
                // Generate filename with timestamp
                String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS").format(new Date());
                String safeTestName = testName.replaceAll("[^a-zA-Z0-9-_.]", "_");
                String fileName = String.format("%s/%s-%s.png", 
                    SCREENSHOT_DIR, safeTestName, timestamp);
                
                logger.info("Saving screenshot to: {}", fileName);
                
                // Take screenshot and save to file
                byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                try (FileOutputStream out = new FileOutputStream(fileName)) {
                    out.write(screenshotBytes);
                    logger.info("Screenshot saved successfully to: {}", fileName);
                }
                
                // Attach to Allure report
                attachScreenshotToAllure(driver, testName);
                
            } catch (Exception e) {
                logger.error("Failed to capture screenshot: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error("Unexpected error in test failure handler: {}", e.getMessage(), e);
        }
    }

    @Attachment(value = "Screenshot on failure", type = "image/png")
    private byte[] attachScreenshotToAllure(WebDriver driver, String testName) {
        try {
            logger.info("Attaching screenshot to Allure report for test: {}", testName);
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            logger.error("Failed to attach screenshot to Allure: {}", e.getMessage(), e);
            return new byte[0];
        }
    }
}
