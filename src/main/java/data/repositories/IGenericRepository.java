package data.repositories;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A generic repository interface for performing CRUD operations and advanced queries on entities.
 * This interface provides both synchronous and asynchronous methods for interacting with a persistent store,
 * leveraging Hibernate as the underlying ORM framework.
 *
 * @param <T>    the type of the entity managed by this repository
 * @param <TKey> the type of the entity's identifier (e.g., Long, UUID)
 */
public interface IGenericRepository<T, TKey> {

    /**
     * Returns the entity class managed by this repository.
     *
     * @return the Class object representing the entity type
     */
    Class<T> getEntityClass();

    /**
     * Creates a new entity in the persistent store.
     *
     * @param entity the entity to create; must not be null
     * @return the created entity, including any generated fields (e.g., ID, audit fields)
     */
    public T create(T entity);

    /**
     * Updates an existing entity in the persistent store.
     *
     * @param entity the entity to update; must not be null
     * @return the updated entity, reflecting the merged state
     */
    public T update(T entity);

    /**
     * Deletes an entity from the persistent store.
     *
     * @param entity the entity to delete; must not be null
     */
    public void delete(T entity);

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id the identifier of the entity; must not be null
     * @return the entity if found
     */
    public T getById(TKey id);

    /**
     * Retrieves a paginated list of all entities, optionally sorted and with specified relations fetched.
     *
     * @param page           the page number (0-based)
     * @param size           the number of entities per page
     * @param orderBy        the field to sort by; may be null for no sorting
     * @param ascending      true for ascending order, false for descending
     * @param fetchRelations the names of relations to eagerly fetch (e.g., "employees", "vehicles"); may be null or empty
     * @return a list of entities for the specified page with the requested relations loaded
     */
    public List<T> getAll(int page, int size, String orderBy, boolean ascending, String... fetchRelations);

    /**
     * Finds entities matching the specified criteria, optionally sorted.
     *
     * @param conditions a map of field names to values for filtering; may be null for no filtering
     * @param orderBy    the field to sort by; may be null for no sorting
     * @param ascending  true for ascending order, false for descending
     * @return a list of matching entities
     */
    public List<T> findByCriteria(Map<String, Object> conditions, String orderBy, boolean ascending);

    /**
     * Finds entities with aggregated data from a related collection, sorted by the aggregation result.
     * For example, this can be used to sort entities by a summed value from a related collection,
     * such as total revenue from transport services for a company.
     *
     * @param joinRelation     the name of the relation to join and aggregate over (e.g., "transportServices")
     * @param aggregationField the field in the joined relation to aggregate (e.g., "price")
     * @param groupByField     the field in the root entity to group by (e.g., "id")
     * @param ascending        true for ascending order, false for descending
     * @return a list of entities sorted by the aggregated value
     */
    public List<T> findWithAggregation(String joinRelation, String aggregationField, String groupByField, boolean ascending);

    /**
     * Finds entities with a join condition, optionally sorted.
     * Intended for use cases like filtering Drivers by Qualification.
     *
     * @param joinField          the field to join on (e.g., "qualifications")
     * @param joinConditionField the field in the joined entity to filter (e.g., "name")
     * @param joinConditionValue the value to match in the joined entity
     * @param orderBy            the field to sort by; may be null for no sorting
     * @param ascending          true for ascending order, false for descending
     * @param eagerFetch         true to eagerly fetch the joined relationship, ensuring it is fully loaded in the result;
     *                           false to leave it lazily loaded, which may require an active session for subsequent access
     * @return a list of matching entities
     */
    public List<T> findWithJoin(String joinField, String joinConditionField, Object joinConditionValue,
                                String orderBy, boolean ascending, boolean eagerFetch);

    /**
     * Asynchronously creates a new entity in the persistent store.
     *
     * @param entity the entity to create; must not be null
     * @return a CompletableFuture resolving to the created entity
     */
    public CompletableFuture<T> createAsync(T entity);

    /**
     * Asynchronously updates an existing entity in the persistent store.
     *
     * @param entity the entity to update; must not be null
     * @return a CompletableFuture resolving to the updated entity
     */
    public CompletableFuture<T> updateAsync(T entity);

    /**
     * Asynchronously deletes an entity from the persistent store.
     *
     * @param entity the entity to delete; must not be null
     * @return a CompletableFuture that completes when the deletion is done
     */
    public CompletableFuture<Void> deleteAsync(T entity);

    /**
     * Asynchronously retrieves an entity by its identifier.
     *
     * @param id the identifier of the entity; must not be null
     * @return a CompletableFuture resolving to the entity if found
     */
    public CompletableFuture<T> getByIdAsync(TKey id);

    /**
     * Asynchronously retrieves a paginated list of all entities, optionally sorted and with specified relations fetched.
     *
     * @param page           the page number (0-based)
     * @param size           the number of entities per page
     * @param orderBy        the field to sort by; may be null for no sorting
     * @param ascending      true for ascending order, false for descending
     * @param fetchRelations the names of relations to eagerly fetch (e.g., "employees", "vehicles"); may be null or empty
     * @return a CompletableFuture resolving to a list of entities
     */
    public CompletableFuture<List<T>> getAllAsync(int page, int size, String orderBy, boolean ascending, String... fetchRelations);

    /**
     * Asynchronously finds entities matching the specified criteria, optionally sorted.
     *
     * @param conditions a map of field names to values for filtering; may be null for no filtering
     * @param orderBy    the field to sort by; may be null for no sorting
     * @param ascending  true for ascending order, false for descending
     * @return a CompletableFuture resolving to a list of matching entities
     */
    CompletableFuture<List<T>> findByCriteriaAsync(Map<String, Object> conditions, String orderBy, boolean ascending);

    /**
     * Asynchronously finds entities with aggregated data from a related collection, sorted by the aggregation result.
     *
     * @param joinRelation     the name of the relation to join and aggregate over (e.g., "transportServices")
     * @param aggregationField the field in the joined relation to aggregate (e.g., "price")
     * @param groupByField     the field in the root entity to group by (e.g., "id")
     * @param ascending        true for ascending order, false for descending
     * @return a CompletableFuture resolving to a list of entities
     */
    public CompletableFuture<List<T>> findWithAggregationAsync(String joinRelation, String aggregationField, String groupByField, boolean ascending);

    /**
     * Asynchronously finds entities with a join condition, optionally sorted.
     *
     * @param joinField          the field to join on (e.g., "qualifications")
     * @param joinConditionField the field in the joined entity to filter (e.g., "name")
     * @param joinConditionValue the value to match in the joined entity
     * @param orderBy            the field to sort by; may be null for no sorting
     * @param ascending          true for ascending order, false for descending
     * @param eagerFetch         true to eagerly fetch the joined relationship, ensuring it is fully loaded in the result;
     *                           false to leave it lazily loaded, which may require an active session for subsequent access
     * @return a CompletableFuture resolving to a list of matching entities
     */
    public CompletableFuture<List<T>> findWithJoinAsync(String joinField, String joinConditionField, Object joinConditionValue,
                                                        String orderBy, boolean ascending, boolean eagerFetch);

}