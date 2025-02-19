package data.repositories;

import jakarta.persistence.criteria.JoinType;

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
     * Asynchronously finds an entity by its ID.
     *
     * @param id The ID of the entity.
     * @return A CompletableFuture containing an Optional entity.
     */
    public CompletableFuture<Optional<T>> getByIdAsync(TKey id);

    /**
     * Retrieves all entities with pagination and optional relation fetching.
     *
     * @param page           The page number (0-based).
     * @param pageSize       The number of results per page.
     * @param fetchRelations A boolean flag indicating whether to fetch related entities.
     * @param orderBy        Name of the column that you want to order by
     * @param ascending      If {@code true} the order will be ascending
     * @return A paginated {@link List<T>}  of entities.
     */
    public List<T> getAll(int page,
                          int pageSize,
                          Map<String, JoinType> fetchRelations,
                          String orderBy,
                          boolean ascending);

    /**
     * Asynchronously retrieves all entities with pagination and optional relation fetching.
     *
     * @param page           The page number (0-based).
     * @param pageSize       The number of results per page.
     * @param fetchRelations A boolean flag indicating whether to fetch related entities.
     * @param orderBy        Name of the column that you want to order by
     * @param ascending      If {@code true} the order will be ascending
     * @return A CompletableFuture containing a paginated list of entities.
     */
    public CompletableFuture<List<T>> getAllAsync(
            int page,
            int pageSize,
            Map<String, JoinType> fetchRelations,
            String orderBy,
            boolean ascending);

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
     * Finds entities that match the specified criteria.
     *
     * @param conditions A map of field names and their expected values for filtering.
     * @param orderByField    The field name to sort by.
     * @param ascending  Whether the results should be sorted in ascending order.
     * @return A list of entities that match the given criteria. If no entities are found,
     *         an empty list will be returned.
     */
    public List<T> findByCriteria(Map<String, Object> conditions, String orderByField, boolean ascending);

    /**
     * Asynchronously finds entities that match the specified criteria.
     *
     * @param conditions A map of field names and their expected values for filtering.
     * @param orderByField    The field name to sort by.
     * @param ascending  Whether the results should be sorted in ascending order.
     * @return A CompletableFuture containing a list of entities matching the criteria.
     */
    public CompletableFuture<List<T>> findByCriteriaAsync(Map<String, Object> conditions, String orderByField, boolean ascending);

    /**
     * Asynchronously retrieves all entities sorted by the specified field.
     *
     * @param sortField The field to sort by.
     * @param ascending Whether to sort in ascending order.
     * @return A CompletableFuture containing a list of sorted entities.
     */
    public CompletableFuture<List<T>> findAllSortedAsync(String sortField, boolean ascending);

}
