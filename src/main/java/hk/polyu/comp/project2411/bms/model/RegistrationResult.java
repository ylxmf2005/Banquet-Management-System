package hk.polyu.comp.project2411.bms.model;

// Class representing the result of a Registration attempt
public class RegistrationResult {
    private boolean success;
    private String message;

    // Constructors, getters, and setters
    public RegistrationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}