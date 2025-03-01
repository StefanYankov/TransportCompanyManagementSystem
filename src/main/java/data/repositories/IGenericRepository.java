package data.repositories;

import data.repositories.exceptions.RepositoryException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A generic repository interface for performing CRUD operations and advanced queries on entities.
 * This interface provides synchronous methods for interacting with a persistent store,
 * leveraging Hibernate as the underlying ORM framework. Methods may throw unchecked exceptions, such as
 * {@link RepositoryException}, which are documented below.
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
     * @throws RepositoryException if the entity is null, constraints are violated
     *                             (e.g., duplicate unique fields), or persistence fails due to database errors
     */
    T create(T entity);

    /**
     * Updates an existing entity in the persistent store.
     *
     * @param entity the entity to update; must not be null
     * @return the updated entity, reflecting the merged state
     * @throws RepositoryException if the entity is null, the entity is not found
     *                             in the persistent store, or the update fails (e.g., due to optimistic locking conflicts)
     */
    T update(T entity);

    /**
     * Deletes an entity from the persistent store.
     *
     * @param entity the entity to delete; must not be null
     * @throws RepositoryException if the entity is null or deletion fails
     *                             (e.g., due to database constraints or transaction errors)
     */
    void delete(T entity);

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id the identifier of the entity; must not be null
     * @return an Optional containing the entity if found, or empty if not found
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException if retrieval fails due to database errors
     */
    Optional<T> getById(TKey id) throws RepositoryException;

    /**
     * Retrieves an entity by its identifier, optionally fetching specified relationships eagerly.
     * This method allows dynamic eager loading of related entities or collections (e.g., "qualifications" for a Driver),
     * overriding the default lazy loading behavior defined in the entity mappings.
     *
     * @param id the identifier of the entity; must not be null
     * @param fetchRelations variable argument list of relationship names to fetch eagerly (e.g., "qualifications", "transportCompany");
     *                       if empty or null, no additional relationships are fetched beyond the entity itself
     * @return an Optional containing the entity if found, or empty if not found; relationships specified in fetchRelations
     *         are eagerly loaded if present
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException if retrieval fails due to database errors or invalid fetchRelations (e.g., a non-existent relationship name)
     */
    Optional<T> getById(TKey id, String... fetchRelations) throws RepositoryException;

    /**
     * Retrieves a paginated list of all entities, optionally sorted and with specified relations fetched.
     *
     * @param page           the page number (0-based)
     * @param size           the number of entities per page
     * @param orderBy        the field to sort by; may be null for no sorting
     * @param ascending      true for ascending order, false for descending
     * @param fetchRelations the names of relations to eagerly fetch (e.g., "employees", "vehicles"); may be null or empty
     * @return a list of entities for the specified page with the requested relations loaded
     * @throws RepositoryException if the query fails (e.g., invalid orderBy field or database errors)
     */
    List<T> getAll(int page, int size, String orderBy, boolean ascending, String... fetchRelations);

    /**
     * Finds entities matching the given criteria, optionally sorted, with specified relationships eagerly fetched.
     *
     * @param conditions     map of field names to values for filtering (e.g., {"name": "Fast Transport"})
     * @param orderBy        the field to sort by; may be null for no sorting
     * @param ascending      true for ascending order, false for descending
     * @param fetchRelations the relationships to eagerly fetch (e.g., "qualifications"); may be null or empty
     * @return a list of matching entities
     * @throws RepositoryException if the query fails (e.g., invalid field name)
     */
    public List<T> findByCriteria(Map<String, Object> conditions, String orderBy, Boolean ascending, String... fetchRelations) throws RepositoryException;

    public List<T> findByCriteria(Map<String, Object> conditions, String orderBy, Boolean ascending, int page, int pageSize, String... fetchRelations) throws RepositoryException;


        /**
         * Finds entities with aggregated data from a related collection, sorted by the aggregation result.
         *
         * @param joinRelation     the name of the relation to join and aggregate over (e.g., "transportServices")
         * @param aggregationField the field in the joined relation to aggregate (e.g., "price")
         * @param groupByField     the field in the root entity to group by (e.g., "id")
         * @param ascending        true for ascending order, false for descending
         * @return a list of entities sorted by the aggregated value
         * @throws RepositoryException if the query fails (e.g., invalid joinRelation, aggregationField, or database errors)
         */
    List<T> findWithAggregation(String joinRelation, String aggregationField, String groupByField, boolean ascending);

    /**
     * Finds entities with a join condition, optionally sorted, with specified relationships eagerly fetched.
     *
     * @param joinField          the field to join on (e.g., "qualifications")
     * @param joinConditionField the field in the joined entity to filter (e.g., "name")
     * @param joinConditionValue the value to match in the joined entity
     * @param orderBy            the field to sort by; may be null for no sorting
     * @param ascending          true for ascending order, false for descending
     * @param fetchRelations     the relationships to eagerly fetch (e.g., "qualifications", "dispatcher"); may be null or empty
     * @return a list of matching entities
     * @throws RepositoryException if the query fails (e.g., invalid joinField or database errors)
     */
    List<T> findWithJoin(String joinField, String joinConditionField, Object joinConditionValue,
                         String orderBy, boolean ascending, String... fetchRelations) throws RepositoryException;

    /**
     * Counts related entities of type R associated with entities of type T via a relationship,
     * grouped by a specified field, with pagination and sorting options.
     *
     * @param <R>                the type of the related entity
     * @param relatedEntityClass the class of the related entity (e.g., TransportService.class)
     * @param relationField      the field name in T representing the relationship to R (e.g., "transportServices")
     * @param groupByField       the field in T to group by (e.g., "id")
     * @param countField         the field in R to count (e.g., "id")
     * @param page               the page number (0-based) for pagination
     * @param size               the number of results per page
     * @param orderBy            the field to sort by (e.g., "count"); may be null for groupByField sorting
     * @param ascending          true for ascending order, false for descending
     * @return a map of groupByField values to counts of related entities
     * @throws RepositoryException if the query fails (e.g., invalid fields or database errors)
     */
    public <R> Map<TKey, Long> countRelatedEntities(Class<R> relatedEntityClass, String relationField, String groupByField, String countField, int page, int size, String orderBy, boolean ascending) throws RepositoryException;


    /**
     * Updates an existing entity in the persistent store and maps it to a DTO within the same transaction.
     *
     * @param <D>            the type of the DTO to return
     * @param entity         the entity to update; must not be null
     * @param mapper         a function to map the updated entity to a DTO; must not be null
     * @param lazyInitializer an optional consumer to initialize lazy-loaded relationships
     * @return the mapped DTO representing the updated entity
     * @throws RepositoryException if the entity or mapper is null, or update fails
     */
    public <D> D updateAndMap(T entity, Function<T, D> mapper, Consumer<T> lazyInitializer) throws RepositoryException;


    /**
     * Retrieves an entity by its identifier and maps it to a DTO within the same transaction.
     *
     * @param <D>            the type of the DTO to return
     * @param id             the identifier of the entity; must not be null
     * @param mapper         a function to map the entity to a DTO; must not be null
     * @param lazyInitializer an optional consumer to initialize lazy-loaded relationships
     * @return an Optional containing the mapped DTO if found, or empty if not found
     * @throws IllegalArgumentException if the ID or mapper is null
     * @throws RepositoryException if retrieval fails due to database errors
     */
    <D> Optional<D> getByIdAndMap(TKey id, Function<T, D> mapper, Consumer<T> lazyInitializer) throws RepositoryException;

    /**
     * Retrieves a paginated list of all entities and maps them to DTOs within the same transaction.
     *
     * @param <D>            the type of the DTO to return
     * @param page           the page number (0-based)
     * @param size           the number of entities per page
     * @param orderBy        the field to sort by; may be null for no sorting
     * @param ascending      true for ascending order, false for descending
     * @param mapper         a function to map each entity to a DTO; must not be null
     * @param lazyInitializer an optional consumer to initialize lazy-loaded relationships
     * @return a list of mapped DTOs
     * @throws IllegalArgumentException if the mapper is null
     * @throws RepositoryException if the query fails due to database errors
     */
    <D> List<D> getAllAndMap(int page, int size, String orderBy, boolean ascending, Function<T, D> mapper, Consumer<T> lazyInitializer) throws RepositoryException;

    /**
     * Retrieves all related entities of type R associated with a specific entity of type T via a many-to-many relationship,
     * with pagination and sorting options.
     *
     * @param <R>                the type of the related entity
     * @param relatedEntityClass the class of the related entity (e.g., Driver.class)
     * @param relationField      the field name in R representing the many-to-many relationship to T (e.g., "qualifications")
     * @param entityId           the ID of the entity of type T
     * @param page               the page number (0-based) for pagination
     * @param size               the number of entities per page
     * @param orderBy            the field in R to sort by (e.g., "familyName"); may be null for no sorting
     * @param ascending          true for ascending order, false for descending
     * @return a paginated and sorted list of related entities of type R
     * @throws RepositoryException if the query fails (e.g., invalid relationField, entity not found, invalid orderBy field)
     */
    <R> List<R> findRelatedEntities(Class<R> relatedEntityClass, String relationField, TKey entityId, int page, int size, String orderBy, boolean ascending);
}