package hk.polyu.comp.project2411.bms.service;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.dao.AccountDAO;
import hk.polyu.comp.project2411.bms.dao.AttendeeAccountDAO;
import hk.polyu.comp.project2411.bms.dao.BanquetDAO;
import hk.polyu.comp.project2411.bms.dao.DbInitDAO;
import hk.polyu.comp.project2411.bms.dao.MealDAO;
import hk.polyu.comp.project2411.bms.dao.ReportDAO;
import hk.polyu.comp.project2411.bms.dao.ReserveDAO;
import hk.polyu.comp.project2411.bms.exceptions.AuthenticationException;
import hk.polyu.comp.project2411.bms.exceptions.RegistrationException;
import hk.polyu.comp.project2411.bms.exceptions.ValidationException;
import hk.polyu.comp.project2411.bms.model.Account;
import hk.polyu.comp.project2411.bms.model.AttendeeAccount;
import hk.polyu.comp.project2411.bms.model.Banquet;
import hk.polyu.comp.project2411.bms.model.Meal;
import hk.polyu.comp.project2411.bms.model.RegistrationResult;
import hk.polyu.comp.project2411.bms.model.Reserve;
import hk.polyu.comp.project2411.bms.model.SearchCriteria;

public class BMSMain {
    private SQLConnection sqlConnection;
    private DbInitDAO dbInitDao;
    private BanquetDAO banquetDao;
    private AttendeeAccountDAO attendeeAccountDao;
    private AccountDAO accountDao;
    private ReserveDAO reserveDao;
    private MealDAO mealDao;
    private ReportDAO reportDAO;

    public BMSMain() throws SQLException {
        this.sqlConnection = new SQLConnection();
        this.dbInitDao = new DbInitDAO(sqlConnection);
        this.banquetDao = new BanquetDAO(sqlConnection);
        this.attendeeAccountDao = new AttendeeAccountDAO(sqlConnection);
        this.accountDao = new AccountDAO(sqlConnection);
        this.reserveDao = new ReserveDAO(sqlConnection);
        this.mealDao = new MealDAO(sqlConnection);
        this.reportDAO = new ReportDAO(sqlConnection);
        
        // Create the tables if not exists
        initDatabase(true, true);
        // initDatabase(false);
    }

    // Close the SQLConnection when done
    public void close() throws SQLException {
        sqlConnection.closeConnection();
    }

    public boolean initDatabase(boolean clearIfExists, boolean createSampleData) {
        return dbInitDao.initDb(clearIfExists, createSampleData);
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

    public boolean updateAttendeeRegistrationData(Reserve registrationData) throws SQLException {
        return reserveDao.updateAttendeeRegistrationData(registrationData);
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

    public boolean registerAttendee(AttendeeAccount attendee) throws ValidationException, SQLException {
        return attendeeAccountDao.registerAttendee(attendee);
    }

    public boolean updateAttendeeProfile(AttendeeAccount attendee) throws ValidationException, SQLException {
        return attendeeAccountDao.updateAttendeeProfile(attendee);
    }
    
    public List<Banquet> getAvailableUnregisteredBanquets(String attendeeEmail) throws SQLException {
        return banquetDao.getAvailableUnregisteredBanquets(attendeeEmail);
    }

    public RegistrationResult registerForBanquet(Reserve registrationData) throws RegistrationException, SQLException {
        return reserveDao.registerForBanquet(registrationData);
    }

    public List<Reserve> searchRegistrations(String attendeeEmail, SearchCriteria criteria) throws SQLException {
        return attendeeAccountDao.searchRegistrations(attendeeEmail, criteria);
    }

    public Banquet getBanquetByBIN(int BIN) throws SQLException {
        return banquetDao.getBanquetByBIN(BIN);
    }

    public List<Reserve> getReservationsByBIN(int banquetBIN) throws SQLException {
        return reserveDao.getReservationsByBIN(banquetBIN);
    }

    public File generateReport() throws Exception {
        return reportDAO.generateReport();
    }

    public boolean deleteAttendee(String email) throws SQLException {
        return attendeeAccountDao.deleteAttendee(email);
    }

    public List<Banquet> searchAvailableUnregisteredBanquets(String attendeeEmail, SearchCriteria criteria) throws SQLException {
        return banquetDao.searchAvailableUnregisteredBanquets(attendeeEmail, criteria);
    }

}
