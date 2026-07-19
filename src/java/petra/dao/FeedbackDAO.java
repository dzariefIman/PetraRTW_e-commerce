package petra.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import petra.model.Feedback;
import petra.util.DBConnection;

public class FeedbackDAO {

    public Feedback findById(int id) throws SQLException {
        String sql = "SELECT f.*, c.cust_name AS customer_name FROM feedback f LEFT JOIN customers c ON f.cust_id = c.cust_id WHERE f.feedback_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public Feedback findByCustAndPurchase(int custId, int purchaseId) throws SQLException {
        String sql = "SELECT * FROM feedback WHERE cust_id = ? AND purchase_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId); ps.setInt(2, purchaseId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public Feedback findByCustAndOrder(int custId, String orderNum) throws SQLException {
        String sql = "SELECT * FROM feedback WHERE cust_id = ? AND order_num = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId); ps.setString(2, orderNum);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public boolean hasFeedback(int custId, int purchaseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM feedback WHERE cust_id = ? AND purchase_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId); ps.setInt(2, purchaseId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1) > 0; }
        }
    }

    public boolean hasFeedbackForOrder(int custId, String orderNum) throws SQLException {
        String sql = "SELECT COUNT(*) FROM feedback f JOIN purchases p ON f.purchase_id = p.purchase_id WHERE f.cust_id = ? AND p.order_num = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId); ps.setString(2, orderNum);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1) > 0; }
        }
    }

    public List<Feedback> findByCustId(int custId) throws SQLException {
        List<Feedback> list = new ArrayList<Feedback>();
        String sql = "SELECT * FROM feedback WHERE cust_id = ? ORDER BY created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<Feedback> findRecent(int limit) throws SQLException {
        List<Feedback> list = new ArrayList<Feedback>();
        String sql = "SELECT f.*, c.cust_name AS customer_name FROM feedback f LEFT JOIN customers c ON f.cust_id = c.cust_id ORDER BY f.created_time DESC OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<Feedback> findAll() throws SQLException {
        List<Feedback> list = new ArrayList<Feedback>();
        String sql = "SELECT f.*, c.cust_name AS customer_name FROM feedback f LEFT JOIN customers c ON f.cust_id = c.cust_id ORDER BY f.created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        return list;
    }

    public List<Feedback> search(String keyword) throws SQLException {
        List<Feedback> list = new ArrayList<Feedback>();
        String sql = "SELECT f.*, c.cust_name AS customer_name FROM feedback f LEFT JOIN customers c ON f.cust_id = c.cust_id WHERE f.product_name LIKE ? OR c.cust_name LIKE ? ORDER BY f.created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%"; ps.setString(1, like); ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public boolean create(Feedback f) throws SQLException {
        String sql = "INSERT INTO feedback (cust_id, product_name, order_num, purchase_id, rating, feedback_text, product_image, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getCustId()); ps.setString(2, f.getProductName()); ps.setString(3, f.getOrderNum());
            ps.setObject(4, f.getPurchaseId()); ps.setInt(5, f.getRating()); ps.setString(6, f.getFeedbackText());
            ps.setString(7, f.getProductImage()); ps.setString(8, "Published");
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Feedback f) throws SQLException {
        String sql = "UPDATE feedback SET rating = ?, feedback_text = ?, product_image = ?, status = ? WHERE feedback_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getRating()); ps.setString(2, f.getFeedbackText()); ps.setString(3, f.getProductImage());
            ps.setString(4, f.getStatus()); ps.setInt(5, f.getFeedbackId());
            return ps.executeUpdate() > 0;
        }
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM feedback";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<Feedback> findAllPaginated(int offset, int perPage) throws SQLException {
        List<Feedback> list = new ArrayList<Feedback>();
        String sql = "SELECT f.*, c.cust_name AS customer_name FROM feedback f LEFT JOIN customers c ON f.cust_id = c.cust_id ORDER BY f.created_time DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, perPage);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    private Feedback map(ResultSet rs) throws SQLException {
        Feedback f = new Feedback();
        f.setFeedbackId(rs.getInt("feedback_id")); f.setCustId(rs.getInt("cust_id"));
        f.setFeedbackText(rs.getString("feedback_text")); f.setProductImage(rs.getString("product_image"));
        f.setStatus(rs.getString("status")); f.setCreatedTime(rs.getString("created_time"));
        f.setOrderNum(rs.getString("order_num")); f.setRating(rs.getInt("rating"));
        f.setPurchaseId((Integer) rs.getObject("purchase_id")); f.setProductName(rs.getString("product_name"));
        try { f.setCustomerName(rs.getString("customer_name")); } catch (Exception e) {}
        return f;
    }
}
