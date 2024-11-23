package hk.polyu.comp.project2411.bms.dao;

import java.sql.SQLException;
import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.model.Meal;

public class MealDAO {
    private SQLConnection sqlConnection;

    public MealDAO(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    // Assuming the frontend has already checked to ensure that the meals for the same banquet are different.
    public boolean addMealToBanquet(int banquetBIN, Meal meal) throws SQLException {
        String sql = "INSERT INTO Meal (BanquetBIN, DishName, Type, Price, SpecialCuisine) VALUES (?, ?, ?, ?, ?)";
        Object[] params = new Object[] {
            banquetBIN,
            meal.getDishName(),
            meal.getType(),
            meal.getPrice(),
            meal.getSpecialCuisine()
        };

        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }
}