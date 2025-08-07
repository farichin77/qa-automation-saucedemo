package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class CartPage extends BasePage {
    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;

    @FindBy(css = ".inventory_item_name")
    private List<WebElement> itemNames;

    @FindBy(css = ".inventory_item_price")
    private List<WebElement> itemPrices;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    @FindBy(css = ".cart_quantity")
    private List<WebElement> itemQuantities;

    @FindBy(css = "button[data-test^='remove']")
    private List<WebElement> removeButtons;

    public CartPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public int getNumberOfItems() {
        try {
            // Wait for cart items or empty cart
            wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".cart_item")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".cart_list"))
            ));
            return getDriver().findElements(By.cssSelector(".cart_item")).size();
        } catch (Exception e) {
            // If no items found, return 0
            return 0;
        }
    }

    public String getItemName(int index) {
        wait.until(ExpectedConditions.visibilityOfAllElements(itemNames));
        if (index >= 0 && index < itemNames.size()) {
            return wait.until(ExpectedConditions.visibilityOf(itemNames.get(index))).getText();
        }
        return null;
    }

    public double getItemPrice(int index) {
        if (index >= 0 && index < itemPrices.size()) {
            String priceText = itemPrices.get(index).getText().replace("$", "");
            return Double.parseDouble(priceText);
        }
        return 0.0;
    }

    public int getItemQuantity(int index) {
        if (index >= 0 && index < itemQuantities.size()) {
            return Integer.parseInt(itemQuantities.get(index).getText());
        }
        return 0;
    }

    public void removeItem(int index) {
        // Wait for cart items to be present
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".cart_item")));
        
        // Get all remove buttons that are currently visible
        List<WebElement> buttons = getDriver().findElements(By.cssSelector("button[id^='remove-']"));
        
        if (index >= 0 && index < buttons.size()) {
            WebElement buttonToClick = buttons.get(index);
            // Wait specifically for this button to be clickable
            wait.until(ExpectedConditions.elementToBeClickable(buttonToClick));
            // Store item ID before removing
            String itemId = buttonToClick.getAttribute("id");
            buttonToClick.click();
            // Wait for specific item to be removed using its ID
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(itemId)));
        }
    }

    public CheckoutPage proceedToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton)).click();
        wait.until(ExpectedConditions.urlContains("checkout-step-one.html"));
        return new CheckoutPage(driver);
    }
}
