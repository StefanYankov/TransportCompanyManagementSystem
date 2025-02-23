package services.common.exceptions;

public class ExceptionMessages {

    public static final String TRANSPORT_CARGO_SERVICE_NOT_FOUND = "TransportCargoService with an id {0} not found.";
    public static final String TRANSPORT_PASSENGER_SERVICE_NOT_FOUND = "TransportPassengerService with an id {0} not found.";
    public static final String TRANSPORT_COMPANY_NAME_ALREADY_EXISTS = "A transport company with the name {0} already exists.";

    private ExceptionMessages() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
