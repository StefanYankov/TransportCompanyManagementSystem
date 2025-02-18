package data.repositories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public Optional<T> getById(TKey id) {
        return executeQuery(session -> Optional.ofNullable(session.get(entityType, id)));
    }

    /** {@inheritDoc} */
    @Override
    public List<T> getAll() {
        return executeQuery(session -> session
                .createQuery("FROM " + entityType.getSimpleName(), entityType).list());
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
      return CompletableFuture.runAsync(() -> {
          try(Session session = sessionFactory.openSession()) {
              Transaction transaction = session.beginTransaction();
              session.persist(entity);
              transaction.commit();
          } catch (Exception e) {
              logger.error("Error saving entity asynchronously", e);
              throw e;
          }
      });

      //  return CompletableFuture.runAsync(() -> add(entity), executorService);
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
    public CompletableFuture<Optional<T>> getByIdAsync(TKey id) {
        return CompletableFuture.supplyAsync(() -> getById(id), executorService);
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<List<T>> getAllAsync() {
        return CompletableFuture.supplyAsync(() -> getAll(), executorService);
    }

    /** {@inheritDoc} */
//    @Override
//    public List<T> findByCriteria(Map<String, Object> conditions) {
//        return executeQuery(session -> {
//            CriteriaBuilder builder = session.getCriteriaBuilder();
//            CriteriaQuery<T> query = builder.createQuery(entityType);
//            Root<T> root = query.from(entityType);
//
//            List<Predicate> predicates = new ArrayList<>();
//            conditions.forEach((field, value) ->
//                    predicates.add(builder.equal(root.get(field), value))
//            );
//
//            query.select(root).where(predicates.toArray(new Predicate[0]));
//            return session.createQuery(query).getResultList();
//        });
//    }

    public List<T> findByCriteria(Map<String, Object> conditions, String orderBy, boolean ascending) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = builder.createQuery(entityType);
            Root<T> root = criteriaQuery.from(entityType);

            List<Predicate> predicates = new ArrayList<>();
            conditions.forEach((field, value) -> predicates.add(builder.equal(root.get(field), value)));

            // Apply sorting
            if (ascending) {
                criteriaQuery.orderBy(builder.asc(root.get(orderBy)));
            } else {
                criteriaQuery.orderBy(builder.desc(root.get(orderBy)));
            }

            criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));

            return session.createQuery(criteriaQuery).getResultList();
        }
    }


    /** {@inheritDoc} */
    @Override
    public CompletableFuture<List<T>> findByCriteriaAsync(Map<String, Object> conditions, String orderBy, boolean ascending) {
                return CompletableFuture.supplyAsync(() -> findByCriteria(conditions, orderBy, ascending), executorService);
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
        return CompletableFuture.supplyAsync(() -> findAllSorted(sortField, ascending), executorService);
    }

    /**
     * Executes a transactional operation.
     *
     * @param action The repository action to execute.
     */
    private void executeTransaction(RepositoryAction action) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            action.execute(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Transaction failed", e);
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
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
        Session session = null;
        try {
            session = sessionFactory.openSession();
            return action.execute(session);
        } catch (Exception ex) {
            logger.error("Query execution failed", ex);
            throw new RuntimeException("Query execution failed", ex);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
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