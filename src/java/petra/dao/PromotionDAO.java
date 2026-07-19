package petra.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import petra.model.Promotion;
import petra.util.DBConnection;

public class PromotionDAO {

    public List<Promotion> findByStatus(String status) throws SQLException {
        List<Promotion> list = new ArrayList<Promotion>();
        String sql = "SELECT * FROM advertisements WHERE status = ? ORDER BY created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<Promotion> findByStatusWithLimit(String status, int limit) throws SQLException {
        List<Promotion> list = new ArrayList<Promotion>();
        String sql = "SELECT * FROM advertisements WHERE status = ? ORDER BY created_time DESC OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status); ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public Promotion findById(int id) throws SQLException {
        String sql = "SELECT * FROM advertisements WHERE ads_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<Promotion> findAll() throws SQLException {
        List<Promotion> list = new ArrayList<Promotion>();
        String sql = "SELECT * FROM advertisements ORDER BY created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        return list;
    }

    public boolean insert(Promotion p) throws SQLException {
        String sql = "INSERT INTO advertisements (ads_title, ads_desc, product_image, product_id, collection, start_date, end_date, status, staff_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getAdsTitle()); ps.setString(2, p.getAdsDesc()); ps.setString(3, p.getProductImage());
            if (p.getProductId() != null) ps.setInt(4, p.getProductId()); else ps.setNull(4, java.sql.Types.INTEGER);
            ps.setString(5, p.getCollection()); ps.setString(6, p.getStartDate()); ps.setString(7, p.getEndDate()); ps.setString(8, p.getStatus());
            if (p.getStaffId() != null) ps.setInt(9, p.getStaffId()); else ps.setNull(9, java.sql.Types.INTEGER);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Promotion p) throws SQLException {
        String sql = "UPDATE advertisements SET ads_title = ?, ads_desc = ?, product_image = ?, product_id = ?, collection = ?, start_date = ?, end_date = ?, status = ?, updated_time = CURRENT_TIMESTAMP WHERE ads_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getAdsTitle()); ps.setString(2, p.getAdsDesc()); ps.setString(3, p.getProductImage());
            if (p.getProductId() != null) ps.setInt(4, p.getProductId()); else ps.setNull(4, java.sql.Types.INTEGER);
            ps.setString(5, p.getCollection()); ps.setString(6, p.getStartDate()); ps.setString(7, p.getEndDate()); ps.setString(8, p.getStatus());
            ps.setInt(9, p.getAdsId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM advertisements WHERE ads_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1, id); return ps.executeUpdate() > 0; }
    }

    private Promotion map(ResultSet rs) throws SQLException {
        Promotion p = new Promotion();
        p.setAdsId(rs.getInt("ads_id")); p.setAdsTitle(rs.getString("ads_title")); p.setAdsDesc(rs.getString("ads_desc"));
        p.setProductImage(rs.getString("product_image")); p.setProductId((Integer) rs.getObject("product_id"));
        p.setCollection(rs.getString("collection")); p.setStartDate(rs.getString("start_date")); p.setEndDate(rs.getString("end_date"));
        p.setStatus(rs.getString("status")); p.setCreatedTime(rs.getString("created_time")); p.setUpdatedTime(rs.getString("updated_time"));
        try { p.setStaffId((Integer) rs.getObject("staff_id")); } catch (Exception e) {}
        return p;
    }
}
