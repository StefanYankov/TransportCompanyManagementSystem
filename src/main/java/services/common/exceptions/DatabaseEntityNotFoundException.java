package services.common.exceptions;

public class DatabaseEntityNotFoundException extends RuntimeException {

    public DatabaseEntityNotFoundException() {
    }

    public DatabaseEntityNotFoundException(String message) {
        super(message);
    }
}
