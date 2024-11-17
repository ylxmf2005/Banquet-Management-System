package main.service;

import java.sql.*;
import java.util.*;
import main.connection.SQLConnection;
import main.exceptions.RegistrationException;
import main.exceptions.ValidationException;
import main.model.AttendeeAccount;
import main.model.Banquet;
import main.model.Meal;
import main.model.RegistrationResult;
import main.model.ReportData;
import main.model.Reserves;
import main.model.SearchCriteria;

// Implementing the BMSMainInterface interface
public class BMSMain implements BMSMainInterface {

    private SQLConnection sqlConnection;

    public BMSMain() {
        this.sqlConnection = new SQLConnection();
    }

    
    // Close the SQLConnection when done
    public void close() {
        sqlConnection.closeConnection();
    }

    // Administrator Functions
    @Override
    public Banquet createBanquet(Banquet banquet) throws SQLException {
        // Generate a new BIN
        String getMaxBinSql = "SELECT MAX(BIN) AS MaxBIN FROM Banquet";
        List<Map<String, Object>> result = sqlConnection.executeQuery(getMaxBinSql);
        int newBin = 1;
        if (!result.isEmpty() && result.get(0).get("MaxBIN") != null) {
            newBin = ((Number) result.get(0).get("MaxBIN")).intValue() + 1;
        }
        banquet.setBIN(newBin);

        // Insert the new banquet into the database
        String sql = "INSERT INTO Banquet (BIN, Name, Date, Time, Address, Location, FirstName, LastName, Available, Quota) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] params = new Object[] {
            banquet.getBIN(),
            banquet.getName(),
            banquet.getDate(),
            banquet.getTime(),
            banquet.getAddress(),
            banquet.getLocation(),
            banquet.getContactFirstName(),
            banquet.getContactLastName(),
            banquet.getAvailable(),
            banquet.getQuota()
        };

        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);

        if (rowsAffected > 0) {
            return banquet;
        } else {
            throw new SQLException("Failed to create the banquet.");
        }
    }
}