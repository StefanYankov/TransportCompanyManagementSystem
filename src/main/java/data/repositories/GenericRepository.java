package data.repositories;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * Generic repository providing basic CRUD operations for entities.
 * Supports both synchronous and asynchronous execution.
 *
 * @param <T>    The entity type.
 * @param <TKey> The type of the primary key.
 */
public class GenericRepository<T, TKey> implements IGenericRepository<T, TKey> {
    private static final Logger logger = LoggerFactory.getLogger(GenericRepository.class);
    private final SessionFactory sessionFactory;
    private final ExecutorService executorService;
    private final Class<T> entityType;

    /**
     * Constructs a GenericRepository with session factory and executor service.
     *
     * @param sessionFactory   Hibernate session factory for database interactions.
     * @param executorService  ExecutorService for asynchronous operations.
     * @param entityType       Class type of the entity.
     */
    public GenericRepository(SessionFactory sessionFactory, ExecutorService executorService, Class<T> entityType) {
        this.sessionFactory = sessionFactory;
        this.executorService = executorService;
        this.entityType = entityType;
    }

    /** {@inheritDoc} */
    @Override
    public void add(T entity) {
        executeTransaction(session -> session.persist(entity));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<T> getById(TKey id) {
        return executeQuery(session -> Optional.ofNullable(session.get(entityType, id)));
    }

    /** {@inheritDoc} */
    @Override
    public List<T> getAll() {
        return this.getAll(0, 0, false);
    }

    /** {@inheritDoc} */
    @Override
    public List<T> getAll(int page, int pageSize) {
        return this.getAll(page, pageSize, false);
    }

    /** {@inheritDoc} */
    @Override
    public List<T> getAll(int page, int pageSize, boolean fetchRelations) {
        return executeQuery(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(entityType);
            Root<T> root = query.from(entityType);
            query.select(root);
            return session.createQuery(query)
                    .setFirstResult(page * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        });
    }

    /** {@inheritDoc} */
    @Override
    public void update(T entity) {
        executeTransaction(session -> session.merge(entity));
    }

    /** {@inheritDoc} */
    @Override
    public void delete(T entity) {
        executeTransaction(session -> session.remove(entity));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Void> addAsync(T entity) {
        return executeTransactionAsync(session -> session.persist(entity));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Void> updateAsync(T entity) {
        return executeTransactionAsync(session -> session.merge(entity));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Void> deleteAsync(T entity) {
        return executeTransactionAsync(session -> session.remove(entity));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Optional<T>> getByIdAsync(TKey id) {
        return executeQueryAsync(session -> Optional.ofNullable(session.get(entityType, id)));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<List<T>> getAllAsync() {
        return getAllAsync(0, 0, false); // Default values
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<List<T>> getAllAsync(int page, int pageSize) {
        return getAllAsync(page, pageSize, false); // Default fetchRelations to false
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<List<T>> getAllAsync(int page, int pageSize, boolean fetchRelations) {
        return executeQueryAsync(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(entityType);
            Root<T> root = query.from(entityType);
            query.select(root);

            // If fetchRelations is true, apply join fetch logic
            if (fetchRelations) {
                root.fetch("relatedEntity", JoinType.LEFT); // Example, change as needed
            }

            return session.createQuery(query)
                    .setFirstResult(page * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findByCriteria(Map<String, Object> conditions, String sortField, boolean ascending) {
        return executeQuery(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(entityType);
            Root<T> root = query.from(entityType);

            List<Predicate> predicates = new ArrayList<>();
            conditions.forEach((field, value) -> predicates.add(builder.equal(root.get(field), value)));
            query.where(predicates.toArray(new Predicate[0]));

            if (ascending) {
                query.orderBy(builder.asc(root.get(sortField)));
            } else {
                query.orderBy(builder.desc(root.get(sortField)));
            }

            return session.createQuery(query).getResultList();
        });
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<List<T>> findByCriteriaAsync(Map<String, Object> conditions, String sortField, boolean ascending) {
        return executeQueryAsync(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(entityType);
            Root<T> root = query.from(entityType);

            List<Predicate> predicates = new ArrayList<>();
            conditions.forEach((field, value) -> predicates.add(builder.equal(root.get(field), value)));
            query.where(predicates.toArray(new Predicate[0]));

            if (ascending) {
                query.orderBy(builder.asc(root.get(sortField)));
            } else {
                query.orderBy(builder.desc(root.get(sortField)));
            }

            return session.createQuery(query).getResultList();
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findAllSorted(String sortField, boolean ascending) {
        return executeQuery(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(entityType);
            Root<T> root = query.from(entityType);

            if (ascending) {
                query.orderBy(builder.asc(root.get(sortField)));
            } else {
                query.orderBy(builder.desc(root.get(sortField)));
            }

            return session.createQuery(query).getResultList();
        });
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<List<T>> findAllSortedAsync(String sortField, boolean ascending) {
        return executeQueryAsync(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(entityType);
            Root<T> root = query.from(entityType);

            if (ascending) {
                query.orderBy(builder.asc(root.get(sortField)));
            } else {
                query.orderBy(builder.desc(root.get(sortField)));
            }

            return session.createQuery(query).getResultList();
        });
    }

    // ### Utility methods ###

    /**
     * Executes a transactional operation.
     *
     * @param action The repository action to execute.
     */
    private void executeTransaction(RepositoryAction action) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            action.execute(session);
            transaction.commit();
        } catch (Exception ex) {
            logger.error("Transaction failed", ex);
            throw new RuntimeException("Transaction failed", ex);
        }
    }

    /**
     * Executes a query and returns a result.
     *
     * @param action The repository action that produces a result.
     * @param <R>    The result type.
     * @return The result of the query.
     */
    private <R> R executeQuery(QueryAction<R> action) {
        try (Session session = sessionFactory.openSession()) {
            return action.execute(session);
        } catch (Exception ex) {
            logger.error("Query execution failed", ex);
            throw new RuntimeException("Query execution failed", ex);
        }
    }

    /**
     * Executes an asynchronous transactional operation.
     *
     * @param action The repository action to execute.
     * @return A CompletableFuture representing the completion of the operation.
     */
    private CompletableFuture<Void> executeTransactionAsync(RepositoryAction action) {
        return CompletableFuture.runAsync(() -> executeTransaction(action), executorService);
    }

    /**
     * Executes an asynchronous query and returns a result.
     *
     * @param action The repository action that produces a result.
     * @param <R>    The result type.
     * @return A CompletableFuture containing the result of the query.
     */
    private <R> CompletableFuture<R> executeQueryAsync(QueryAction<R> action) {
        return CompletableFuture.supplyAsync(() -> executeQuery(action), executorService);
    }

    /**
     * Functional interface for repository actions that modify the database.
     */
    @FunctionalInterface
    interface RepositoryAction {
        void execute(Session session);
    }

    /**
     * Functional interface for repository actions that return a value.
     *
     * @param <R> Return type.
     */
    @FunctionalInterface
    interface QueryAction<R> {
        R execute(Session session);
    }
}
