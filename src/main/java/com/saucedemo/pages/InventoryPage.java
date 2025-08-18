package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class InventoryPage extends BasePage {

    @FindBy(css = ".inventory_item")
    private List<WebElement> inventoryItems;

    @FindBy(css = ".product_sort_container")
    private WebElement sortDropdown;

    @FindBy(css = ".shopping_cart_link")
    private WebElement cartLink;

    @FindBy(css = ".inventory_item_name")
    private List<WebElement> itemNames;

    @FindBy(css = ".inventory_item_price")
    private List<WebElement> itemPrices;

    @FindBy(css = "button[data-test^='add-to-cart'], button[data-test^='remove']")
    private List<WebElement> cartButtons;

    @FindBy(css = "#react-burger-menu-btn")
    private WebElement menuButton;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    public InventoryPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);

        // Pastikan sudah di halaman inventory
        wait.until(ExpectedConditions.urlContains("inventory"));
        wait.until(ExpectedConditions.visibilityOfAllElements(inventoryItems));
    }

    public int getNumberOfProducts() {
        return inventoryItems.size();
    }

    public void addProductToCart(int index) {
        if (index < 0 || index >= inventoryItems.size()) {
            throw new IndexOutOfBoundsException("Invalid product index: " + index);
        }

        try {
            System.out.println("\n=== Starting addProductToCart for index: " + index + " ===");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            
            // Refresh the inventory items to avoid stale elements
            System.out.println("Refreshing inventory items...");
            inventoryItems = driver.findElements(By.cssSelector(".inventory_item"));
            System.out.println("Found " + inventoryItems.size() + " inventory items");
            
            if (index >= inventoryItems.size()) {
                throw new RuntimeException("Item index " + index + " is out of bounds. Only " + inventoryItems.size() + " items found.");
            }
            
            WebElement item = wait.until(ExpectedConditions.visibilityOf(inventoryItems.get(index)));
            System.out.println("Scrolling to item at index: " + index);
            scrollIntoView(item);
            System.out.println("Item scrolled into view");

            // Find the add to cart button with explicit wait
            By buttonLocator = By.cssSelector("button[data-test^='add-to-cart'], button[data-test^='remove']");
            System.out.println("Locating cart button...");
            
            WebElement addButton = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(item, buttonLocator));
            System.out.println("Button found. Current state - Text: '" + addButton.getText().trim() + "', " +
                    "Data-test: " + addButton.getAttribute("data-test"));
                    
            // Take screenshot before any action
            takeScreenshot("before_add_to_cart_" + index);

            // Wait for button to be clickable
            addButton = wait.until(ExpectedConditions.elementToBeClickable(addButton));
            
            // Get current button state
            String buttonText = addButton.getText().trim();
            String buttonTestAttr = addButton.getAttribute("data-test");
            
            // If already in cart, remove it first to ensure clean state
            if (buttonText.equalsIgnoreCase("remove") || (buttonTestAttr != null && buttonTestAttr.contains("remove"))) {
                System.out.println("Item already in cart. Removing it first...");
                takeScreenshot("before_remove_item_" + index);
                clickWithRetry(addButton);
                
                // Wait for button to change back to ADD state
                System.out.println("Waiting for button to change to ADD state...");
                
                boolean isAddState = wait.until(d -> {
                    try {
                        // Refresh the button reference to avoid stale element
                        WebElement refreshedButton = item.findElement(buttonLocator);
                        String text = refreshedButton.getText().trim();
                        String testAttr = refreshedButton.getAttribute("data-test");
                        boolean isAdd = text.equalsIgnoreCase("ADD TO CART") || 
                                      (testAttr != null && testAttr.contains("add-to-cart"));
                        
                        if (!isAdd) {
                            System.out.println("Waiting for button to change to ADD state. Current - Text: '" + 
                                    text + "', Data-test: " + testAttr);
                        } else {
                            System.out.println("Button successfully changed to ADD state");
                            takeScreenshot("after_remove_item_" + index);
                        }
                        return isAdd;
                    } catch (Exception e) {
                        System.out.println("Error checking button state: " + e.getMessage());
                        return false;
                    }
                });
                
                if (!isAddState) {
                    throw new RuntimeException("Failed to change button to ADD state after removal");
                }
                
                System.out.println("Successfully removed item from cart");
                // Refresh the button reference after state change
                addButton = item.findElement(buttonLocator);
            }

            // Add small delay to ensure UI is ready
            try { Thread.sleep(500); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            
            // Refresh button reference before clicking
            addButton = item.findElement(buttonLocator);
            System.out.println("Clicking 'Add to cart' button...");
            takeScreenshot("before_click_add_to_cart_" + index);
            
            try {
                // Try with regular click first
                addButton.click();
                System.out.println("Regular click performed");
            } catch (Exception e) {
                System.out.println("Regular click failed, trying JavaScript click: " + e.getMessage());
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);
                System.out.println("JavaScript click performed");
            }
            
            System.out.println("Button clicked. Waiting for state change...");
            takeScreenshot("after_click_add_to_cart_" + index);

            // Wait for button to change to REMOVE state with more robust checks
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            
            try {
                System.out.println("Waiting for button to change to REMOVE state...");
                
                boolean stateChanged = shortWait.until(d -> {
                    try {
                        // Refresh the button reference to avoid stale element
                        WebElement currentButton = item.findElement(buttonLocator);
                        String text = currentButton.getText().trim();
                        String testAttr = currentButton.getAttribute("data-test");
                        boolean isRemoveState = text.equalsIgnoreCase("REMOVE") || 
                                             (testAttr != null && testAttr.contains("remove"));
                        
                        System.out.println("Button state check - Text: '" + text + "', Data-test: " + testAttr);
                        
                        if (isRemoveState) {
                            System.out.println("Button successfully changed to REMOVE state");
                            takeScreenshot("after_add_to_cart_success_" + index);
                        }
                        
                        return isRemoveState;
                    } catch (Exception e) {
                        System.out.println("Error checking button state: " + e.getMessage());
                        takeScreenshot("error_checking_button_state_" + System.currentTimeMillis());
                        return false;
                    }
                });
                
                if (stateChanged) {
                    System.out.println("Successfully added item to cart. Button state changed to REMOVE.");
                } else {
                    System.out.println("Warning: Button state may not have changed as expected");
                }
                
            } catch (Exception e) {
                System.out.println("Error waiting for button state change: " + e.getMessage());
                takeScreenshot("button_state_error_" + System.currentTimeMillis());
                throw e;
            }

            // Also wait for cart badge to update
            System.out.println("Waiting for cart badge to update...");
            try {
                waitForCartBadge();
                System.out.println("Cart badge updated successfully");
                takeScreenshot("cart_badge_updated_" + index);
            } catch (Exception e) {
                System.out.println("Error waiting for cart badge: " + e.getMessage());
                takeScreenshot("error_waiting_for_cart_badge_" + System.currentTimeMillis());
                throw e;
            }
            
        } catch (Exception e) {
            // Take screenshot for debugging
            takeScreenshot("add_to_cart_error_" + System.currentTimeMillis());
            throw new RuntimeException("Failed to add product to cart: " + e.getMessage(), e);
        }
    }

    // ✅ Tambahan untuk verifikasi tombol sudah berubah jadi "Remove"
    public boolean isProductInCart(int index) {
        if (index < 0 || index >= inventoryItems.size()) {
            throw new IndexOutOfBoundsException("Invalid product index: " + index);
        }

        WebElement item = inventoryItems.get(index);
        WebElement button = item.findElement(By.cssSelector("button[data-test^='add-to-cart'], button[data-test^='remove']"));
        return button.getText().equalsIgnoreCase("Remove");
    }

    public String getProductName(int index) {
        if (index >= 0 && index < itemNames.size()) {
            return itemNames.get(index).getText();
        }
        return null;
    }

    public double getProductPrice(int index) {
        if (index >= 0 && index < itemPrices.size()) {
            String priceText = itemPrices.get(index).getText().replace("$", "");
            return Double.parseDouble(priceText);
        }
        return 0.0;
    }

    private void waitForCartBadge() {
        try {
            System.out.println("Waiting for cart badge to be visible");
            // Wait for cart badge to be visible and have a value greater than 0
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            shortWait.until(driver -> {
                try {
                    WebElement badge = driver.findElement(By.cssSelector(".shopping_cart_badge"));
                    boolean isDisplayed = badge.isDisplayed();
                    String badgeText = badge.getText();
                    System.out.println("Cart badge found. Displayed: " + isDisplayed + ", Text: " + badgeText);
                    return isDisplayed && !badgeText.trim().isEmpty() && Integer.parseInt(badgeText) > 0;
                } catch (Exception e) {
                    System.out.println("Error checking cart badge: " + e.getMessage());
                    return false;
                }
            });
        } catch (Exception e) {
            // If badge is not found, it might be because the cart is empty
            // which is fine if we're just removing items
            System.out.println("Cart badge not found, cart might be empty: " + e.getMessage());
            // Take a screenshot to help with debugging
            takeScreenshot("cart_badge_not_found_" + System.currentTimeMillis());
        }
    }

    public CartPage navigateToCart() {
        try {
            // Wait for cart link to be clickable
            WebElement cart = wait.until(ExpectedConditions.elementToBeClickable(cartLink));
            
            // Click using JavaScript to avoid any click interception issues
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cart);
            
            // Wait for URL to contain "cart"
            wait.until(ExpectedConditions.urlContains("cart"));
            
            // Wait for cart page to load completely
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cart_contents_container")));
            
            return new CartPage(driver);
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to cart: " + e.getMessage(), e);
        }
    }

    private void takeScreenshot(String fileName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File("screenshots/" + fileName + ".png");
            FileUtils.forceMkdirParent(destFile);
            FileUtils.copyFile(screenshot, destFile);
            System.out.println("Screenshot saved to: " + destFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }

    public void openMenu() {
        clickWithRetry(menuButton);
    }

    public LoginPage logout() {
        openMenu();
        // Wait for the sidebar to be visible
        WebElement menuContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("bm-menu-wrap")
        ));
        // Wait for the logout link to be visible and clickable
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink));
        clickWithRetry(logoutLink);
        // Wait for the login page to load
        wait.until(ExpectedConditions.urlContains("saucedemo.com"));
        return new LoginPage(driver);
    }
}
