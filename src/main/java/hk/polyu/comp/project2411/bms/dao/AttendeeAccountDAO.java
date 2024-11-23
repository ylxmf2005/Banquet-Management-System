package hk.polyu.comp.project2411.bms.dao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.exceptions.ValidationException;
import hk.polyu.comp.project2411.bms.model.AttendeeAccount;
import hk.polyu.comp.project2411.bms.model.Banquet;
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
        String sql = "UPDATE Account SET Email = ?, FirstName=?, LastName=?, Address=?, Type=?, Password=?, MobileNo=?, Organization=? WHERE Email=? AND Role='user'";
        Object[] params = new Object[] {
                attendee.getEmail(),
                attendee.getFirstName(),
                attendee.getLastName(),
                attendee.getAddress(),
                attendee.getType(),
                attendee.getPassword(),
                attendee.getMobileNo(),
                attendee.getOrganization(),
                attendee.getOriginalEmail(),
        };
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }
    boolean registerAttendee(AttendeeAccount attendee) throws ValidationException, SQLException {
        String sql = "INSERT INTO AttendeeAccount (Email, FirstName, LastName, Address, Type, Password, MobileNo, Organization)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] params = new Object[] {
                attendee.getEmail(),
                attendee.getFirstName(),
                attendee.getLastName(),
                attendee.getAddress(),
                attendee.getType(),
                attendee.getPassword(),
                attendee.getMobileNo(),
                attendee.getOrganization()
        };
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }

    List<Banquet> searchRegisteredBanquets(String attendeeEmail, SearchCriteria criteria) throws SQLException {
        String sql = "SELECT b.* " +
                "FROM Reserves r " +
                "JOIN Banquet b ON r.BanquetBIN = b.BIN " +
                "JOIN AttendeeAccount a ON r.AttendeeEmail = a.Email " +
                "WHERE r.AttendeeEmail = ? " +
                "AND (? IS NULL OR b.Name LIKE ?) " +    //Banquet Name   if criteria has null, it is not tested.
                "AND (? IS NULL OR b.DateTime = ?) " +   //Date
                "AND (? IS NULL OR a.Type = ?)";         //Attendee Type
        Object[] params = new Object[] {
                attendeeEmail,
                criteria.getBanquetNamePart(),
                criteria.getBanquetNamePart(),
                criteria.getDate(),
                criteria.getDate(),
                criteria.getAttendeeType(),
                criteria.getAttendeeType()
        };
        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, params);

        List<Banquet> banquets = new ArrayList<>();
        for(Map<String, Object> row : results) {
            banquets.add(new Banquet(row));
        }
        return banquets;
    }
}
