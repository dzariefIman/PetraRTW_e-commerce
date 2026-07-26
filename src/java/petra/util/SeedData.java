package petra.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SeedData {

    private static Connection conn;

    public static void main(String[] args) throws Exception {
        conn = DBConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            System.out.println("=== PetraRTW Seed Data ===");

            insertStaff();
            int[] custIds = insertCustomers();
            int[] productIds = insertProducts();
            int[] orderIds = insertOrders(custIds, productIds);
            insertFeedback(custIds, orderIds, productIds);
            int[] issueIds = insertIssues(custIds, orderIds, productIds);
            insertIssueChats(issueIds, custIds);

            conn.commit();
            System.out.println("Done! All seed data inserted successfully.");
        } catch (Exception e) {
            conn.rollback();
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }

    private static void insertStaff() throws SQLException {
        System.out.println("Inserting staff account...");
        String sql = "INSERT INTO sales_admin (staff_name, username, password, staff_email, staff_phone_num) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "Dzarief Admin");
            ps.setString(2, "dzariefAdmin");
            ps.setString(3, PasswordUtil.hash("123"));
            ps.setString(4, "dzarief@petra.rtw");
            ps.setString(5, "012-3456789");
            ps.executeUpdate();
        }
        System.out.println("  Created staff: Admin Staff (username: admin)");
    }

    private static int[] insertCustomers() throws SQLException {
        System.out.println("Inserting 5 customers...");
        String sql = "INSERT INTO customers (cust_name, username, password, cust_email, cust_address, cust_phone_num) VALUES (?, ?, ?, ?, ?, ?)";

        String[][] customers = {
            {"Ahmad Razak", "ahmad_razak", "password123", "ahmad.razak@email.com", "12 Jalan Bukit Bintang, Kuala Lumpur", "012-3456789"},
            {"Siti Nurhaliza", "siti_nur", "password123", "siti.nurhaliza@email.com", "45 Jalan Tun Razak, Kuala Lumpur", "013-4567890"},
            {"Muhammad Irfan", "irfan_m", "password123", "m.irfan@email.com", "88 Jalan Ampang, Kuala Lumpur", "014-5678901"},
            {"Nurul Aisyah", "aisyah_nurul", "password123", "nurul.aisyah@email.com", "23 Jalan Masjid India, Kuala Lumpur", "015-6789012"},
            {"Raj Kumar", "raj_kumar", "password123", "raj.kumar@email.com", "67 Jalan Ipoh, Kuala Lumpur", "016-7890123"}
        };

        int[] ids = new int[customers.length];
        for (int i = 0; i < customers.length; i++) {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, customers[i][0]);
                ps.setString(2, customers[i][1]);
                ps.setString(3, PasswordUtil.hash(customers[i][2]));
                ps.setString(4, customers[i][3]);
                ps.setString(5, customers[i][4]);
                ps.setString(6, customers[i][5]);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    ids[i] = keys.getInt(1);
                }
            }
            System.out.println("  Created customer: " + customers[i][0] + " (ID: " + ids[i] + ")");
        }
        return ids;
    }

    private static int[] insertProducts() throws SQLException {
        System.out.println("Inserting products...");
        String sql = "INSERT INTO products (product_title, product_price, product_desc, product_image, collection, item_type, size_s, size_m, size_l, size_xl) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String[][] products = {
            {"Classic Black T-Shirt", "39.90", "Premium cotton classic black t-shirt for everyday wear.", "default-product.png", "Essentials", "cloth", "10", "15", "12", "8"},
            {"Urban Streetwear Hoodie", "89.90", "Comfortable streetwear hoodie with modern fit.", "default-product.png", "Streetwear", "cloth", "8", "10", "10", "6"},
            {"Slim Fit Chinos", "59.90", "Tailored slim fit chinos for a sharp look.", "default-product.png", "Essentials", "cloth", "12", "12", "10", "8"},
            {"Vintage Denim Jacket", "119.90", "Classic vintage wash denim jacket.", "default-product.png", "Classic", "cloth", "6", "8", "10", "7"},
            {"Athletic Joggers", "49.90", "Lightweight joggers perfect for sports or casual wear.", "default-product.png", "Activewear", "cloth", "10", "12", "8", "6"},
            {"Graphic Print Sweatshirt", "69.90", "Bold graphic print sweatshirt for a statement look.", "default-product.png", "Streetwear", "cloth", "8", "10", "8", "5"},
            {"Formal Oxford Shirt", "79.90", "Classic Oxford button-down shirt for formal occasions.", "default-product.png", "Formal", "cloth", "7", "9", "11", "6"},
            {"Cargo Shorts", "45.90", "Multi-pocket cargo shorts for a utility look.", "default-product.png", "Casual", "cloth", "10", "8", "7", "5"}
        };

        int[] ids = new int[products.length];
        for (int i = 0; i < products.length; i++) {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, products[i][0]);
                ps.setBigDecimal(2, new BigDecimal(products[i][1]));
                ps.setString(3, products[i][2]);
                ps.setString(4, products[i][3]);
                ps.setString(5, products[i][4]);
                ps.setString(6, products[i][5]);
                ps.setInt(7, Integer.parseInt(products[i][6]));
                ps.setInt(8, Integer.parseInt(products[i][7]));
                ps.setInt(9, Integer.parseInt(products[i][8]));
                ps.setInt(10, Integer.parseInt(products[i][9]));
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    ids[i] = keys.getInt(1);
                }
            }
            System.out.println("  Created product: " + products[i][0] + " (ID: " + ids[i] + ")");
        }
        return ids;
    }

    private static int[] insertOrders(int[] custIds, int[] productIds) throws SQLException {
        System.out.println("Inserting orders...");
        String sql = "INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        BigDecimal shippingFee = new BigDecimal("4.50");
        int orderCount = 0;

        Object[][] orders = {
            // Ahmad Razak - 3 orders
            {0, "0001", "credit", "Classic Black T-Shirt x2", "M", 2, productIds[0], "2026-07-01 10:30:00"},
            {0, "0001", "credit", "Classic Black T-Shirt x1", "L", 1, productIds[0], "2026-07-01 10:30:00"},

            {0, "0002", "credit", "Urban Streetwear Hoodie x1", "L", 1, productIds[1], "2026-07-10 14:20:00"},

            {0, "0003", "credit", "Slim Fit Chinos x1", "M", 1, productIds[2], "2026-07-20 09:15:00"},
            {0, "0003", "credit", "Athletic Joggers x2", "M", 2, productIds[4], "2026-07-20 09:15:00"},

            // Siti Nurhaliza - 3 orders
            {1, "0004", "credit", "Graphic Print Sweatshirt x1", "S", 1, productIds[5], "2026-07-02 11:00:00"},

            {1, "0005", "credit", "Vintage Denim Jacket x1", "M", 1, productIds[3], "2026-07-12 16:45:00"},
            {1, "0005", "credit", "Formal Oxford Shirt x1", "S", 1, productIds[6], "2026-07-12 16:45:00"},

            {1, "0006", "credit", "Cargo Shorts x2", "S", 2, productIds[7], "2026-07-22 08:30:00"},

            // Muhammad Irfan - 2 orders
            {2, "0007", "credit", "Urban Streetwear Hoodie x2", "XL", 2, productIds[1], "2026-07-05 13:10:00"},

            {2, "0008", "credit", "Classic Black T-Shirt x1", "L", 1, productIds[0], "2026-07-15 17:00:00"},
            {2, "0008", "credit", "Slim Fit Chinos x1", "L", 1, productIds[2], "2026-07-15 17:00:00"},

            // Nurul Aisyah - 3 orders
            {3, "0009", "credit", "Formal Oxford Shirt x2", "M", 2, productIds[6], "2026-07-08 10:00:00"},

            {3, "0010", "credit", "Athletic Joggers x1", "S", 1, productIds[4], "2026-07-18 12:30:00"},

            {3, "0011", "credit", "Vintage Denim Jacket x1", "S", 1, productIds[3], "2026-07-25 15:00:00"},
            {3, "0011", "credit", "Graphic Print Sweatshirt x1", "M", 1, productIds[5], "2026-07-25 15:00:00"},

            // Raj Kumar - 2 orders
            {4, "0012", "credit", "Cargo Shorts x3", "L", 3, productIds[7], "2026-07-06 09:45:00"},

            {4, "0013", "credit", "Athletic Joggers x1", "L", 1, productIds[4], "2026-07-16 11:20:00"},
            {4, "0013", "credit", "Urban Streetwear Hoodie x1", "M", 1, productIds[1], "2026-07-16 11:20:00"}
        };

        int[] orderIds = new int[orders.length];

        for (int i = 0; i < orders.length; i++) {
            int custIdx = (int) orders[i][0];
            String orderNum = (String) orders[i][1];
            String paymentMethod = (String) orders[i][2];
            String desc = (String) orders[i][3];
            String size = (String) orders[i][4];
            int qty = (int) orders[i][5];
            int productId = (int) orders[i][6];
            String createdTime = (String) orders[i][7];

            BigDecimal price = getProductPrice(productId);
            BigDecimal subTotal = price.multiply(new BigDecimal(qty));
            BigDecimal totalPrice = subTotal.add(shippingFee);

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, custIds[custIdx]);
                ps.setString(2, orderNum);
                ps.setString(3, paymentMethod);
                ps.setString(4, desc);
                ps.setString(5, size);
                ps.setInt(6, qty);
                ps.setBigDecimal(7, subTotal);
                ps.setBigDecimal(8, shippingFee);
                ps.setBigDecimal(9, BigDecimal.ZERO);
                ps.setBigDecimal(10, totalPrice);
                ps.setString(11, "default-product.png");
                ps.setInt(12, productId);
                ps.setString(13, createdTime);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    orderIds[i] = keys.getInt(1);
                }
            }
            orderCount++;
            System.out.println("  Created order: " + desc + " (Order " + orderNum + ", ID: " + orderIds[i] + ")");
        }
        return orderIds;
    }

    private static BigDecimal getProductPrice(int productId) throws SQLException {
        String sql = "SELECT product_price FROM products WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("product_price");
            }
        }
        return new BigDecimal("39.90");
    }

    private static void insertFeedback(int[] custIds, int[] orderIds, int[] productIds) throws SQLException {
        System.out.println("Inserting feedback...");
        String sql = "INSERT INTO feedback (cust_id, product_name, order_num, order_id, rating, feedback_text, product_image, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        String[][] feedbacks = {
            // Ahmad - feedback on first order
            {String.valueOf(custIds[0]), "Classic Black T-Shirt", "0001", String.valueOf(orderIds[0]), "5", "Excellent quality! The fabric is soft and fits perfectly. Highly recommended.", "default-product.png", "Published"},

            // Siti - feedback on first order
            {String.valueOf(custIds[1]), "Graphic Print Sweatshirt", "0004", String.valueOf(orderIds[5]), "4", "Love the design! The print quality is great but the sizing runs a bit small.", "default-product.png", "Published"},

            // Irfan - feedback on second order
            {String.valueOf(custIds[2]), "Classic Black T-Shirt", "0008", String.valueOf(orderIds[10]), "3", "Decent shirt for the price. Expected a thicker material though.", "default-product.png", "Published"},

            // Aisyah - feedback on first order
            {String.valueOf(custIds[3]), "Formal Oxford Shirt", "0009", String.valueOf(orderIds[12]), "5", "Perfect for office wear! Very comfortable and looks professional.", "default-product.png", "Published"},

            // Raj - feedback on first order
            {String.valueOf(custIds[4]), "Cargo Shorts", "0012", String.valueOf(orderIds[16]), "4", "Good cargo shorts with plenty of pockets. Material is sturdy.", "default-product.png", "Published"}
        };

        for (String[] fb : feedbacks) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, Integer.parseInt(fb[0]));
                ps.setString(2, fb[1]);
                ps.setString(3, fb[2]);
                ps.setInt(4, Integer.parseInt(fb[3]));
                ps.setInt(5, Integer.parseInt(fb[4]));
                ps.setString(6, fb[5]);
                ps.setString(7, fb[6]);
                ps.setString(8, fb[7]);
                ps.executeUpdate();
            }
            System.out.println("  Created feedback by cust " + fb[0] + " for order " + fb[2]);
        }
    }

    private static int[] insertIssues(int[] custIds, int[] orderIds, int[] productIds) throws SQLException {
        System.out.println("Inserting issues...");
        String sql = "INSERT INTO issues (cust_id, order_id, order_num, title, issue_desc, product_image, status, created_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        String[][] issues = {
            // Ahmad - wrong size issue
            {String.valueOf(custIds[0]), String.valueOf(orderIds[1]), "0001", "Wrong size delivered", "I ordered size L for the Classic Black T-Shirt but received size M instead. Please help me exchange this.", "default-product.png", "New", "2026-07-03 09:00:00"},

            // Siti - damaged item
            {String.valueOf(custIds[1]), String.valueOf(orderIds[6]), "0005", "Item arrived damaged", "The Vintage Denim Jacket I received has a torn pocket on the left side. I would like a replacement or refund.", "default-product.png", "In Progress", "2026-07-14 10:30:00"},

            // Irfan - late delivery
            {String.valueOf(custIds[2]), String.valueOf(orderIds[9]), "0007", "Late delivery complaint", "My order took over 2 weeks to arrive even though the estimated delivery was 5-7 business days. Very disappointed.", "default-product.png", "New", "2026-07-20 14:15:00"},

            // Aisyah - quality issue
            {String.valueOf(custIds[3]), String.valueOf(orderIds[13]), "0010", "Quality issue with product", "The Athletic Joggers started fading after just one wash. The color quality is not up to standard.", "default-product.png", "New", "2026-07-20 11:00:00"},

            // Raj - missing item
            {String.valueOf(custIds[4]), String.valueOf(orderIds[18]), "0013", "Missing item in order", "I ordered Urban Streetwear Hoodie and Athletic Joggers together but only received the joggers. The hoodie is missing.", "default-product.png", "In Progress", "2026-07-18 16:45:00"}
        };

        int[] issueIds = new int[issues.length];
        for (int i = 0; i < issues.length; i++) {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, Integer.parseInt(issues[i][0]));
                ps.setInt(2, Integer.parseInt(issues[i][1]));
                ps.setString(3, issues[i][2]);
                ps.setString(4, issues[i][3]);
                ps.setString(5, issues[i][4]);
                ps.setString(6, issues[i][5]);
                ps.setString(7, issues[i][6]);
                ps.setString(8, issues[i][7]);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    issueIds[i] = keys.getInt(1);
                }
                // Set ticket_num = issue_id
                try (PreparedStatement upd = conn.prepareStatement("UPDATE issues SET ticket_num = ? WHERE issue_id = ?")) {
                    upd.setInt(1, issueIds[i]);
                    upd.setInt(2, issueIds[i]);
                    upd.executeUpdate();
                }
            }
            System.out.println("  Created issue: " + issues[i][3] + " (Ticket #" + issueIds[i] + ")");
        }
        return issueIds;
    }

    private static void insertIssueChats(int[] issueIds, int[] custIds) throws SQLException {
        System.out.println("Inserting issue chat messages...");
        String sql = "INSERT INTO issue_chat (issue_id, staff_id, cust_id, chat_text, created_time) VALUES (?, ?, ?, ?, ?)";

        Object[][] chats = {
            // Issue 1 - Ahmad's wrong size
            {issueIds[0], null, custIds[0], "Hi, I just received my order and the t-shirt size is wrong. I ordered L but got M. Can I exchange it?", "2026-07-03 09:05:00"},
            {issueIds[0], 1, null, "Hello Ahmad! Sorry to hear about the size mix-up. We can arrange an exchange for you. Please ship the item back to us and we will send the correct size.", "2026-07-03 10:00:00"},
            {issueIds[0], null, custIds[0], "Thank you! How do I ship it back? Do you provide a return label?", "2026-07-03 11:30:00"},

            // Issue 2 - Siti's damaged item
            {issueIds[1], null, custIds[1], "The denim jacket I received has a torn pocket. I am very disappointed as this was supposed to be a gift.", "2026-07-14 10:35:00"},
            {issueIds[1], 1, null, "Hi Siti, we sincerely apologize for the defective product. Could you please send us a photo of the damage so we can process a replacement?", "2026-07-14 11:00:00"},
            {issueIds[1], null, custIds[1], "Sure, here is the photo. The tear is about 3 inches long on the left pocket.", "2026-07-14 12:00:00"},
            {issueIds[1], 1, null, "Thank you for the photo. We have confirmed the defect. A replacement jacket will be sent to you within 3 business days.", "2026-07-14 14:00:00"},

            // Issue 3 - Irfan's late delivery
            {issueIds[2], null, custIds[2], "My order was placed on July 5th and it is now July 20th. Still no delivery. The estimated time was 5-7 days.", "2026-07-20 14:20:00"},

            // Issue 4 - Aisyah's quality issue
            {issueIds[3], null, custIds[3], "The joggers lost color after the first wash. I followed the washing instructions on the label. This is not acceptable.", "2026-07-20 11:05:00"},

            // Issue 5 - Raj's missing item
            {issueIds[4], null, custIds[4], "I received my package but it only contained the joggers. The hoodie is completely missing from the package.", "2026-07-18 16:50:00"},
            {issueIds[4], 1, null, "Hello Raj, we apologize for the missing item. We are checking with our warehouse and will get back to you within 24 hours.", "2026-07-18 17:30:00"}
        };

        for (Object[] chat : chats) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, (int) chat[0]);
                if (chat[1] != null) ps.setInt(2, (int) chat[1]); else ps.setNull(2, java.sql.Types.INTEGER);
                if (chat[2] != null) ps.setInt(3, (int) chat[2]); else ps.setNull(3, java.sql.Types.INTEGER);
                ps.setString(4, (String) chat[3]);
                ps.setString(5, (String) chat[4]);
                ps.executeUpdate();
            }
            System.out.println("  Created chat message for issue #" + chat[0]);
        }
    }
}
