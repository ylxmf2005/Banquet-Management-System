package hk.polyu.comp.project2411.bms.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.exceptions.RegistrationException;
import hk.polyu.comp.project2411.bms.model.Banquet;
import hk.polyu.comp.project2411.bms.model.RegistrationResult;
import hk.polyu.comp.project2411.bms.model.Reserve;


public class ReserveDAO {
    private SQLConnection sqlConnection;

    public ReserveDAO(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public boolean updateAttendeeRegistrationData(String email, Reserve registrationData) throws SQLException {
        String sql = "UPDATE Reserve SET SeatNo=?, RegTime=?, DrinkChoice=?, MealChoice=?, Remarks=? WHERE attendeeEmail=? AND BanquetBIN=?";
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

    public List<Reserve> getReservesByAttendeeEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Reserve WHERE AttendeeEmail=?";
        Object[] param = {email};
        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, param);
        List<Reserve> reserves = new ArrayList<>();
        for (Map<String, Object> result : results) 
            reserves.add(new Reserve(result));
        return reserves;
    }

    public boolean deleteReserve(String attendeeEmail, int banquetBIN) throws SQLException {
        String sql = "DELETE FROM Reserve WHERE AttendeeEmail=? AND BanquetBIN=?";
        Object[] params = {attendeeEmail, banquetBIN};
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }

    public boolean updateRegistration(String attendeeEmail, int banquetBIN, String newDrinkChoice, String newMealChoice, String newRemarks) throws SQLException{
        String sql = "UPDATE Reserve SET banquetBIN=?, newDrinkChoice=?, newMealChoice=?, newRemarks=? WHERE attendeeEmail=?";
        Object[] params = new Object[] {
                banquetBIN,
                newDrinkChoice,
                newMealChoice,
                newRemarks,
                attendeeEmail
        };
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }

    private int getRegisteredNumberForBanquet(Banquet banquet) throws SQLException {
        /*Count number of people reserved for the banquet.*/
        String sql = "SELECT COUNT(*) FROM Reserve WHERE BanquetBIN=?";
        Object[] param = {banquet.getBIN()};
        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, param);
        if (!results.isEmpty()) {
            return ((Number) results.get(0).get("Count")).intValue();
        }
        return 0;
    }

    public RegistrationResult registerForBanquet(String attendeeEmail, int banquetBIN, int seatNo, String drinkChoice, String mealChoice, String remarks) throws RegistrationException, SQLException {
        String sql = "INSERT INTO Reserve (AttendeeEmail, BanquetBIN, SeatNo, DrinkChoice, MealChoice, Remarks) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Object[] params = new Object[] {
                attendeeEmail,
                banquetBIN,
                seatNo,
                drinkChoice,
                mealChoice,
                remarks
        };
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        if (rowsAffected > 0) {
            return new RegistrationResult(true, "You have successfully registered the banquet.");
        }
        else return new RegistrationResult(false, "You have not successfully registered the banquet.");
    }
}
