package petra.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:derby://localhost:1527/petra";

    private static final String[][] CREDENTIALS = {
        {"petra", "petra"},
        {"app", "app"},
        {"petra", ""},
        {"petra", "petra"},
    };

    static {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Derby driver not found. Add derbyclient.jar to WEB-INF/lib.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        SQLException last = null;
        for (String[] cred : CREDENTIALS) {
            try {
                return DriverManager.getConnection(URL, cred[0], cred[1]);
            } catch (SQLException e) {
                last = e;
            }
        }
        throw new SQLException("Cannot connect to Derby at " + URL + ". Last error: " + last.getMessage(), last);
    }
}
