package hk.polyu.comp.project2411.bms.dao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.exceptions.ValidationException;
import hk.polyu.comp.project2411.bms.model.AttendeeAccount;
import hk.polyu.comp.project2411.bms.model.Reserve;
import hk.polyu.comp.project2411.bms.model.SearchCriteria;


public class AttendeeAccountDAO {
    private SQLConnection sqlConnection;

    public AttendeeAccountDAO(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public AttendeeAccount getAttendeeByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Account WHERE Email = ? AND Role = 'user'";
        Object[] params = new Object[] { email };

        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, params);

        if (!results.isEmpty()) {
            Map<String, Object> row = results.get(0);
            return new AttendeeAccount(row);
        } else {
            return null; // Attendee not found
        }
    }

    public boolean updateAttendeeProfile(AttendeeAccount attendee) throws ValidationException, SQLException {
        String sql;
        Object[] params;
        
        if (attendee.getPassword() == null || attendee.getPassword().isEmpty()) {
            sql = "UPDATE Account SET Email = ?, FirstName=?, LastName=?, Address=?, Type=?, MobileNo=?, Organization=? WHERE Email=? AND Role='user'";
            params = new Object[] {
                    attendee.getEmail(),
                    attendee.getFirstName(),
                    attendee.getLastName(),
                    attendee.getAddress(),
                    attendee.getType(),
                    attendee.getMobileNo(),
                    attendee.getOrganization(),
                    attendee.getOriginalEmail()
            }; 
        } else {
            sql = "UPDATE Account SET Email = ?, FirstName=?, LastName=?, Address=?, Type=?, Password=?, MobileNo=?, Organization=? WHERE Email=? AND Role='user'";
            params = new Object[] {
                    attendee.getEmail(),
                    attendee.getFirstName(),
                    attendee.getLastName(),
                    attendee.getAddress(),
                    attendee.getType(),
                    Utils.encoding(attendee.getPassword()),
                    attendee.getMobileNo(),
                    attendee.getOrganization(),
                    attendee.getOriginalEmail()
            };
        }
        
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }
    
    public boolean registerAttendee(AttendeeAccount attendee) throws ValidationException, SQLException {
        String sql = "INSERT INTO Account (Role, Email, FirstName, LastName, Address, Type, Password, MobileNo, Organization)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] params = new Object[] {
                attendee.getRole(), // user
                attendee.getEmail(),
                attendee.getFirstName(),
                attendee.getLastName(),
                attendee.getAddress(),
                attendee.getType(),
                Utils.encoding(attendee.getPassword()),
                attendee.getMobileNo(),
                attendee.getOrganization()
        };
        // System.out.println("Role:" + attendee.getRole());
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }

    public List<Reserve> searchRegistrations(String attendeeEmail, SearchCriteria criteria) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT r.* FROM Reserve r " +
            "JOIN Banquet b ON b.BIN = r.BanquetBIN " +
            "WHERE r.AttendeeEmail = ?");
        
        List<Object> params = new ArrayList<>();
        params.add(attendeeEmail);

        if (criteria.getBanquetName() != null && !criteria.getBanquetName().trim().isEmpty()) {
            sql.append(" AND LOWER(b.Name) LIKE LOWER(?)");
            params.add("%" + criteria.getBanquetName() + "%");
        }

        if (criteria.getStartDate() != null) {
            sql.append(" AND b.DateTime >= TO_TIMESTAMP(?, 'YYYY-MM-DD\"T\"HH24:MI:SS.FF3\"Z\"')");
            params.add(criteria.getStartDate());
        }

        if (criteria.getEndDate() != null) {
            sql.append(" AND b.DateTime <= TO_TIMESTAMP(?, 'YYYY-MM-DD\"T\"HH24:MI:SS.FF3\"Z\"')");
            params.add(criteria.getEndDate());
        }

        sql.append(" ORDER BY r.RegTime");

        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(
            sql.toString(), 
            params.toArray()
        );

        List<Reserve> reservations = new ArrayList<>();
        for (Map<String, Object> result : results) {
            reservations.add(new Reserve(result));
        }
        return reservations;
    }
}

