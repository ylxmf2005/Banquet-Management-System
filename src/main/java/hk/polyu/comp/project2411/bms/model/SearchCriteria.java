package hk.polyu.comp.project2411.bms.model;

public class SearchCriteria {
    private String banquetName;
    private String startDate;
    private String endDate;

    public String getBanquetName() {
        return banquetName;
    }

    public void setBanquetName(String banquetName) {
        this.banquetName = banquetName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}