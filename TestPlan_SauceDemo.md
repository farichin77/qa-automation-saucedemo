---
title: Test Plan -- SauceDemo E-Commerce Website
---

# 1. Introduction

Tujuan dari test plan ini adalah memastikan bahwa seluruh fitur utama
pada website SauceDemo berjalan sesuai ekspektasi dengan menggunakan
framework automation berbasis Java, Selenium, TestNG, dan Page Object
Model (POM).

# 2. Scope

In-Scope:

-   - Autentikasi (Login/Logout)
    - Inventory & produk
    - Keranjang belanja (Cart)
    - Checkout & validasi form

Out-of-Scope:

-   - Integrasi payment gateway (karena hanya simulasi checkout)
    - Performance & load testing

# 3. Test Objectives

- Memvalidasi fungsi login (positif & negatif).
- Memastikan navigasi inventory, cart, dan checkout berjalan lancar.
- Menguji validasi form saat checkout.
- Menjamin integritas data antar halaman.

# 4. Test Approach

- Framework: Selenium + TestNG + POM
- Design Pattern: Page Object Model
- Data Driven Testing: digunakan untuk credential login dan variasi
input checkout
- Reporting: Allure Report
- Execution: automated test suite via Maven (run-tests.bat)

# 5. Test Environment

- OS: Windows 10+
- Browser: Chrome (latest, WebDriverManager)\
- Java Version: JDK 11+
- Build Tool: Maven

# 6. Test Items

- Pages: LoginPage, InventoryPage, CartPage, CheckoutPage\
- Tests: LoginTest, InventoryTest, CartTest, CheckoutTest

# 7. Test Cases Overview

Login:

-   - Valid login (standard_user)
    - Invalid login (wrong password)
    - Locked out user
    - Empty username/password
    - Logout

Inventory:

-   - Verifikasi jumlah produk (6)
    - Add to cart (single & multiple)
    - Verifikasi detail produk
    - Navigasi ke cart

Cart:

-   - Verifikasi jumlah item di cart
    - Hapus item dari cart
    - Verifikasi detail item di cart
    - Navigasi ke checkout

Checkout:

-   - Checkout sukses (lengkap isi form)
    - Validasi form kosong
    - Complete checkout flow

# 8. Entry Criteria

- Test environment siap
- Web Saucedemo dapat diakses
- Test data (credentials) tersedia

# 9. Exit Criteria

- Semua test suite berjalan dengan status Pass ≥ 95%
- Semua defect kritikal sudah ditangani

# 10. Risks & Mitigation

- Risiko: perubahan UI pada SauceDemo → Mitigasi: gunakan locator yang
stabil
- Risiko: perbedaan versi browser/driver → Mitigasi: WebDriverManager

# 11. Deliverables

- Test Plan
- Test Scripts (Java + Selenium + TestNG)\
- TestNG suite file (testng.xml)\
- Test Report (Allure)
