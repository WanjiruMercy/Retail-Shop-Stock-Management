-- Case 3: Retail Shop Stock
-- Run this SQL to create the database tables and insert sample data

CREATE TABLE Products (
    product_id INTEGER PRIMARY KEY,
    product_name TEXT NOT NULL,
    price REAL NOT NULL,
    quantity_in_stock INTEGER NOT NULL
);

CREATE TABLE Sales (
    sale_id INTEGER PRIMARY KEY,
    product_id INTEGER NOT NULL,
    quantity_sold INTEGER NOT NULL,
    sale_date TEXT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

-- 6 sample products (one with stock below 5)
INSERT INTO Products VALUES (1, 'Sugar 2kg', 250.00, 20);
INSERT INTO Products VALUES (2, 'Rice 5kg', 650.00, 15);
INSERT INTO Products VALUES (3, 'Soap Bar', 120.00, 3);
INSERT INTO Products VALUES (4, 'Cooking Oil 1L', 380.00, 10);
INSERT INTO Products VALUES (5, 'Salt 1kg', 60.00, 25);
INSERT INTO Products VALUES (6, 'Tea Leaves 500g', 450.00, 8);

-- 3 sample sales
INSERT INTO Sales VALUES (1, 1, 2, '2026-11-10');
INSERT INTO Sales VALUES (2, 2, 1, '2026-11-10');
INSERT INTO Sales VALUES (3, 4, 3, '2026-11-11');