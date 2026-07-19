package petra.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import petra.model.Issue;
import petra.util.DBConnection;

public class IssueDAO {

    public Issue findById(int id) throws SQLException {
        String sql = "SELECT i.*, c.cust_name AS customer_name, c.cust_phone_num AS customer_phone, c.cust_email AS customer_email, p.purchase_desc AS item_description, "
                   + "COALESCE(lu.last_update, i.created_time) AS last_update "
                   + "FROM issues i LEFT JOIN customers c ON i.cust_id = c.cust_id LEFT JOIN purchases p ON i.purchase_id = p.purchase_id "
                   + "LEFT JOIN (SELECT issue_id, MAX(created_time) AS last_update FROM issue_comments GROUP BY issue_id) lu ON lu.issue_id = i.issue_id WHERE i.issue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public Issue findByIdAndCustId(int id, int custId) throws SQLException {
        String sql = "SELECT * FROM issues WHERE issue_id = ? AND cust_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); ps.setInt(2, custId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public boolean hasIssueForPurchase(int custId, int purchaseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM issues WHERE cust_id = ? AND purchase_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId); ps.setInt(2, purchaseId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1) > 0; }
        }
    }

    public List<Issue> findByCustId(int custId) throws SQLException {
        List<Issue> list = new ArrayList<Issue>();
        String sql = "SELECT i.*, COALESCE(lu.last_update, i.created_time) AS last_update "
            + "FROM issues i "
            + "LEFT JOIN (SELECT issue_id, MAX(created_time) AS last_update FROM issue_comments GROUP BY issue_id) lu ON lu.issue_id = i.issue_id "
            + "WHERE i.cust_id = ? ORDER BY i.created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<Issue> findByCustIdWithFilters(int custId, String ticketNum, String status) throws SQLException {
        List<Issue> list = new ArrayList<Issue>();
        StringBuilder sql = new StringBuilder("SELECT i.*, COALESCE(lu.last_update, i.created_time) AS last_update "
            + "FROM issues i "
            + "LEFT JOIN (SELECT issue_id, MAX(created_time) AS last_update FROM issue_comments GROUP BY issue_id) lu ON lu.issue_id = i.issue_id "
            + "WHERE i.cust_id = ?");
        if (ticketNum != null && !ticketNum.isEmpty()) sql.append(" AND CAST(i.ticket_num AS VARCHAR(20)) LIKE ?");
        if (status != null && !status.isEmpty()) sql.append(" AND i.status = ?");
        sql.append(" ORDER BY i.created_time DESC");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1; ps.setInt(idx++, custId);
            if (ticketNum != null && !ticketNum.isEmpty()) ps.setString(idx++, "%" + ticketNum + "%");
            if (status != null && !status.isEmpty()) ps.setString(idx++, status);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public int create(Issue issue) throws SQLException {
        String sql = "INSERT INTO issues (cust_id, purchase_id, order_num, title, issue_desc, product_image, status, created_time) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, issue.getCustId()); ps.setObject(2, issue.getPurchaseId()); ps.setString(3, issue.getOrderNum());
            ps.setString(4, issue.getTitle()); ps.setString(5, issue.getIssueDesc()); ps.setString(6, issue.getProductImage()); ps.setString(7, "New");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next(); int id = keys.getInt(1);
                try (PreparedStatement upd = conn.prepareStatement("UPDATE issues SET ticket_num = ? WHERE issue_id = ?")) { upd.setInt(1, id); upd.setInt(2, id); upd.executeUpdate(); }
                return id;
            }
        }
    }

    public List<Issue> findAllWithFilters(String ticketNum, String custName, String status, int page, int perPage) throws SQLException {
        List<Issue> list = new ArrayList<Issue>();
        StringBuilder sql = new StringBuilder("SELECT i.*, c.cust_name AS customer_name, COALESCE(lu.last_update, i.created_time) AS last_update "
            + "FROM issues i LEFT JOIN customers c ON i.cust_id = c.cust_id "
            + "LEFT JOIN (SELECT issue_id, MAX(created_time) AS last_update FROM issue_comments GROUP BY issue_id) lu ON lu.issue_id = i.issue_id WHERE 1=1");
        if (ticketNum != null && !ticketNum.isEmpty()) sql.append(" AND CAST(i.ticket_num AS VARCHAR(20)) LIKE ?");
        if (custName != null && !custName.isEmpty()) sql.append(" AND c.cust_name LIKE ?");
        if (status != null && !status.isEmpty()) sql.append(" AND i.status = ?");
        sql.append(" ORDER BY i.created_time DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (ticketNum != null && !ticketNum.isEmpty()) ps.setString(idx++, "%" + ticketNum + "%");
            if (custName != null && !custName.isEmpty()) ps.setString(idx++, "%" + custName + "%");
            if (status != null && !status.isEmpty()) ps.setString(idx++, status);
            ps.setInt(idx++, (page - 1) * perPage); ps.setInt(idx++, perPage);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) { Issue i = map(rs); try { i.setCustomerName(rs.getString("customer_name")); } catch (Exception e) {} list.add(i); } }
        }
        return list;
    }

    public int countAllWithFilters(String ticketNum, String custName, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM issues i LEFT JOIN customers c ON i.cust_id = c.cust_id WHERE 1=1");
        if (ticketNum != null && !ticketNum.isEmpty()) sql.append(" AND CAST(i.ticket_num AS VARCHAR(20)) LIKE ?");
        if (custName != null && !custName.isEmpty()) sql.append(" AND c.cust_name LIKE ?");
        if (status != null && !status.isEmpty()) sql.append(" AND i.status = ?");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (ticketNum != null && !ticketNum.isEmpty()) ps.setString(idx++, "%" + ticketNum + "%");
            if (custName != null && !custName.isEmpty()) ps.setString(idx++, "%" + custName + "%");
            if (status != null && !status.isEmpty()) ps.setString(idx++, status);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    public boolean updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE issues SET status = ?, closed_time = ? WHERE issue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, "Closed".equals(status) ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) : null);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Issue map(ResultSet rs) throws SQLException {
        Issue i = new Issue();
        i.setIssueId(rs.getInt("issue_id")); i.setTicketNum((Integer) rs.getObject("ticket_num"));
        i.setCustId((Integer) rs.getObject("cust_id")); i.setPurchaseId((Integer) rs.getObject("purchase_id"));
        i.setOrderNum(rs.getString("order_num")); i.setTitle(rs.getString("title")); i.setIssueDesc(rs.getString("issue_desc"));
        i.setProductImage(rs.getString("product_image")); i.setStatus(rs.getString("status"));
        i.setClosedTime(rs.getString("closed_time")); i.setCreatedTime(rs.getString("created_time"));
        try { i.setLastUpdate(rs.getString("last_update")); } catch (Exception e) {}
        try { i.setStaffId((Integer) rs.getObject("staff_id")); } catch (Exception e) {}
        try { i.setCustomerName(rs.getString("customer_name")); } catch (Exception e) {}
        try { i.setCustomerPhone(rs.getString("customer_phone")); } catch (Exception e) {}
        try { i.setCustomerEmail(rs.getString("customer_email")); } catch (Exception e) {}
        try { i.setItemDescription(rs.getString("item_description")); } catch (Exception e) {}
        return i;
    }
}
