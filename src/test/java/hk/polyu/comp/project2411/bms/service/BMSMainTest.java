package hk.polyu.comp.project2411.bms.service;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.model.Banquet;

public class BMSMainTest {
    private BMSMain bmsMain;
    private SQLConnection mockSqlConnection;
    public void testCreateBanquetSuccess() throws SQLException {
        Banquet banquet = new Banquet(
            "Ethan Lee",
            Timestamp.valueOf(LocalDateTime.of(2024,        12, 31, 18, 0)),
            "Hung Lai Road 1",
            "1F",
            "Leo",
            "Sun",
            "Y",
            50  
        );
        Banquet createdBanquet = bmsMain.createBanquet(banquet);
        System.out.println(createdBanquet);
    }
}