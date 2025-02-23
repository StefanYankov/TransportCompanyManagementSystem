package data.repositories;

import data.repositories.exceptions.RepositoryException;
import data.repositories.exceptions.RepositoryMessages;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Implementation of the {@link IGenericRepository} interface using Hibernate for persistence.
 * This class provides concrete implementations for CRUD operations and advanced queries,
 * leveraging a {@link SessionFactory} for database access and an {@link ExecutorService} for asynchronous operations.
 *
 * @param <T>    the type of the entity managed by this repository
 * @param <TKey> the type of the entity's identifier
 */
public class GenericRepository<T, TKey> implements IGenericRepository<T, TKey> {
    private static final Logger logger = LoggerFactory.getLogger(GenericRepository.class);
    private final SessionFactory sessionFactory;
    private final ExecutorService executorService;
    private final Class<T> entityClass;

    /**
     * Constructs a new GenericRepository with the specified dependencies.
     *
     * @param sessionFactory  the Hibernate SessionFactory for database access
     * @param executorService the ExecutorService for asynchronous operations
     * @param entityClass     the Class object representing the entity type
     */
    public GenericRepository(SessionFactory sessionFactory, ExecutorService executorService, Class<T> entityClass) {
        this.sessionFactory = sessionFactory;
        this.executorService = executorService;
        this.entityClass = entityClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public T create(T entity) throws RepositoryException {
        if (entity == null) {
            throw new RepositoryException(
                    RepositoryMessages
                            .format(RepositoryMessages.NULL_ENTITY, "create", entityClass.getSimpleName())
            );
        }
        // TODO: add specific exception handling for constrain validation, for example duplicate entities
        return executeInTransaction(session -> {
            session.persist(entity);
            return entity;
        }, "create");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T update(T entity) throws RepositoryException {
        if (entity == null) {
            throw new RepositoryException(
                    RepositoryMessages
                            .format(RepositoryMessages.NULL_ENTITY, "update", entityClass.getSimpleName())
            );
        }
        return executeInTransaction(session -> session.merge(entity), "update");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(T entity) throws RepositoryException {
        if (entity == null) {
            throw new RepositoryException(
                    RepositoryMessages
                            .format(RepositoryMessages.NULL_ENTITY, "delete", entityClass.getSimpleName())
            );
        }
        executeInTransaction(session -> {
            session.remove(session.contains(entity) ? entity : session.merge(entity));
            return null;
        }, "delete");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getById(TKey id) throws RepositoryException {
        if (id == null) {
            throw new RepositoryException(
                    RepositoryMessages.format(RepositoryMessages.NULL_ID, entityClass.getSimpleName())
            );
        }
        try (Session session = sessionFactory.openSession()) {
            T entity = session.get(entityClass, id);
            if (entity == null) {
                throw new RepositoryException(
                        RepositoryMessages.format(RepositoryMessages.ENTITY_NOT_FOUND, entityClass.getSimpleName(), id)
                );
            }
            return entity;
        } catch (Exception e) {
            throw new RepositoryException(
                    RepositoryMessages.format(RepositoryMessages.GET_BY_ID_FAILED, entityClass.getSimpleName(), id),
                    e
            );
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getAll(int page, int size, String orderBy, boolean ascending, String... fetchRelations) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);

            // Eagerly fetch specified relations
            if (fetchRelations != null) {
                for (String relation : fetchRelations) {
                    if (relation != null && !relation.trim().isEmpty()) {
                        root.fetch(relation, JoinType.LEFT); // LEFT join to include entities with null relations
                    }
                }
            }

            cq.select(root).distinct(true); // distinct to avoid duplicates from joins
            if (orderBy != null && !orderBy.isEmpty()) {
                cq.orderBy(ascending ? cb.asc(root.get(orderBy)) : cb.desc(root.get(orderBy)));
            }
            Query<T> query = session.createQuery(cq);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } catch (Exception e) {
            throw new RepositoryException(
                    RepositoryMessages.format(RepositoryMessages.GET_ALL_FAILED, entityClass.getSimpleName()),
                    e
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findByCriteria(Map<String, Object> conditions, String orderBy, boolean ascending) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(root);
            List<Predicate> predicates = new ArrayList<>();
            if (conditions != null) {
                for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                    String[] pathParts = entry.getKey().split("\\.");
                    Path<T> path = root;
                    for (String part : pathParts) {
                        path = path.get(part);
                    }
                    predicates.add(cb.equal(path, entry.getValue()));
                }
            }
            cq.where(predicates.toArray(new Predicate[0]));
            if (orderBy != null && !orderBy.isEmpty()) {
                cq.orderBy(ascending ? cb.asc(root.get(orderBy)) : cb.desc(root.get(orderBy)));
            }
            Query<T> query = session.createQuery(cq);
            return query.getResultList();
        } catch (Exception e) {
            throw new RepositoryException(
                    RepositoryMessages.format(RepositoryMessages.FIND_BY_CRITERIA_FAILED, entityClass.getSimpleName()),
                    e
            );
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findWithAggregation(String joinRelation, String aggregationField, String groupByField, boolean ascending) {
        // Step 1: Open a Hibernate session using try-with-resources to ensure it closes automatically
        try (Session session = sessionFactory.openSession()) {
            // Step 2: Get the CriteriaBuilder from the session, which is used to construct the query
            CriteriaBuilder cb = session.getCriteriaBuilder();

            // Step 3: Create a CriteriaQuery for the entity type T (e.g., TransportCompany), defining the query's result type
            CriteriaQuery<T> cq = cb.createQuery(entityClass);

            // Step 4: Define the root of the query, representing the main entity table (e.g., transport_companies)
            Root<T> root = cq.from(entityClass);

            // Step 5: Check if a join relationship is provided (e.g., "transportServices") to perform aggregation
            if (joinRelation != null && !joinRelation.trim().isEmpty()) {
                // Step 6: Create a LEFT JOIN between the root entity (e.g., TransportCompany) and the related entity
                // (e.g., TransportService) using the specified joinRelation field. LEFT JOIN ensures entities with
                // no related records are included (e.g., companies with no services).
                Join<T, ?> join = root.join(joinRelation, JoinType.LEFT);

                // Step 7: Build the query:
                // - select(root): Selects the entire root entity (e.g., all TransportCompany columns)
                // - groupBy(root.get(groupByField)): Groups results by the specified field (e.g., TransportCompany.id)
                // - orderBy: Orders results by the sum of the aggregation field (e.g., price) in ascending or descending order
                cq.select(root)
                        .groupBy(root.get(groupByField))
                        .orderBy(ascending ? cb.asc(cb.sum(join.get(aggregationField))) // If ascending, sort by SUM(price) ASC
                                : cb.desc(cb.sum(join.get(aggregationField)))); // If descending, sort by SUM(price) DESC
            } else {
                // Step 8: Fallback case if no joinRelation is provided (no aggregation):
                // - select(root): Selects the entire root entity
                // - orderBy: Orders by the groupByField directly (e.g., id) without aggregation
                cq.select(root).orderBy(ascending ? cb.asc(root.get(groupByField)) // Sort by groupByField ASC
                        : cb.desc(root.get(groupByField))); // Sort by groupByField DESC
            }

            // Step 9: Convert the CriteriaQuery into an executable Hibernate Query object
            Query<T> query = session.createQuery(cq);

            // Step 10: Execute the query and return the list of entities (e.g., List<TransportCompany>)
            return query.getResultList();
        } catch (Exception e) {
            // Step 11: Handle any exceptions (e.g., database errors, invalid field names) by wrapping them in a
            // RepositoryException with a descriptive message, including the entity class name
            throw new RepositoryException(
                    RepositoryMessages.format(RepositoryMessages.FIND_WITH_AGGREGATION_FAILED, entityClass.getSimpleName()),
                    e
            );
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findWithJoin(String joinField, String joinConditionField, Object joinConditionValue,
                                String orderBy, boolean ascending, boolean eagerFetch) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            Join<T, ?> join = root.join(joinField);
            if (eagerFetch) {
                root.fetch(joinField, JoinType.LEFT); // Eagerly fetch the relationship
            }
            cq.select(root)
                    .where(cb.equal(join.get(joinConditionField), joinConditionValue));
            if (orderBy != null && !orderBy.isEmpty()) {
                cq.orderBy(ascending ? cb.asc(root.get(orderBy)) : cb.desc(root.get(orderBy)));
            }
            Query<T> query = session.createQuery(cq);
            return query.getResultList();
        } catch (Exception e) {
            throw new RepositoryException(
                    RepositoryMessages.format(RepositoryMessages.FIND_WITH_JOIN_FAILED, entityClass.getSimpleName()),
                    e
            );
        }
    }

    // ### Asynchronous methods ###

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<T> createAsync(T entity) {
        return CompletableFuture.supplyAsync(() -> create(entity), executorService);
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<T> updateAsync(T entity) {
        return CompletableFuture.supplyAsync(() -> update(entity), executorService);
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Void> deleteAsync(T entity) {
        return CompletableFuture.runAsync(() -> delete(entity), executorService);
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<T> getByIdAsync(TKey id) {
        return CompletableFuture.supplyAsync(() -> getById(id), executorService);
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<List<T>> getAllAsync(int page, int size, String orderBy, boolean ascending, String... fetchRelations) {
        return CompletableFuture.supplyAsync(() -> getAll(page, size, orderBy, ascending, fetchRelations), executorService);
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<List<T>> findByCriteriaAsync(Map<String, Object> conditions, String orderBy, boolean ascending) {
        return CompletableFuture.supplyAsync(() -> findByCriteria(conditions, orderBy, ascending), executorService);
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<List<T>> findWithAggregationAsync(String joinRelation, String aggregationField, String groupByField, boolean ascending) {
        return CompletableFuture.supplyAsync(() -> findWithAggregation(joinRelation, aggregationField, groupByField, ascending), executorService);
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<List<T>> findWithJoinAsync(String joinField, String joinConditionField, Object joinConditionValue,
                                                        String orderBy, boolean ascending, boolean eagerFetch) {
        return CompletableFuture.supplyAsync(() -> findWithJoin(joinField, joinConditionField, joinConditionValue, orderBy, ascending, eagerFetch),
                executorService);
    }

    // ### Utility methods ###

    /**
     * Executes an operation within a transaction, rolling back on failure.
     *
     * @param operation     the operation to execute
     * @param operationName the name of the operation (e.g., "create", "update")
     * @param <R>           the return type of the operation
     * @return the result of the operation
     * @throws RepositoryException if the operation fails
     */
    private <R> R executeInTransaction(Function<Session, R> operation, String operationName) throws RepositoryException {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                R result = operation.apply(session);
                tx.commit();
                return result;
            } catch (Exception e) {
                tx.rollback();
                logger.error("Transaction failed", e);
                throw new RepositoryException(
                        RepositoryMessages.format(RepositoryMessages.TRANSACTION_FAILED, operationName, entityClass.getSimpleName()),
                        e
                );
            }
        } catch (Exception e) {

            throw new RepositoryException(
                    RepositoryMessages.format(RepositoryMessages.SESSION_OPEN_FAILED, operationName, entityClass.getSimpleName()),
                    e
            );
        }
    }
}
