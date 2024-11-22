package hk.polyu.comp.project2411.bms.connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryResultDeprecated implements AutoCloseable {
    private ResultSet resultSet;
    private Statement statement;

    public QueryResultDeprecated(ResultSet resultSet, Statement statement) {
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