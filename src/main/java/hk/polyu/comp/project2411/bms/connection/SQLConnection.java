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

    public SQLConnection() throws SQLException {
        connect();
    }

    private void connect() throws SQLException {
        // Load the Oracle Driver
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (Exception e) {
            System.err.println("Could not load the Oracle JDBC driver.");
            e.printStackTrace();
            return;
        }

        // Establish the connection
        conn = DriverManager.getConnection(url, username, password);

        System.out.println("Successfully connected to the database.");
    }

    private void checkConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            connect();
        }
    }

    // Begin a transaction by disabling auto-commit mode
    public void beginTransaction() throws SQLException {
        checkConnection();
        conn.setAutoCommit(false);
    }

    // Commit the current transaction and enable auto-commit mode
    public void commitTransaction() throws SQLException {
        checkConnection();
        conn.commit();
        conn.setAutoCommit(true);
    }

    // Roll back the current transaction and enable auto-commit mode
    public void rollbackTransaction() throws SQLException {
        checkConnection();
        conn.rollback();
        conn.setAutoCommit(true);
    }

    // Close the connection
    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("Database connection closed.");
        }
    }

    public List<Map<String, Object>> executeQuery(String sql) throws SQLException {
        checkConnection();
        List<Map<String, Object>> results = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Process each row in the result set
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                // Retrieve each column in the row
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }
        } finally {
            // Ensure resources are closed properly
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
        return results;
    }

    public int executeUpdate(String sql) throws SQLException {
        checkConnection();
        int result = 0;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            result = stmt.executeUpdate(sql);
        } finally {
            // Ensure the statement is closed
            if (stmt != null)
                stmt.close();
        }
        return result;
    }

    public List<Map<String, Object>> executePreparedQuery(String sql, Object[] params) throws SQLException {
        checkConnection();
        List<Map<String, Object>> results = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            // Set the prepared statement parameters
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            rs = pstmt.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Process each row in the result set
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                // Retrieve each column in the row
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }
        } finally {
            // Ensure resources are closed properly
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return results;
    }

    public int executePreparedUpdate(String sql, Object[] params) throws SQLException {
        checkConnection();
        int result = 0;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            // Set the prepared statement parameters
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            result = pstmt.executeUpdate();
        } finally {
            // Ensure the prepared statement is closed
            if (pstmt != null)
                pstmt.close();
        }
        return result;
    }
}