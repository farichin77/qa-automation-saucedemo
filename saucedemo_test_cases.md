# 🧪 Test Case SauceDemo (19 Cases with Priority)

## 1. Login (5 Test Case)

| ID     | Test Case               | Objective                            | Steps                                                                 | Expected Result                                                   | Priority | Status |
|--------|--------------------------|--------------------------------------|----------------------------------------------------------------------|------------------------------------------------------------------|----------|------|
| TC-001 | Successful Login         | User berhasil login dengan kredensial valid | Input username = `standard_user`, password = `secret_sauce`           | User diarahkan ke **Inventory Page**                             | High     | Pass |
| TC-002 | Invalid Login (wrong password) | User tidak dapat login dengan password salah | Input username = `standard_user`, password = `wrong_pass`             | Error message: *"Username and password do not match"*            | High     |  Pass |
| TC-003 | Locked Out User          | Locked out user tidak dapat login     | Input username = `locked_out_user`, password = `secret_sauce`         | Error message: *"Sorry, this user has been locked out."*         | High     | Pass |
| TC-004 | Empty Username/Password  | Tidak bisa login tanpa username/password | Kosongkan username & password → klik login                           | Error: *"Username is required"* / *"Password is required"*       | Medium   | Pass |
| TC-005 | Logout                   | User dapat logout                    | Login → Klik menu (☰) → Logout                                       | User kembali ke **Login Page**                                   | Medium   | Pass |

---

## 2. Inventory (5 Test Case)

| ID     | Test Case                | Objective                                   | Steps                                                                 | Expected Result                                   | Priority | Status |
|--------|---------------------------|---------------------------------------------|----------------------------------------------------------------------|--------------------------------------------------|----------|-------|
| TC-006 | Verify Product Count      | Pastikan jumlah produk sesuai (6)            | Login → Buka Inventory Page                                           | Ada **6 produk** yang ditampilkan                 | High     | Pass  |
| TC-007 | Add to Cart (Single)      | Tambahkan 1 produk ke cart                   | Klik "Add to cart" pada 1 produk                                      | Cart badge menunjukkan angka **1**                | High     | Pass  |
| TC-008 | Add to Cart (Multiple)    | Tambahkan lebih dari 1 produk ke cart        | Klik "Add to cart" pada beberapa produk                               | Cart badge sesuai jumlah produk dipilih           | High     |  Pass |
| TC-009 | Verify Product Detail     | Pastikan detail produk benar                 | Klik salah satu produk                                                 | Halaman detail sesuai dengan produk dipilih       | Medium   |  Pass |
| TC-010 | Navigate to Cart          | User bisa ke halaman cart                    | Klik icon **cart**                                                    | User diarahkan ke halaman **Cart Page**           | High     |  Pass |

---

## 3. Cart (4 Test Case)

| ID     | Test Case                | Objective                                   | Steps                                                                 | Expected Result                                   | Priority | Status |
|--------|---------------------------|---------------------------------------------|----------------------------------------------------------------------|--------------------------------------------------|----------|--------|
| TC-011 | Verify Cart Item Count    | Jumlah item sesuai                          | Tambahkan produk → Buka cart                                          | Jumlah item sesuai dengan yang ditambahkan        | High     | Pass   |
| TC-012 | Remove Item from Cart     | User dapat menghapus item                   | Klik tombol "Remove" pada item di cart                                | Item hilang dari cart                             | High     | Pass   |
| TC-013 | Verify Cart Item Detail   | Pastikan detail sesuai                       | Tambahkan produk → Cek di cart                                        | Nama, harga sesuai dengan produk dipilih          | Medium   | Pass   |
| TC-014 | Navigate to Checkout      | User dapat melanjutkan ke checkout           | Klik tombol "Checkout"                                                | User diarahkan ke **Checkout Page**               | High     | Pass   |

---

## 4. Checkout (6 Test Case)

| ID     | Test Case                   | Objective                                                                             | Steps                                                            | Expected Result                                    | Priority | Status |
|--------|-----------------------------|---------------------------------------------------------------------------------------|------------------------------------------------------------------|----------------------------------------------------|----------|--------|
| TC-015 | Checkout with Valid Data    | User berhasil checkout dengan form lengkap                                            | Isi First Name, Last Name, Zip Code → Continue → Finish          | Order sukses, muncul halaman **Checkout Complete** | High     | Pass   |
| TC-016 | Checkout Empty Form         | Tidak bisa lanjut tanpa isi form                                                      | Kosongkan field → Klik continue                                  | Error muncul (misalnya "First Name is required")   | High     | Pass   |
| TC-017 | Checkout Missing First Name | Validasi jika First Name kosong                                                       | Isi Last Name + Zip Code saja → Klik continue                    | Error: "First Name is required"                    | Medium   | Pass   |
| TC-018 | Checkout Missing Last Name  | Validasi jika Last Name kosong                                                        | Isi First Name + Zip Code saja → Klik continue                   | Error: "Last Name is required"                     | Medium   | Pass   |
| TC-019 | Complete Checkout Flow      | Pastikan full flow checkout berjalan lancar                                           | Login → Add product → Cart → Checkout → Complete                 | Order complete page tampil                         | High     | Pass   |
| TC-020 | Cekout With empty Card      | Pastikan tombol cekout Disabled ketika tidak ada produk yang di masukkan ke keranjang | Login → Add product → No Cart → Tombol Cekout Disable → Complete | tombol cekout Disabled                             | medium   | failed |
---


