package hk.polyu.comp.project2411.bms.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.model.Banquet;
import hk.polyu.comp.project2411.bms.model.SearchCriteria;

public class BanquetDAO {
    private SQLConnection sqlConnection;
    private MealDAO mealDAO;

    public BanquetDAO(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
        this.mealDAO = new MealDAO(sqlConnection);
    }

    public boolean deleteBanquet(int BIN) throws SQLException {
        String sql = "DELETE FROM Banquet WHERE BIN=?";
        Object[] params = new Object[] { BIN };
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }

    private int getNextBIN() throws SQLException {
        String getMaxBinSql = "SELECT MAX(BIN) AS MaxBIN FROM Banquet";
        List<Map<String, Object>> result = sqlConnection.executeQuery(getMaxBinSql);
        int newBIN = 1;
        if (!result.isEmpty() && result.get(0).get("MAXBIN") != null) {
            newBIN = ((Number) result.get(0).get("MAXBIN")).intValue() + 1;
        }
        return newBIN;
    }

    private int insertBanquet(Banquet banquet) throws SQLException {
        String sql = "INSERT INTO Banquet (BIN, Name, DateTime, Address, Location, ContactFirstName, ContactLastName, Available, Quota) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] params = new Object[] { banquet.getBIN(), banquet.getName(), banquet.getDateTime(),
                banquet.getAddress(), banquet.getLocation(), banquet.getContactFirstName(),
                banquet.getContactLastName(), banquet.getAvailable(), banquet.getQuota() };
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected;
    }

    public Banquet createBanquet(Banquet banquet) throws SQLException {
        try {
            // Begin transaction
            sqlConnection.beginTransaction();

            int newBIN = getNextBIN();
            banquet.setBIN(newBIN);

            int rowsAffected = insertBanquet(banquet);
            if (rowsAffected <= 0) {
                // Insert failed; roll back transaction
                sqlConnection.rollbackTransaction();
                throw new SQLException("Failed to insert banquet.");
            }

            boolean mealsAdded = mealDAO.addMealsToBanquet(newBIN, banquet.getMeals());
            if (!mealsAdded) {
                // Adding meals failed; roll back transaction
                sqlConnection.rollbackTransaction();
                throw new SQLException("Failed to add meals to the banquet.");
            }

            // Commit transaction
            sqlConnection.commitTransaction();
            return banquet;
        } catch (SQLException e) {
            // Roll back transaction in case of any exception
            sqlConnection.rollbackTransaction();
            throw e;
        }
    }

    public Map<String, Integer> getAttendeeTypeCounts(int banquetBIN) throws SQLException {
        String sql = "SELECT a.Type AS AttendeeType, COUNT(*) AS Count " +
                "FROM Reserve r " +
                "JOIN Account a ON r.AttendeeEmail = a.Email " +
                "WHERE r.BanquetBIN = ? " +
                "GROUP BY a.Type";
        Object[] params = new Object[]{banquetBIN};
        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, params);

        Map<String, Integer> attendeeTypeCounts = new HashMap<>();
        for (Map<String, Object> row : results) {
            String attendeeType = (String) row.get("ATTENDEETYPE");
            int count = ((Number) row.get("COUNT")).intValue();
            attendeeTypeCounts.put(attendeeType, count);
        }
        return attendeeTypeCounts;
    }

    public Map<String, Integer> getOrganizationCounts(int banquetBIN) throws SQLException {
        String sql = "SELECT a.Organization AS Organization, COUNT(*) AS Count " +
                "FROM Reserve r " +
                "JOIN Account a ON r.AttendeeEmail = a.Email " +
                "WHERE r.BanquetBIN = ? " +
                "GROUP BY a.Organization";
        Object[] params = new Object[]{banquetBIN};
        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, params);

        Map<String, Integer> organizationCounts = new HashMap<>();
        for (Map<String, Object> row : results) {
            String organization = (String) row.get("ORGANIZATION");
            int count = ((Number) row.get("COUNT")).intValue();
            organizationCounts.put(organization, count);
        }
        return organizationCounts;
    }

    public Map<String, Integer> getOverallAttendeeTypeCounts() throws SQLException {
        String sql = "SELECT a.Type AS AttendeeType, COUNT(*) AS Count " +
                "FROM Reserve r " +
                "JOIN Account a ON r.AttendeeEmail = a.Email " +
                "GROUP BY a.Type";
        List<Map<String, Object>> results = sqlConnection.executeQuery(sql);

        Map<String, Integer> attendeeTypeCounts = new HashMap<>();
        for (Map<String, Object> row : results) {
            String attendeeType = (String) row.get("ATTENDEETYPE");
            int count = ((Number) row.get("COUNT")).intValue();
            attendeeTypeCounts.put(attendeeType, count);
        }
        return attendeeTypeCounts;
    }

    public Map<String, Integer> getOverallOrganizationCounts() throws SQLException {
        String sql = "SELECT a.Organization AS Organization, COUNT(*) AS Count " +
                "FROM Reserve r " +
                "JOIN Account a ON r.AttendeeEmail = a.Email " +
                "GROUP BY a.Organization";
        List<Map<String, Object>> results = sqlConnection.executeQuery(sql);

        Map<String, Integer> organizationCounts = new HashMap<>();
        for (Map<String, Object> row : results) {
            String organization = (String) row.get("ORGANIZATION");
            int count = ((Number) row.get("COUNT")).intValue();
            organizationCounts.put(organization, count);
        }
        return organizationCounts;
    }

    public boolean updateBanquet(Banquet banquet) throws SQLException {
        try {
            // Begin transaction
            sqlConnection.beginTransaction();

            String sql = "UPDATE Banquet SET Name=?, DateTime=?, Address=?, Location=?, ContactFirstName=?, ContactLastName=?, Available=?, Quota=? WHERE BIN=?";
            Object[] params = new Object[] { 
                banquet.getName(), 
                banquet.getDateTime(), 
                banquet.getAddress(),
                banquet.getLocation(), 
                banquet.getContactFirstName(), 
                banquet.getContactLastName(),
                banquet.getAvailable(), 
                banquet.getQuota(), 
                banquet.getBIN() 
            };

            int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
            if (rowsAffected <= 0) {
                sqlConnection.rollbackTransaction();
                return false;
            }

            boolean mealsUpdated = mealDAO.updateMealsForBanquet(banquet.getBIN(), banquet.getMeals());
            if (!mealsUpdated) {
                sqlConnection.rollbackTransaction();
                return false;
            }

            // Commit transaction
            sqlConnection.commitTransaction();
            return true;
        } catch (SQLException e) {
            // Roll back transaction in case of any exception
            sqlConnection.rollbackTransaction();
            throw e;
        }
    }

    public List<Banquet> getAllBanquets() throws SQLException {
        String sql = "SELECT * FROM Banquet";
        List<Map<String, Object>> result = sqlConnection.executeQuery(sql);
        List<Banquet> banquets = new ArrayList<>();
        if (!result.isEmpty()) {
            for (Map<String, Object> row : result) {
                Banquet banquet = new Banquet(row);
                banquet.setMeals(mealDAO.getMealsForBanquet(banquet.getBIN()));
                banquets.add(banquet);
            }
        } else
            return null;
        return banquets;
    }

    // Deprecated
    public List<Banquet> getAvailableBanquets() throws SQLException {
        String sql = "SELECT * FROM Banquet WHERE Available = 'Y'";
        List<Map<String, Object>> result = sqlConnection.executeQuery(sql);
        List<Banquet> banquets = new ArrayList<>();
        if (!result.isEmpty()) {
            for (Map<String, Object> row : result) {
                Banquet banquet = new Banquet(row);
                banquet.setMeals(mealDAO.getMealsForBanquet(banquet.getBIN()));
                banquets.add(banquet);
            }
        } else
            return null;
        return banquets;
    }

    public List<Map<String, Object>> getAllBanquetsWithReservationStats() throws SQLException {
        String sql = "SELECT b.BIN, b.Name, b.DateTime, b.Location, b.Quota, " +
                "(SELECT COUNT(*) FROM Reserve r WHERE r.BanquetBIN = b.BIN) AS SeatsReserved " +
                "FROM Banquet b";
        List<Map<String, Object>> results = sqlConnection.executeQuery(sql);
        return results;
    }

    // Method to get attendee details for a banquet
    public List<Map<String, Object>> getAttendees(int banquetBIN) throws SQLException {
        String sql = "SELECT r.SeatNo, r.AttendeeEmail, a.FirstName, a.LastName, r.MealChoice, r.DrinkChoice, r.Remarks " +
                "FROM Reserve r " +
                "JOIN Account a ON r.AttendeeEmail = a.Email " +
                "WHERE r.BanquetBIN = ? " +
                "ORDER BY r.SeatNo";
        Object[] params = new Object[]{banquetBIN};
        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, params);
        return results;
    }

    public Map<String, Integer> getMealChoiceCounts(int banquetBIN) throws SQLException {
        String sql = "SELECT MealChoice, COUNT(*) as Count FROM Reserve WHERE BanquetBIN = ? GROUP BY MealChoice";
        Object[] params = new Object[]{banquetBIN};
        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, params);

        Map<String, Integer> mealChoiceCounts = new HashMap<>();
        for (Map<String, Object> row : results) {
            String mealChoice = (String) row.get("MEALCHOICE");
            int count = ((Number) row.get("COUNT")).intValue();
            mealChoiceCounts.put(mealChoice, count);
        }
        return mealChoiceCounts;
    }

    // Method to get drink choice counts for a specific banquet
    public Map<String, Integer> getDrinkChoiceCounts(int banquetBIN) throws SQLException {
        String sql = "SELECT DrinkChoice, COUNT(*) as Count FROM Reserve WHERE BanquetBIN = ? GROUP BY DrinkChoice";
        Object[] params = new Object[]{banquetBIN};
        List<Map<String, Object>> results = sqlConnection.executePreparedQuery(sql, params);

        Map<String, Integer> drinkChoiceCounts = new HashMap<>();
        for (Map<String, Object> row : results) {
            String drinkChoice = (String) row.get("DRINKCHOICE");
            int count = ((Number) row.get("COUNT")).intValue();
            drinkChoiceCounts.put(drinkChoice, count);
        }
        return drinkChoiceCounts;
    }

    public Map<String, Integer> getOverallPopularDrinks() throws SQLException {
        String sql = "SELECT DrinkChoice, COUNT(*) AS SelectionCount " +
                "FROM Reserve " +
                "GROUP BY DrinkChoice " +
                "ORDER BY SelectionCount DESC";
        List<Map<String, Object>> results = sqlConnection.executeQuery(sql);

        Map<String, Integer> drinkChoiceCounts = new LinkedHashMap<>();
        for (Map<String, Object> row : results) {
            String drinkChoice = (String) row.get("DRINKCHOICE");
            int count = ((Number) row.get("SELECTIONCOUNT")).intValue();
            drinkChoiceCounts.put(drinkChoice, count);
        }
        return drinkChoiceCounts;
    }

    public List<Banquet> getAvailableUnregisteredBanquets(String attendeeEmail) throws SQLException {
        String sql = "SELECT * FROM Banquet b WHERE b.Available = 'Y' " +
                    "AND NOT EXISTS (SELECT 1 FROM Reserve r " +
                    "WHERE r.BanquetBIN = b.BIN " +
                    "AND r.AttendeeEmail = ?)";
                    
        Object[] params = new Object[] { attendeeEmail };
        List<Map<String, Object>> result = sqlConnection.executePreparedQuery(sql, params);
        List<Banquet> banquets = new ArrayList<>();
        
        if (!result.isEmpty()) {
            for (Map<String, Object> row : result) {
                Banquet banquet = new Banquet(row);
                banquet.setMeals(mealDAO.getMealsForBanquet(banquet.getBIN()));
                banquets.add(banquet);
            }
            return banquets;
        } else {
            return null;
        }
    }

    public Banquet getBanquetByBIN(int BIN) throws SQLException {
        String sql = "SELECT * FROM Banquet WHERE BIN = ?";
        Object[] params = new Object[] { BIN };
        List<Map<String, Object>> result = sqlConnection.executePreparedQuery(sql, params);
        
        if (!result.isEmpty()) {
            Banquet banquet = new Banquet(result.get(0));
            banquet.setMeals(mealDAO.getMealsForBanquet(banquet.getBIN()));
            return banquet;
        }
        return null;
    }


    // Method to get registration counts per day
    public Map<String, Integer> getRegistrationTrends() throws SQLException {
        String sql = "SELECT TO_CHAR(RegTime, 'YYYY-MM-DD') AS RegistrationDate, COUNT(*) AS RegistrationCount " +
                "FROM Reserve " +
                "GROUP BY TO_CHAR(RegTime, 'YYYY-MM-DD') " +
                "ORDER BY RegistrationDate";
        List<Map<String, Object>> results = sqlConnection.executeQuery(sql);

        Map<String, Integer> registrationTrends = new LinkedHashMap<>();
        for (Map<String, Object> row : results) {
            String date = (String) row.get("REGISTRATIONDATE");
            int count = ((Number) row.get("REGISTRATIONCOUNT")).intValue();
            registrationTrends.put(date, count);
        }
        return registrationTrends;
    }

    // Method to get registration counts per hour
    public Map<Integer, Integer> getPeakRegistrationTimes() throws SQLException {
        String sql = "SELECT EXTRACT(HOUR FROM RegTime) AS RegistrationHour, COUNT(*) AS RegistrationCount " +
                "FROM Reserve " +
                "GROUP BY EXTRACT(HOUR FROM RegTime) " +
                "ORDER BY RegistrationHour";
        List<Map<String, Object>> results = sqlConnection.executeQuery(sql);

        Map<Integer, Integer> peakTimes = new LinkedHashMap<>();
        for (Map<String, Object> row : results) {
            int hour = ((Number) row.get("REGISTRATIONHOUR")).intValue();
            int count = ((Number) row.get("REGISTRATIONCOUNT")).intValue();
            peakTimes.put(hour, count);
        }
        return peakTimes;
    }

    public Map<String, Integer> getOverallPopularMeals() throws SQLException {
        String sql = "SELECT MealChoice, COUNT(*) AS SelectionCount " +
                "FROM Reserve " +
                "GROUP BY MealChoice " +
                "ORDER BY SelectionCount DESC";
        List<Map<String, Object>> results = sqlConnection.executeQuery(sql);

        Map<String, Integer> mealChoiceCounts = new LinkedHashMap<>();
        for (Map<String, Object> row : results) {
            String mealChoice = (String) row.get("MEALCHOICE");
            int count = ((Number) row.get("SELECTIONCOUNT")).intValue();
            mealChoiceCounts.put(mealChoice, count);
        }
        return mealChoiceCounts;
    }

    public List<Banquet> searchAvailableUnregisteredBanquets(String attendeeEmail, SearchCriteria criteria) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM Banquet b WHERE b.Available = 'Y' " +
            "AND NOT EXISTS (SELECT 1 FROM Reserve r " +
            "WHERE r.BanquetBIN = b.BIN " +
            "AND r.AttendeeEmail = ?)");
        
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

        List<Map<String, Object>> result = sqlConnection.executePreparedQuery(sql.toString(), params.toArray());
        List<Banquet> banquets = new ArrayList<>();
        
        if (!result.isEmpty()) {
            for (Map<String, Object> row : result) {
                Banquet banquet = new Banquet(row);
                banquet.setMeals(mealDAO.getMealsForBanquet(banquet.getBIN()));
                banquets.add(banquet);
            }
            return banquets;
        }
        return null;
    }
}