package data.common.seeding;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.repositories.GenericRepository;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import data.repositories.IGenericRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GenericSeeder<T, TKey> implements ISeeder<T, TKey> {

    private static final Logger logger = LoggerFactory.getLogger(GenericSeeder.class);

    private final IGenericRepository<T, TKey> repository;
    private final String jsonFilePath;
    private final Class<T> entityType;
    private final Gson gson;

    /**
     * Constructs a new GenericSeeder with the given repository, JSON file path, and entity type.
     *
     * @param repository   The generic repository interface for database interactions.
     * @param jsonFilePath The path to the JSON file containing the data to seed.
     * @param entityType   The class type of the entity to seed.
     * @param gson         The Gson instance to be used for JSON parsing.
     */
    public GenericSeeder(
            IGenericRepository<T, TKey> repository,
            String jsonFilePath,
            Class<T> entityType,
            Gson gson) {
        this.repository = repository;
        this.jsonFilePath = jsonFilePath;
        this.entityType = entityType;
        this.gson = gson;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Void> seedAsync() {
        return CompletableFuture.supplyAsync(() -> repository.getAll())
                .thenCompose(entities -> {
                    if (entities.isEmpty()) {
                        return loadDataFromJson()
                                .thenCompose(newEntities -> insertEntitiesAsync(newEntities));
                    } else {
                        // Logging when the entities already exist
                        logger.info("{} already exists in the database. Skipping seeding.", entityType.getSimpleName());
                        return CompletableFuture.completedFuture(null);
                    }
                });
    }

    /**
     * Loads the data from the specified JSON file asynchronously.
     * This method parses the JSON file and maps it to a list of entity objects using Gson.
     *
     * @return A CompletableFuture containing the list of entities loaded from the JSON file.
     */
    private CompletableFuture<List<T>> loadDataFromJson() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Read the file content as a String
                String jsonContent = new String(Files.readAllBytes(new File(jsonFilePath).toPath()));

                // Use Gson to parse the JSON content into a list of entities
                Type listType = TypeToken.getParameterized(List.class, entityType).getType();
                return gson.fromJson(jsonContent, listType);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read data from JSON file", e);
            }
        });
    }

    /**
     * Inserts the loaded entities into the database asynchronously.
     *
     * @param newEntities The entities to be inserted.
     * @return A CompletableFuture that indicates when the insertion is complete.
     */
    private CompletableFuture<Void> insertEntitiesAsync(List<T> newEntities) {
        CompletableFuture<Void> insertFutures = newEntities.stream()
                .map(entity -> CompletableFuture.runAsync(() -> repository.add(entity)))
                .reduce(CompletableFuture::allOf)
                .orElse(CompletableFuture.completedFuture(null));

        return insertFutures.thenRun(() -> {
            // Logging after successful insertion
            logger.info("{} seeded successfully.", entityType.getSimpleName());
        });
    }
}
