package data.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Generic repository interface defining CRUD operations.
 *
 * @param <T>    Entity type
 * @param <TKey> Entity primary key type
 */
public interface IGenericRepository<T, TKey> {

    /**
     * Finds an entity by its ID.
     *
     * @param id the primary key of the entity
     * @return an Optional containing the entity if found, otherwise empty
     */
    public Optional<T> getById(TKey id);

    /**
     * Retrieves all entities.
     *
     * @return a list of all entities
     */
    public List<T> getAll();

    /**
     * Adds a new entity.
     *
     * @param entity the entity to save
     */
    public void add(T entity);

    /**
     * Updates an existing entity.
     *
     * @param entity The entity to update.
     */
    public void update(T entity);

    /**
     * Deletes an entity.
     *
     * @param entity The entity to delete.
     */
    public void delete(T entity);

    /**
     * Finds entities that match the specified criteria.
     *
     * @param conditions A map of field names and their expected values for the WHERE clause.
     * @return A list of entities matching the criteria.
     */
    public List<T> findByCriteria(Map<String, Object> conditions);

    /**
     * Retrieves all entities sorted by the specified field.
     *
     * @param sortField The field to sort by.
     * @param ascending Whether to sort in ascending order.
     * @return A list of entities sorted by the specified field.
     */
    public List<T> findAllSorted(String sortField, boolean ascending);

    /**
     * Asynchronously finds an entity by its ID.
     *
     * @param id The ID of the entity.
     * @return A CompletableFuture containing an Optional entity.
     */
    CompletableFuture<Optional<T>> getByIdAsync(TKey id);

    /**
     * Asynchronously retrieves all entities.
     *
     * @return A CompletableFuture containing a list of all entities.
     */
    CompletableFuture<List<T>> getAllAsync();

    /**
     * Asynchronously adds a new entity.
     *
     * @param entity The entity to save.
     * @return A CompletableFuture representing the completion of the operation.
     */
    CompletableFuture<Void> addAsync(T entity);

    /**
     * Asynchronously updates an existing entity.
     *
     * @param entity The entity to update.
     * @return A CompletableFuture representing the completion of the operation.
     */
    CompletableFuture<Void> updateAsync(T entity);

    /**
     * Asynchronously deletes an entity.
     *
     * @param entity The entity to delete.
     * @return A CompletableFuture representing the completion of the operation.
     */
    CompletableFuture<Void> deleteAsync(T entity);
}
