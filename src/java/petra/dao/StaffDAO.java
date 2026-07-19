package petra.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import petra.model.Staff;
import petra.util.DBConnection;
import petra.util.PasswordUtil;

public class StaffDAO {

    public Staff findById(int id) throws SQLException {
        String sql = "SELECT * FROM staff WHERE staff_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Staff findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM staff WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Staff authenticate(String username, String password) throws SQLException {
        Staff s = findByUsername(username);
        if (s != null && PasswordUtil.verify(password, s.getPassword())) {
            return s;
        }
        return null;
    }

    private Staff map(ResultSet rs) throws SQLException {
        Staff s = new Staff();
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
