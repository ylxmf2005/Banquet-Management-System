package hk.polyu.comp.project2411.bms.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.model.Meal;

public class MealDAO {
    private SQLConnection sqlConnection;

    public MealDAO(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    // Adds multiple meals to a banquet
    public boolean addMealsToBanquet(int banquetBIN, List<Meal> meals) throws SQLException {
        for (Meal meal : meals) {
            if (!addMealToBanquet(banquetBIN, meal)) {
                // If adding any meal fails, return false
                return false;
            }
        }
        return true;
    }

    // Adds a single meal to a banquet
    public boolean addMealToBanquet(int banquetBIN, Meal meal) throws SQLException {
        String sql = "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (?, ?, ?, ?, ?)";
        Object[] params = new Object[] { banquetBIN, meal.getDishName(), meal.getType(), meal.getPrice(),
                meal.getSpecialCuisine() };

        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }

    // Deprecated
    // Deletes multiple meals from a banquet
    public boolean deleteMealsFromBanquet(int banquetBIN, List<Meal> meals) throws SQLException {
        for (Meal meal : meals) {
            if (!deleteMealFromBanquet(banquetBIN, meal)) {
                // If deleting any meal fails, return false
                return false;
            }
        }
        return true;
    }

    // Deprecated
    // Deletes a single meal from a banquet
    public boolean deleteMealFromBanquet(int banquetBIN, Meal meal) throws SQLException {
        String sql = "DELETE FROM Meal WHERE BanquetBIN=? AND DishName=?";
        Object[] params = new Object[] { banquetBIN, meal.getDishName() };
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }

    // Retrieves all meals associated with a banquet
    public List<Meal> getMealsForBanquet(int banquetBIN) throws SQLException {
        String sql = "SELECT * FROM Meal WHERE BanquetBIN=?";
        Object[] params = new Object[] { banquetBIN };
        List<Map<String, Object>> result = sqlConnection.executePreparedQuery(sql, params);

        List<Meal> meals = new ArrayList<>();
        for (Map<String, Object> row : result) {
            meals.add(new Meal(row));
        }
        return meals;
    }

    public boolean updateMeal(int banquetBIN, String oldDishName, Meal newMeal) throws SQLException {
        String sql = "UPDATE Meal SET DishName=?, Type=?, Price=?, SpecialCuisine=? " +
                    "WHERE BanquetBIN=? AND DishName=?";
        Object[] params = new Object[] { 
            newMeal.getDishName(), 
            newMeal.getType(), 
            newMeal.getPrice(),
            newMeal.getSpecialCuisine(),
            banquetBIN,
            oldDishName
        };
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }

    public boolean updateMealsForBanquet(int banquetBIN, List<Meal> newMeals) throws SQLException {
        List<Meal> oldMeals = getMealsForBanquet(banquetBIN);
        
        if (oldMeals.size() != newMeals.size()) {
            return false;
        }

        for (int i = 0; i < oldMeals.size(); i++) {
            if (!updateMeal(banquetBIN, oldMeals.get(i).getDishName(), newMeals.get(i))) {
                return false;
            }
        }
        return true;
    }
}