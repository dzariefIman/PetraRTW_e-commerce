package petra.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import petra.model.CartDBItem;
import petra.model.Purchase;
import petra.util.DBConnection;

public class PurchaseDAO {

    public Purchase findById(int id) throws SQLException {
        String sql = "SELECT p.*, sp.shop_product_title FROM purchases p LEFT JOIN shop_products sp ON p.shop_product_id = sp.shop_product_id WHERE p.purchase_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<Purchase> findByCustId(int custId) throws SQLException {
        List<Purchase> list = new ArrayList<Purchase>();
        String sql = "SELECT * FROM purchases WHERE cust_id = ? ORDER BY created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<Purchase> findOrdersByCustId(int custId, String sort) throws SQLException {
        String orderBy = "MIN(created_time) DESC";
        if ("oldest".equals(sort)) {
            orderBy = "MIN(created_time) ASC";
        }
        String sql = "SELECT MIN(purchase_id) AS first_id, order_num, cust_id, MIN(created_time) AS created_time, "
                   + "payment_method, purchase_desc, SUM(quantity) AS total_qty, SUM(item_sub_total) AS subtotal, "
                   + "shipping_fee, voucher_amount, total_price, COUNT(*) AS item_count "
                   + "FROM purchases WHERE cust_id = ? "
                   + "GROUP BY order_num, cust_id, payment_method, purchase_desc, shipping_fee, voucher_amount, total_price "
                   + "ORDER BY " + orderBy;
        List<Purchase> list = new ArrayList<Purchase>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Purchase p = new Purchase();
                    p.setPurchaseId(rs.getInt("first_id"));
                    p.setOrderNum(rs.getString("order_num"));
                    p.setCustId(rs.getInt("cust_id"));
                    p.setCreatedTime(rs.getString("created_time"));
                    p.setPaymentMethod(rs.getString("payment_method"));
                    p.setPurchaseDesc(rs.getString("purchase_desc"));
                    p.setTotalQty(rs.getInt("total_qty"));
                    p.setItemCount(rs.getInt("item_count"));
                    p.setShippingFee(rs.getBigDecimal("shipping_fee"));
                    p.setVoucherAmount(rs.getBigDecimal("voucher_amount"));
                    p.setTotalPrice(rs.getBigDecimal("total_price"));
                    list.add(p);
                }
            }
        }
        return list;
    }

    public List<Purchase> findItemsByOrderNum(String orderNum, int custId) throws SQLException {
        String sql = "SELECT p.*, sp.shop_product_title FROM purchases p LEFT JOIN shop_products sp ON p.shop_product_id = sp.shop_product_id WHERE p.order_num = ? AND p.cust_id = ? ORDER BY p.purchase_id";
        List<Purchase> list = new ArrayList<Purchase>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNum);
            ps.setInt(2, custId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Purchase findOrderSummary(String orderNum, int custId) throws SQLException {
        String sql = "SELECT MIN(purchase_id) AS first_id, order_num, cust_id, MIN(created_time) AS created_time, "
                   + "payment_method, purchase_desc, SUM(quantity) AS total_qty, SUM(item_sub_total) AS subtotal, "
                   + "shipping_fee, voucher_amount, total_price, COUNT(*) AS item_count "
                   + "FROM purchases WHERE order_num = ? AND cust_id = ? "
                   + "GROUP BY order_num, cust_id, payment_method, purchase_desc, shipping_fee, voucher_amount, total_price";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNum);
            ps.setInt(2, custId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Purchase p = new Purchase();
                    p.setPurchaseId(rs.getInt("first_id"));
                    p.setOrderNum(rs.getString("order_num"));
                    p.setCustId(rs.getInt("cust_id"));
                    p.setCreatedTime(rs.getString("created_time"));
                    p.setPaymentMethod(rs.getString("payment_method"));
                    p.setPurchaseDesc(rs.getString("purchase_desc"));
                    p.setTotalQty(rs.getInt("total_qty"));
                    p.setItemCount(rs.getInt("item_count"));
                    p.setShippingFee(rs.getBigDecimal("shipping_fee"));
                    p.setVoucherAmount(rs.getBigDecimal("voucher_amount"));
                    p.setTotalPrice(rs.getBigDecimal("total_price"));
                    return p;
                }
            }
        }
        return null;
    }

    public int createOrder(int custId, String paymentMethod, List<CartDBItem> cart) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int nextNum = 1;
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(CAST(order_num AS INTEGER)), 0) + 1 FROM purchases")) {
                rs.next(); nextNum = rs.getInt(1);
            }
            String orderNum = String.format("%04d", nextNum);

            BigDecimal shippingFee = new BigDecimal("4.50");
            BigDecimal totalPrice = BigDecimal.ZERO;
            StringBuilder desc = new StringBuilder();
            for (CartDBItem item : cart) {
                if (!item.isOutOfStock()) {
                    totalPrice = totalPrice.add(item.getSubtotal());
                    if (desc.length() > 0) desc.append(", ");
                    desc.append(item.getTitle()).append(" x").append(item.getQuantity());
                }
            }
            totalPrice = totalPrice.add(shippingFee);

            String now = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            String insertSql = "INSERT INTO purchases (cust_id, order_num, payment_method, purchase_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, shop_product_id, created_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String stockSql = "UPDATE shop_products SET "
                            + "size_s = CASE WHEN ? = 'S' THEN size_s - ? ELSE size_s END, "
                            + "size_m = CASE WHEN ? = 'M' THEN size_m - ? ELSE size_m END, "
                            + "size_l = CASE WHEN ? = 'L' THEN size_l - ? ELSE size_l END, "
                            + "size_xl = CASE WHEN ? = 'XL' THEN size_xl - ? ELSE size_xl END "
                            + "WHERE shop_product_id = ?";
            int purchaseId = 0;
            boolean first = true;
            for (CartDBItem item : cart) {
                if (item.isOutOfStock()) continue;
                try (PreparedStatement ps = conn.prepareStatement(insertSql, first ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS)) {
                    ps.setInt(1, custId);
                    ps.setString(2, orderNum);
                    ps.setString(3, paymentMethod);
                    ps.setString(4, desc.toString());
                    ps.setString(5, item.getSize());
                    ps.setInt(6, item.getQuantity());
                    ps.setBigDecimal(7, item.getSubtotal());
                    ps.setBigDecimal(8, shippingFee);
                    ps.setBigDecimal(9, BigDecimal.ZERO);
                    ps.setBigDecimal(10, totalPrice);
                    ps.setString(11, item.getProductImage());
                    ps.setInt(12, item.getShopProductId());
                    ps.setString(13, now);
                    ps.executeUpdate();
                    if (first) {
                        try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); purchaseId = keys.getInt(1); }
                        first = false;
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(stockSql)) {
                    ps.setString(1, item.getSize());
                    ps.setInt(2, item.getQuantity());
                    ps.setString(3, item.getSize());
                    ps.setInt(4, item.getQuantity());
                    ps.setString(5, item.getSize());
                    ps.setInt(6, item.getQuantity());
                    ps.setString(7, item.getSize());
                    ps.setInt(8, item.getQuantity());
                    ps.setInt(9, item.getShopProductId());
                    ps.executeUpdate();
                }
            }
            conn.commit();
            return purchaseId;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw e;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    public List<Purchase> findAll() throws SQLException {
        List<Purchase> list = new ArrayList<Purchase>();
        String sql = "SELECT p.*, c.cust_name AS customer_name FROM purchases p LEFT JOIN customers c ON p.cust_id = c.cust_id ORDER BY p.created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { Purchase p = map(rs); try { p.setCustomerName(rs.getString("customer_name")); } catch (Exception e) {} list.add(p); }
        }
        return list;
    }

    public List<Purchase> search(String keyword) throws SQLException {
        List<Purchase> list = new ArrayList<Purchase>();
        String sql = "SELECT p.*, c.cust_name AS customer_name FROM purchases p LEFT JOIN customers c ON p.cust_id = c.cust_id WHERE CAST(p.order_num AS VARCHAR(20)) LIKE ? OR c.cust_name LIKE ? OR c.username LIKE ? ORDER BY p.created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) { Purchase p = map(rs); try { p.setCustomerName(rs.getString("customer_name")); } catch (Exception e) {} list.add(p); } }
        }
        return list;
    }

    public List<Purchase> findByOrderNum(String orderNum) throws SQLException {
        List<Purchase> list = new ArrayList<Purchase>();
        String sql = "SELECT p.*, c.cust_name AS customer_name, c.cust_phone_num AS customer_phone FROM purchases p LEFT JOIN customers c ON c.cust_id = p.cust_id WHERE p.order_num = ? ORDER BY p.purchase_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNum);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { Purchase p = map(rs); try { p.setCustomerName(rs.getString("customer_name")); p.setCustomerPhone(rs.getString("customer_phone")); } catch (Exception e) {} list.add(p); }
            }
        }
        return list;
    }

    public List<Purchase> findOrderSummaries(int page, int perPage, String custId, String date) throws SQLException {
        List<Purchase> list = new ArrayList<Purchase>();
        StringBuilder where = new StringBuilder();
        List<Object> params = new ArrayList<Object>();
        if (custId != null && !custId.trim().isEmpty()) { where.append(" AND p.cust_id = ?"); params.add(Integer.parseInt(custId.trim())); }
        if (date != null && !date.trim().isEmpty()) { where.append(" AND DATE(p.created_time) = ?"); params.add(date.trim()); }

        String sql = "SELECT o.order_num, o.cust_id, o.created_time, o.total_qty, o.total_price, o.item_count, "
                   + "i.product_image, i.purchase_desc AS first_item, i.size AS first_size, c.cust_name AS customer_name "
                   + "FROM (SELECT order_num, cust_id, MIN(purchase_id) AS first_id, MIN(created_time) AS created_time, "
                   + "SUM(quantity) AS total_qty, SUM(total_price) AS total_price, COUNT(*) AS item_count "
                   + "FROM purchases WHERE 1=1" + where.toString()
                   + " GROUP BY order_num, cust_id) o "
                   + "JOIN purchases i ON i.purchase_id = o.first_id "
                   + "LEFT JOIN customers c ON c.cust_id = o.cust_id "
                   + "ORDER BY o.created_time DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        params.add((page - 1) * perPage); params.add(perPage);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) { Purchase p = new Purchase(); p.setOrderNum(rs.getString("order_num")); p.setCustId(rs.getInt("cust_id")); p.setCreatedTime(rs.getString("created_time")); p.setTotalQty(rs.getInt("total_qty")); p.setTotalPrice(rs.getBigDecimal("total_price")); p.setItemCount(rs.getInt("item_count")); p.setFirstItemDesc(rs.getString("first_item")); p.setFirstSize(rs.getString("first_size")); p.setProductImage(rs.getString("product_image")); try { p.setCustomerName(rs.getString("customer_name")); } catch (Exception e) {} list.add(p); } }
        }
        return list;
    }

    public int countOrderSummaries(String custId, String date) throws SQLException {
        StringBuilder where = new StringBuilder();
        List<Object> params = new ArrayList<Object>();
        if (custId != null && !custId.trim().isEmpty()) { where.append(" AND cust_id = ?"); params.add(Integer.parseInt(custId.trim())); }
        if (date != null && !date.trim().isEmpty()) { where.append(" AND DATE(created_time) = ?"); params.add(date.trim()); }
        String sql = "SELECT COUNT(*) FROM (SELECT order_num FROM purchases WHERE 1=1" + where.toString() + " GROUP BY order_num, cust_id) t";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    private Purchase map(ResultSet rs) throws SQLException {
        Purchase p = new Purchase();
        p.setPurchaseId(rs.getInt("purchase_id"));
        p.setCustId(rs.getInt("cust_id"));
        p.setOrderNum(rs.getString("order_num"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setPurchaseDesc(rs.getString("purchase_desc"));
        p.setSize(rs.getString("size"));
        p.setQuantity(rs.getInt("quantity"));
        p.setItemSubTotal(rs.getBigDecimal("item_sub_total"));
        p.setShippingFee(rs.getBigDecimal("shipping_fee"));
        p.setVoucherAmount(rs.getBigDecimal("voucher_amount"));
        p.setTotalPrice(rs.getBigDecimal("total_price"));
        p.setProductImage(rs.getString("product_image"));
        p.setCreatedTime(rs.getString("created_time"));
        try { p.setShopProductId((Integer) rs.getObject("shop_product_id")); } catch (Exception e) {}
        try { p.setProductTitle(rs.getString("shop_product_title")); } catch (Exception e) {}
        return p;
    }
}