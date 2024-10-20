package org.example.hotel_task.exception;

public class InvalidEmailAddressException extends RuntimeException{
    public InvalidEmailAddressException(String message) {
        super(message);
    }

    public InvalidEmailAddressException(String message, Throwable cause) {
        super(message, cause);
    }
}
