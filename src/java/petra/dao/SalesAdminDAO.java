package petra.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import petra.model.SalesAdmin;
import petra.util.DBConnection;
import petra.util.PasswordUtil;

public class SalesAdminDAO {

    public SalesAdmin findById(int id) throws SQLException {
        String sql = "SELECT * FROM sales_admin WHERE staff_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public SalesAdmin findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM sales_admin WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public SalesAdmin findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM sales_admin WHERE staff_email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public SalesAdmin authenticate(String username, String password) throws SQLException {
        SalesAdmin s = findByUsername(username);
        if (s != null && PasswordUtil.verify(password, s.getPassword())) {
            return s;
        }
        return null;
    }

    public boolean update(SalesAdmin s, boolean changePassword, String newPassword) throws SQLException {
        String sql;
        boolean hasPicture = s.getProfilePicture() != null && !s.getProfilePicture().isEmpty();
        if (changePassword && newPassword != null && !newPassword.isEmpty()) {
            if (hasPicture) {
                sql = "UPDATE sales_admin SET staff_name=?, username=?, staff_email=?, staff_address=?, staff_phone_num=?, password=?, profile_picture=? WHERE staff_id=?";
            } else {
                sql = "UPDATE sales_admin SET staff_name=?, username=?, staff_email=?, staff_address=?, staff_phone_num=?, password=? WHERE staff_id=?";
            }
        } else {
            if (hasPicture) {
                sql = "UPDATE sales_admin SET staff_name=?, username=?, staff_email=?, staff_address=?, staff_phone_num=?, profile_picture=? WHERE staff_id=?";
            } else {
                sql = "UPDATE sales_admin SET staff_name=?, username=?, staff_email=?, staff_address=?, staff_phone_num=? WHERE staff_id=?";
            }
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, s.getStaffName());
            ps.setString(i++, s.getUsername());
            ps.setString(i++, s.getStaffEmail());
            ps.setString(i++, s.getStaffAddress());
            ps.setString(i++, s.getStaffPhoneNum());
            if (changePassword && newPassword != null && !newPassword.isEmpty()) {
                ps.setString(i++, PasswordUtil.hash(newPassword));
            }
            if (hasPicture) {
                ps.setString(i++, s.getProfilePicture());
            }
            ps.setInt(i++, s.getStaffId());
            return ps.executeUpdate() > 0;
        }
    }

    private SalesAdmin map(ResultSet rs) throws SQLException {
        SalesAdmin s = new SalesAdmin();
        s.setStaffId(rs.getInt("staff_id"));
        s.setStaffName(rs.getString("staff_name"));
        s.setUsername(rs.getString("username"));
        s.setPassword(rs.getString("password"));
        s.setStaffEmail(rs.getString("staff_email"));
        s.setStaffAddress(rs.getString("staff_address"));
        s.setStaffPhoneNum(rs.getString("staff_phone_num"));
        s.setProfilePicture(rs.getString("profile_picture"));
        return s;
    }
}
