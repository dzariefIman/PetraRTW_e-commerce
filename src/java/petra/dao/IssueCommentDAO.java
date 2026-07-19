package petra.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import petra.model.IssueComment;
import petra.util.DBConnection;

public class IssueCommentDAO {

    public List<IssueComment> findByIssueId(int issueId) throws SQLException {
        List<IssueComment> list = new ArrayList<IssueComment>();
        String sql = "SELECT c.*, "
            + "  CASE WHEN c.staff_id IS NOT NULL THEN s.staff_name ELSE cu.cust_name END AS sender_name, "
            + "  CASE WHEN c.staff_id IS NOT NULL THEN 'staff' ELSE 'customer' END AS sender_role, "
            + "  pr.comment_text AS reply_comment, "
            + "  CASE WHEN pr.staff_id IS NOT NULL THEN ps.staff_name ELSE pc.cust_name END AS reply_sender_name "
            + "FROM issue_comments c "
            + "LEFT JOIN staff s ON c.staff_id = s.staff_id "
            + "LEFT JOIN issues i ON c.issue_id = i.issue_id "
            + "LEFT JOIN customers cu ON i.cust_id = cu.cust_id "
            + "LEFT JOIN issue_comments pr ON c.reply_to_comment_id = pr.issue_comments_id "
            + "LEFT JOIN staff ps ON pr.staff_id = ps.staff_id "
            + "LEFT JOIN issues pi ON pr.issue_id = pi.issue_id "
            + "LEFT JOIN customers pc ON pi.cust_id = pc.cust_id "
            + "WHERE c.issue_id = ? ORDER BY c.created_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public int addMessage(int issueId, Integer staffId, String commentText, String issueAttachment, Integer replyToCommentId) throws SQLException {
        String sql;
        if (replyToCommentId != null && replyToCommentId > 0) {
            sql = "INSERT INTO issue_comments (issue_id, staff_id, comment_text, issue_attachment, reply_to_comment_id, created_time) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        } else {
            sql = "INSERT INTO issue_comments (issue_id, staff_id, comment_text, issue_attachment, created_time) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, issueId); ps.setObject(2, staffId); ps.setString(3, commentText); ps.setString(4, issueAttachment);
            if (replyToCommentId != null && replyToCommentId > 0) {
                ps.setInt(5, replyToCommentId);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); return keys.getInt(1); }
        }
    }

    public boolean deleteMessage(int messageId, int issueId) throws SQLException {
        String sql = "DELETE FROM issue_comments WHERE issue_comments_id = ? AND issue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId); ps.setInt(2, issueId);
            return ps.executeUpdate() > 0;
        }
    }

    private IssueComment map(ResultSet rs) throws SQLException {
        IssueComment c = new IssueComment();
        c.setIssueCommentsId(rs.getInt("issue_comments_id")); c.setIssueId(rs.getInt("issue_id"));
        c.setStaffId((Integer) rs.getObject("staff_id")); c.setCommentText(rs.getString("comment_text"));
        c.setCreatedTime(rs.getString("created_time")); c.setIssueAttachment(rs.getString("issue_attachment"));
        c.setUpdatedTime(rs.getString("updated_time"));
        try { c.setSenderName(rs.getString("sender_name")); } catch (Exception e) {}
        try { c.setSenderRole(rs.getString("sender_role")); } catch (Exception e) {}
        try { c.setReplyToCommentId((Integer) rs.getObject("reply_to_comment_id")); } catch (Exception e) {}
        try { c.setReplyComment(rs.getString("reply_comment")); } catch (Exception e) {}
        try { c.setReplySenderName(rs.getString("reply_sender_name")); } catch (Exception e) {}
        return c;
    }
}
