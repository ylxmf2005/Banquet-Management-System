package hk.polyu.comp.project2411.bms.dao;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;

public class DbInitDAO {
    private SQLConnection sqlConnection;

    public DbInitDAO() throws SQLException {
        this.sqlConnection = new SQLConnection();
    }

    public DbInitDAO(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public boolean initDb() {
        return initDb(false, true);
    }

    public boolean initDb(boolean clearIfExists) {
        return initDb(clearIfExists, true);
    }
    
    public boolean initDb(boolean clearIfExists, boolean createSampleData) {
        try {
            // Drop existing tables if clearIfExists is true
            if (clearIfExists) {
                if (tableExists("Banquet")) dropTable("Banquet");
                if (tableExists("Meal")) dropTable("Meal");
                if (tableExists("Account")) dropTable("Account");
                if (tableExists("Reserve")) dropTable("Reserve");
            }
    
            // Create tables if they do not exist
            if (!tableExists("Banquet")) {
                createBanquetTable();
            }
            if (!tableExists("Meal")) {
                createMealTable();
            }
            if (!tableExists("Account")) {
                createAccountTable();
                createDefaultAdminAccount();
            }
            if (!tableExists("Reserve")) {
                createReserveTable();
            }

            if (createSampleData) {
                SampleDataDAO sampleDataDAO = new SampleDataDAO(sqlConnection);
                sampleDataDAO.createTestBanquet();
                sampleDataDAO.createTestMealsForBanquet();
                sampleDataDAO.createTestAttendeeAccount();
                sampleDataDAO.createTestReserves();
            }

            createTrigger();

            System.out.println("Database initialization completed.");
            return true;
        } catch (SQLException e) {
            System.err.println("An SQL exception occurred during database initialization.");
            e.printStackTrace();
            return false;
        }
    }

    private void dropTable(String tableName) throws SQLException {
        String sql = "DROP TABLE " + tableName + " CASCADE CONSTRAINTS";
        sqlConnection.executeUpdate(sql);
        System.out.println("Table " + tableName + " dropped.");
    }

    private boolean tableExists(String tableName) throws SQLException {
        String sql = "SELECT table_name FROM user_tables WHERE table_name = ?";
        Object[] params = new Object[]{tableName.toUpperCase()};
        List<Map<String, Object>> result = sqlConnection.executePreparedQuery(sql, params);
        return !result.isEmpty();
    }

    /**
     * Creates the Account table.
     *
     * @throws SQLException If a database access error occurs.
     */
    private void createAccountTable() throws SQLException {
        String sql = "CREATE TABLE Account (" +
                "Email VARCHAR2(255) NOT NULL, " +
                "Role VARCHAR2(50) NOT NULL, " +
                "FirstName VARCHAR2(255) NOT NULL, " +
                "LastName VARCHAR2(255) NOT NULL, " +
                "MobileNo VARCHAR2(20) NOT NULL, " +
                "Password VARCHAR2(255) NOT NULL, " +
                "Address VARCHAR2(255) NOT NULL, " +
                "Type VARCHAR2(50) NOT NULL, " +
                "Organization VARCHAR2(255) NOT NULL, " +
                "PRIMARY KEY (Email), " +

                "CONSTRAINT chk_email_format CHECK (Email LIKE '%@%.%'), " +
                "CONSTRAINT chk_role CHECK (Role IN ('admin', 'user')), " +
                "CONSTRAINT chk_mobile_format CHECK (REGEXP_LIKE(MobileNo, '^[0-9]{8}$')), " +
                "CONSTRAINT chk_firstname CHECK (REGEXP_LIKE(FirstName, '^[a-zA-Z]+$')), " +
                "CONSTRAINT chk_lastname CHECK (REGEXP_LIKE(LastName, '^[a-zA-Z]+$'))" +
                ")";
        sqlConnection.executeUpdate(sql);
        System.out.println("Table Account created.");
    }
    
    /**
     * Creates the default admin account.
     *
     * @throws SQLException If a database access error occurs.
     */

    private void createDefaultAdminAccount() throws SQLException {
        String passwd = "2411project";
        passwd = Utils.encoding(passwd);
        String sql = "INSERT INTO Account (Email, Role, FirstName, LastName, MobileNo, Password, Address, Type, Organization) " +
                "VALUES ('bmsadmin@polyu.hk', 'admin', 'Admin', 'User', '88888888', '" + 
                passwd + "', 'PolyU', 'Staff', 'PolyU')";
        sqlConnection.executeUpdate(sql);
        System.out.println("Default admin account created.");
    }

    /**
     * Creates the Banquet table.
     *
     * @throws SQLException If a database access error occurs.
     */
    private void createBanquetTable() throws SQLException {
        String sql = "CREATE TABLE Banquet (" +
                "BIN NUMBER NOT NULL, " +
                "Name VARCHAR2(255) NOT NULL, " +
                "DateTime DATE NOT NULL, " +
                "Address VARCHAR2(255) NOT NULL, " +
                "Location VARCHAR2(255) NOT NULL, " +
                "ContactFirstName VARCHAR2(255) NOT NULL, " +
                "ContactLastName VARCHAR2(255) NOT NULL, " +
                "Available VARCHAR2(1) NOT NULL, " +
                "Quota NUMBER NOT NULL, " +
                "PRIMARY KEY(BIN), " +

                "CONSTRAINT chk_banquet_available CHECK (Available IN ('Y', 'N')), " +
                "CONSTRAINT chk_banquet_quota CHECK (Quota >= 0), " +
                "CONSTRAINT chk_contact_firstname CHECK (REGEXP_LIKE(ContactFirstName, '^[a-zA-Z]+$')), " +
                "CONSTRAINT chk_contact_lastname CHECK (REGEXP_LIKE(ContactLastName, '^[a-zA-Z]+$'))" +
                ")";
        sqlConnection.executeUpdate(sql);
        System.out.println("Table Banquet created.");
    }

    /**
     * Creates the Meal table.
     *
     * @throws SQLException If a database access error occurs.
     */
    private void createMealTable() throws SQLException {
        String sql = "CREATE TABLE Meal (" +
                "BanquetBIN NUMBER NOT NULL, " +
                "DishName VARCHAR2(255) NOT NULL, " +
                "Type VARCHAR2(50) NOT NULL, " +
                "Price NUMBER(10, 2) NOT NULL, " +
                "SpecialCuisine VARCHAR2(255) NOT NULL, " +
                "PRIMARY KEY (BanquetBIN, DishName), " +
                "FOREIGN KEY (BanquetBIN) REFERENCES Banquet(BIN) ON DELETE CASCADE, " +
                "CONSTRAINT chk_meal_price CHECK (Price >= 0)" +
                ")";
        sqlConnection.executeUpdate(sql);
        System.out.println("Table Meal created.");
    }


    /**
     * Creates the Reserve table.
     *
     * @throws SQLException If a database access error occurs.
     */
    private void createReserveTable() throws SQLException {
        String sql = "CREATE TABLE Reserve (" +
                "AttendeeEmail VARCHAR2(255) NOT NULL, " +
                "BanquetBIN NUMBER NOT NULL, " +
                "SeatNo NUMBER NOT NULL, " +
                "RegTime TIMESTAMP NOT NULL, " +
                "DrinkChoice VARCHAR2(50) NOT NULL, " +
                "MealChoice VARCHAR2(255) NOT NULL, " +
                "Remarks VARCHAR2(255), " +
                "PRIMARY KEY (AttendeeEmail, BanquetBIN), " +
                "FOREIGN KEY (AttendeeEmail) REFERENCES Account(Email) ON DELETE CASCADE, " +
                "FOREIGN KEY (BanquetBIN) REFERENCES Banquet(BIN) ON DELETE CASCADE, " +
                "FOREIGN KEY (BanquetBIN, MealChoice) REFERENCES Meal(BanquetBIN, DishName), " +
                "CONSTRAINT unique_seat_per_banquet UNIQUE (BanquetBIN, SeatNo), " +
                "CONSTRAINT chk_seat_no CHECK (SeatNo > 0)" +
                ")";
        sqlConnection.executeUpdate(sql);
        System.out.println("Table Reserve created.");
    }

    private void createTrigger() throws SQLException {
        String sql = "CREATE OR REPLACE TRIGGER email_update_cascade_trigger " +
                "AFTER UPDATE OF email ON Account " +
                "FOR EACH ROW " +
                "BEGIN " +
                "UPDATE Reserve " +
                "SET AttendeeEmail = :NEW.email " +
                "WHERE AttendeeEmail = :OLD.email; " +
                "END;";
        sqlConnection.executeUpdate(sql);
        sql = "CREATE OR REPLACE TRIGGER meal_update_cascade_trigger " +
                "AFTER UPDATE OF DishName ON Meal " +
                "FOR EACH ROW " +
                "BEGIN " +
                "UPDATE Reserve " +
                "SET MealChoice = :NEW.DishName " +
                "WHERE BanquetBIN = :OLD.BanquetBIN AND MealChoice = :OLD.DishName; " +
                "END;";
        sqlConnection.executeUpdate(sql);
        System.out.println("Trigger created");
    }

    /**
     * Closes the SQL connection.
     */
    public void close() throws SQLException {
        sqlConnection.closeConnection();
    }
}
