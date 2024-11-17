package main.exceptions;

// Exception class for authentication errors
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}