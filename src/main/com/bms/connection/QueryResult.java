package main.com.bms.connection;
import java.sql.*;

public class QueryResult implements AutoCloseable {
    private ResultSet resultSet;
    private Statement statement;

    public QueryResult(ResultSet resultSet, Statement statement) {
        this.resultSet = resultSet;
        this.statement = statement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    @Override
    public void close() throws SQLException {
        // Close the ResultSet and Statement to release resources
        if (resultSet != null && !resultSet.isClosed()) {
            resultSet.close();
        }
        if (statement != null && !statement.isClosed()) {
            statement.close();
        }
    }
}