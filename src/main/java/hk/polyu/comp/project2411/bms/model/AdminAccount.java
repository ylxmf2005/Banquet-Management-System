package hk.polyu.comp.project2411.bms.model;

public class AdminAccount implements Account {
    private String email;
    private String password;

    public AdminAccount(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}
