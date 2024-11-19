package com.project2411.bms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Banquet {
    private int BIN; 
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

    public Banquet(String name, String date, String time, String address, String location,
                   String contactFirstName, String contactLastName, String available, int quota) {
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

    public Banquet(int BIN, String name, String date, String time, String address, String location, String contactFirstName, String contactLastName, String available, int quota) {
        this(name, date, time, address, location, contactFirstName, contactLastName, available, quota);
        this.BIN = BIN;
    }

    public Banquet(Map<String, Object> row) {
        this(
            (String) row.get("Name"),
            (String) row.get("Date"),
            (String) row.get("Time"),
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
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
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