package data.common.seeding;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Gson adapter for serializing/deserializing {@link LocalDate} to/from JSON using ISO format.
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext context) {
        // Converts to ISO string (e.g., "2025-02-21") for Gson
        return localDate != null ? new JsonPrimitive(localDate.format(FORMATTER)) : JsonNull.INSTANCE;
    }

    @Override
    public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return null;
        }
        // Parses ISO string from JSON; H2 stores as DATE
        return LocalDate.parse(jsonElement.getAsString(), FORMATTER);
    }
}