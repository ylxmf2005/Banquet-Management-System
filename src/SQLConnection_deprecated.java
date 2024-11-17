import java.sql.*;
import java.util.*;

public class SQLConnection_deprecated {
    private Connection conn = null;
    private String url = "jdbc:oracle:thin:@5.181.225.147:40511:XE";
    private String username = "system";
    private String password = "2411project";

    public SQLConnection_deprecated() {
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

    /**
     * Execute a SELECT statement and return the result as a List of Maps.
     * Each Map represents a row, with column names as keys.
     * The method closes the ResultSet and Statement internally.
     *
     * @param sql The SQL query to execute.
     * @return A List of Maps representing the result set.
     */
    public List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> results = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            // Get metadata to retrieve column names
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Iterate over the ResultSet and build the list of maps
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    // Retrieve column name and value
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
            // Close resources in the reverse order of their creation
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

    /**
     * Execute an INSERT, UPDATE, or DELETE statement and return the number of
     * affected rows.
     *
     * @param sql The SQL statement to execute.
     * @return The number of affected rows.
     */
    public int executeUpdate(String sql) {
        int result = 0;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            result = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("An SQL exception occurred while executing the INSERT/UPDATE/DELETE statement.");
            e.printStackTrace();
        } finally {
            // Close the Statement
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

    /**
     * Execute a parameterized PreparedStatement query and return the result as a
     * List of Maps.
     * Each Map represents a row, with column names as keys.
     * The method closes the ResultSet and PreparedStatement internally.
     *
     * @param sql    The SQL query to execute.
     * @param params The parameters to set in the PreparedStatement.
     * @return A List of Maps representing the result set.
     */
    public List<Map<String, Object>> executePreparedQuery(String sql, Object[] params) {
        List<Map<String, Object>> results = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            rs = pstmt.executeQuery();

            // Get metadata to retrieve column names
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Iterate over the ResultSet and build the list of maps
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    // Retrieve column name and value
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
            // Close resources in the reverse order of their creation
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

    /**
     * Execute a parameterized PreparedStatement update and return the number of
     * affected rows.
     *
     * @param sql    The SQL statement to execute.
     * @param params The parameters to set in the PreparedStatement.
     * @return The number of affected rows.
     */
    public int executePreparedUpdate(String sql, Object[] params) {
        int result = 0;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(
                    "An SQL exception occurred while executing the parameterized INSERT/UPDATE/DELETE statement.");
            e.printStackTrace();
        } finally {
            // Close the PreparedStatement
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