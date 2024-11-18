package main.java.com.project2411.bms.model;

// Class representing the criteria for searching registrations
public class SearchCriteria {
    private String date;
    private String banquetNamePart;
    private String attendeeType;

    // Constructors, getters, and setters omitted
    public SearchCriteria(String date, String banquetNamePart, String attendeeType) {
        this.date = date;
        this.banquetNamePart = banquetNamePart;
        this.attendeeType = attendeeType;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getBanquetNamePart() {
        return banquetNamePart;
    }
    public void setBanquetNamePart(String banquetNamePart) {
        this.banquetNamePart = banquetNamePart;
    }
    public String getAttendeeType() {
        return attendeeType;
    }
}