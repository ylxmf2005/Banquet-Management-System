package main.java.com.project2411.bms.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import main.java.com.project2411.bms.connection.SQLConnection;
import main.java.com.project2411.bms.model.Banquet;


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
    boolean updateBanquet(Banquet banquet) throws SQLException {
        //Validate the banquet
        if (banquet == null || banquet.getBIN() == 0) {
            throw new IllegalArgumentException("Banquet object or BIN must not be null or zero.");
        }
        //Build SQL query while checking if banquet has valid fields.
        StringBuilder sql = new StringBuilder("UPDATE Banquet SET ");
        List<Object> params = new ArrayList<>();

        if (banquet.getName() != null) {
            sql.append("NAME = ?, ");
            params.add(banquet.getName());
        }
        if (banquet.getDate() != null) {
            sql.append("DATE = ?, ");
            params.add(banquet.getDate());
        }
        if (banquet.getTime() != null) {
            sql.append("TIME = ?, ");
            params.add(banquet.getTime());
        }
        if (banquet.getAddress() != null) {
            sql.append("ADDRESS = ?, ");
            params.add(banquet.getAddress());
        }
        if (banquet.getLocation() != null) {
            sql.append("LOCATION = ?, ");
            params.add(banquet.getLocation());
        }
        if (banquet.getContactFirstName() != null) {
            sql.append("FIRST_NAME = ?, ");
            params.add(banquet.getContactFirstName());
        }
        if (banquet.getContactLastName() != null) {
            sql.append("LAST_NAME = ?, ");
            params.add(banquet.getContactLastName());
        }
        if (banquet.getAvailable() != null) {
            sql.append("AVAILABLE = ?, ");
            params.add(banquet.getAvailable());
        }
        if (banquet.getQuota() != 0) {
            sql.append("QUOTA = ?");
            params.add(banquet.getQuota());
        }

        sql.setLength(sql.length()-2); //removing the last ", "
        sql.append(" WHERE BIN = ?");
        params.add(banquet.getBIN());

        int rowsUpdated = sqlConnection.executePreparedUpdate(sql.toString(), params.toArray());
        return rowsUpdated > 0;
    }
    
}
