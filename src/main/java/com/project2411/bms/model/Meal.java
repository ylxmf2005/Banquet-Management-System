package main.java.com.project2411.bms.model;

// Class representing a Meal associated with a Banquet
public class Meal {
    private int banquetBIN;
    private String dishName;
    private String type; // e.g., "fish", "chicken", etc.
    private double price;
    private String specialCuisine;

    // Constructors, getters, and setters omitted
    public Meal(int banquetBIN, String dishName, String type, double price, String specialCuisine) {
        this.banquetBIN = banquetBIN;
        this.dishName = dishName;
        this.type = type;
        this.price = price;
        this.specialCuisine = specialCuisine;
    }
}
