-- PetraRTW Database Schema
-- Matches class diagram exactly

-- Staff
CREATE TABLE staff (
    staff_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    staff_name VARCHAR(100),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    staff_email VARCHAR(100),
    staff_address VARCHAR(255),
    staff_phone_num VARCHAR(20),
    profile_picture VARCHAR(500),
    PRIMARY KEY (staff_id)
);

-- Customers
CREATE TABLE customers (
    cust_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    cust_name VARCHAR(100),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    cust_email VARCHAR(100),
    cust_address VARCHAR(255),
    cust_phone_num VARCHAR(20),
    PRIMARY KEY (cust_id)
);

-- Shop Products
CREATE TABLE shop_products (
    shop_product_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    shop_product_title VARCHAR(255) NOT NULL,
    shop_product_price DECIMAL(10,2),
    shop_product_desc LONG VARCHAR,
    product_image VARCHAR(500),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    collection VARCHAR(100),
    item_type VARCHAR(20) DEFAULT 'cloth',
    size_s INTEGER DEFAULT 0,
    size_m INTEGER DEFAULT 0,
    size_l INTEGER DEFAULT 0,
    size_xl INTEGER DEFAULT 0,
    advertisement_id INTEGER,
    PRIMARY KEY (shop_product_id)
);

-- Advertisements / Promotions
CREATE TABLE advertisements (
    ads_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    ads_title VARCHAR(255),
    ads_desc LONG VARCHAR,
    product_image VARCHAR(500),
    product_id INTEGER,
    start_date VARCHAR(20),
    end_date VARCHAR(20),
    status VARCHAR(20) DEFAULT 'draft',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP,
    collection VARCHAR(100),
    staff_id INTEGER,
    PRIMARY KEY (ads_id)
);

-- Purchases / Orders
CREATE TABLE purchases (
    purchase_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    cust_id INTEGER,
    order_num VARCHAR(20),
    payment_method VARCHAR(20),
    purchase_desc LONG VARCHAR,
    size VARCHAR(10),
    quantity INTEGER DEFAULT 1,
    item_sub_total DECIMAL(10,2),
    shipping_fee DECIMAL(10,2) DEFAULT 0,
    voucher_amount DECIMAL(10,2) DEFAULT 0,
    total_price DECIMAL(10,2),
    product_image VARCHAR(500),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    shop_product_id INTEGER,
    PRIMARY KEY (purchase_id)
);

-- Issues / Complaints
CREATE TABLE issues (
    issue_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    ticket_num INTEGER,
    cust_id INTEGER,
    purchase_id INTEGER,
    order_num VARCHAR(20),
    title VARCHAR(255) NOT NULL,
    issue_desc LONG VARCHAR,
    product_image VARCHAR(500),
    status VARCHAR(20) DEFAULT 'New',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_time TIMESTAMP,
    staff_id INTEGER,
    PRIMARY KEY (issue_id)
);

-- Issue Comments
CREATE TABLE issue_comments (
    issue_comments_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    issue_id INTEGER NOT NULL,
    staff_id INTEGER,
    cust_id INTEGER,
    comment_text LONG VARCHAR NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    issue_attachment VARCHAR(500),
    updated_time TIMESTAMP,
    reply_to_comment_id INTEGER,
    PRIMARY KEY (issue_comments_id)
);

-- ALTER TABLE issue_comments ADD COLUMN cust_id INTEGER;
-- ALTER TABLE issue_comments ADD COLUMN reply_to_comment_id INTEGER;

-- Feedback
CREATE TABLE feedback (
    feedback_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    cust_id INTEGER,
    feedback_text LONG VARCHAR,
    product_image VARCHAR(500),
    status VARCHAR(20),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_num VARCHAR(20),
    rating INTEGER,
    purchase_id INTEGER,
    product_name VARCHAR(255),
    PRIMARY KEY (feedback_id)
);

-- Cart (session cart stored in DB)
CREATE TABLE cart (
    cart_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    cust_id INTEGER NOT NULL,
    shop_product_id INTEGER NOT NULL,
    size VARCHAR(5) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (cart_id),
    FOREIGN KEY (cust_id) REFERENCES customers(cust_id),
    FOREIGN KEY (shop_product_id) REFERENCES shop_products(shop_product_id)
);