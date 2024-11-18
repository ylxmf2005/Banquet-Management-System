package com.project2411.bms.model;

// Class representing an Attendee Account
public class AttendeeAccount {
    private String email; // Account ID
    private String firstName;
    private String lastName;
    private String address;
    private String type; // Attendee Type (e.g., staff, student)
    private String password;
    private String mobileNo;
    private String organization; // Affiliated Organization

    // Constructors, getters, and setters omitted
    public AttendeeAccount(String email, String firstName, String lastName, String address, String type,
                           String password, String mobileNo, String organization) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.type = type;
        this.password = password;
        this.mobileNo = mobileNo;
        this.organization = organization;
    }

    /*Getters*/
    public String getEmail() {
        return email;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getAddress() {
        return address;
    }
    public String getType() {
        return type;
    }
    public String getPassword() {
        return password;
    }
    public String getMobileNo() {
        return mobileNo;
    }
    public String getOrganization() {
        return organization;
    }
}
