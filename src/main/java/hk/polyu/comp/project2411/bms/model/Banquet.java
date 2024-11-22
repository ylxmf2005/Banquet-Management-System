package hk.polyu.comp.project2411.bms.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Banquet {
    private int BIN; 
    private String name;
    private Timestamp dateTime;
    private String address;
    private String location;
    private String contactFirstName;
    private String contactLastName;
    private String available; // e.g., "Y" or "N"
    private int quota;
    private List<Meal> meals;

    public Banquet(String name, Timestamp dateTime, String address, String location,
                   String contactFirstName, String contactLastName, String available, int quota) {
        this.name = name;
        this.dateTime = dateTime;
        this.address = address;
        this.location = location;
        this.contactFirstName = contactFirstName;
        this.contactLastName = contactLastName;
        this.available = available;
        this.quota = quota;
        meals = new ArrayList<>();
    }

    public Banquet(int BIN, String name, Timestamp dateTime, String address, String location, String contactFirstName, String contactLastName, String available, int quota) {
        this(name, dateTime, address, location, contactFirstName, contactLastName, available, quota);
        this.BIN = BIN;
    }

    public Banquet(Map<String, Object> row) {
        this(
            (String) row.get("Name"),
            Timestamp.valueOf((String) row.get("DateTime")),
            (String) row.get("Address"),
            (String) row.get("Location"),
            (String) row.get("FirstName"),
            (String) row.get("LastName"),
            (String) row.get("Available"),
            ((Number) row.get("Quota")).intValue()
        );
        if (row.get("BIN") != null) {
            this.BIN = ((Number) row.get("BIN")).intValue();
        }
    }

    public void setMeals(Meal meal) {
        if (meals.size() == 4) {
            throw new IllegalArgumentException("Meals for this banquet is fully set.");
        }
        meals.add(meal);
    }
    
    // Getters
    public int getBIN() {
        return BIN;
    }
    public String getName() {
        return name;
    }
    public Timestamp getDateTime() {
        return dateTime;
    }

    public String getAddress() {
        return address;
    }
    public String getLocation() {
        return location;
    }
    public String getContactFirstName() {
        return contactFirstName;
    }
    public String getContactLastName() {
        return contactLastName;
    }
    public String getAvailable() {
        return available;
    }
    public int getQuota() {
        return quota;
    }
    public List<Meal> getMeals() {
        return meals;
    }
    
    // Setters
    public void setBIN(int BIN) {
        this.BIN = BIN;
    }
    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    @Override
    public String toString() {
        return "Banquet{" +
                "BIN=" + BIN +
                ", name='" + name + '\'' +
                ", dateTime='" + dateTime.toString() + '\'' +
                ", address='" + address + '\'' +
                ", location='" + location + '\'' +
                ", contactFirstName='" + contactFirstName + '\'' +
                ", contactLastName='" + contactLastName + '\'' +
                ", available='" + available + '\'' +
                ", quota=" + quota +
                ", meals=" + meals +
                '}';
    }
}