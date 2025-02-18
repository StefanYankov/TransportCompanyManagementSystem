package data.common.seeding;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for seeders that are responsible for populating the database with initial data.
 * This interface is designed to handle asynchronous seeding operations.
 *
 * @param <T>  The type of the entity to seed.
 * @param <TKey> The type of the primary key for the entity.
 */
public interface ISeeder<T, TKey> {
    /**
     * Seeds data asynchronously into the database.
     * This method will check if the table for the entity is empty, and if so, will populate it with data.
     * The data will be read from a JSON file.
     *
     * @return A CompletableFuture that indicates when the seeding process is complete.
     */
    public CompletableFuture<Void> seedAsync();
}
