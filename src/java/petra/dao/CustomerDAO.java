package petra.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import petra.model.Customer;
import petra.util.DBConnection;
import petra.util.PasswordUtil;

public class CustomerDAO {

    public Customer findById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE cust_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Customer findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM customers WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Customer findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM customers WHERE cust_email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Customer authenticate(String username, String password) throws SQLException {
        Customer c = findByUsername(username);
        if (c != null && PasswordUtil.verify(password, c.getPassword())) {
            return c;
        }
        return null;
    }

    public boolean insert(Customer c) throws SQLException {
        String sql = "INSERT INTO customers (cust_name, username, password, cust_email, cust_address, cust_phone_num) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCustName());
            ps.setString(2, c.getUsername());
            ps.setString(3, PasswordUtil.hash(c.getPassword()));
            ps.setString(4, c.getCustEmail());
            ps.setString(5, c.getCustAddress());
            ps.setString(6, c.getCustPhoneNum());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<Customer>();
        String sql = "SELECT * FROM customers ORDER BY cust_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public int countWithFilters(String searchId, String searchName, String searchEmail) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM customers WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        if (searchId != null && !searchId.trim().isEmpty()) {
            sql.append(" AND CAST(cust_id AS VARCHAR(20)) LIKE ?");
            params.add("%" + searchId.trim() + "%");
        }
        if (searchName != null && !searchName.trim().isEmpty()) {
            sql.append(" AND cust_name LIKE ?");
            params.add("%" + searchName.trim() + "%");
        }
        if (searchEmail != null && !searchEmail.trim().isEmpty()) {
            sql.append(" AND cust_email LIKE ?");
            params.add("%" + searchEmail.trim() + "%");
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    public List<Customer> findAllWithFilters(String searchId, String searchName, String searchEmail, int limit, int offset) throws SQLException {
        List<Customer> list = new ArrayList<Customer>();
        StringBuilder sql = new StringBuilder("SELECT * FROM customers WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        if (searchId != null && !searchId.trim().isEmpty()) {
            sql.append(" AND CAST(cust_id AS VARCHAR(20)) LIKE ?");
            params.add("%" + searchId.trim() + "%");
        }
        if (searchName != null && !searchName.trim().isEmpty()) {
            sql.append(" AND cust_name LIKE ?");
            params.add("%" + searchName.trim() + "%");
        }
        if (searchEmail != null && !searchEmail.trim().isEmpty()) {
            sql.append(" AND cust_email LIKE ?");
            params.add("%" + searchEmail.trim() + "%");
        }
        sql.append(" ORDER BY cust_id");
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(limit);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private Customer map(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustId(rs.getInt("cust_id"));
        c.setCustName(rs.getString("cust_name"));
        c.setUsername(rs.getString("username"));
        c.setPassword(rs.getString("password"));
        c.setCustEmail(rs.getString("cust_email"));
        c.setCustAddress(rs.getString("cust_address"));
        c.setCustPhoneNum(rs.getString("cust_phone_num"));
        return c;
    }
}
