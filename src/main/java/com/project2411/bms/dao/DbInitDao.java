package com.project2411.bms.dao;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.project2411.bms.connection.SQLConnection;
// TODO: Add check constraints for data entries
public class DbInitDao {
    private SQLConnection sqlConnection;

    public DbInitDao() {
        this.sqlConnection = new SQLConnection();
    }

    public DbInitDao(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public void initDb() {
        try {
            if (!tableExists("AttendeeAccount"))createAttendeeAccountTable();
            if (!tableExists("Banquet")) createBanquetTable();
            if (!tableExists("Meal")) createMealTable();
            if (!tableExists("Reserves"))createReservesTable();

            System.out.println("Database initialization completed.");
        } catch (SQLException e) {
            System.err.println("An SQL exception occurred during database initialization.");
            e.printStackTrace();
        }
    }
    private boolean tableExists(String tableName) throws SQLException {
        String sql = "SELECT table_name FROM user_tables WHERE table_name = ?";
        Object[] params = new Object[]{tableName.toUpperCase()};
        List<Map<String, Object>> result = sqlConnection.executePreparedQuery(sql, params);
        return !result.isEmpty();
    }

    /**
     * Creates the AttendeeAccount table.
     *
     * @throws SQLException If a database access error occurs.
     */
    private void createAttendeeAccountTable() throws SQLException {
        String sql = "CREATE TABLE AttendeeAccount (" +
                "Email VARCHAR2(255) PRIMARY KEY, " +
                "FirstName VARCHAR2(255), " +
                "LastName VARCHAR2(255), " +
                "MobileNo VARCHAR2(20), " +
                "Password VARCHAR2(255), " +
                "Location VARCHAR2(255), " +
                "Address VARCHAR2(255), " +
                "Type VARCHAR2(50), " +
                "Organization VARCHAR2(255)" +
                ")";
        sqlConnection.executeUpdate(sql);
        System.out.println("Table AttendeeAccount created.");
    }

    /**
     * Creates the Banquet table.
     *
     * @throws SQLException If a database access error occurs.
     */
    private void createBanquetTable() throws SQLException {
        String sql = "CREATE TABLE Banquet (" +
                "BIN NUMBER PRIMARY KEY, " +
                "Name VARCHAR2(255), " +
                "Date VARCHAR2(50), " +
                "Time VARCHAR2(50), " +
                "Address VARCHAR2(255), " +
                "Location VARCHAR2(255), " +
                "FirstName VARCHAR2(255), " +
                "LastName VARCHAR2(255), " +
                "Available VARCHAR2(1), " +
                "Quota NUMBER" +
                ")";
        sqlConnection.executeUpdate(sql);

        // Optionally create a sequence and trigger for auto-incrementing BIN
        String createSequence = "CREATE SEQUENCE banquet_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE";
        String createTrigger = "CREATE OR REPLACE TRIGGER banquet_trigger BEFORE INSERT ON Banquet " +
                "FOR EACH ROW WHEN (new.BIN IS NULL) " +
                "BEGIN " +
                "SELECT banquet_seq.NEXTVAL INTO :new.BIN FROM dual; " +
                "END;";

        sqlConnection.executeUpdate(createSequence);
        sqlConnection.executeUpdate(createTrigger);

        System.out.println("Table Banquet created with sequence and trigger for BIN.");
    }

    /**
     * Creates the Meal table.
     *
     * @throws SQLException If a database access error occurs.
     */
    private void createMealTable() throws SQLException {
        String sql = "CREATE TABLE Meal (" +
                "BanquetBIN NUMBER, " +
                "DishName VARCHAR2(255), " +
                "Type VARCHAR2(50), " +
                "Price NUMBER(10, 2), " +
                "SpecialCuisine VARCHAR2(255), " +
                "PRIMARY KEY (BanquetBIN, DishName), " +
                "FOREIGN KEY (BanquetBIN) REFERENCES Banquet(BIN)" +
                ")";
        sqlConnection.executeUpdate(sql);
        System.out.println("Table Meal created.");
    }

    /**
     * Creates the Reserves table.
     *
     * @throws SQLException If a database access error occurs.
     */
    private void createReservesTable() throws SQLException {
        String sql = "CREATE TABLE Reserves (" +
                "AttendeeEmail VARCHAR2(255), " +
                "BanquetBIN NUMBER, " +
                "SeatNo NUMBER, " +
                "RegTime TIMESTAMP, " +
                "DrinkChoice VARCHAR2(50), " +
                "MealChoice VARCHAR2(255), " +
                "Remarks VARCHAR2(255), " +
                "PRIMARY KEY (AttendeeEmail, BanquetBIN), " +
                "FOREIGN KEY (AttendeeEmail) REFERENCES AttendeeAccount(Email), " +
                "FOREIGN KEY (BanquetBIN) REFERENCES Banquet(BIN)" +
                ")";
        sqlConnection.executeUpdate(sql);
        System.out.println("Table Reserves created.");
    }

    /**
     * Closes the SQL connection.
     */
    public void close() {
        sqlConnection.closeConnection();
    }

    // Main method for testing
    public static void main(String[] args) {
        DbInitDao dbInitDao = new DbInitDao();
        dbInitDao.initDb();
        dbInitDao.close();
    }
}