package data.common.seeding;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gson adapter for serializing/deserializing {@link LocalDateTime} to/from JSON using ISO format.
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
        // Converts to ISO string (e.g., "2025-02-21T10:15:30") for Gson
        return localDateTime != null ? new JsonPrimitive(localDateTime.format(FORMATTER)) : JsonNull.INSTANCE;
    }

    @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        // Handle null JSON values
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return null;
        }
        //  Parses ISO string from JSON; H2 stores as TIMESTAMP
        return LocalDateTime.parse(jsonElement.getAsString(), FORMATTER);
    }
}