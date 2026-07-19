package petra.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import petra.model.CartDBItem;
import petra.model.Product;
import petra.util.DBConnection;

public class CartDAO {

    public List<CartDBItem> findByCustId(int custId) throws SQLException {
        String sql = "SELECT c.cart_id, c.cust_id, c.shop_product_id, c.size, c.quantity, "
                   + "p.shop_product_title, p.shop_product_price, p.product_image, "
                   + "p.size_s, p.size_m, p.size_l, p.size_xl "
                   + "FROM cart c "
                   + "JOIN shop_products p ON c.shop_product_id = p.shop_product_id "
                   + "WHERE c.cust_id = ? "
                   + "ORDER BY c.created_time";
        List<CartDBItem> items = new ArrayList<CartDBItem>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }
        }
        return items;
    }

    public CartDBItem findExisting(int custId, int productId, String size) throws SQLException {
        String sql = "SELECT c.cart_id, c.cust_id, c.shop_product_id, c.size, c.quantity, "
                   + "p.shop_product_title, p.shop_product_price, p.product_image, "
                   + "p.size_s, p.size_m, p.size_l, p.size_xl "
                   + "FROM cart c "
                   + "JOIN shop_products p ON c.shop_product_id = p.shop_product_id "
                   + "WHERE c.cust_id = ? AND c.shop_product_id = ? AND c.size = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId);
            ps.setInt(2, productId);
            ps.setString(3, size);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public void addItem(int custId, int productId, String size, int quantity) throws SQLException {
        CartDBItem existing = findExisting(custId, productId, size);
        if (existing != null) {
            updateQuantity(existing.getCartId(), existing.getQuantity() + quantity);
        } else {
            String sql = "INSERT INTO cart (cust_id, shop_product_id, size, quantity) VALUES (?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, custId);
                ps.setInt(2, productId);
                ps.setString(3, size);
                ps.setInt(4, quantity);
                ps.executeUpdate();
            }
        }
    }

    public void updateQuantity(int cartId, int quantity) throws SQLException {
        String sql = "UPDATE cart SET quantity = ? WHERE cart_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, cartId);
            ps.executeUpdate();
        }
    }

    public void removeItem(int cartId) throws SQLException {
        String sql = "DELETE FROM cart WHERE cart_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        }
    }

    public void clearCart(int custId) throws SQLException {
        String sql = "DELETE FROM cart WHERE cust_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId);
            ps.executeUpdate();
        }
    }

    public int getItemCount(int custId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart WHERE cust_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    private CartDBItem mapRow(ResultSet rs) throws SQLException {
        CartDBItem item = new CartDBItem();
        item.setCartId(rs.getInt("cart_id"));
        item.setCustId(rs.getInt("cust_id"));
        item.setShopProductId(rs.getInt("shop_product_id"));
        item.setSize(rs.getString("size"));
        item.setQuantity(rs.getInt("quantity"));
        item.setTitle(rs.getString("shop_product_title"));
        item.setPrice(rs.getBigDecimal("shop_product_price"));
        item.setProductImage(rs.getString("product_image"));
        String sz = item.getSize();
        int stock = 0;
        if ("S".equals(sz)) stock = rs.getInt("size_s");
        else if ("M".equals(sz)) stock = rs.getInt("size_m");
        else if ("L".equals(sz)) stock = rs.getInt("size_l");
        else if ("XL".equals(sz)) stock = rs.getInt("size_xl");
        item.setStock(stock);
        return item;
    }
}