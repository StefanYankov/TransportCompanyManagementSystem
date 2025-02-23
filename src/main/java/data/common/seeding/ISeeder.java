package data.common.seeding;

import java.util.concurrent.CompletableFuture;

/**
 * Defines a contract for seeders responsible for populating the database with initial data.
 * Supports both synchronous and asynchronous seeding from external sources (e.g., JSON files).
 *
 * @param <T>    the type of entity to seed
 * @param <TKey> the type of the entity's primary key
 */
public interface ISeeder<T, TKey> {
    /**
     * Seeds data into the database synchronously.
     * This method will check if the table for the entity is empty, and if so, will populate it with data.
     * The data will be read from a JSON file.
     */
    public void seed();

    /**
     * Seeds data into the database asynchronously.
     * This method will check if the table for the entity is empty, and if so, will populate it with data.
     * The data will be read from a JSON file.
     *
     * @return A CompletableFuture that indicates when the seeding process is complete.
     */
    public CompletableFuture<Void> seedAsync();
}
