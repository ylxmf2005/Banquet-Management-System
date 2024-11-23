package hk.polyu.comp.project2411.bms.connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLConnection {
    private Connection conn = null;
    private String url = "jdbc:oracle:thin:@5.181.225.147:40511:XE";
    private String username = "system";
    private String password = "2411project";

    public SQLConnection() {
        connect();
    }

    private void connect() {
        try {
            // Load the Oracle Driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Establish the connection
            conn = DriverManager.getConnection(url, username, password);

            System.out.println("Successfully connected to the database.");
        } catch (SQLException e) {
            System.err.println("An SQL exception occurred while connecting to the database.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC driver not found.");
            e.printStackTrace();
        }
    }

    private void checkConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("An SQL exception occurred while checking the database connection.");
            e.printStackTrace();
        }
    }

    // Close the connection
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("An SQL exception occurred while closing the database connection.");
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> executeQuery(String sql) {
        checkConnection();
        List<Map<String, Object>> results = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }
        } catch (SQLException e) {
            System.err.println("An SQL exception occurred while executing the SELECT statement.");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                System.err.println("An SQL exception occurred while closing the ResultSet.");
                e.printStackTrace();
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                System.err.println("An SQL exception occurred while closing the Statement.");
                e.printStackTrace();
            }
        }
        return results;
    }

    public int executeUpdate(String sql) {
        checkConnection();
        int result = 0;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            result = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("An SQL exception occurred while executing the INSERT/UPDATE/DELETE statement.");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                System.err.println("An SQL exception occurred while closing the Statement.");
                e.printStackTrace();
            }
        }
        return result;
    }

    public List<Map<String, Object>> executePreparedQuery(String sql, Object[] params) {
        checkConnection();
        List<Map<String, Object>> results = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            rs = pstmt.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }
        } catch (SQLException e) {
            System.err.println("An SQL exception occurred while executing the parameterized SELECT statement.");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                System.err.println("An SQL exception occurred while closing the ResultSet.");
                e.printStackTrace();
            }
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                System.err.println("An SQL exception occurred while closing the PreparedStatement.");
                e.printStackTrace();
            }
        }
        return results;
    }

    public int executePreparedUpdate(String sql, Object[] params) {
        checkConnection();
        int result = 0;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("An SQL exception occurred while executing the parameterized INSERT/UPDATE/DELETE statement.");
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                System.err.println("An SQL exception occurred while closing the PreparedStatement.");
                e.printStackTrace();
            }
        }
        return result;
    }
}