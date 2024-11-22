package hk.polyu.comp.project2411.bms.service;

import java.sql.SQLException;
import java.util.List;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.dao.AttendeeAccountDao;
import hk.polyu.comp.project2411.bms.dao.BanquetDAO;
import hk.polyu.comp.project2411.bms.dao.DbInitDao;
import hk.polyu.comp.project2411.bms.exceptions.ValidationException;
import hk.polyu.comp.project2411.bms.model.AttendeeAccount;
import hk.polyu.comp.project2411.bms.model.Banquet;
import hk.polyu.comp.project2411.bms.model.Meal;
import hk.polyu.comp.project2411.bms.model.Reserves;

// Implementing the BMSMainInterface
// Temporaily don't implement the interface until we implement all the methods
public class BMSMain {
    private SQLConnection sqlConnection;
    private DbInitDao dbInitDao;
    private BanquetDAO banquetDao;
    private AttendeeAccountDao attendeeAccountDao;

    public BMSMain() {
        this.sqlConnection = new SQLConnection();
        this.dbInitDao = new DbInitDao(sqlConnection);
        this.banquetDao = new BanquetDAO(sqlConnection);
        this.attendeeAccountDao = new AttendeeAccountDao(sqlConnection);
        
        // Create the tables if not exists
    }

    // Close the SQLConnection when done
    public void close() {
        sqlConnection.closeConnection();
    }

    public boolean initDatabase(boolean clearIfExists) {
        return dbInitDao.initDb(clearIfExists);
    }

    // Administrator Functions
    public Banquet createBanquet(Banquet banquet) throws SQLException {
        return banquetDao.createBanquet(banquet);
    }

    public boolean updateBanquet(Banquet banquet) throws SQLException {
        return banquetDao.updateBanquet(banquet);
    }
    
    public boolean addMealToBanquet(int banquetBIN, Meal meal) throws SQLException {
        return banquetDao.addMealToBanquet(banquetBIN, meal);
    }

    public AttendeeAccount getAttendeeByEmail(String email) throws SQLException {
        return attendeeAccountDao.getAttendeeByEmail(email);
    }

    public boolean updateAttendeeRegistrationData(String email, Reserves registrationData) throws SQLException {
        return attendeeAccountDao.updateAttendeeRegistrationData(email, registrationData);
    }

    // Attendee Functions

    public boolean updateAttendeeProfile(AttendeeAccount attendee) throws ValidationException, SQLException {
        return attendeeAccountDao.updateAttendeeProfile(attendee);
    }

    public List<Banquet> getAvailableBanquets() throws SQLException {
        return banquetDao.getAvailableBanquets();
    }

}
