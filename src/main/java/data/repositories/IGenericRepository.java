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
     * Retrieves all entities with pagination.
     *
     * @param page     The page number (0-based).
     * @param pageSize The number of results per page.
     * @return A paginated list of entities.
     */
    public List<T> getAll(int page, int pageSize);

    /**
     * Retrieves all entities with pagination and optional relation fetching.
     *
     * @param page          The page number (0-based).
     * @param pageSize      The number of results per page.
     * @param fetchRelations A boolean flag indicating whether to fetch related entities.
     * @return A paginated list of entities.
     */
    public List<T> getAll(int page, int pageSize, boolean fetchRelations);

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
     * @param conditions A map of field names and their expected values for filtering.
     * @param orderBy    The field name to sort by.
     * @param ascending  Whether the results should be sorted in ascending order.
     * @return A list of entities matching the criteria, ordered as specified.
     */
    public List<T> findByCriteria(Map<String, Object> conditions, String orderBy, boolean ascending);

    /**
     * Retrieves all entities sorted by the specified field.
     *
     * @param sortField The field to sort by.
     * @param ascending Whether to sort in ascending order.
     * @return A list of sorted entities.
     */
    public List<T> findAllSorted(String sortField, boolean ascending);

    /**
     * Asynchronously finds an entity by its ID.
     *
     * @param id The ID of the entity.
     * @return A CompletableFuture containing an Optional entity.
     */
    public CompletableFuture<Optional<T>> getByIdAsync(TKey id);

    /**
     * Asynchronously retrieves all entities.
     *
     * @return A CompletableFuture containing a list of all entities.
     */
    public CompletableFuture<List<T>> getAllAsync();

    /**
     * Asynchronously retrieves all entities with pagination.
     *
     * @param page     The page number (0-based).
     * @param pageSize The number of results per page.
     * @return A CompletableFuture containing a paginated list of entities.
     */
    public CompletableFuture<List<T>> getAllAsync(int page, int pageSize);

    /**
     * Asynchronously retrieves all entities with pagination and optional relation fetching.
     *
     * @param page          The page number (0-based).
     * @param pageSize      The number of results per page.
     * @param fetchRelations A boolean flag indicating whether to fetch related entities.
     * @return A CompletableFuture containing a paginated list of entities.
     */
    public CompletableFuture<List<T>> getAllAsync(int page, int pageSize, boolean fetchRelations);

    /**
     * Asynchronously adds a new entity.
     *
     * @param entity The entity to save.
     * @return A CompletableFuture representing the completion of the operation.
     */
    public CompletableFuture<Void> addAsync(T entity);

    /**
     * Asynchronously updates an existing entity.
     *
     * @param entity The entity to update.
     * @return A CompletableFuture representing the completion of the operation.
     */
    public CompletableFuture<Void> updateAsync(T entity);

    /**
     * Asynchronously deletes an entity.
     *
     * @param entity The entity to delete.
     * @return A CompletableFuture representing the completion of the operation.
     */
    public CompletableFuture<Void> deleteAsync(T entity);

    /**
     * Asynchronously finds entities that match the specified criteria.
     *
     * @param conditions A map of field names and their expected values for filtering.
     * @param orderBy    The field name to sort by.
     * @param ascending  Whether the results should be sorted in ascending order.
     * @return A CompletableFuture containing a list of entities matching the criteria.
     */
    public CompletableFuture<List<T>> findByCriteriaAsync(Map<String, Object> conditions, String orderBy, boolean ascending);

    /**
     * Asynchronously retrieves all entities sorted by the specified field.
     *
     * @param sortField The field to sort by.
     * @param ascending Whether to sort in ascending order.
     * @return A CompletableFuture containing a list of sorted entities.
     */
    public CompletableFuture<List<T>> findAllSortedAsync(String sortField, boolean ascending);
}
