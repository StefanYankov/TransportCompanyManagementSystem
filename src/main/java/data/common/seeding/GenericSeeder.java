package data.common.seeding;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.repositories.IGenericRepository;
import jakarta.validation.Validator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A generic seeder that populates a database table with initial data from a JSON file.
 * <p>
 * This class checks if the database table is empty using a limited query (first 10 records).
 * If empty, it reads entities from a specified JSON file and inserts them into the database
 * using the provided repository. It supports any entity type and key type, making it reusable
 * across different domain models. Logging is integrated to track seeding progress and errors.
 * The JSON file can be specified as an absolute path (e.g., from filesystem) or a classpath resource.
 * </p>
 * @param <T> the type of entity to seed
 * @param <TKey> the type of the entity's primary key
 */
public class GenericSeeder<T, TKey> implements ISeeder<T, TKey> {
    private static final Logger logger = LoggerFactory.getLogger(GenericSeeder.class);

    private final IGenericRepository<T, TKey> repository;
    private final String jsonFilePath;
    private final Class<T> entityType;
    private final Gson gson;
    private final Validator validator;

    /**
     * Constructs a new GenericSeeder with the specified dependencies.
     *
     * @param repository the repository to interact with the database, must not be null
     * @param jsonFilePath the path to the JSON seed file (absolute or classpath-relative), must not be null
     * @param entityType the class of the entity to seed, must not be null
     * @param gson the Gson instance for JSON deserialization, must not be null
     * @param validator the validator for entity validation (optional, can be null if no validation is desired)
     * @throws NullPointerException if repository, jsonFilePath, entityType, or gson is null
     */
    public GenericSeeder(
            IGenericRepository<T, TKey> repository,
            String jsonFilePath,
            Class<T> entityType,
            Gson gson,
            Validator validator) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.jsonFilePath = Objects.requireNonNull(jsonFilePath, "JSON file path cannot be null");
        this.entityType = Objects.requireNonNull(entityType, "Entity type cannot be null");
        this.gson = Objects.requireNonNull(gson, "Gson instance cannot be null");
        this.validator = validator; // Can be null if not used yet
    }

    /** {@inheritDoc} */
    @Override
    public void seed() {
        logger.debug("Checking if {} table is empty", entityType.getSimpleName());

        List<T> entities = repository.getAll(0, 10, "name", true);
        if (entities.isEmpty()) {
            try {
                List<T> newEntities = loadDataFromJson();
                if (newEntities == null || newEntities.isEmpty()) {
                    logger.warn("No data to seed for {} in file {}", entityType.getSimpleName(), jsonFilePath);
                    return;
                }
                insertEntities(newEntities);
                logger.info("Seeded {} {} entities from {}", newEntities.size(), entityType.getSimpleName(), jsonFilePath);
            } catch (IOException e) {
                logger.error("Failed to seed {} from {}", entityType.getSimpleName(), jsonFilePath, e);
                throw new RuntimeException("Failed to seed data from JSON: " + jsonFilePath, e);
            }
        } else {
            logger.debug("Skipping seeding for {} - database already contains data", entityType.getSimpleName());
        }
    }

    /** {@inheritDoc} */
    protected List<T> loadDataFromJson() throws IOException {
        // First try absolute file path (for Main method compatibility)
        File jsonFile = new File(jsonFilePath);
        if (jsonFile.exists() && jsonFile.isFile()) {
            String jsonContent = Files.readString(jsonFile.toPath());
            Type listType = TypeToken.getParameterized(List.class, entityType).getType();
            return gson.fromJson(jsonContent, listType);
        }
        // Fallback to classpath resource
        var resourceStream = getClass().getClassLoader().getResourceAsStream(jsonFilePath);
        if (resourceStream == null) {
            throw new IOException("JSON seed file not found as file or classpath resource: " + jsonFilePath);
        }
        try (var inputStream = resourceStream) {
            String jsonContent = new String(inputStream.readAllBytes());
            Type listType = TypeToken.getParameterized(List.class, entityType).getType();
            return gson.fromJson(jsonContent, listType);
        }
    }

    /** {@inheritDoc} */
    protected void insertEntities(List<T> newEntities) {
        for (T entity : newEntities) {
            if (validator != null) {
                Set<ConstraintViolation<T>> violations = validator.validate(entity);
                if (!violations.isEmpty()) {
                    logger.warn("Skipping invalid entity {} due to validation errors: {}", entity, violations);
                    continue;
                }
            }
            try {
                repository.create(entity);
            } catch (Exception e) {
                logger.error("Failed to insert entity {}: {}", entity, e.getMessage(), e);
                throw e;
            }
        }
    }
}