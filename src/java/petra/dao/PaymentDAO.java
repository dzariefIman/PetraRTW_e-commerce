package petra.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import petra.model.Payment;
import petra.util.DBConnection;

public class PaymentDAO {

    public Payment findById(int id) throws SQLException {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public Payment findLatestByOrderNum(String orderNum) throws SQLException {
        String sql = "SELECT * FROM payments WHERE order_num = ? ORDER BY created_time DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNum);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<Payment> findByOrderNum(String orderNum) throws SQLException {
        List<Payment> list = new ArrayList<Payment>();
        String sql = "SELECT * FROM payments WHERE order_num = ? ORDER BY created_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNum);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public int create(Payment p) throws SQLException {
        String sql = "INSERT INTO payments (order_id, order_num, payment_method, gateway, gateway_ref, amount, currency, status, error_code, error_message, created_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getOrderId());
            ps.setString(2, p.getOrderNum());
            ps.setString(3, p.getPaymentMethod());
            if (p.getGateway() != null) ps.setString(4, p.getGateway()); else ps.setNull(4, java.sql.Types.VARCHAR);
            if (p.getGatewayRef() != null) ps.setString(5, p.getGatewayRef()); else ps.setNull(5, java.sql.Types.VARCHAR);
            ps.setBigDecimal(6, p.getAmount());
            ps.setString(7, p.getCurrency());
            ps.setString(8, p.getStatus());
            if (p.getErrorCode() != null) ps.setString(9, p.getErrorCode()); else ps.setNull(9, java.sql.Types.VARCHAR);
            if (p.getErrorMessage() != null) ps.setString(10, p.getErrorMessage()); else ps.setNull(10, java.sql.Types.VARCHAR);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); return keys.getInt(1); }
        }
    }

    public boolean updateStatus(int paymentId, String status, String errorCode, String errorMessage) throws SQLException {
        String sql = "UPDATE payments SET status = ?, error_code = ?, error_message = ?, updated_time = CURRENT_TIMESTAMP WHERE payment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            if (errorCode != null) ps.setString(2, errorCode); else ps.setNull(2, java.sql.Types.VARCHAR);
            if (errorMessage != null) ps.setString(3, errorMessage); else ps.setNull(3, java.sql.Types.VARCHAR);
            ps.setInt(4, paymentId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean markPaid(int paymentId, String gatewayRef) throws SQLException {
        String sql = "UPDATE payments SET status = 'PAID', gateway_ref = ?, paid_at = CURRENT_TIMESTAMP, updated_time = CURRENT_TIMESTAMP WHERE payment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gatewayRef);
            ps.setInt(2, paymentId);
            return ps.executeUpdate() > 0;
        }
    }

    private Payment map(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setOrderId(rs.getInt("order_id"));
        p.setOrderNum(rs.getString("order_num"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setGateway(rs.getString("gateway"));
        p.setGatewayRef(rs.getString("gateway_ref"));
        p.setAmount(rs.getBigDecimal("amount"));
        p.setCurrency(rs.getString("currency"));
        p.setStatus(rs.getString("status"));
        p.setErrorCode(rs.getString("error_code"));
        p.setErrorMessage(rs.getString("error_message"));
        p.setPaidAt(rs.getTimestamp("paid_at"));
        p.setCreatedTime(rs.getTimestamp("created_time"));
        p.setUpdatedTime(rs.getTimestamp("updated_time"));
        return p;
    }
}