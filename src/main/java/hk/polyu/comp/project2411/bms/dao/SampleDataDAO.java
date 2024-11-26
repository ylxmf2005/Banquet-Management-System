package hk.polyu.comp.project2411.bms.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;

public class SampleDataDAO {

    private SQLConnection sqlConnection;

    // Instance variables to store data for use in multiple methods
    private List<String> attendeeEmails;
    private Map<Integer, List<String>> banquetMealsMap;

    public SampleDataDAO() throws SQLException {
        this.sqlConnection = new SQLConnection();
        this.attendeeEmails = new ArrayList<>();
        this.banquetMealsMap = new HashMap<>();
    }

    public SampleDataDAO(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
        this.attendeeEmails = new ArrayList<>();
        this.banquetMealsMap = new HashMap<>();
    }

    public void createTestAttendeeAccount() throws SQLException {
        String passwd = Utils.encoding("2411project");
        String sql = "INSERT INTO Account (Email, Role, FirstName, LastName, MobileNo, Password, Location, Address, Type, Organization) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Object[][] params = {
            // Students
            {"student1@polyu.edu.hk", "user", "John", "Wong", "12345678", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student2@polyu.edu.hk", "user", "Mary", "Chan", "23456789", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student3@polyu.edu.hk", "user", "Peter", "Lam", "34567890", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student4@polyu.edu.hk", "user", "Sarah", "Lee", "45678901", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student5@polyu.edu.hk", "user", "David", "Yip", "56789012", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student6@polyu.edu.hk", "user", "Alice", "Lau", "67890123", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student7@polyu.edu.hk", "user", "Brian", "Ho", "78901234", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student8@polyu.edu.hk", "user", "Cindy", "Tang", "89012345", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student9@polyu.edu.hk", "user", "Derek", "Cheung", "90123456", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student10@polyu.edu.hk", "user", "Emily", "Choi", "01234567", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student11@polyu.edu.hk", "user", "Frank", "Kwok", "11223344", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student12@polyu.edu.hk", "user", "Grace", "Mak", "22334455", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student13@polyu.edu.hk", "user", "Henry", "Au", "33445566", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student14@polyu.edu.hk", "user", "Ivy", "Yeung", "44556677", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},
            {"student15@polyu.edu.hk", "user", "Jack", "Tsang", "55667788", passwd, "PolyU", "Hung Hom", "Student", "PolyU"},

            // Staff
            {"staff1@polyu.edu.hk", "user", "James", "Chen", "67890123", passwd, "PolyU", "Staff Quarters", "Staff", "PolyU"},
            {"staff2@polyu.edu.hk", "user", "Linda", "Zhang", "78901234", passwd, "PolyU", "Staff Quarters", "Staff", "PolyU"},
            {"staff3@polyu.edu.hk", "user", "Robert", "Liu", "89012345", passwd, "PolyU", "Staff Quarters", "Staff", "PolyU"},
            {"staff4@polyu.edu.hk", "user", "Kelly", "Wong", "12344321", passwd, "PolyU", "Staff Quarters", "Staff", "PolyU"},
            {"staff5@polyu.edu.hk", "user", "Leo", "Leung", "23455432", passwd, "PolyU", "Staff Quarters", "Staff", "PolyU"},
            {"staff6@polyu.edu.hk", "user", "Nancy", "Cheng", "34566543", passwd, "PolyU", "Staff Quarters", "Staff", "PolyU"},
            {"staff7@polyu.edu.hk", "user", "Oscar", "Tam", "45677654", passwd, "PolyU", "Staff Quarters", "Staff", "PolyU"},
            {"staff8@polyu.edu.hk", "user", "Paul", "Yuen", "56788765", passwd, "PolyU", "Staff Quarters", "Staff", "PolyU"},

            // Guests
            {"guest1@example.com", "user", "Michael", "Smith", "90123456", passwd, "PolyU", "Hung Hom", "Guest", "ABC Company"},
            {"guest2@example.com", "user", "Emma", "Brown", "01234567", passwd, "PolyU", "Hung Hom", "Guest", "XYZ Limited"},
            {"guest3@example.com", "user", "William", "Taylor", "11223344", passwd, "PolyU", "Hung Hom", "Guest", "DEF Corp"},
            {"guest4@example.com", "user", "Sophia", "Davis", "22334455", passwd, "PolyU", "Hung Hom", "Guest", "GHI Inc"},
            {"guest5@example.com", "user", "Daniel", "Wilson", "33445566", passwd, "PolyU", "Hung Hom", "Guest", "JKL LLC"},
            {"guest6@example.com", "user", "Olivia", "Moore", "44556677", passwd, "PolyU", "Hung Hom", "Guest", "MNO Ltd"},
            {"guest7@example.com", "user", "Matthew", "Johnson", "55667788", passwd, "PolyU", "Hung Hom", "Guest", "PQR Co"},
            {"guest8@example.com", "user", "Chloe", "Lee", "66778899", passwd, "PolyU", "Hung Hom", "Guest", "STU Group"},
            {"guest9@example.com", "user", "Joshua", "Martin", "77889900", passwd, "PolyU", "Hung Hom", "Guest", "VWX Ltd"},
            {"guest10@example.com", "user", "Lily", "Thomas", "88990011", passwd, "PolyU", "Hung Hom", "Guest", "YZA Corp"}
        };

        for (Object[] param : params) {
            sqlConnection.executePreparedUpdate(sql, param);
            String email = (String) param[0];
            attendeeEmails.add(email);
        }
        System.out.println("Test attendee accounts created.");
    }

    public void createTestBanquet() throws SQLException {
        String sql = "INSERT INTO Banquet (BIN, Name, DateTime, Address, Location, ContactFirstName, ContactLastName, Available, Quota) VALUES (?, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'), ?, ?, ?, ?, ?, ?)";
    
        Object[][] params = {
            {1, "Annual Graduation Dinner", "2024-06-30 18:00:00", "Hotel ICON", "Ballroom A", "James", "Chen", "Y", 200},
            {2, "COMP Department Gathering", "2024-07-15 19:00:00", "PolyU Campus", "PQ Wing", "Linda", "Zhang", "Y", 150},
            {3, "Research Excellence Award Ceremony", "2024-08-01 18:30:00", "PolyU Campus", "Jockey Club Auditorium", "Robert", "Liu", "Y", 300},
            {4, "International Conference Reception", "2024-09-15 19:00:00", "Hotel ICON", "Function Room B", "William", "Taylor", "Y", 180},
            {5, "Alumni Homecoming Dinner", "2024-10-01 18:00:00", "Nina Hotel", "Grand Ballroom", "Mary", "Chan", "Y", 250},
            {6, "Staff Appreciation Dinner", "2024-11-30 19:00:00", "Cordis Hotel", "Diamond Room", "Peter", "Lam", "Y", 120},
            {7, "New Year Celebration", "2024-12-31 19:00:00", "Regal Hotel", "Crystal Ballroom", "Sarah", "Lee", "Y", 400},
            {8, "Spring Festival Gala", "2025-02-10 18:30:00", "InterContinental", "Victoria Harbour Room", "David", "Yip", "Y", 300},
            {9, "Technology Innovation Summit", "2025-03-20 09:00:00", "PolyU Campus", "Lecture Theatre A", "Alice", "Lau", "Y", 350},
            {10, "Cultural Exchange Night", "2025-04-15 18:00:00", "Hotel ICON", "Ballroom B", "Henry", "Au", "Y", 220},
            {11, "Environmental Awareness Seminar", "2025-05-10 10:00:00", "PolyU Campus", "EF Wing", "Emily", "Choi", "Y", 180},
            {12, "PolyU Sports Day", "2025-06-05 08:00:00", "PolyU Sports Ground", "Main Field", "Frank", "Kwok", "Y", 500},
            {13, "Art and Design Showcase", "2025-07-25 17:00:00", "PolyU Campus", "VA Building", "Grace", "Mak", "Y", 200},
            {14, "Business Networking Event", "2025-08-30 18:00:00", "Cordis Hotel", "Sky Lounge", "Leo", "Leung", "Y", 150},
            {15, "Charity Fundraising Gala", "2025-09-20 19:00:00", "Hotel ICON", "Grand Hall", "Kelly", "Wong", "Y", 300},
            {16, "International Student Welcome Party", "2025-10-10 18:00:00", "PolyU Campus", "Student Union", "Brian", "Ho", "Y", 250},
            {17, "Annual Research Symposium", "2025-11-15 09:00:00", "PolyU Campus", "ST Wing", "Nancy", "Cheng", "Y", 400},
            {18, "Healthcare Conference", "2025-12-05 08:30:00", "Regal Hotel", "Conference Room 1", "Oscar", "Tam", "Y", 200},
            {19, "Music Festival", "2026-01-20 16:00:00", "PolyU Campus", "Central Lawn", "Paul", "Yuen", "Y", 600},
            {20, "Innovation and Entrepreneurship Fair", "2026-02-25 09:00:00", "PolyU Campus", "Innovation Tower", "Ivy", "Yeung", "Y", 300}
        };
    
        for (Object[] param : params) {
            sqlConnection.executePreparedUpdate(sql, param);
        }
        System.out.println("Test banquets created.");
    }

    public void createTestMealsForBanquet() throws SQLException {
        String sql = "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (?, ?, ?, ?, ?)";

        Object[][] mealPool = {
            { "Steamed Fish with Ginger", "Seafood", 188.00, "Cantonese"},
            { "Braised Beef Short Ribs", "Meat", 228.00, "Western"},
            { "Mushroom Wellington", "Vegetarian", 168.00, "Western"},
            { "Roasted Duck Breast", "Poultry", 198.00, "French"},
            { "Baked Salmon", "Seafood", 178.00, "Western"},
            { "Char Siu Pork", "Meat", 158.00, "Cantonese"},
            { "Vegetable Curry", "Vegetarian", 148.00, "Indian"},
            { "Chicken Cordon Bleu", "Poultry", 168.00, "French"},
            { "Lobster Thermidor", "Seafood", 288.00, "French"},
            { "Prime Rib Steak", "Meat", 258.00, "Western"},
            { "Eggplant Parmesan", "Vegetarian", 148.00, "Italian"},
            { "Peking Duck", "Poultry", 238.00, "Chinese"},
            { "Sea Bass Fillet", "Seafood", 198.00, "Mediterranean"},
            { "Lamb Rack", "Meat", 238.00, "French"},
            { "Vegetable Lasagna", "Vegetarian", 158.00, "Italian"},
            { "Turkey Roulade", "Poultry", 178.00, "Western"},
            { "Black Cod", "Seafood", 248.00, "Japanese"},
            { "Wagyu Beef", "Meat", 328.00, "Japanese"},
            { "Truffle Risotto", "Vegetarian", 188.00, "Italian"},
            { "Duck Confit", "Poultry", 208.00, "French"},
            { "Grilled Prawns", "Seafood", 188.00, "Mediterranean"},
            { "Veal Cutlet", "Meat", 218.00, "Italian"},
            { "Impossible Wellington", "Vegetarian", 178.00, "Western"},
            { "Chicken Supreme", "Poultry", 168.00, "French"},
            { "Boston Lobster", "Seafood", 298.00, "Western"},
            { "Tomahawk Steak", "Meat", 338.00, "Western"},
            { "Wild Mushroom Pasta", "Vegetarian", 168.00, "Italian"},
            { "Roasted Guinea Fowl", "Poultry", 188.00, "French"},
            { "Steamed Garoupa", "Seafood", 208.00, "Cantonese"},
            { "Braised Pork Belly", "Meat", 188.00, "Chinese"},
            { "Buddha's Delight", "Vegetarian", 158.00, "Chinese"},
            { "Soy Sauce Chicken", "Poultry", 168.00, "Cantonese"},
            { "Grilled Octopus", "Seafood", 218.00, "Mediterranean"},
            { "Beef Bourguignon", "Meat", 238.00, "French"},
            { "Vegetable Stir Fry", "Vegetarian", 128.00, "Chinese"},
            { "Grilled Chicken Caesar Salad", "Poultry", 148.00, "Western"}
        };

        // Assign meals to banquets
        int[][] banquetMeals = {
            {1, 0, 1, 2, 3},    // Banquet 1 meals
            {2, 4, 5, 6, 7},
            {3, 8, 9, 10, 11},
            {4, 12, 13, 14, 15},
            {5, 16, 17, 18, 19},
            {6, 20, 21, 22, 23},
            {7, 24, 25, 26, 27},
            {8, 28, 29, 30, 31},
            {9, 0, 5, 10, 14},   // Reusing meals for diversity
            {10, 4, 9, 18, 23},
            {11, 12, 25, 2, 31},
            {12, 0, 4, 8, 12},
            {13, 16, 20, 24, 28},
            {14, 1, 5, 13, 21},
            {15, 2, 6, 14, 22},
            {16, 3, 7, 15, 23},
            {17, 0, 8, 16, 24},
            {18, 1, 9, 17, 25},
            {19, 2, 10, 18, 26},
            {20, 3, 11, 19, 27}
        };

        for (int[] banquetMeal : banquetMeals) {
            int banquetBIN = banquetMeal[0];
            List<String> mealsForBanquet = new ArrayList<>();

            for (int i = 1; i <= 4; i++) {
                Object[] meal = mealPool[banquetMeal[i]];
                Object[] params = {
                    banquetBIN,
                    meal[0],  // DishName
                    meal[1],  // Type
                    meal[2],  // Price
                    meal[3]   // SpecialCuisine
                };
                sqlConnection.executePreparedUpdate(sql, params);

                mealsForBanquet.add((String) meal[0]);
            }
            // Add to banquetMealsMap
            banquetMealsMap.put(banquetBIN, mealsForBanquet);
        }
        System.out.println("Test meals created.");
    }

    public void createTestReserves() throws SQLException {
        String sql = "INSERT INTO Reserve (AttendeeEmail, BanquetBIN, SeatNo, RegTime, DrinkChoice, MealChoice, Remarks) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Predefined list of drinks
        String[] drinks = {"Oolong Tea", "Green Tea", "Coffee", "Cola", "Sprite", "Orange Juice", "Chinese Tea", "Water"};

        String[] possibleRemarks = {null, "No spicy", "No garlic", "No sauce", "Extra sauce", "Medium rare", "Medium well", "Well done", "Vegetarian", "Allergic to nuts", "No dairy", "Gluten-free"};

        Random rand = new Random();

        // For seat number tracking
        Map<Integer, Integer> seatNumberMap = new HashMap<>();

        // For tracking which attendees have reserved which banquets
        Map<Integer, Set<String>> banquetAttendeesMap = new HashMap<>();

        for (int banquetBIN = 1; banquetBIN <= 20; banquetBIN++) {

            // Decide number of reservations for this banquet
            int numReservations = (int) Math.round(rand.nextGaussian() * 5 + 20); // Mean 20, std dev 5
            numReservations = Math.max(10, Math.min(numReservations, 30)); // Limit between 10 and 30

            // Initialize seat number to 1
            int seatNo = 1;
            seatNumberMap.put(banquetBIN, seatNo);

            // Initialize set of attendees for this banquet
            Set<String> attendeesForBanquet = new HashSet<>();
            banquetAttendeesMap.put(banquetBIN, attendeesForBanquet);

            // Get the banquet date from the database
            String dateSql = "SELECT DateTime FROM Banquet WHERE BIN = ?";
            Object[] dateParams = {banquetBIN};
            List<Map<String, Object>> dateResult = sqlConnection.executePreparedQuery(dateSql, dateParams);

            if (dateResult.isEmpty()) {
                System.out.println("No date found for Banquet BIN " + banquetBIN);
                continue;
            }

            Timestamp banquetDateTime = (Timestamp) dateResult.get(0).get("DATETIME");

            for (int i = 0; i < numReservations; i++) {
                // Randomly pick an attendee who hasn't reserved this banquet
                String attendeeEmail = null;
                int attempts = 0;
                do {
                    if (attempts >= 100) {
                        System.out.println("Not enough attendees available for Banquet BIN " + banquetBIN);
                        break;
                    }
                    attendeeEmail = attendeeEmails.get(rand.nextInt(attendeeEmails.size()));
                    attempts++;
                } while (attendeesForBanquet.contains(attendeeEmail));

                if (attendeeEmail == null) {
                    continue;
                }

                attendeesForBanquet.add(attendeeEmail);

                // Seat number
                seatNo = seatNumberMap.get(banquetBIN);

                // Registration time: Randomly choose a time between 60 days before banquet and 1 day before banquet
                long banquetTime = banquetDateTime.getTime();
                long minRegTime = banquetTime - (60L * 24 * 60 * 60 * 1000); // 60 days before
                long maxRegTime = banquetTime - (1L * 24 * 60 * 60 * 1000);   // 1 day before

                long regTimeMillis = minRegTime + (long)(rand.nextDouble() * (maxRegTime - minRegTime));
                Timestamp regTime = new Timestamp(regTimeMillis);

                // Drink choice
                String drinkChoice = drinks[rand.nextInt(drinks.length)];

                // Meal choice, randomly pick from meals assigned to this banquet
                List<String> mealsForBanquet = banquetMealsMap.get(banquetBIN);
                if (mealsForBanquet == null || mealsForBanquet.isEmpty()) {
                    System.out.println("No meals found for Banquet BIN " + banquetBIN);
                    continue;
                }
                String mealChoice = mealsForBanquet.get(rand.nextInt(mealsForBanquet.size()));

                // Remarks
                String remarks = possibleRemarks[rand.nextInt(possibleRemarks.length)];

                // Prepare parameters
                Object[] params = {
                    attendeeEmail,
                    banquetBIN,
                    seatNo,
                    regTime,
                    drinkChoice,
                    mealChoice,
                    remarks
                };

                sqlConnection.executePreparedUpdate(sql, params);

                // Increment seat number
                seatNumberMap.put(banquetBIN, seatNo + 1);
            }
        }
        System.out.println("Test reserves created.");
    }
}