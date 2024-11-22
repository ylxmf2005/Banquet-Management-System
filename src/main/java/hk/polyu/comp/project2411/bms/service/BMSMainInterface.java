package hk.polyu.comp.project2411.bms.service;
import java.sql.SQLException;
import java.util.List;

import hk.polyu.comp.project2411.bms.exceptions.AuthenticationException;
import hk.polyu.comp.project2411.bms.exceptions.RegistrationException;
import hk.polyu.comp.project2411.bms.exceptions.ValidationException;
import hk.polyu.comp.project2411.bms.model.Account;
import hk.polyu.comp.project2411.bms.model.AttendeeAccount;
import hk.polyu.comp.project2411.bms.model.Banquet;
import hk.polyu.comp.project2411.bms.model.Meal;
import hk.polyu.comp.project2411.bms.model.RegistrationResult;
import hk.polyu.comp.project2411.bms.model.ReportData;
import hk.polyu.comp.project2411.bms.model.Reserves;
import hk.polyu.comp.project2411.bms.model.SearchCriteria;

public interface BMSMainInterface {

    // Administrator Functions

    /**
     * Creates a new banquet.
     *
     * @param banquet Banquet object containing the banquet information.
     * @return Banquet object including the generated BIN.
     * @throws SQLException If a database access error occurs.
     */
    Banquet createBanquet(Banquet banquet) throws SQLException;

    /**
     * Updates existing banquet information.
     *
     * @param banquet Banquet object containing the updated banquet information (must include BIN).
     * @return True if the update is successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean updateBanquet(Banquet banquet) throws SQLException;

    /**
     * Adds a meal to a specified banquet.
     *
     * @param banquetBIN The BIN of the banquet.
     * @param meal       Meal object to be added.
     * @return True if the meal is added successfully, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean addMealToBanquet(int banquetBIN, Meal meal) throws SQLException;

    /**
     * Retrieves the full information of an attendee by email.
     *
     * @param email The email address of the attendee.
     * @return AttendeeAccount object containing the attendee's information.
     * @throws SQLException If a database access error occurs.
     */
    AttendeeAccount getAttendeeByEmail(String email) throws SQLException;

    /**
     * Updates an attendee's registration data fields.
     *
     * @param email            The email address of the attendee.
     * @param registrationData Reserves object containing the updated registration data.
     * @return True if the update is successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean updateAttendeeRegistrationData(String email, Reserves registrationData) throws SQLException;

    /**
     * Generates a report on registration status.
     *
     * @return ReportData object containing registration status data.
     * @throws SQLException If a database access error occurs.
     */
    ReportData getRegistrationStatusReport() throws SQLException;

    /**
     * Generates a report on popular meals.
     *
     * @return ReportData object containing data on popular meals.
     * @throws SQLException If a database access error occurs.
     */
    ReportData getPopularMealsReport() throws SQLException;

    /**
     * Generates a report on attendance behavior.
     *
     * @return ReportData object containing attendance behavior data.
     * @throws SQLException If a database access error occurs.
     */
    ReportData getAttendanceBehaviorReport() throws SQLException;

    // Attendee Functions

    /**
     * Registers a new attendee account.
     *
     * @param attendee AttendeeAccount object containing the attendee's information.
     * @return True if the registration is successful, false otherwise.
     * @throws ValidationException If validation of the input data fails.
     * @throws SQLException        If a database access error occurs.
     */
    boolean registerAttendee(AttendeeAccount attendee) throws ValidationException, SQLException;

    /**
     * Authenticates a user with email and password.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @return AttendeeAccount object if the account is an attendee. AdminAccount object if the account is an administrator.
     * @throws AuthenticationException If authentication fails.
     * @throws SQLException            If a database access error occurs.
     */
    Account authenticateUser(String email, String password) throws AuthenticationException, SQLException;

    /**
     * Updates an attendee's personal information in their account profile.
     *
     * @param attendee AttendeeAccount object containing the updated information.
     * @return True if the update is successful, false otherwise.
     * @throws ValidationException If validation of the input data fails.
     * @throws SQLException        If a database access error occurs.
     */
    boolean updateAttendeeProfile(AttendeeAccount attendee) throws ValidationException, SQLException;

    /**
     * Retrieves a list of available banquets.
     *
     * @return List of Banquet objects that are available.
     * @throws SQLException If a database access error occurs.
     */
    List<Banquet> getAvailableBanquets() throws SQLException;

    /**
     * Registers an attendee for a banquet.
     *
     * @param attendeeEmail The email address of the attendee.
     * @param banquetBIN    The BIN of the banquet.
     * @param drinkChoice   The attendee's choice of drink.
     * @param mealChoice    The attendee's choice of meal.
     * @param remarks       Any additional remarks (e.g., seating preference).
     * @return RegistrationResult object containing the outcome of the registration.
     * @throws RegistrationException If registration fails (e.g., due to insufficient seats).
     * @throws SQLException          If a database access error occurs.
     */
    RegistrationResult registerForBanquet(String attendeeEmail, int banquetBIN, String drinkChoice, String mealChoice, String remarks) throws RegistrationException, SQLException;

    /**
     * Searches for banquets that the attendee has registered for, based on various criteria.
     *
     * @param attendeeEmail The email address of the attendee.
     * @param criteria      SearchCriteria object containing the search parameters.
     * @return List of Banquet objects that match the criteria.
     * @throws SQLException If a database access error occurs.
     */
    List<Banquet> searchRegisteredBanquets(String attendeeEmail, SearchCriteria criteria) throws SQLException;

    /**
     * Updates an attendee's registration information for a specific banquet.
     *
     * @param attendeeEmail  The email address of the attendee.
     * @param banquetBIN     The BIN of the banquet.
     * @param newDrinkChoice The new drink choice.
     * @param newMealChoice  The new meal choice.
     * @param newRemarks     The new remarks.
     * @return True if the update is successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean updateRegistration(String attendeeEmail, int banquetBIN, String newDrinkChoice, String newMealChoice, String newRemarks) throws SQLException;

}