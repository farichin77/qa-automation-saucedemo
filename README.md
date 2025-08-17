# Sauce Demo E-Commerce Test Automation Framework

![Java](https://img.shields.io/badge/Java-11-red)
![Selenium](https://img.shields.io/badge/Selenium-4.10.0-green)
![TestNG](https://img.shields.io/badge/TestNG-7.7.1-orange)
![Allure](https://img.shields.io/badge/Allure-2.23.0-blue)

## Overview
This project demonstrates a robust test automation framework for the Sauce Demo e-commerce website (https://www.saucedemo.com). It showcases best practices in test automation including Page Object Model, data-driven testing, and comprehensive reporting.

## Project Structure

```
src/
├── main/java/com/saucedemo/pages/
│   ├── LoginPage.java
│   ├── InventoryPage.java
│   ├── CartPage.java
│   └── CheckoutPage.java
└── test/java/com/saucedemo/tests/
    ├── BaseTest.java
    ├── LoginTest.java
    ├── InventoryTest.java
    ├── CartTest.java
    └── CheckoutTest.java
```

## Prerequisites

- Java JDK 11 or higher
- Maven
- Chrome browser

## Test Cases Covered

### Login Tests
- Successful login
- Locked out user
- Invalid credentials
- Empty credentials
- Logout functionality

### Inventory Tests
- Product count verification
- Adding single product to cart
- Adding multiple products to cart
- Product details verification
- Cart navigation

### Cart Tests
- Cart item count verification
- Remove items from cart
- Cart item details verification
- Checkout navigation

### Checkout Tests
- Successful checkout
- Form validation (empty fields)
- Complete checkout flow

## Running the Tests

1. Clone the repository
2. Navigate to the project root directory
3. Run the tests using Maven and alurre report:
   ```
   .\run-tests.bat

   ```

## Configuration

The test suite configuration is defined in `testng.xml`. You can modify this file to run specific test classes or methods.

## Notes

- The tests use Chrome WebDriver by default
- WebDriverManager is used to handle driver binaries automatically
- Tests are designed to run independently
- Each test class focuses on specific functionality of the application
