package hk.polyu.comp.project2411.bms.model;

// Class representing a Meal associated with a Banquet
public class Meal {
    private String dishName;
    private String type; // e.g., "fish", "chicken", etc.
    private double price;
    private String specialCuisine;

    public Meal(int banquetBIN, String dishName, String type, double price, String specialCuisine) {
        this.dishName = dishName;
        this.type = type;
        this.price = price;
        this.specialCuisine = specialCuisine;
    }
    public Meal() {
        
    }

    // Getters
    public String getDishName() {
        return dishName;
    }
    public String getType() {
        return type;
    }
    public double getPrice() {
        return price;
    }
    public String getSpecialCuisine() {
        return specialCuisine;
    }

}
