package hk.polyu.comp.project2411.bms.model;

import java.util.Map;
public class AdminAccount implements Account {
    private String role = "admin";
    private String email;
    private String password;

    public AdminAccount(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AdminAccount(Map<String, Object> row) {
        this.email = (String) row.get("Email");
        this.password = (String) row.get("Password");
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}
