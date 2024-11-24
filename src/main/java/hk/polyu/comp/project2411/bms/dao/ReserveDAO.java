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

    public boolean updateAttendeeRegistrationData(Reserve registrationData) throws SQLException {
        String sql = "UPDATE Reserve SET SeatNo=?, RegTime=?, DrinkChoice=?, MealChoice=?, Remarks=? WHERE attendeeEmail=? AND BanquetBIN=?";
        Object[] params = new Object[] {
            registrationData.getSeatNo(),
            registrationData.getRegTime(),
            registrationData.getDrinkChoice(),
            registrationData.getMealChoice(),
            registrationData.getRemarks(),
            registrationData.getAttendeeEmail(),
            registrationData.getBanquetBIN()
        };
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }

    private boolean insertAttendeeRegistrationData(Reserve registrationData) throws SQLException {
        String sql = "INSERT INTO Reserve (AttendeeEmail, BanquetBIN, SeatNo, RegTime, DrinkChoice, MealChoice, Remarks)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] params = new Object[] {
                registrationData.getAttendeeEmail(),
                registrationData.getBanquetBIN(),
                registrationData.getSeatNo(),
                registrationData.getRegTime(),
                registrationData.getDrinkChoice(),
                registrationData.getMealChoice(),
                registrationData.getRemarks()
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

    private int getRegisteredNumberForBanquet(int bin) throws SQLException {
        /*Count number of people reserved for the banquet.*/
        String sql = "SELECT COUNT(*) FROM Reserve WHERE BanquetBIN=?";
        Object[] param = {bin};
        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, param);
        if (!results.isEmpty()) {
            return ((Number) results.get(0).get("COUNT")).intValue();
        }
        return 0;
    }

    public Banquet getBanquet(int BIN) throws SQLException {
        String sql = "SELECT * FROM Banquet WHERE BIN=?";
        Object[] params = new Object[] { BIN };
        List<Map<String, Object>> result = sqlConnection.executeQuery(sql);
        if(result.get(0) == null) return null;
        return new Banquet(result.get(0));
    }

    public int getAvailableSeatNo(Banquet banquet) throws SQLException {
        String sql = "SELECT SeatNo FROM Reserve WHERE BanquetBIN=? ORDER BY SeatNo ASC";
        Object[] params = new Object[] { banquet.getBIN() };
        List<Map<String, Object>> results = sqlConnection.executeQuery(sql);
        if(results.isEmpty()) return 1;
        int cur = 1;
        for(Map<String, Object> result : results) {
            if(cur == ((Number) result.get("SEATNO")).intValue()) cur ++;
            else break;
        }
        return cur;
    }

    public RegistrationResult registerForBanquet(Reserve registrationData) throws RegistrationException, SQLException {
        Banquet curBan = getBanquet(registrationData.getBanquetBIN());
        if(curBan == null) throw new RegistrationException("Banquet not found");
        if(getRegisteredNumberForBanquet(registrationData.getBanquetBIN()) >= curBan.getQuota())
            throw new RegistrationException("The quota of the banquet is not enough");
        int curSeatNo = getAvailableSeatNo(curBan);
        registrationData.setSeatNo(curSeatNo);
        if(insertAttendeeRegistrationData(registrationData)) {
            return new RegistrationResult(true, "You have successfully registered the banquet.");
        }
        else return new RegistrationResult(false, "You have not successfully registered the banquet.");
    }
}
