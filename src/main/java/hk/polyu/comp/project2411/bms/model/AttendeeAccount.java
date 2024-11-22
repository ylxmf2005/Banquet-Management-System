package hk.polyu.comp.project2411.bms.model;
import java.util.Map;

public class AttendeeAccount {
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
        this(
            (String) row.get("Email"),
            (String) row.get("FirstName"),
            (String) row.get("LastName"),
            (String) row.get("Address"),
            (String) row.get("Type"),
            (String) row.get("Password"),
            (String) row.get("MobileNo"),
            (String) row.get("Organization")
        );
    }

    // Getters
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
