package hk.polyu.comp.project2411.bms.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.exceptions.AuthenticationException;
import hk.polyu.comp.project2411.bms.model.Account;
import hk.polyu.comp.project2411.bms.model.AdminAccount;
import hk.polyu.comp.project2411.bms.model.AttendeeAccount;
import hk.polyu.comp.project2411.bms.model.PasswordEncoding;

public class AccountDAO {
    private SQLConnection sqlConnection;

    public AccountDAO(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }
    
    public Account authenticateAccount(String email, String password) throws AuthenticationException, SQLException {
        password = PasswordEncoding.encoding(password);
        String sql = "SELECT * FROM Account WHERE Email = ?";
        Object[] params = new Object[] { email };

        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, params);

        if (!results.isEmpty()) {
            Map<String, Object> row = results.get(0);
            // System.out.println(row);
            String storedPassword = (String) row.get("PASSWORD");
            if (storedPassword.equals(password)) {
                String role = (String) row.get("ROLE") == null ? (String) row.get("role") : (String) row.get("ROLE");
                if ("admin".equalsIgnoreCase(role)) {
                    return new AdminAccount(row);
                } else if ("user".equalsIgnoreCase(role)) {
                    return new AttendeeAccount(row);
                } else {
                    throw new AuthenticationException("Unknown role.");
                }
            } else {
                throw new AuthenticationException("Incorrect password.");
            }
        } else {
            throw new AuthenticationException("Attendee not found.");
        }
    }
}
