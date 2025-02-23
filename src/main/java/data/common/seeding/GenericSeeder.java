package data.common.seeding;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import data.repositories.IGenericRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic implementation of {@link ISeeder} for seeding entities from JSON files into the database.
 * Checks if the database is empty for the entity type and seeds data only on first run.
 *
 * @param <T>    the type of entity to seed
 * @param <TKey> the type of the entity's primary key
 */
public class GenericSeeder<T, TKey> implements ISeeder<T, TKey> {

    private static final Logger logger = LoggerFactory.getLogger(GenericSeeder.class);
    private static final int INITIAL_CHECK_SIZE = 1; // Smaller page size for faster empty check

    private final IGenericRepository<T, TKey> repository;
    private final String jsonFilePath;
    private final Class<T> entityType;
    private final Gson gson;

    /**
     * Constructs a new GenericSeeder with the specified dependencies.
     *
     * @param repository   the repository for database operations
     * @param jsonFilePath absolute path to the JSON file containing seed data
     * @param entityType   the class of the entity to seed (e.g., {@code TransportCompany.class})
     * @param gson         Gson instance for JSON deserialization
     * @throws NullPointerException if any parameter is null
     */
    public GenericSeeder(
            IGenericRepository<T, TKey> repository,
            String jsonFilePath,
            Class<T> entityType,
            Gson gson) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.jsonFilePath = Objects.requireNonNull(jsonFilePath, "JSON file path cannot be null");
        this.entityType = Objects.requireNonNull(entityType, "Entity type cannot be null");
        this.gson = Objects.requireNonNull(gson, "Gson instance cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    public void seed() {
        logger.debug("Checking if {} table is empty", entityType.getSimpleName());

        List<T> entities = repository.getAll(0, 10,  "name", true);

        if (entities.isEmpty()) {
            try {
                List<T> newEntities = loadDataFromJson();
                if (newEntities == null || newEntities.isEmpty()) {
                    // Handle empty/null JSON data gracefully
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
    @Override
    public CompletableFuture<Void> seedAsync() {
        logger.debug("Asynchronously checking if {} table is empty", entityType.getSimpleName());
        return repository.getAllAsync(0, INITIAL_CHECK_SIZE, "id", true)
                .thenCompose(entities -> {
                    if (entities.isEmpty()) {
                        logger.debug("No {} entities found, proceeding with async seeding", entityType.getSimpleName());
                        return loadDataFromJsonAsync()
                                .thenCompose(newEntities -> {
                                    if (newEntities == null || newEntities.isEmpty()) {
                                        logger.warn("No data to seed for {} in file {}", entityType.getSimpleName(), jsonFilePath);
                                        return CompletableFuture.completedFuture(null);
                                    }
                                    return insertEntitiesAsync(newEntities);
                                });
                    } else {
                        logger.debug("Skipping async seeding for {} - data exists", entityType.getSimpleName());
                        return CompletableFuture.completedFuture(null);
                    }
                })
                .exceptionally(throwable -> {
                    logger.error("Async seeding failed for {} from {}", entityType.getSimpleName(), jsonFilePath, throwable);
                    throw new RuntimeException("Async seeding failed: " + jsonFilePath, throwable);
                });
    }


    /**
     * Loads entity data from the specified JSON file synchronously.
     *
     * @return a list of entities parsed from JSON, or null if the file is invalid/empty
     * @throws IOException if file reading fails
     */
    private List<T> loadDataFromJson() throws IOException {
        //  Using File assumes an absolute path
        File jsonFile = new File(jsonFilePath);
        if (!jsonFile.exists() || !jsonFile.isFile()) {
            throw new IOException("JSON seed file not found or invalid: " + jsonFilePath);
        }
        String jsonContent = Files.readString(jsonFile.toPath()); // My tweak: Simpler file reading
        Type listType = TypeToken.getParameterized(List.class, entityType).getType();
        return gson.fromJson(jsonContent, listType);
    }

    /**
     * Loads entity data from the specified JSON file asynchronously.
     *
     * @return a CompletableFuture containing the list of entities parsed from JSON
     */
    private CompletableFuture<List<T>> loadDataFromJsonAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File jsonFile = new File(jsonFilePath);
                if (!jsonFile.exists() || !jsonFile.isFile()) {
                    throw new IOException("JSON seed file not found or invalid: " + jsonFilePath);
                }
                String jsonContent = Files.readString(jsonFile.toPath());
                Type listType = TypeToken.getParameterized(List.class, entityType).getType();
                return gson.fromJson(jsonContent, listType);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load JSON: " + jsonFilePath, e);
            }
        });
    }

    /**
     * Inserts entities into the database synchronously.
     *
     * @param newEntities the list of entities to insert
     */
    private void insertEntities(List<T> newEntities) {
        // Synchronous insert is fine for small datasets;
        // TODO: implement batching for large ones
        for (T entity : newEntities) {
            repository.create(entity);
        }
    }

    /**
     * Inserts entities into the database asynchronously.
     *
     * @param newEntities the list of entities to insert
     * @return a CompletableFuture indicating when insertion is complete
     */
    private CompletableFuture<Void> insertEntitiesAsync(List<T> newEntities) {
        List<CompletableFuture<T>> futures = newEntities.stream()
                .map(entity -> repository.createAsync(entity))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> logger.info("Seeded {} {} entities asynchronously from {}",
                        newEntities.size(), entityType.getSimpleName(), jsonFilePath));
    }
}
