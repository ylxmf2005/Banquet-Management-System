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
                " VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?)";
        Object[] params = new Object[] {
                registrationData.getAttendeeEmail(),
                registrationData.getBanquetBIN(),
                registrationData.getSeatNo(),
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
        String sql = "SELECT COUNT(*) AS cnt FROM Reserve WHERE BanquetBIN=?";
        Object[] param = {bin};
        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, param);
        if (!results.isEmpty()) {
            return ((Number) results.get(0).get("CNT")).intValue();
        }
        return 0;
    }

    public Banquet getBanquet(int BIN) throws SQLException {
        String sql = "SELECT * FROM Banquet WHERE BIN=?";
        Object[] params = new Object[] { BIN };
        List<Map<String, Object>> result = sqlConnection.executePreparedQuery(sql, params);
        if(result.isEmpty()) return null;
        return new Banquet(result.get(0));
    }

    public int getAvailableSeatNo(Banquet banquet) throws SQLException {
        // Lock and get all seat numbers for this banquet
        String sql = "SELECT SeatNo FROM Reserve WHERE BanquetBIN=? ORDER BY SeatNo ASC FOR UPDATE";
        Object[] params = new Object[] { banquet.getBIN() };
        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, params);
        
        if(results.isEmpty()) return 1;
        
        // Find the first available seat number
        int cur = 1;
        for(Map<String, Object> result : results) {
            int seatNo = ((Number) result.get("SEATNO")).intValue();
            if(cur < seatNo) {
                break;
            }
            cur = seatNo + 1;
        }
        return cur;
    }

    public RegistrationResult registerForBanquet(Reserve registrationData) throws RegistrationException, SQLException 
        {
        try {
            sqlConnection.beginTransaction();
            
            String lockSql = "SELECT * FROM Banquet WHERE BIN=?";
            Object[] lockParams = new Object[]{ registrationData.getBanquetBIN() };
            List<Map<String, Object>> result = sqlConnection.executePreparedQuery(lockSql, lockParams);
            
            if(result.isEmpty()) {
                throw new RegistrationException("Banquet not found");
            }
            
            Banquet curBan = new Banquet(result.get(0));
            
            String countSql = "SELECT COUNT(*) AS cnt FROM Reserve WHERE BanquetBIN=?";
            Object[] countParams = {registrationData.getBanquetBIN()};
            List<Map<String, Object>> countResult = sqlConnection.executePreparedQuery(countSql, countParams);
            int currentRegistrations = ((Number) countResult.get(0).get("CNT")).intValue();
            
            if(currentRegistrations >= curBan.getQuota()) {
                throw new RegistrationException("The quota of the banquet is not enough");
            }
            
            String checkDupSql = "SELECT COUNT(*) AS cnt FROM Reserve WHERE BanquetBIN=? AND AttendeeEmail=?";
            Object[] checkDupParams = {registrationData.getBanquetBIN(), registrationData.getAttendeeEmail()};
            List<Map<String, Object>> dupResult = sqlConnection.executePreparedQuery(checkDupSql, checkDupParams);
            if (((Number) dupResult.get(0).get("CNT")).intValue() > 0) {
                throw new RegistrationException("You have already registered for this banquet");
            }
            
            String checkSeatSql = "SELECT COUNT(*) AS cnt FROM Reserve WHERE BanquetBIN=? AND SeatNo=? FOR UPDATE";
            while (true) {
                int curSeatNo = getAvailableSeatNo(curBan);
                registrationData.setSeatNo(curSeatNo);
                
                Object[] checkSeatParams = {registrationData.getBanquetBIN(), curSeatNo};
                List<Map<String, Object>> seatResult = sqlConnection.executePreparedQuery(checkSeatSql, checkSeatParams);
                if (((Number) seatResult.get(0).get("CNT")).intValue() == 0) {
                    break;
                }
            }
            
            boolean success = insertAttendeeRegistrationData(registrationData);
            
            if(success) {
                sqlConnection.commitTransaction();
                return new RegistrationResult(true, "You have successfully registered the banquet.");
            } else {
                sqlConnection.rollbackTransaction();
                return new RegistrationResult(false, "Registration failed due to system error.");
            }
            
        } catch (SQLException e) {
            sqlConnection.rollbackTransaction();
            throw e;
        } catch (RegistrationException e) {
            sqlConnection.rollbackTransaction();
            throw e;
        }
    }

    public List<Reserve> getReservationsByBIN(int banquetBIN) throws SQLException {
        String sql = "SELECT * FROM Reserve WHERE BanquetBIN=? ORDER BY SeatNo ASC";
        Object[] params = {banquetBIN};
        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, params);
        List<Reserve> reserves = new ArrayList<>();
        for (Map<String, Object> result : results) {
            reserves.add(new Reserve(result));
        }
        return reserves;
    }

}
