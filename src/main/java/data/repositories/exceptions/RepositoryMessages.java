package data.repositories.exceptions;

import java.text.MessageFormat;

public final class RepositoryMessages {
    // Messages for RepositoryException
    public static final String NULL_ENTITY = "Cannot {0} null entity of type {1}";
    public static final String TRANSACTION_FAILED = "Failed to {0} entity of type {1}";
    public static final String SESSION_OPEN_FAILED = "Failed to open session for {0} operation on {1}";
    public static final String NULL_ID = "Cannot retrieve entity with null ID for type {0}";
    public static final String ENTITY_NOT_FOUND = "Entity of type {0} with ID {1} not found";
    public static final String GET_BY_ID_FAILED = "Failed to retrieve entity of type {0} with ID {1}";
    public static final String GET_ALL_FAILED = "Failed to retrieve all entities of type {0}";
    public static final String FIND_BY_CRITERIA_FAILED = "Failed to find entities of type {0} by criteria";
    public static final String FIND_WITH_AGGREGATION_FAILED = "Failed to find entities of type {0} with aggregation";
    public static final String FIND_WITH_JOIN_FAILED = "Failed to find entities of type {0} with join";

    // Private constructor to prevent instantiation
    private RepositoryMessages() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    // Helper method to format messages (optional, for convenience)
    public static String format(String pattern, Object... arguments) {
        return MessageFormat.format(pattern, arguments);
    }
}