package petra.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import petra.model.Cart;
import petra.model.Order;
import petra.util.DBConnection;

public class OrderDAO {

    public Order findById(int id) throws SQLException {
        String sql = "SELECT o.*, pr.product_title FROM orders o LEFT JOIN products pr ON o.product_id = pr.product_id WHERE o.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<Order> findByCustId(int custId) throws SQLException {
        List<Order> list = new ArrayList<Order>();
        String sql = "SELECT * FROM orders WHERE cust_id = ? ORDER BY created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<Order> findOrdersByCustId(int custId, String sort) throws SQLException {
        String orderBy = "MIN(created_time) DESC";
        if ("oldest".equals(sort)) {
            orderBy = "MIN(created_time) ASC";
        }
        String sql = "SELECT MIN(order_id) AS first_id, order_num, cust_id, MIN(created_time) AS created_time, "
                   + "payment_method, SUM(quantity) AS total_qty, SUM(item_sub_total) AS subtotal, "
                   + "shipping_fee, voucher_amount, total_price, COUNT(*) AS item_count, "
                   + "MIN(order_status) AS order_status "
                   + "FROM orders WHERE cust_id = ? "
                   + "GROUP BY order_num, cust_id, payment_method, shipping_fee, voucher_amount, total_price "
                   + "ORDER BY " + orderBy;
        List<Order> list = new ArrayList<Order>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("first_id"));
                    o.setOrderNum(rs.getString("order_num"));
                    o.setCustId(rs.getInt("cust_id"));
                    o.setCreatedTime(rs.getString("created_time"));
                    o.setPaymentMethod(rs.getString("payment_method"));
                    o.setTotalQty(rs.getInt("total_qty"));
                    o.setItemCount(rs.getInt("item_count"));
                    o.setShippingFee(rs.getBigDecimal("shipping_fee"));
                    o.setVoucherAmount(rs.getBigDecimal("voucher_amount"));
                    o.setTotalPrice(rs.getBigDecimal("total_price"));
                    try { o.setOrderStatus(rs.getString("order_status")); } catch (Exception e) {}
                    list.add(o);
                }
            }
        }
        return list;
    }

    public List<Order> findItemsByOrderNum(String orderNum, int custId) throws SQLException {
        String sql = "SELECT o.*, pr.product_title FROM orders o LEFT JOIN products pr ON o.product_id = pr.product_id WHERE o.order_num = ? AND o.cust_id = ? ORDER BY o.order_id";
        List<Order> list = new ArrayList<Order>();
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

    public Order findOrderSummary(String orderNum, int custId) throws SQLException {
        String sql = "SELECT MIN(order_id) AS first_id, order_num, cust_id, MIN(created_time) AS created_time, "
                   + "payment_method, SUM(quantity) AS total_qty, SUM(item_sub_total) AS subtotal, "
                   + "shipping_fee, voucher_amount, total_price, COUNT(*) AS item_count, "
                   + "MIN(order_status) AS order_status "
                   + "FROM orders WHERE order_num = ? AND cust_id = ? "
                   + "GROUP BY order_num, cust_id, payment_method, shipping_fee, voucher_amount, total_price";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNum);
            ps.setInt(2, custId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("first_id"));
                    o.setOrderNum(rs.getString("order_num"));
                    o.setCustId(rs.getInt("cust_id"));
                    o.setCreatedTime(rs.getString("created_time"));
                    o.setPaymentMethod(rs.getString("payment_method"));
                    o.setTotalQty(rs.getInt("total_qty"));
                    o.setItemCount(rs.getInt("item_count"));
                    o.setShippingFee(rs.getBigDecimal("shipping_fee"));
                    o.setVoucherAmount(rs.getBigDecimal("voucher_amount"));
                    o.setTotalPrice(rs.getBigDecimal("total_price"));
                    try { o.setOrderStatus(rs.getString("order_status")); } catch (Exception e) {}
                    return o;
                }
            }
        }
        return null;
    }

    public int createOrder(int custId, String paymentMethod, List<Cart> cart) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int nextNum = 1;
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(CAST(order_num AS INTEGER)), 0) + 1 FROM orders")) {
                rs.next(); nextNum = rs.getInt(1);
            }
            String orderNum = String.format("%04d", nextNum);

            BigDecimal shippingFee = new BigDecimal("4.50");
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (Cart item : cart) {
                if (!item.isOutOfStock()) {
                    totalPrice = totalPrice.add(item.getSubtotal());
                }
            }
            totalPrice = totalPrice.add(shippingFee);

            String now = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            String insertSql = "INSERT INTO orders (cust_id, order_num, payment_method, item_desc, size, quantity, item_sub_total, shipping_fee, voucher_amount, total_price, product_image, product_id, created_time, order_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String stockSql = "UPDATE products SET "
                            + "size_s = CASE WHEN ? = 'S' THEN size_s - ? ELSE size_s END, "
                            + "size_m = CASE WHEN ? = 'M' THEN size_m - ? ELSE size_m END, "
                            + "size_l = CASE WHEN ? = 'L' THEN size_l - ? ELSE size_l END, "
                            + "size_xl = CASE WHEN ? = 'XL' THEN size_xl - ? ELSE size_xl END "
                            + "WHERE product_id = ?";
            int orderId = 0;
            boolean first = true;
            for (Cart item : cart) {
                if (item.isOutOfStock()) continue;
                String itemDesc = item.getTitle() + " x" + item.getQuantity();
                try (PreparedStatement ps = conn.prepareStatement(insertSql, first ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS)) {
                    ps.setInt(1, custId);
                    ps.setString(2, orderNum);
                    ps.setString(3, paymentMethod);
                    ps.setString(4, itemDesc);
                    ps.setString(5, item.getSize());
                    ps.setInt(6, item.getQuantity());
                    ps.setBigDecimal(7, item.getSubtotal());
                    ps.setBigDecimal(8, shippingFee);
                    ps.setBigDecimal(9, BigDecimal.ZERO);
                    ps.setBigDecimal(10, totalPrice);
                    ps.setString(11, item.getProductImage());
                    ps.setInt(12, item.getProductId());
                    ps.setString(13, now);
                    ps.setString(14, "AWAITING_PAYMENT");
                    ps.executeUpdate();
                    if (first) {
                        try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); orderId = keys.getInt(1); }
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
                    ps.setInt(9, item.getProductId());
                    ps.executeUpdate();
                }
            }
            conn.commit();
            return orderId;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw e;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    public List<Order> findAll() throws SQLException {
        List<Order> list = new ArrayList<Order>();
        String sql = "SELECT o.*, c.cust_name AS customer_name FROM orders o LEFT JOIN customers c ON o.cust_id = c.cust_id ORDER BY o.created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { Order o = map(rs); try { o.setCustomerName(rs.getString("customer_name")); } catch (Exception e) {} list.add(o); }
        }
        return list;
    }

    public List<Order> search(String keyword) throws SQLException {
        List<Order> list = new ArrayList<Order>();
        String sql = "SELECT o.*, c.cust_name AS customer_name FROM orders o LEFT JOIN customers c ON o.cust_id = c.cust_id WHERE o.order_num LIKE ? OR c.cust_name LIKE ? OR c.username LIKE ? ORDER BY o.created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) { Order o = map(rs); try { o.setCustomerName(rs.getString("customer_name")); } catch (Exception e) {} list.add(o); } }
        }
        return list;
    }

    public List<Order> findByOrderNum(String orderNum) throws SQLException {
        List<Order> list = new ArrayList<Order>();
        String sql = "SELECT o.*, c.cust_name AS customer_name, c.cust_phone_num AS customer_phone FROM orders o LEFT JOIN customers c ON c.cust_id = o.cust_id WHERE o.order_num = ? ORDER BY o.order_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNum);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { Order o = map(rs); try { o.setCustomerName(rs.getString("customer_name")); o.setCustomerPhone(rs.getString("customer_phone")); } catch (Exception e) {} list.add(o); }
            }
        }
        return list;
    }

    public List<Order> findOrderSummaries(int page, int perPage, String custId, String date) throws SQLException {
        List<Order> list = new ArrayList<Order>();
        StringBuilder where = new StringBuilder();
        List<Object> params = new ArrayList<Object>();
        if (custId != null && !custId.trim().isEmpty()) {
            String idStr = custId.trim().toUpperCase().replace("CUST", "");
            try { int id = Integer.parseInt(idStr); where.append(" AND cust_id = ?"); params.add(id); } catch (NumberFormatException e) {}
        }
        if (date != null && !date.trim().isEmpty()) { where.append(" AND CAST(created_time AS DATE) = CAST(? AS DATE)"); params.add(date.trim()); }

        String sql = "SELECT o.order_num, o.cust_id, o.created_time, o.total_qty, o.total_price, o.item_count, "
                   + "i.product_image, i.item_desc AS first_item, i.size AS first_size, c.cust_name AS customer_name "
                   + "FROM (SELECT order_num, cust_id, MIN(order_id) AS first_id, MIN(created_time) AS created_time, "
                   + "SUM(quantity) AS total_qty, SUM(total_price) AS total_price, COUNT(*) AS item_count "
                   + "FROM orders WHERE 1=1" + where.toString()
                   + " GROUP BY order_num, cust_id) o "
                   + "JOIN orders i ON i.order_id = o.first_id "
                   + "LEFT JOIN customers c ON c.cust_id = o.cust_id "
                   + "ORDER BY o.created_time DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        params.add((page - 1) * perPage); params.add(perPage);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) { Order o = new Order(); o.setOrderNum(rs.getString("order_num")); o.setCustId(rs.getInt("cust_id")); o.setCreatedTime(rs.getString("created_time")); o.setTotalQty(rs.getInt("total_qty")); o.setTotalPrice(rs.getBigDecimal("total_price")); o.setItemCount(rs.getInt("item_count")); o.setFirstItemDesc(rs.getString("first_item")); o.setFirstSize(rs.getString("first_size")); o.setProductImage(rs.getString("product_image")); try { o.setCustomerName(rs.getString("customer_name")); } catch (Exception e) {} list.add(o); } }
        }
        return list;
    }

    public int countOrderSummaries(String custId, String date) throws SQLException {
        StringBuilder where = new StringBuilder();
        List<Object> params = new ArrayList<Object>();
        if (custId != null && !custId.trim().isEmpty()) {
            String idStr = custId.trim().toUpperCase().replace("CUST", "");
            try { int id = Integer.parseInt(idStr); where.append(" AND cust_id = ?"); params.add(id); } catch (NumberFormatException e) {}
        }
        if (date != null && !date.trim().isEmpty()) { where.append(" AND CAST(created_time AS DATE) = CAST(? AS DATE)"); params.add(date.trim()); }
        String sql = "SELECT COUNT(*) FROM (SELECT order_num FROM orders WHERE 1=1" + where.toString() + " GROUP BY order_num, cust_id) t";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    public boolean updateOrderStatus(String orderNum, String status) throws SQLException {
        String sql = "UPDATE orders SET order_status = ? WHERE order_num = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, orderNum);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePaymentInfo(String orderNum, String paymentMethod, String gateway, String gatewayRef) throws SQLException {
        String sql = "UPDATE orders SET payment_method = ?, payment_gateway = ?, gateway_ref = ?, order_status = 'PAID' WHERE order_num = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentMethod);
            ps.setString(2, gateway);
            ps.setString(3, gatewayRef);
            ps.setString(4, orderNum);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean cancelOrder(String orderNum, int custId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String selectSql = "SELECT product_id, size, quantity FROM orders WHERE order_num = ? AND cust_id = ?";
            String restoreSql = "UPDATE products SET "
                    + "size_s = CASE WHEN ? = 'S' THEN size_s + ? ELSE size_s END, "
                    + "size_m = CASE WHEN ? = 'M' THEN size_m + ? ELSE size_m END, "
                    + "size_l = CASE WHEN ? = 'L' THEN size_l + ? ELSE size_l END, "
                    + "size_xl = CASE WHEN ? = 'XL' THEN size_xl + ? ELSE size_xl END "
                    + "WHERE product_id = ?";
            String updateSql = "UPDATE orders SET order_status = 'CANCELLED' WHERE order_num = ? AND cust_id = ?";
            String voidPaymentSql = "UPDATE payments SET status = 'CANCELLED' WHERE order_num = ? AND status IN ('INITIATED', 'REDIRECTED')";

            List<int[]> items = new ArrayList<int[]>();
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setString(1, orderNum);
                ps.setInt(2, custId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int[] item = new int[] { rs.getInt("product_id"), 0, rs.getInt("quantity") };
                        String sz = rs.getString("size");
                        if ("S".equals(sz)) item[1] = 0;
                        else if ("M".equals(sz)) item[1] = 1;
                        else if ("L".equals(sz)) item[1] = 2;
                        else if ("XL".equals(sz)) item[1] = 3;
                        items.add(item);
                    }
                }
            }

            for (int[] item : items) {
                try (PreparedStatement ps = conn.prepareStatement(restoreSql)) {
                    ps.setInt(1, item[0]); ps.setInt(2, item[2]);
                    ps.setInt(3, item[0]); ps.setInt(4, item[2]);
                    ps.setInt(5, item[0]); ps.setInt(6, item[2]);
                    ps.setInt(7, item[0]); ps.setInt(8, item[2]);
                    ps.setInt(9, item[0]);
                    ps.executeUpdate();
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, orderNum);
                ps.setInt(2, custId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(voidPaymentSql)) {
                ps.setString(1, orderNum);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw e;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    public boolean changePayment(String orderNum, int custId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String selectSql = "SELECT product_id, size, quantity FROM orders WHERE order_num = ? AND cust_id = ?";
            String restoreSql = "UPDATE products SET "
                    + "size_s = CASE WHEN ? = 'S' THEN size_s + ? ELSE size_s END, "
                    + "size_m = CASE WHEN ? = 'M' THEN size_m + ? ELSE size_m END, "
                    + "size_l = CASE WHEN ? = 'L' THEN size_l + ? ELSE size_l END, "
                    + "size_xl = CASE WHEN ? = 'XL' THEN size_xl + ? ELSE size_xl END "
                    + "WHERE product_id = ?";
            String deleteOrderSql = "DELETE FROM orders WHERE order_num = ? AND cust_id = ?";
            String voidPaymentSql = "UPDATE payments SET status = 'VOIDED' WHERE order_num = ? AND status IN ('INITIATED', 'REDIRECTED')";

            List<int[]> items = new ArrayList<int[]>();
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setString(1, orderNum);
                ps.setInt(2, custId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int[] item = new int[] { rs.getInt("product_id"), 0, rs.getInt("quantity") };
                        String sz = rs.getString("size");
                        if ("S".equals(sz)) item[1] = 0;
                        else if ("M".equals(sz)) item[1] = 1;
                        else if ("L".equals(sz)) item[1] = 2;
                        else if ("XL".equals(sz)) item[1] = 3;
                        items.add(item);
                    }
                }
            }

            for (int[] item : items) {
                try (PreparedStatement ps = conn.prepareStatement(restoreSql)) {
                    ps.setInt(1, item[0]); ps.setInt(2, item[2]);
                    ps.setInt(3, item[0]); ps.setInt(4, item[2]);
                    ps.setInt(5, item[0]); ps.setInt(6, item[2]);
                    ps.setInt(7, item[0]); ps.setInt(8, item[2]);
                    ps.setInt(9, item[0]);
                    ps.executeUpdate();
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(deleteOrderSql)) {
                ps.setString(1, orderNum);
                ps.setInt(2, custId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(voidPaymentSql)) {
                ps.setString(1, orderNum);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw e;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    public List<int[]> findItemDataByOrderNum(String orderNum, int custId) throws SQLException {
        String sql = "SELECT product_id, size, quantity FROM orders WHERE order_num = ? AND cust_id = ?";
        List<int[]> items = new ArrayList<int[]>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNum);
            ps.setInt(2, custId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String sz = rs.getString("size");
                    int sizeIdx = 1;
                    if ("S".equals(sz)) sizeIdx = 0;
                    else if ("M".equals(sz)) sizeIdx = 1;
                    else if ("L".equals(sz)) sizeIdx = 2;
                    else if ("XL".equals(sz)) sizeIdx = 3;
                    items.add(new int[] { rs.getInt("product_id"), rs.getInt("quantity"), sizeIdx });
                }
            }
        }
        return items;
    }

    private Order map(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setCustId(rs.getInt("cust_id"));
        o.setOrderNum(rs.getString("order_num"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setItemDesc(rs.getString("item_desc"));
        o.setSize(rs.getString("size"));
        o.setQuantity(rs.getInt("quantity"));
        o.setItemSubTotal(rs.getBigDecimal("item_sub_total"));
        o.setShippingFee(rs.getBigDecimal("shipping_fee"));
        o.setVoucherAmount(rs.getBigDecimal("voucher_amount"));
        o.setTotalPrice(rs.getBigDecimal("total_price"));
        o.setProductImage(rs.getString("product_image"));
        o.setCreatedTime(rs.getString("created_time"));
        try { o.setProductId((Integer) rs.getObject("product_id")); } catch (Exception e) {}
        try { o.setProductTitle(rs.getString("product_title")); } catch (Exception e) {}
        try { o.setOrderStatus(rs.getString("order_status")); } catch (Exception e) {}
        try { o.setCustomerName(rs.getString("customer_name")); } catch (Exception e) {}
        try { o.setCustomerPhone(rs.getString("customer_phone")); } catch (Exception e) {}
        return o;
    }
}
