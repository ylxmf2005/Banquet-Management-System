package hk.polyu.comp.project2411.bms.connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnectionDeprecated {
    private Connection conn = null;
    private String url = "jdbc:oracle:thin:@5.181.225.147:40511:XE";
    private String username = "system";
    private String password = "project2411project";

    public SQLConnectionDeprecated() throws ClassNotFoundException, SQLException {
        // Load the Oracle Driver
        Class.forName("oracle.jdbc.driver.OracleDriver");

        // Establish the connection
        conn = DriverManager.getConnection(url, username, password);

        System.out.println("Successfully connected to the database.");
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

    // Execute a SELECT statement and return a QueryResult
    public QueryResultDeprecated executeQuery(String sql) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        return new QueryResultDeprecated(rs, stmt);
    }

    // Execute an INSERT, UPDATE, or DELETE statement and return the number of affected rows
    public int executeUpdate(String sql) throws SQLException {
        int result = 0;
        try (Statement stmt = conn.createStatement()) {
            result = stmt.executeUpdate(sql);
        }
        return result;
    }

    // Execute a parameterized PreparedStatement query and return a QueryResult
    public QueryResultDeprecated executePreparedQuery(String sql, Object[] params) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
        ResultSet rs = pstmt.executeQuery();
        return new QueryResultDeprecated(rs, pstmt);
    }

    // Execute a parameterized PreparedStatement update and return the number of affected rows
    public int executePreparedUpdate(String sql, Object[] params) throws SQLException {
        int result = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            result = pstmt.executeUpdate();
        }
        return result;
    }
}