package data.common;

public class ModelValidation {

    public static final String ID_REQUIRED = "ID is required";

    public static final int MIN_NAME_LENGTH = 1;
    public static final int MAX_NAME_LENGTH = 50;
    public static final String FIRST_NAME_IS_REQUIRED = "First name is required.";
    public static final String LAST_NAME_IS_REQUIRED = "Last name is required.";
    public static final String NAME_IS_REQUIRED = "Name is required.";

    public static final int MIN_ADDRESS_LENGTH = 1;
    public static final int MAX_ADDRESS_LENGTH = 125;
    public static final String ADDRESS_IS_REQUIRED = "Address is required.";

    public static final int DECIMAL_PRECISION = 18;
    public static final int DECIMAL_SCALE = 2;
    public static final String MINIMUM_ALLOWED_SALARY = "0.0";
    public static final String SALARY_CANNOT_BE_A_NEGATIVE_VALUE = "Salary cannot be negative value";
    public static final String SALARY_IS_REQUIRED = "Salary is required.";

    public static final int TELEPHONE_LENGTH = 14;
    public static final String TELEPHONE_CANNOT_BE_BLANK = "Telephone number cannot be blank";
    public static final String TELEPHONE_NUMBER_LENGTH_EXCEEDED = "Telephone number length cannot exceed " + TELEPHONE_LENGTH;
    public static final String EMAIL_CANNOT_BE_BLANK = "Email cannot be blank";
    public static final String INVALID_EMAIL_MESSAGE = "Email address is invalid";

    public static final int MINIMUM_ALLOWED_DIMENSIONS = 0;
    public static final String INVALID_ALLOWED_DIMENSIONS_MESSAGE = "Allowed Dimensions, must be greater than" + MINIMUM_ALLOWED_DIMENSIONS;

    public static final int DESCRIPTION_MAXIMUM_LENGTH = 255;
    public static final String DESCRIPTION_ABOVE_MAXIMUM_LENGTH = "Description is too long! Maximum allowed length is " + DESCRIPTION_MAXIMUM_LENGTH;

    public static final String MINIMUM_ALLOWED_PRICE = "0.0";
    public static final String PRICE_SHOULD_BE_A_POSITIVE_VALUE = "Price should be positive";

    public static final int MINIMUM_NUMBER_OF_PASSENGERS = 0;
    public static final String PASSENGERS_MUST_BE_GREATER_THAN = "Passengers must be greater than " + MINIMUM_NUMBER_OF_PASSENGERS;
    public static final String MAX_PASSENGER_CAPACITY_REQUIRED = "Maximum passenger capacity is required";
    public static final String PASSENGER_OVERHEAD_STORAGE_CANNOT_BE_NULL = "Password overhead cannot be null";

    public static final String TRANSPORT_COMPANY_IS_REQUIRED = "Transport company is required.";

    public static final String MINIMUM_ALLOWED_LUGGAGE_CAPACITY = "0.0";
    public static final String INVALID_MINIMUM_LUGGAGE_CAPACITY = "Luggage capacity needs to be greater than " + MINIMUM_ALLOWED_LUGGAGE_CAPACITY;

    public static final String QUALIFICATION_NAME_NOT_BLANK = "Qualification name cannot be blank";
    public static final int QUALIFICATION_NAME_MAX_NAME_LENGTH = 50;
    public static final String QUALIFICATION_NAME_LENGTH_EXCEEDED = "Qualification name cannot exceed "+ QUALIFICATION_NAME_MAX_NAME_LENGTH;
    public static final String QUALIFICATION_DESCRIPTION_NOT_BLANK = "Qualification description cannot be blank.";
    public static final int MAX_DESCRIPTION_LENGTH = 255;
    public static final String QUALIFICATION_DESCRIPTION_LENGTH_EXCEEDED = "Qualification description cannot exceed "+ MAX_DESCRIPTION_LENGTH;

    public static final String CLIENT_NAME_NOT_BLANK = "Client name cannot be blank";
    public static final int CLIENT_NAME_MAX_NAME_LENGTH = 50;
    public static final String CLIENT_NAME_LENGTH_EXCEEDED = "Client name cannot exceed " + CLIENT_NAME_MAX_NAME_LENGTH;

    public static final String STARTING_DATE_REQUIRED = "Starting date is required";
    public static final String STARTING_DATE_MUST_BE_FUTURE_OR_PRESENT = "Starting date must be in the future or present";
    public static final String ENDING_DATE_REQUIRED = "Ending date is required";
    public static final String ENDING_DATE_MUST_BE_FUTURE = "Ending date must be in the future";

    public static final String PRICE_REQUIRED = "Price is required";

    public static final int MAX_REGISTRATION_PLATE_LENGTH = 10;
    public static final String REGISTRATION_PLATE_REQUIRED = "Registration plate is required";
    public static final String REGISTRATION_PLATE_TOO_LONG = "Registration plate is too long";

    public static final String MIN_CARGO_CAPACITY = "0.0";
    public static final String INVALID_CARGO_MAX_CAPACITY = "Maximum cargo capacity must be greater than " + MIN_CARGO_CAPACITY;
    public static final String MAX_CARGO_CAPACITY_REQUIRED = "Maximum cargo capacity is required";
    public static final String CURRENT_CARGO_CAPACITY_REQUIRED = "Current cargo capacity is required";
    public static final String CARGO_TYPE_REQUIRED = "Cargo type is required";
    public static final String LUGGAGE_CAPACITY_REQUIRED = "Luggage capacity is required";
    public static final String DESTINATION_CANNOT_BLANK = "Destination cannot be blank";
    public static final String DESTINATION_ADDRESS_MAX_LENGTH_EXCEEDED = "Destination address length cannot exceed " + MAX_ADDRESS_LENGTH ;

    public static final String DIMENSION_REQUIRED = "Dimension is required.";
    public static final String RESTROOM_FLAG_IS_REQUIRED = "Restroom flag is required.";


    private ModelValidation() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
