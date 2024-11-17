package main.com.bms.exceptions;
// Exception class for registration errors (e.g., banquet is full)
public class RegistrationException extends Exception {
    public RegistrationException(String message) {
        super(message);
    }
}