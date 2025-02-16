package data.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Generic repository providing basic CRUD operations for entities extending BaseModel.
 * Supports both synchronous and asynchronous execution using an injected ExecutorService.
 *
 * @param <T>  The entity type.
 * @param <TKey> The type of the primary key.
 */
public class GenericRepository<T, TKey> implements IGenericRepository<T, TKey> {
    private static final Logger logger = LoggerFactory.getLogger(GenericRepository.class);
    // Hibernate SessionFactory for database interactions
    private final SessionFactory sessionFactory;
    // ExecutorService for asynchronous operations
    private final ExecutorService executorService;
    private final Class<T> entityType;

    /**
     * Constructs a GenericRepository with session factory and executor service.
     *
     * @param sessionFactory   Hibernate session factory for database interactions.
     * @param executorService  ExecutorService for asynchronous operations.
     * @param entityType       Class type of the entity.
     */
    public GenericRepository(
            SessionFactory sessionFactory,
            ExecutorService executorService,
            Class<T> entityType) {
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
    public Optional<T> getById(TKey TKey) {
        return executeQuery(session -> Optional.ofNullable(session.get(entityType, TKey)));
    }

    /** {@inheritDoc} */
    @Override
    public List<T> getAll() {
        return executeQuery(session -> session.createQuery("FROM " + entityType.getSimpleName(), entityType).list());
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
    public CompletableFuture<Void> addAsync(T entity) {
        return CompletableFuture.runAsync(() -> add(entity), executorService);
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Void> updateAsync(T entity) {
        return CompletableFuture.runAsync(() -> update(entity), executorService);
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Void> deleteAsync(T entity) {
        return CompletableFuture.runAsync(() -> delete(entity), executorService);
    }

    /** {@inheritDoc} */
    public CompletableFuture<Optional<T>> getByIdAsync(TKey TKey) {
        return CompletableFuture.supplyAsync(() -> getById(TKey), executorService);
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<List<T>> getAllAsync() {
        return CompletableFuture.supplyAsync(() -> getAll(), executorService);
    }


    /**
     * Executes a transactional operation.
     *
     * @param action The repository action to execute.
     */
    private void executeTransaction(RepositoryAction<T> action) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            action.execute(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Transaction failed", e);
            throw new RuntimeException(e);
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
            throw new RuntimeException(ex);
        }
    }

    /**
     * Functional interface for repository actions that modify the database.
     *
     * @param <T> Entity type.
     */
    @FunctionalInterface
    interface RepositoryAction<T> {
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
