package main.com.bms.model;

// Class representing the result of a Registration attempt
public class RegistrationResult {
    private boolean success;
    private String message;

    // Constructors, getters, and setters
    public RegistrationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters and setters omitted
}