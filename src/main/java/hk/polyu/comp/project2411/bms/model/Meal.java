package hk.polyu.comp.project2411.bms.model;

import java.util.Map;

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
    
    public Meal(Map<String, Object> row) {
        Map<String, Object> lowerCaseRow = Utils.getLowerCasedMap(row);

        this.dishName = (String) lowerCaseRow.get("dishname");
        this.type = (String) lowerCaseRow.get("type");
        this.price = ((Number) lowerCaseRow.get("price")).doubleValue();
        this.specialCuisine = (String) lowerCaseRow.get("specialcuisine");
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
