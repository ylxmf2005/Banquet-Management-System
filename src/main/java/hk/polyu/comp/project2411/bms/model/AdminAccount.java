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
        Map<String, Object> lowerCaseRow = Utils.getLowerCasedMap(row);
        this.email = (String) lowerCaseRow.get("email");
        this.password = (String) lowerCaseRow.get("passowrd");
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}
