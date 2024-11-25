package hk.polyu.comp.project2411.bms.dao;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.exceptions.AuthenticationException;
import hk.polyu.comp.project2411.bms.exceptions.ValidationException;
import hk.polyu.comp.project2411.bms.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BMSTest {
    private static SQLConnection sqlConnection;
    private static DbInitDAO dbInitDAO;
    private static AccountDAO accountDAO;
    private static BanquetDAO banquetDAO;
    private static MealDAO mealDAO;
    private static ReserveDAO reserveDAO;
    private static AttendeeAccountDAO attendeeAccountDAO;
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        sqlConnection = new SQLConnection();
        dbInitDAO = new DbInitDAO(sqlConnection);
        accountDAO = new AccountDAO(sqlConnection);
        banquetDAO = new BanquetDAO(sqlConnection);
        mealDAO = new MealDAO(sqlConnection);
        reserveDAO = new ReserveDAO(sqlConnection);
        attendeeAccountDAO = new AttendeeAccountDAO(sqlConnection);
        dbInitDAO.initDb(true);
    }
    @Test
    public void testAttendee() throws AuthenticationException, SQLException, ValidationException {
        Account admin = accountDAO.authenticateAccount("bmsadmin@polyu.hk", "2411project");
        AttendeeAccount user = (AttendeeAccount) accountDAO.authenticateAccount("test@polyu.hk","2411project");
        assert(attendeeAccountDAO.getAttendeeByEmail("test@polyu.hk") != null);
        user.setOriginalEmail("test1@polyu.hk");
        attendeeAccountDAO.updateAttendeeProfile(user);
        AttendeeAccount newUsr = new AttendeeAccount("test2@polyu.hk","T","est","none","none","123456","10203040",user.getOrganization());
        attendeeAccountDAO.registerAttendee(newUsr);
        attendeeAccountDAO.searchRegistrations("test1@polyu.hk", new SearchCriteria());
    }
    @Test
    public void testBanquet() throws AuthenticationException, SQLException, ValidationException {
        Banquet newBan = new Banquet("Winter Gala", new Timestamp(20241201180000L), "789 Banquet Ave, HK", "HK", "Alice", "Johnson", "Y", 100);
        banquetDAO.createBanquet(newBan);
        banquetDAO.deleteBanquet(1);
        assert (banquetDAO.getAllBanquets() != null);
        assert (banquetDAO.getAvailableBanquets() != null);
        assert (banquetDAO.getBanquetByBIN(1) == null);
        AttendeeAccount user = attendeeAccountDAO.getAttendeeByEmail("test@polyu.hk");
        assert(banquetDAO.getAvailableUnregisteredBanquets("test@polyu.hk") != null);
        assert(banquetDAO.getAttendees(2) != null);
        newBan.setBIN(1);
        banquetDAO.updateBanquet(newBan);
    }
    @Test
    public void testMeal() throws AuthenticationException, SQLException, ValidationException {
        List<Meal> meals = new ArrayList<>();
        meals.add(new Meal(1, "Roast Chicken", "chicken", 200.0, "No"));
        mealDAO.addMealsToBanquet(1,meals);
    }
    @Test
    public void testReserve() throws AuthenticationException, SQLException, ValidationException {
        assert(reserveDAO.getBanquet(2) != null);
        reserveDAO.deleteReserve("test@polyu.hk",2);
        assert(reserveDAO.getReservationsByBIN(2) != null);
        assert(reserveDAO.getAvailableSeatNo(reserveDAO.getBanquet(2)) == 1);
        reserveDAO.updateAttendeeRegistrationData(reserveDAO.getReservesByAttendeeEmail("test@polyu.hk").get(0));
    }
}
