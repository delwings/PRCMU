package edu.unimagdalena.RCMU.api.error;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
