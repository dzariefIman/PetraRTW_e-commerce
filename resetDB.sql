-- PetraRTW Database Reset Script
-- Drops all tables. Run BEFORE schema.sql, then seedData.sql.
-- Usage order: resetDB.sql -> schema.sql -> seedData.sql

DROP TABLE issue_chat;
DROP TABLE issues;
DROP TABLE feedback;
DROP TABLE payments;
DROP TABLE orders;
DROP TABLE cart;
DROP TABLE advertisements;
DROP TABLE products;
DROP TABLE customers;
DROP TABLE sales_admin;