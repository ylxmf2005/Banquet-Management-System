package hk.polyu.comp.project2411.bms.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.model.Banquet;
import hk.polyu.comp.project2411.bms.model.Meal;

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

    public boolean updateBanquet(Banquet banquet) throws SQLException {
        try {
            // Begin transaction
            sqlConnection.beginTransaction();

            String sql = "UPDATE Banquet SET Name=?, DateTime=?, Address=?, Location=?, ContactFirstName=?, ContactLastName=?, Available=?, Quota=? WHERE BIN=?";
            Object[] params = new Object[] { banquet.getName(), banquet.getDateTime(), banquet.getAddress(),
                    banquet.getLocation(), banquet.getContactFirstName(), banquet.getContactLastName(),
                    banquet.getAvailable(), banquet.getQuota(), banquet.getBIN() };

            int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
            if (rowsAffected <= 0) {
                // Update failed; roll back transaction
                sqlConnection.rollbackTransaction();
                return false;
            }

            // Delete existing meals for the banquet
            List<Meal> existingMeals = mealDAO.getMealsForBanquet(banquet.getBIN());
            boolean mealsDeleted = mealDAO.deleteMealsFromBanquet(banquet.getBIN(), existingMeals);
            if (!mealsDeleted) {
                // Deleting meals failed; roll back transaction
                sqlConnection.rollbackTransaction();
                return false;
            }

            // Add new meals for the banquet
            boolean mealsAdded = mealDAO.addMealsToBanquet(banquet.getBIN(), banquet.getMeals());
            if (!mealsAdded) {
                // Adding meals failed; roll back transaction
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
}