package hk.polyu.comp.project2411.bms.model;
import java.util.Map;

public class AttendeeAccount implements  Account {
    private String originalEmail;
    private String role = "user";
    private String email; // Account ID
    private String firstName;
    private String lastName;
    private String address;
    private String type; // Attendee Type (e.g., staff, student)
    private String password;
    private String mobileNo;
    private String organization; // Affiliated Organization

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

    public AttendeeAccount(Map<String, Object> row) {
        Map <String, Object> lowerCaseRow = Utils.getLowerCasedMap(row);
        this.email = (String) lowerCaseRow.get("email");
        this.firstName = (String) lowerCaseRow.get("firstname");
        this.lastName = (String) lowerCaseRow.get("lastname");
        this.address = (String) lowerCaseRow.get("address");
        this.type = (String) lowerCaseRow.get("type");
        this.password = (String) lowerCaseRow.get("password");
        this.mobileNo = (String) lowerCaseRow.get("mobileno");
        this.organization = (String) lowerCaseRow.get("organization");
    }

    // Getters
    public String getRole() {
        return role;
    }
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

    public String getOriginalEmail() {
        return originalEmail;
    }
}
