package petra.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import petra.model.Product;
import petra.util.DBConnection;

public class ProductDAO {

    public Product findById(int id) throws SQLException {
        String sql = "SELECT * FROM shop_products WHERE shop_product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<Product> findByGroup(String collection) throws SQLException {
        List<Product> list = new ArrayList<Product>();
        String sql = "SELECT * FROM shop_products WHERE collection = ? ORDER BY created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, collection);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<String> findAllCollections() throws SQLException {
        List<String> groups = new ArrayList<String>();
        String sql = "SELECT DISTINCT collection FROM shop_products WHERE collection IS NOT NULL AND collection != '' ORDER BY collection";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) groups.add(rs.getString("collection"));
        }
        return groups;
    }

    public List<Product> findUncategorized() throws SQLException {
        List<Product> list = new ArrayList<Product>();
        String sql = "SELECT * FROM shop_products WHERE collection IS NULL OR collection = '' ORDER BY created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Product> findAll() throws SQLException {
        List<Product> list = new ArrayList<Product>();
        String sql = "SELECT * FROM shop_products ORDER BY created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public int countAll(String searchName, String searchGroup) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM shop_products WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        if (searchName != null && !searchName.trim().isEmpty()) {
            sql.append(" AND shop_product_title LIKE ?");
            params.add("%" + searchName.trim() + "%");
        }
        if (searchGroup != null && !searchGroup.trim().isEmpty()) {
            sql.append(" AND collection = ?");
            params.add(searchGroup.trim());
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    public List<Product> findAllPaged(int page, int perPage, String searchName, String searchGroup) throws SQLException {
        List<Product> list = new ArrayList<Product>();
        StringBuilder sql = new StringBuilder("SELECT * FROM shop_products WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        if (searchName != null && !searchName.trim().isEmpty()) {
            sql.append(" AND shop_product_title LIKE ?");
            params.add("%" + searchName.trim() + "%");
        }
        if (searchGroup != null && !searchGroup.trim().isEmpty()) {
            sql.append(" AND collection = ?");
            params.add(searchGroup.trim());
        }
        sql.append(" ORDER BY shop_product_title ASC");
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * perPage);
        params.add(perPage);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public Product findByTitle(String title) throws SQLException {
        String sql = "SELECT shop_product_id FROM shop_products WHERE shop_product_title = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { Product p = new Product(); p.setShopProductId(rs.getInt(1)); return p; }
            }
        }
        return null;
    }

    public boolean insert(Product p) throws SQLException {
        String sql = "INSERT INTO shop_products (shop_product_title, shop_product_price, shop_product_desc, product_image, collection, item_type, size_s, size_m, size_l, size_xl) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getShopProductTitle());
            ps.setBigDecimal(2, p.getShopProductPrice());
            ps.setString(3, p.getShopProductDesc());
            ps.setString(4, p.getProductImage());
            ps.setString(5, p.getCollection());
            ps.setString(6, p.getItemType() != null ? p.getItemType() : "cloth");
            ps.setInt(7, p.getSizeS());
            ps.setInt(8, p.getSizeM());
            ps.setInt(9, p.getSizeL());
            ps.setInt(10, p.getSizeXL());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) p.setShopProductId(rs.getInt(1)); }
            }
            return affected > 0;
        }
    }

    public boolean update(Product p) throws SQLException {
        String sql = "UPDATE shop_products SET shop_product_title = ?, shop_product_price = ?, shop_product_desc = ?, product_image = ?, collection = ?, item_type = ?, size_s = ?, size_m = ?, size_l = ?, size_xl = ? WHERE shop_product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getShopProductTitle());
            ps.setBigDecimal(2, p.getShopProductPrice());
            ps.setString(3, p.getShopProductDesc());
            ps.setString(4, p.getProductImage());
            ps.setString(5, p.getCollection());
            ps.setString(6, p.getItemType() != null ? p.getItemType() : "cloth");
            ps.setInt(7, p.getSizeS());
            ps.setInt(8, p.getSizeM());
            ps.setInt(9, p.getSizeL());
            ps.setInt(10, p.getSizeXL());
            ps.setInt(11, p.getShopProductId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM shop_products WHERE shop_product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setShopProductId(rs.getInt("shop_product_id"));
        p.setShopProductTitle(rs.getString("shop_product_title"));
        p.setShopProductPrice(rs.getBigDecimal("shop_product_price"));
        p.setShopProductDesc(rs.getString("shop_product_desc"));
        p.setProductImage(rs.getString("product_image"));
        p.setCreatedTime(rs.getString("created_time"));
        p.setCollection(rs.getString("collection"));
        try { p.setItemType(rs.getString("item_type")); } catch (Exception e) { p.setItemType("cloth"); }
        p.setSizeS(rs.getInt("size_s"));
        p.setSizeM(rs.getInt("size_m"));
        p.setSizeL(rs.getInt("size_l"));
        p.setSizeXL(rs.getInt("size_xl"));
        try { p.setAdvertisementId((Integer) rs.getObject("advertisement_id")); } catch (Exception e) {}
        return p;
    }
}
