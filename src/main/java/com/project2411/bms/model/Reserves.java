package com.project2411.bms.model;
import java.sql.Timestamp;
// Class representing a Reservation (Registration)
public class Reserves {
    private String attendeeEmail;
    private int banquetBIN;
    private int seatNo;
    private Timestamp regTime; // Registration time
    private String drinkChoice;
    private String mealChoice;
    private String remarks;

    // Constructors, getters, and setters omitted
    public Reserves(String attendeeEmail, int banquetBIN, int seatNo, Timestamp regTime,
                    String drinkChoice, String mealChoice, String remarks) {
        this.attendeeEmail = attendeeEmail;
        this.banquetBIN = banquetBIN;
        this.seatNo = seatNo;
        this.regTime = regTime;
        this.drinkChoice = drinkChoice;
        this.mealChoice = mealChoice;
        this.remarks = remarks;
    }

    /*Check if an account is valid or not*/
    public boolean isValidAccount(AttendeeAccount account) {
        return (isValidEmail(account.getEmail()) && isValidNumber(account.getMobileNo())
                && isValidName(account.getFirstName(), account.getLastName()));
    }
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
    private boolean isValidNumber(String number) {
        if (number == null || number.isEmpty()) return false;
        String numberRegex = "^[0-9]{8}$";
        return number.matches(numberRegex);
    }
    private boolean isValidName(String firstName, String lastName) {
        if (firstName == null || lastName == null || firstName.isEmpty() || lastName.isEmpty()) return false;
        String nameRegex = "^[A-Za-z]+$";
        return firstName.matches(nameRegex) && lastName.matches(nameRegex);
    }
    /*Getters*/
    public String getAttendeeEmail() {
        return attendeeEmail;
    }
    public int getBanquetBIN() {
        return banquetBIN;
    }
    public int getSeatNo() {
        return seatNo;
    }
    public Timestamp getRegTime() {
        return regTime;
    }
    public String getDrinkChoice() {
        return drinkChoice;
    }
    public String getMealChoice() {
        return mealChoice;
    }
    public String getRemarks() {
        return remarks;
    }
}
