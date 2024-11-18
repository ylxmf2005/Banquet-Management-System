package com.project2411.bms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.project2411.bms.connection.SQLConnection;
import com.project2411.bms.model.Banquet;


// Implementing the BMSMainInterface interface
// Temporaily don't implement the interface until we implement all the methods
public class BMSMain {
    private SQLConnection sqlConnection;

    public BMSMain() {
        this.sqlConnection = new SQLConnection();
        // Create the tables if not exists
        
        
    }

    // Close the SQLConnection when done
    public void close() {
        sqlConnection.closeConnection();
    }

    // Administrator Functions
    public Banquet createBanquet(Banquet banquet) throws SQLException {
        // Generate a new BIN
        String getMaxBinSql = "SELECT MAX(BIN) AS MaxBIN FROM Banquet";
        List<Map<String, Object>> result = sqlConnection.executeQuery(getMaxBinSql);
        int newBIN = 1;
        if (!result.isEmpty() && result.get(0).get("MaxBIN") != null) {
            newBIN = ((Number) result.get(0).get("MaxBIN")).intValue() + 1;
        }
        banquet.setBIN(newBIN);

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