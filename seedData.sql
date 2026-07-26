-- PetraRTW Seed Data (Apache Derby / ij)
-- Generated from SeedData.java

connect 'jdbc:derby://localhost:1527/petra;user=petra;password=petra';

-- =============================================
-- STAFF
-- =============================================
INSERT INTO sales_admin (staff_name, username, password, staff_email, staff_phone_num)
VALUES ('Admin Staff', 'admin', 'hashed_admin123', 'admin@petra.rtw', '014-9876543');

-- =============================================
-- CUSTOMERS
-- =============================================
INSERT INTO customers (cust_name, username, password, cust_email, cust_address, cust_phone_num)
VALUES ('Ahmad Razak', 'ahmad_razak', 'hashed_password123', 'ahmad.razak@email.com', '12 Jalan Bukit Bintang, Kuala Lumpur', '012-3456789');

INSERT INTO customers (cust_name, username, password, cust_email, cust_address, cust_phone_num)
VALUES ('Siti Nurhaliza', 'siti_nur', 'hashed_password123', 'siti.nurhaliza@email.com', '45 Jalan Tun Razak, Kuala Lumpur', '013-4567890');

INSERT INTO customers (cust_name, username, password, cust_email, cust_address, cust_phone_num)
VALUES ('Muhammad Irfan', 'irfan_m', 'hashed_password123', 'm.irfan@email.com', '88 Jalan Ampang, Kuala Lumpur', '014-5678901');

INSERT INTO customers (cust_name, username, password, cust_email, cust_address, cust_phone_num)
VALUES ('Nurul Aisyah', 'aisyah_nurul', 'hashed_password123', 'nurul.aisyah@email.com', '23 Jalan Masjid India, Kuala Lumpur', '015-6789012');

INSERT INTO customers (cust_name, username, password, cust_email, cust_address, cust_phone_num)
VALUES ('Raj Kumar', 'raj_kumar', 'hashed_password123', 'raj.kumar@email.com', '67 Jalan Ipoh, Kuala Lumpur', '016-7890123');

-- =============================================
-- PRODUCTS
-- =============================================
INSERT INTO products (product_title, product_price, product_desc, product_image, collection, item_type, size_s, size_m, size_l, size_xl)
VALUES ('Classic Black T-Shirt', 39.90, 'Premium cotton classic black t-shirt for everyday wear.', 'default-product.png', 'Essentials', 'cloth', 10, 15, 12, 8);

INSERT INTO products (product_title, product_price, product_desc, product_image, collection, item_type, size_s, size_m, size_l, size_xl)
VALUES ('Urban Streetwear Hoodie', 89.90, 'Comfortable streetwear hoodie with modern fit.', 'default-product.png', 'Streetwear', 'cloth', 8, 10, 10, 6);

INSERT INTO products (product_title, product_price, product_desc, product_image, collection, item_type, size_s, size_m, size_l, size_xl)
VALUES ('Slim Fit Chinos', 59.90, 'Tailored slim fit chinos for a sharp look.', 'default-product.png', 'Essentials', 'cloth', 12, 12, 10, 8);

INSERT INTO products (product_title, product_price, product_desc, product_image, collection, item_type, size_s, size_m, size_l, size_xl)
VALUES ('Vintage Denim Jacket', 119.90, 'Classic vintage wash denim jacket.', 'default-product.png', 'Classic', 'cloth', 6, 8, 10, 7);

INSERT INTO products (product_title, product_price, product_desc, product_image, collection, item_type, size_s, size_m, size_l, size_xl)
VALUES ('Athletic Joggers', 49.90, 'Lightweight joggers perfect for sports or casual wear.', 'default-product.png', 'Activewear', 'cloth', 10, 12, 8, 6);

INSERT INTO products (product_title, product_price, product_desc, product_image, collection, item_type, size_s, size_m, size_l, size_xl)
VALUES ('Graphic Print Sweatshirt', 69.90, 'Bold graphic print sweatshirt for a statement look.', 'default-product.png', 'Streetwear', 'cloth', 8, 10, 8, 5);

INSERT INTO products (product_title, product_price, product_desc, product_image, collection, item_type, size_s, size_m, size_l, size_xl)
VALUES ('Formal Oxford Shirt', 79.90, 'Classic Oxford button-down shirt for formal occasions.', 'default-product.png', 'Formal', 'cloth', 7, 9, 11, 6);

INSERT INTO products (product_title, product_price, product_desc, product_image, collection, item_type, size_s, size_m, size_l, size_xl)
VALUES ('Cargo Shorts', 45.90, 'Multi-pocket cargo shorts for a utility look.', 'default-product.png', 'Casual', 'cloth', 10, 8, 7, 5);

-- =============================================
-- ORDERS
-- =============================================
-- sub_total = price * quantity, total = sub_total + 4.50 shipping
-- Ahmad Razak (cust_id=1) orders
INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (1, '0001', 'credit', 'Classic Black T-Shirt x2', 'M', 2, 79.80, 4.50, 0, 84.30, 'default-product.png', 1, '2026-07-01 10:30:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (1, '0001', 'credit', 'Classic Black T-Shirt x1', 'L', 1, 39.90, 4.50, 0, 44.40, 'default-product.png', 1, '2026-07-01 10:30:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (1, '0002', 'credit', 'Urban Streetwear Hoodie x1', 'L', 1, 89.90, 4.50, 0, 94.40, 'default-product.png', 2, '2026-07-10 14:20:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (1, '0003', 'credit', 'Slim Fit Chinos x1', 'M', 1, 59.90, 4.50, 0, 64.40, 'default-product.png', 3, '2026-07-20 09:15:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (1, '0003', 'credit', 'Athletic Joggers x2', 'M', 2, 99.80, 4.50, 0, 104.30, 'default-product.png', 5, '2026-07-20 09:15:00');

-- Siti Nurhaliza (cust_id=2) orders
INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (2, '0004', 'credit', 'Graphic Print Sweatshirt x1', 'S', 1, 69.90, 4.50, 0, 74.40, 'default-product.png', 6, '2026-07-02 11:00:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (2, '0005', 'credit', 'Vintage Denim Jacket x1', 'M', 1, 119.90, 4.50, 0, 124.40, 'default-product.png', 4, '2026-07-12 16:45:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (2, '0005', 'credit', 'Formal Oxford Shirt x1', 'S', 1, 79.90, 4.50, 0, 84.40, 'default-product.png', 7, '2026-07-12 16:45:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (2, '0006', 'credit', 'Cargo Shorts x2', 'S', 2, 91.80, 4.50, 0, 96.30, 'default-product.png', 8, '2026-07-22 08:30:00');

-- Muhammad Irfan (cust_id=3) orders
INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (3, '0007', 'credit', 'Urban Streetwear Hoodie x2', 'XL', 2, 179.80, 4.50, 0, 184.30, 'default-product.png', 2, '2026-07-05 13:10:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (3, '0008', 'credit', 'Classic Black T-Shirt x1', 'L', 1, 39.90, 4.50, 0, 44.40, 'default-product.png', 1, '2026-07-15 17:00:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (3, '0008', 'credit', 'Slim Fit Chinos x1', 'L', 1, 59.90, 4.50, 0, 64.40, 'default-product.png', 3, '2026-07-15 17:00:00');

-- Nurul Aisyah (cust_id=4) orders
INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (4, '0009', 'credit', 'Formal Oxford Shirt x2', 'M', 2, 159.80, 4.50, 0, 164.30, 'default-product.png', 7, '2026-07-08 10:00:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (4, '0010', 'credit', 'Athletic Joggers x1', 'S', 1, 49.90, 4.50, 0, 54.40, 'default-product.png', 5, '2026-07-18 12:30:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (4, '0011', 'credit', 'Vintage Denim Jacket x1', 'S', 1, 119.90, 4.50, 0, 124.40, 'default-product.png', 4, '2026-07-25 15:00:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (4, '0011', 'credit', 'Graphic Print Sweatshirt x1', 'M', 1, 69.90, 4.50, 0, 74.40, 'default-product.png', 6, '2026-07-25 15:00:00');

-- Raj Kumar (cust_id=5) orders
INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (5, '0012', 'credit', 'Cargo Shorts x3', 'L', 3, 137.70, 4.50, 0, 142.20, 'default-product.png', 8, '2026-07-06 09:45:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (5, '0013', 'credit', 'Athletic Joggers x1', 'L', 1, 49.90, 4.50, 0, 54.40, 'default-product.png', 5, '2026-07-16 11:20:00');

INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time)
VALUES (5, '0013', 'credit', 'Urban Streetwear Hoodie x1', 'M', 1, 89.90, 4.50, 0, 94.40, 'default-product.png', 2, '2026-07-16 11:20:00');

-- =============================================
-- FEEDBACK
-- =============================================
-- order_ids: 1..19 in insertion order
-- feedback references specific order_ids
INSERT INTO feedback (cust_id, product_name, order_num, order_id, rating, feedback_text, product_image, status)
VALUES (1, 'Classic Black T-Shirt', '0001', 1, 5, 'Excellent quality! The fabric is soft and fits perfectly. Highly recommended.', 'default-product.png', 'Published');

INSERT INTO feedback (cust_id, product_name, order_num, order_id, rating, feedback_text, product_image, status)
VALUES (2, 'Graphic Print Sweatshirt', '0004', 6, 4, 'Love the design! The print quality is great but the sizing runs a bit small.', 'default-product.png', 'Published');

INSERT INTO feedback (cust_id, product_name, order_num, order_id, rating, feedback_text, product_image, status)
VALUES (3, 'Classic Black T-Shirt', '0008', 11, 3, 'Decent shirt for the price. Expected a thicker material though.', 'default-product.png', 'Published');

INSERT INTO feedback (cust_id, product_name, order_num, order_id, rating, feedback_text, product_image, status)
VALUES (4, 'Formal Oxford Shirt', '0009', 13, 5, 'Perfect for office wear! Very comfortable and looks professional.', 'default-product.png', 'Published');

INSERT INTO feedback (cust_id, product_name, order_num, order_id, rating, feedback_text, product_image, status)
VALUES (5, 'Cargo Shorts', '0012', 17, 4, 'Good cargo shorts with plenty of pockets. Material is sturdy.', 'default-product.png', 'Published');

-- =============================================
-- ISSUES (ticket_num is set to issue_id after insert)
-- =============================================
INSERT INTO issues (ticket_num, cust_id, order_id, order_num, title, issue_desc, product_image, status, created_time)
VALUES (1, 1, 2, '0001', 'Wrong size delivered', 'I ordered size L for the Classic Black T-Shirt but received size M instead. Please help me exchange this.', 'default-product.png', 'New', '2026-07-03 09:00:00');

INSERT INTO issues (ticket_num, cust_id, order_id, order_num, title, issue_desc, product_image, status, created_time)
VALUES (2, 2, 7, '0005', 'Item arrived damaged', 'The Vintage Denim Jacket I received has a torn pocket on the left side. I would like a replacement or refund.', 'default-product.png', 'In Progress', '2026-07-14 10:30:00');

INSERT INTO issues (ticket_num, cust_id, order_id, order_num, title, issue_desc, product_image, status, created_time)
VALUES (3, 3, 10, '0007', 'Late delivery complaint', 'My order took over 2 weeks to arrive even though the estimated delivery was 5-7 business days. Very disappointed.', 'default-product.png', 'New', '2026-07-20 14:15:00');

INSERT INTO issues (ticket_num, cust_id, order_id, order_num, title, issue_desc, product_image, status, created_time)
VALUES (4, 4, 14, '0010', 'Quality issue with product', 'The Athletic Joggers started fading after just one wash. The color quality is not up to standard.', 'default-product.png', 'New', '2026-07-20 11:00:00');

INSERT INTO issues (ticket_num, cust_id, order_id, order_num, title, issue_desc, product_image, status, created_time)
VALUES (5, 5, 19, '0013', 'Missing item in order', 'I ordered Urban Streetwear Hoodie and Athletic Joggers together but only received the joggers. The hoodie is missing.', 'default-product.png', 'In Progress', '2026-07-18 16:45:00');

-- =============================================
-- ISSUE CHAT
-- =============================================
-- Issue 1 - Ahmad's wrong size
INSERT INTO issue_chat (issue_id, staff_id, cust_id, chat_text, created_time)
VALUES (1, NULL, 1, 'Hi, I just received my order and the t-shirt size is wrong. I ordered L but got M. Can I exchange it?', '2026-07-03 09:05:00');

INSERT INTO issue_chat (issue_id, staff_id, cust_id, chat_text, created_time)
VALUES (1, 1, NULL, 'Hello Ahmad! Sorry to hear about the size mix-up. We can arrange an exchange for you. Please ship the item back to us and we will send the correct size.', '2026-07-03 10:00:00');

INSERT INTO issue_chat (issue_id, staff_id, cust_id, chat_text, created_time)
VALUES (1, NULL, 1, 'Thank you! How do I ship it back? Do you provide a return label?', '2026-07-03 11:30:00');

-- Issue 2 - Siti's damaged item
INSERT INTO issue_chat (issue_id, staff_id, cust_id, chat_text, created_time)
VALUES (2, NULL, 2, 'The denim jacket I received has a torn pocket. I am very disappointed as this was supposed to be a gift.', '2026-07-14 10:35:00');

INSERT INTO issue_chat (issue_id, staff_id, cust_id, chat_text, created_time)
VALUES (2, 1, NULL, 'Hi Siti, we sincerely apologize for the defective product. Could you please send us a photo of the damage so we can process a replacement?', '2026-07-14 11:00:00');

INSERT INTO issue_chat (issue_id, staff_id, cust_id, chat_text, created_time)
VALUES (2, NULL, 2, 'Sure, here is the photo. The tear is about 3 inches long on the left pocket.', '2026-07-14 12:00:00');

INSERT INTO issue_chat (issue_id, staff_id, cust_id, chat_text, created_time)
VALUES (2, 1, NULL, 'Thank you for the photo. We have confirmed the defect. A replacement jacket will be sent to you within 3 business days.', '2026-07-14 14:00:00');

-- Issue 3 - Irfan's late delivery
INSERT INTO issue_chat (issue_id, staff_id, cust_id, chat_text, created_time)
VALUES (3, NULL, 3, 'My order was placed on July 5th and it is now July 20th. Still no delivery. The estimated time was 5-7 days.', '2026-07-20 14:20:00');

-- Issue 4 - Aisyah's quality issue
INSERT INTO issue_chat (issue_id, staff_id, cust_id, chat_text, created_time)
VALUES (4, NULL, 4, 'The joggers lost color after the first wash. I followed the washing instructions on the label. This is not acceptable.', '2026-07-20 11:05:00');

-- Issue 5 - Raj's missing item
INSERT INTO issue_chat (issue_id, staff_id, cust_id, chat_text, created_time)
VALUES (5, NULL, 5, 'I received my package but it only contained the joggers. The hoodie is completely missing from the package.', '2026-07-18 16:50:00');

INSERT INTO issue_chat (issue_id, staff_id, cust_id, chat_text, created_time)
VALUES (5, 1, NULL, 'Hello Raj, we apologize for the missing item. We are checking with our warehouse and will get back to you within 24 hours.', '2026-07-18 17:30:00');

disconnect;
