package hk.polyu.comp.project2411.bms.model;

import java.sql.Timestamp;

public class SearchCriteria {
    private Timestamp date;
    private String banquetNamePart;
    private String attendeeType;

    public SearchCriteria(Timestamp date, String banquetNamePart, String attendeeType) {
        this.date = date;
        this.banquetNamePart = banquetNamePart;
        this.attendeeType = attendeeType;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
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