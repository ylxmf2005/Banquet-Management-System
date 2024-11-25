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
                createTestReserves();
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
                "Location VARCHAR2(255) NOT NULL, " +
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
        String sql = "INSERT INTO Account (Email, Role, FirstName, LastName, MobileNo, Password, Location, Address, Type, Organization) " +
                "VALUES ('bmsadmin@polyu.hk', 'admin', 'Admin', 'User', '88888888', '" + 
                passwd + "', 'PolyU', 'PolyU', 'Staff', 'PolyU')";
        sqlConnection.executeUpdate(sql);
        System.out.println("Default admin account created.");
    }

    private void createTestAttendeeAccount() throws SQLException {
        String passwd = "2411project";
        passwd = Utils.encoding(passwd);
        String[] sqls = {
            "INSERT INTO Account (Email, Role, FirstName, LastName, MobileNo, Password, Location, Address, Type, Organization) " +
            "VALUES ('test@polyu.hk', 'user', 'San', 'Zhang', '11451134', '" + passwd + "', 'PolyU HJ202', 'PolyU HJ202', 'Student', 'PolyU')",
            
            "INSERT INTO Account (Email, Role, FirstName, LastName, MobileNo, Password, Location, Address, Type, Organization) " +
            "VALUES ('guest1@example.com', 'user', 'Guest', 'One', '12345678', '" + passwd + "', 'PolyU', 'PolyU', 'Guest', 'Example Org')",
            
            "INSERT INTO Account (Email, Role, FirstName, LastName, MobileNo, Password, Location, Address, Type, Organization) " +
            "VALUES ('student1@polyu.hk', 'user', 'Student', 'One', '87654321', '" + passwd + "', 'PolyU', 'PolyU', 'Student', 'PolyU')"
            
        };

        for (String sql : sqls) {
            sqlConnection.executeUpdate(sql);
        }
        System.out.println("Test attendee accounts created.");
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

    private void createTestBanquet() throws SQLException {
        String[] sqls = {
            "INSERT INTO Banquet (BIN, Name, DateTime, Address, Location, ContactFirstName, ContactLastName, Available, Quota) " +
            "VALUES (1, 'Annual Graduation Dinner', TO_DATE('2024-12-31 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), '15 Fat Kwong Street', 'Hall of HMT', 'San', 'Zhang', 'Y', 100)",
            
            "INSERT INTO Banquet (BIN, Name, DateTime, Address, Location, ContactFirstName, ContactLastName, Available, Quota) " +
            "VALUES (2, 'New Year Celebration', TO_DATE('2024-12-25 19:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'Core A, PolyU', 'Jockey Club Auditorium', 'John', 'Smith', 'Y', 200)",
            
            "INSERT INTO Banquet (BIN, Name, DateTime, Address, Location, ContactFirstName, ContactLastName, Available, Quota) " +
            "VALUES (3, 'Research Award Ceremony', TO_DATE('2024-11-15 12:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Core P, PolyU', 'Chung Sze Yuen Building', 'Mary', 'Johnson', 'N', 50)",
            
            "INSERT INTO Banquet (BIN, Name, DateTime, Address, Location, ContactFirstName, ContactLastName, Available, Quota) " +
            "VALUES (4, 'Alumni Gathering', TO_DATE('2025-01-15 18:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'Hotel ICON', 'Ballroom', 'David', 'Lee', 'Y', 150)",
            
            "INSERT INTO Banquet (BIN, Name, DateTime, Address, Location, ContactFirstName, ContactLastName, Available, Quota) " +
            "VALUES (5, 'Department Dinner', TO_DATE('2024-10-01 19:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Core Y, PolyU', 'COMP Department', 'Wei', 'Wang', 'Y', 80)"
        };

        for (String sql : sqls) {
            sqlConnection.executeUpdate(sql);
        }
        System.out.println("Test banquets created.");
    }

    private void createTestMealsForBanquet() throws SQLException {
        String[] sqls = {
            // Meals for Banquet 1 (Annual Graduation Dinner)
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (1, 'Grilled Salmon', 'Fish', 150.00, 'French Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (1, 'Beef Tenderloin', 'Beef', 180.00, 'Western Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (1, 'Vegetarian Lasagna', 'Vegetarian', 120.00, 'Italian Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (1, 'Roasted Chicken', 'Poultry', 130.00, 'American Cuisine')",
            
            // Meals for Banquet 2 (New Year Celebration)
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (2, 'Peking Duck', 'Poultry', 180.00, 'Chinese Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (2, 'Steamed Fish', 'Fish', 160.00, 'Cantonese Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (2, 'Buddha Delight', 'Vegetarian', 120.00, 'Chinese Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (2, 'BBQ Pork', 'Pork', 140.00, 'Cantonese Cuisine')",
            
            // Meals for Banquet 3 (Research Award Ceremony)
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (3, 'Sushi Platter', 'Fish', 160.00, 'Japanese Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (3, 'Wagyu Beef', 'Beef', 200.00, 'Japanese Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (3, 'Tempura Set', 'Mixed', 150.00, 'Japanese Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (3, 'Tofu Steak', 'Vegetarian', 130.00, 'Japanese Fusion')",
            
            // Meals for Banquet 4 (Alumni Gathering)
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (4, 'Lamb Curry', 'Lamb', 170.00, 'Indian Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (4, 'Butter Chicken', 'Poultry', 150.00, 'Indian Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (4, 'Palak Paneer', 'Vegetarian', 130.00, 'Indian Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (4, 'Fish Tikka', 'Fish', 160.00, 'Indian Cuisine')",
            
            // Meals for Banquet 5 (Department Dinner)
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (5, 'Seafood Paella', 'Seafood', 190.00, 'Spanish Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (5, 'Grilled Chicken', 'Poultry', 150.00, 'Mediterranean')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (5, 'Vegetable Risotto', 'Vegetarian', 140.00, 'Italian Cuisine')",
            "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (5, 'Beef Tapas', 'Beef', 160.00, 'Spanish Cuisine')"
        };

        for (String sql : sqls) {
            sqlConnection.executeUpdate(sql);
        }
        System.out.println("Test meals created.");
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

    private void createTestReserves() throws SQLException {
        String[] sqls = {
            "INSERT INTO Reserve (AttendeeEmail, BanquetBIN, SeatNo, RegTime, DrinkChoice, MealChoice, Remarks) " +
            "VALUES ('test@polyu.hk', 1, 1, CURRENT_TIMESTAMP, 'Cold Lemon Tea', 'Grilled Salmon', 'Test')",
            
            "INSERT INTO Reserve (AttendeeEmail, BanquetBIN, SeatNo, RegTime, DrinkChoice, MealChoice, Remarks) " +
            "VALUES ('test@polyu.hk', 2, 5, TIMESTAMP '2024-01-20 14:30:00', 'Orange Juice', 'Peking Duck', 'No spicy')",
            
            "INSERT INTO Reserve (AttendeeEmail, BanquetBIN, SeatNo, RegTime, DrinkChoice, MealChoice, Remarks) " +
            "VALUES ('test@polyu.hk', 4, 8, TIMESTAMP '2024-01-22 09:15:00', 'Coca Cola', 'Lamb Curry', 'Medium spicy')",
            
            "INSERT INTO Reserve (AttendeeEmail, BanquetBIN, SeatNo, RegTime, DrinkChoice, MealChoice, Remarks) " +
            "VALUES ('guest1@example.com', 1, 4, TIMESTAMP '2024-01-21 10:45:00', 'Iced Tea', 'Roasted Chicken', 'Prefer crispy skin')",
            
            "INSERT INTO Reserve (AttendeeEmail, BanquetBIN, SeatNo, RegTime, DrinkChoice, MealChoice, Remarks) " +
            "VALUES ('guest1@example.com', 5, 12, TIMESTAMP '2024-01-21 11:20:00', 'Sprite', 'Seafood Paella', 'Extra seafood')",
            
            "INSERT INTO Reserve (AttendeeEmail, BanquetBIN, SeatNo, RegTime, DrinkChoice, MealChoice, Remarks) " +
            "VALUES ('student1@polyu.hk', 5, 3, TIMESTAMP '2024-01-21 16:30:00', 'Sprite', 'Vegetable Risotto', 'Extra cheese please')",
            
            "INSERT INTO Reserve (AttendeeEmail, BanquetBIN, SeatNo, RegTime, DrinkChoice, MealChoice, Remarks) " +
            "VALUES ('student1@polyu.hk', 2, 15, TIMESTAMP '2024-01-21 17:45:00', 'Green Tea', 'Buddha Delight', 'No mushrooms')",
            
            "INSERT INTO Reserve (AttendeeEmail, BanquetBIN, SeatNo, RegTime, DrinkChoice, MealChoice, Remarks) " +
            "VALUES ('student1@polyu.hk', 4, 20, TIMESTAMP '2024-01-22 10:00:00', 'Mango Juice', 'Butter Chicken', 'Extra naan bread')"
        };

        for (String sql : sqls) {
            sqlConnection.executeUpdate(sql);
        }
        System.out.println("Test reserves created.");
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
