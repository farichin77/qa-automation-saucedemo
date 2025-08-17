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

        WebElement item = inventoryItems.get(index);
        scrollIntoView(item);

        WebElement addButton = item.findElement(By.cssSelector("button[data-test^='add-to-cart'], button[data-test^='remove']"));

        wait.until(ExpectedConditions.elementToBeClickable(addButton));
        clickWithRetry(addButton);

        // Tunggu sampai berubah ke "Remove" atau badge muncul
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));
        shortWait.until(ExpectedConditions.or(
                ExpectedConditions.attributeContains(addButton, "data-test", "remove"),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".shopping_cart_badge"))
        ));
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

    public CartPage navigateToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(cartLink));
        clickWithRetry(cartLink);

        wait.until(ExpectedConditions.urlContains("cart.html"));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".cart_list")));

        return new CartPage(driver);
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
