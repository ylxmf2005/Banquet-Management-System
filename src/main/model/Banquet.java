package main.model;

import java.util.ArrayList;
import java.util.List;

// Class representing a Banquet
public class Banquet {
    private int BIN; // Banquet Identification Number
    private String name;
    private String date;
    private String time;
    private String address;
    private String location;
    private String contactFirstName;
    private String contactLastName;
    private String available; // e.g., "Y" or "N"
    private int quota;
    private List<Meal> meals;

    // Constructors, getters, and setters omitted.
    public Banquet(int BIN, String name, String date, String time, String address, String location,
                   String contactFirstName, String contactLastName, String available, int quota) {
        this.BIN = BIN;
        this.name = name;
        this.date = date;
        this.time = time;
        this.address = address;
        this.location = location;
        this.contactFirstName = contactFirstName;
        this.contactLastName = contactLastName;
        this.available = available;
        this.quota = quota;
        meals = new ArrayList<>();
    }
    public void setMeals(Meal meal) {
        if (meals.size() == 4) {
            throw new IllegalArgumentException("Meals for this banquet is fully set.");
        }
        meals.add(meal);
    }
    
    /*Getters*/
    public int getBIN() {
        return BIN;
    }
    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
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
}
