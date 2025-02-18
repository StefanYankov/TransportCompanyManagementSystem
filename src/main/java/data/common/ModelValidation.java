package data.common;

public class ModelValidation {


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
    public static final String INVALID_EMAIL_MESSAGE = "Email address is invalid";

    public static final int MINIMUM_ALLOWED_DIMENSIONS = 0;
    public static final String INVALID_ALLOWED_DIMENSIONS_MESSAGE = "Allowed Dimensions, must be greater than" + MINIMUM_ALLOWED_DIMENSIONS;

    public static final int DESCRIPTION_MAXIMUM_LENGTH = 255;
    public static final String DESCRIPTION_ABOVE_MAXIMUM_LENGTH = "Description is too long! Maximum allowed length is " + DESCRIPTION_MAXIMUM_LENGTH;

    public static final String MINIMUM_ALLOWED_PRICE = "0.0";
    public static final String PRICE_SHOULD_BE_A_POSITIVE_VALUE = "Price should be positive";

    public static final int MINIMUM_NUMBER_OF_PASSENGERS = 0;
    public static final String PASSENGERS_MUST_BE_GREATER_THAN = "Passengers must be greater than " + MINIMUM_NUMBER_OF_PASSENGERS;
    public static final String TRANSPORT_COMPANY_IS_REQUIRED = "Transport company is required.";


    public static final String MINIMUM_ALLOWED_LUGGAGE_CAPACITY = "0.0";
    public static final String INVALID_MINIMUM_LUGGAGE_CAPACITY = "Luggage capacity needs to be greater than" + MINIMUM_ALLOWED_LUGGAGE_CAPACITY;


    private ModelValidation() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
