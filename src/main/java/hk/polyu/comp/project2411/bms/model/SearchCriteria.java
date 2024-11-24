package hk.polyu.comp.project2411.bms.model;

import java.sql.Timestamp;

public class SearchCriteria {
    private Timestamp startDate;
    private Timestamp endDate;
    private String banquetNamePart;
    // private String attendeeType;

    public SearchCriteria(Timestamp startDate, Timestamp endDate, String banquetNamePart) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.banquetNamePart = "%" + banquetNamePart + "%";
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getBanquetNamePart() {
        return banquetNamePart;
    }

    public void setBanquetNamePart(String banquetNamePart) {
        this.banquetNamePart = "%" + banquetNamePart + "%";
    }

}