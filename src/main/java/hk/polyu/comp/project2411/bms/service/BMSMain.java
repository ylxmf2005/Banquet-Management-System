package hk.polyu.comp.project2411.bms.service;

import java.sql.SQLException;
import java.util.List;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.dao.AccountDAO;
import hk.polyu.comp.project2411.bms.dao.AttendeeAccountDAO;
import hk.polyu.comp.project2411.bms.dao.BanquetDAO;
import hk.polyu.comp.project2411.bms.dao.DbInitDAO;
import hk.polyu.comp.project2411.bms.dao.MealDAO;
import hk.polyu.comp.project2411.bms.dao.ReserveDAO;
import hk.polyu.comp.project2411.bms.exceptions.AuthenticationException;
import hk.polyu.comp.project2411.bms.exceptions.ValidationException;
import hk.polyu.comp.project2411.bms.model.Account;
import hk.polyu.comp.project2411.bms.model.AttendeeAccount;
import hk.polyu.comp.project2411.bms.model.Banquet;
import hk.polyu.comp.project2411.bms.model.Meal;
import hk.polyu.comp.project2411.bms.model.Reserve;

// Implementing the BMSMainInterface
// Temporaily don't implement the interface until we implement all the methods
public class BMSMain {
    private SQLConnection sqlConnection;
    private DbInitDAO dbInitDao;
    private BanquetDAO banquetDao;
    private AttendeeAccountDAO attendeeAccountDao;
    private AccountDAO accountDao;
    private ReserveDAO reserveDao;
    private MealDAO mealDao;

    public BMSMain() {
        this.sqlConnection = new SQLConnection();
        this.dbInitDao = new DbInitDAO(sqlConnection);
        this.banquetDao = new BanquetDAO(sqlConnection);
        this.attendeeAccountDao = new AttendeeAccountDAO(sqlConnection);
        this.accountDao = new AccountDAO(sqlConnection);
        this.reserveDao = new ReserveDAO(sqlConnection);
        
        // Create the tables if not exists
        initDatabase(true); // set to true for test because our database structure is not finalized
    }

    // Close the SQLConnection when done
    public void close() {
        sqlConnection.closeConnection();
    }

    public boolean initDatabase(boolean clearIfExists) {
        return dbInitDao.initDb(clearIfExists);
    }

    // Login Functions
    public Account authenticateAccount(String email, String password) throws AuthenticationException, SQLException {
        return accountDao.authenticateAccount(email, password);
    }

    // Administrator Functions
    public Banquet createBanquet(Banquet banquet) throws SQLException {
        return banquetDao.createBanquet(banquet);
    }

    public boolean updateBanquet(Banquet banquet) throws SQLException {
        return banquetDao.updateBanquet(banquet);
    }
    
    public boolean addMealToBanquet(int banquetBIN, Meal meal) throws SQLException {
        return mealDao.addMealToBanquet(banquetBIN, meal);
    }

    public AttendeeAccount getAttendeeByEmail(String email) throws SQLException {
        return attendeeAccountDao.getAttendeeByEmail(email);
    }

    public List<Reserve> getReservesByAttendeeEmail(String email) throws SQLException {
        return reserveDao.getReservesByAttendeeEmail(email);
    }

    public boolean updateAttendeeRegistrationData(String email, Reserve registrationData) throws SQLException {
        return attendeeAccountDao.updateAttendeeRegistrationData(email, registrationData);
    }

    public List<Banquet> getAllBanquets() throws SQLException {
        return banquetDao.getAllBanquets();
    }

    public boolean deleteBanquet(int banquetBIN) throws SQLException {
        return banquetDao.deleteBanquet(banquetBIN);
    }

    public boolean deleteReserve(String attendeeEmail, int banquetBIN) throws SQLException {
        return reserveDao.deleteReserve(attendeeEmail, banquetBIN);
    }

    // Attendee Functions

    public boolean updateAttendeeProfile(AttendeeAccount attendee) throws ValidationException, SQLException {
        return attendeeAccountDao.updateAttendeeProfile(attendee);
    }
    
    public List<Banquet> getAvailableBanquets() throws SQLException {
        return banquetDao.getAvailableBanquets();
    }


}
