package hk.polyu.comp.project2411.bms.dao;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;

// TODO: Add check constraints for data entries
public class DbInitDAO {
    private SQLConnection sqlConnection;

    public DbInitDAO() throws SQLException {
        this.sqlConnection = new SQLConnection();
    }

    public DbInitDAO(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    //TODO: (Optional) Add methods to initialize specified tables
    public boolean initDb() {
        return initDb(false);
    }
    
    public boolean initDb(boolean clearIfExists) {
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
                createTestBanquet();
            }
            if (!tableExists("Meal")) {
                createMealTable();
                createTestMealsForBanquet();
            }
            if (!tableExists("Account")) {
                createAccountTable();
                createDefaultAdminAccount();
                createTestAttendeeAccount();
            }

            if (!tableExists("Reserve")) {
                createReserveTable();
                createTestReserve();
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
                "Email VARCHAR2(255), " +
                "Role VARCHAR2(50), " +
                "FirstName VARCHAR2(255), " +
                "LastName VARCHAR2(255), " +
                "MobileNo VARCHAR2(20), " +
                "Password VARCHAR2(255), " +
                "Location VARCHAR2(255), " +
                "Address VARCHAR2(255), " +
                "Type VARCHAR2(50), " +
                "Organization VARCHAR2(255)," +
                "PRIMARY KEY (Email)" +
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
        String sql = "INSERT INTO Account (Email, Role, Password) VALUES ('bmsadmin@polyu.hk', 'admin', '" +
                passwd + "')";
        sqlConnection.executeUpdate(sql);
        System.out.println("Default admin account created.");
    }

    private void createTestAttendeeAccount() throws SQLException {
        String passwd = "2411project";
        passwd = Utils.encoding(passwd);
        String sql = "INSERT INTO Account (Email, Role, FirstName, LastName, MobileNo, Password, Location, Address, Type, Organization) " +
                     "VALUES ('test@polyu.hk', 'user', 'San', 'Zhang', '114514', '" +
                passwd + "', 'PolyU HJ202', 'PolyU HJ202', 'Student', 'PolyU')";

        sqlConnection.executeUpdate(sql);
        System.out.println("Test attendee account created.");
    }

    /**
     * Creates the Banquet table.
     *
     * @throws SQLException If a database access error occurs.
     */
    private void createBanquetTable() throws SQLException {
        String sql = "CREATE TABLE Banquet (" +
                "BIN NUMBER, " +
                "Name VARCHAR2(255), " +
                "DateTime DATE , " +
                "Address VARCHAR2(255), " +
                "Location VARCHAR2(255), " +
                "ContactFirstName VARCHAR2(255), " +
                "ContactLastName VARCHAR2(255), " +
                "Available VARCHAR2(1), " +
                "Quota NUMBER," +
                "PRIMARY KEY(BIN)" +
                ")";
        sqlConnection.executeUpdate(sql);
        System.out.println("Table Banquet created.");
    }

    private void createTestBanquet() throws SQLException {
        String sql = "INSERT INTO Banquet (BIN, Name, DateTime, Address, Location, ContactFirstName, ContactLastName, Available, Quota) " +
                     "VALUES (1, 'Test Banquet', TO_DATE('2024-12-31 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), '15 Fat Kwong Street', 'Hall of HMT', 'San', 'Zhang', 'Y', 100)";
        sqlConnection.executeUpdate(sql);
        System.out.println("Test banquet created.");
    }

    private void createTestMealsForBanquet() throws SQLException {
        String[] sqls = {
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (1, 'Grilled Salmon with Lemon Sauce', 'Fish', 150.00, 'French Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (1, 'Roasted Chicken with Garlic', 'Chicken', 120.00, 'Mediterranean Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (1, 'Braised Short Ribs', 'Beef', 180.00, 'American Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (1, 'Kung Pao Tofu', 'Vegetarian', 100.00, 'Chinese Cuisine')"
        };
    
        for (String sql : sqls) {
            sqlConnection.executeUpdate(sql);
        }
        System.out.println("Test meals for banquet created.");
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
                "ON DELETE CASCADE" +
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
                "AttendeeEmail VARCHAR2(255), " +
                "BanquetBIN NUMBER, " +
                "SeatNo NUMBER, " +
                "RegTime TIMESTAMP, " +
                "DrinkChoice VARCHAR2(50), " +
                "MealChoice VARCHAR2(255), " +
                "Remarks VARCHAR2(255), " +
                "PRIMARY KEY (AttendeeEmail, BanquetBIN), " +
                "FOREIGN KEY (AttendeeEmail) REFERENCES Account(Email) " +
                "ON DELETE CASCADE, " +
                "FOREIGN KEY (BanquetBIN) REFERENCES Banquet(BIN)" +
                "ON DELETE CASCADE" +
                ")";
        sqlConnection.executeUpdate(sql);
        System.out.println("Table Reserve created.");
    }

    private void createTestReserve() throws SQLException {
        String sql = "INSERT INTO Reserve (AttendeeEmail, BanquetBIN, SeatNo, RegTime, DrinkChoice, MealChoice, Remarks) " +
                     "VALUES ('test@polyu.hk', 1, 1, CURRENT_TIMESTAMP, 'Cold Lemon Tea', 'Chicken and rice', 'Test')";
        sqlConnection.executeUpdate(sql);
        System.out.println("Test reserve created.");
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
        System.out.println("Trigger created");
    }
    /**
     * Closes the SQL connection.
     */
    public void close() throws SQLException {
        sqlConnection.closeConnection();
    }
}
