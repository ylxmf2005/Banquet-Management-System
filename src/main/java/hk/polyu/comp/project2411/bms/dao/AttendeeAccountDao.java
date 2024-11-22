package hk.polyu.comp.project2411.bms.dao;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.exceptions.ValidationException;
import hk.polyu.comp.project2411.bms.model.AttendeeAccount;
import hk.polyu.comp.project2411.bms.model.Reserves;

public class AttendeeAccountDao {
    private SQLConnection sqlConnection;

    public AttendeeAccountDao(SQLConnection sqlConnection) {
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

    public boolean updateAttendeeRegistrationData(String email, Reserves registrationData) throws SQLException {
        String sql = "UPDATE Reserves SET SeatNo=?, RegTime=?, DrinkChoice=?, MealChoice=?, Remarks=? WHERE Email=? AND BanquetBIN=?";
        Object[] params = new Object[] {
            registrationData.getSeatNo(),
            registrationData.getRegTime(),
            registrationData.getDrinkChoice(),
            registrationData.getMealChoice(),
            registrationData.getRemarks(),
            email,
            registrationData.getBanquetBIN()
        };
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
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
}
