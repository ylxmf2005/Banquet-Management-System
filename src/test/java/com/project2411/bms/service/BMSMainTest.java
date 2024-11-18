package com.project2411.bms.service;
import java.sql.SQLException;

import com.project2411.bms.connection.SQLConnection;
import com.project2411.bms.model.Banquet;

public class BMSMainTest {
    private BMSMain bmsMain;
    private SQLConnection mockSqlConnection;
    public void testCreateBanquetSuccess() throws SQLException {
        // Arrange
        Banquet banquet = new Banquet("Ethan Lee","2024-12-31", "18:00", "Hung Lai Road 1", "1F", "Leo", "Sun", "Y", 50);
        Banquet createdBanquet = bmsMain.createBanquet(banquet);
        System.out.println(createdBanquet);
    }
}