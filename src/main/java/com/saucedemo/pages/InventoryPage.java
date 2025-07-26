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

    @FindBy(css = "button[data-test^='add-to-cart']")
    private List<WebElement> addToCartButtons;

    @FindBy(css = "#react-burger-menu-btn")
    private WebElement menuButton;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    public InventoryPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public int getNumberOfProducts() {
        return inventoryItems.size();
    }

    public void addProductToCart(int index) {
        wait.until(ExpectedConditions.visibilityOfAllElements(addToCartButtons));
        if (index >= 0 && index < addToCartButtons.size()) {
            wait.until(ExpectedConditions.elementToBeClickable(addToCartButtons.get(index))).click();
        }
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

    public CartPage navigateToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(cartLink)).click();
        wait.until(ExpectedConditions.urlContains("cart.html"));
        return new CartPage(driver);
    }

    public void openMenu() {
        menuButton.click();
    }

    public LoginPage logout() {
        wait.until(ExpectedConditions.elementToBeClickable(menuButton)).click();
        // Wait for menu to slide out and become visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("bm-menu-wrap")));
        // Additional wait for menu animation
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
        // Wait for redirect to login page
        wait.until(ExpectedConditions.urlContains("/"));
        return new LoginPage(driver);
    }
}
