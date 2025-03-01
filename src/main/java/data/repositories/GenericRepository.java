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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenericRepository<T, TKey> implements IGenericRepository<T, TKey> {
    private static final Logger logger = LoggerFactory.getLogger(GenericRepository.class);
    private final SessionFactory sessionFactory;
    private final Class<T> entityClass;

    public GenericRepository(SessionFactory sessionFactory, Class<T> entityClass) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
    }

    /** {@inheritDoc} */
    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    /** {@inheritDoc} */
    @Override
    public T create(T entity) throws RepositoryException {
        if (entity == null) {
            throw new RepositoryException(
                    RepositoryMessages.format(RepositoryMessages.NULL_ENTITY, "create", entityClass.getSimpleName())
            );
        }
        return executeInTransaction(session -> {
            session.persist(entity);
            return entity;
        }, "create");
    }

    /** {@inheritDoc} */
    @Override
    public T update(T entity) throws RepositoryException {
        if (entity == null) {
            throw new RepositoryException(
                    RepositoryMessages.format(RepositoryMessages.NULL_ENTITY, "update", entityClass.getSimpleName())
            );
        }
        return executeInTransaction(session -> session.merge(entity), "update");
    }

    /** {@inheritDoc} */
    @Override
    public void delete(T entity) throws RepositoryException {
        if (entity == null) {
            throw new RepositoryException(
                    RepositoryMessages.format(RepositoryMessages.NULL_ENTITY, "delete", entityClass.getSimpleName())
            );
        }
        executeInTransaction(session -> {
            session.remove(entity);
            return null;
        }, "delete");
    }

    /** {@inheritDoc} */
    @Override
    public Optional<T> getById(TKey id) throws RepositoryException {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        return Optional.ofNullable(executeInTransaction(session -> {
            return session.get(entityClass, id); // Simple get within transaction
        }, "getById"));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<T> getById(TKey id, String... fetchRelations) throws RepositoryException {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        return Optional.ofNullable(executeInTransaction(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            for (String relation : fetchRelations) {
                root.fetch(relation, JoinType.LEFT);
            }
            cq.select(root).where(cb.equal(root.get("id"), id));
            return session.createQuery(cq).uniqueResult();
        }, "getByIdWithFetch"));
    }

    /** {@inheritDoc} */
    @Override
    public List<T> getAll(int page, int size, String orderBy, boolean ascending, String... fetchRelations) throws RepositoryException {

        if (page < 0 || size <= 0) {
            return Collections.emptyList();
        }

        return executeInTransaction(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);

            // Handle fetch relations
            if ((fetchRelations != null) && (fetchRelations.length > 0)) {
                for (String relation : fetchRelations) {
                    root.fetch(relation, JoinType.LEFT);
                }
            }

            cq.select(root);
            if (orderBy != null) {
                cq.orderBy(ascending ? cb.asc(root.get(orderBy)) : cb.desc(root.get(orderBy)));
            }

            Query<T> query = session.createQuery(cq);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.list();
        }, "getAll");
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findByCriteria(Map<String, Object> conditions, String orderBy, Boolean ascending, String... fetchRelations) throws RepositoryException {
        return findByCriteria(conditions, orderBy, ascending, 0, Integer.MAX_VALUE, fetchRelations);
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findByCriteria(Map<String, Object> conditions, String orderBy, Boolean ascending, int page, int size, String... fetchRelations) throws RepositoryException {
        return executeInTransaction(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);

            cq.select(root);
            for (String relation : fetchRelations) {
                root.fetch(relation, JoinType.LEFT);
            }

            List<Predicate> predicates = conditions
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        String[] pathParts = entry.getKey().split("\\.");
                        Path<?> path = root;
                        for (String part : pathParts) {
                            path = path.get(part);
                        }
                        return cb.equal(path, entry.getValue());
                    })
                    .collect(Collectors.toList());
            cq.where(predicates.toArray(new Predicate[0]));

            if (orderBy != null) {
                cq.orderBy(ascending ? cb.asc(root.get(orderBy)) : cb.desc(root.get(orderBy)));
            }

            Query<T> query = session.createQuery(cq);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        }, "findByCriteria");
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findWithAggregation(String joinRelation, String aggregationField, String groupByField, boolean ascending) throws RepositoryException {
        return executeInTransaction(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            Join<T, ?> join = root.join(joinRelation, JoinType.LEFT);

            cq.select(root)
                    .groupBy(root.get(groupByField))
                    .orderBy(ascending ? cb.asc(cb.sum(join.get(aggregationField))) : cb.desc(cb.sum(join.get(aggregationField))));

            return session.createQuery(cq).list();
        }, "findWithAggregation");
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findWithJoin(String joinField, String joinConditionField, Object joinConditionValue,
                                String orderBy, boolean ascending, String... fetchRelations) throws RepositoryException {
        return executeInTransaction(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            Join<T, ?> join = root.join(joinField, JoinType.LEFT);

            // Eagerly fetch specified relations
            if (fetchRelations != null && fetchRelations.length > 0) {
                for (String relation : fetchRelations) {
                    root.fetch(relation, JoinType.LEFT);
                }
            }

            cq.select(root)
                    .where(cb.equal(join.get(joinConditionField), joinConditionValue));

            if (orderBy != null) {
                if (ascending) {
                    cq.orderBy(cb.asc(root.get(orderBy)));
                } else {
                    cq.orderBy(cb.desc(root.get(orderBy)));
                }
            }

            return session.createQuery(cq).list();
        }, "findWithJoin");
    }

    /** {@inheritDoc} */
    @Override
    public <D> D updateAndMap(T entity, Function<T, D> mapper, Consumer<T> lazyInitializer) throws RepositoryException {
        if (entity == null) {
            throw new RepositoryException(
                    RepositoryMessages.format(RepositoryMessages.NULL_ENTITY, "updateAndMap", entityClass.getSimpleName())
            );
        }
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper function must not be null");
        }
        return executeInTransaction(session -> {
            T updatedEntity = session.merge(entity);
            if (lazyInitializer != null) {
                lazyInitializer.accept(updatedEntity);
            }
            return mapper.apply(updatedEntity);
        }, "updateAndMap");
    }

    /** {@inheritDoc} */
    @Override
    public <R> Map<TKey, Long> countRelatedEntities(Class<R> relatedEntityClass, String relationField, String groupByField, String countField, int page, int size, String orderBy, boolean ascending) throws RepositoryException {
        return executeInTransaction(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<T> root = cq.from(entityClass);
            Join<T, R> join = root.join(relationField, JoinType.LEFT);

            cq.multiselect(
                    root.get(groupByField),
                    cb.count(join.get(countField))
            ).groupBy(root.get(groupByField));

            if (orderBy != null && orderBy.equals("count")) {
                cq.orderBy(ascending ? cb.asc(cb.count(join.get(countField))) : cb.desc(cb.count(join.get(countField))));
            } else if (orderBy != null) {
                cq.orderBy(ascending ? cb.asc(root.get(orderBy)) : cb.desc(root.get(orderBy)));
            } else {
                cq.orderBy(ascending ? cb.asc(root.get(groupByField)) : cb.desc(root.get(groupByField)));
            }

            Query<Object[]> query = session.createQuery(cq);
            query.setFirstResult(page * size);
            query.setMaxResults(size);

            List<Object[]> results = query.getResultList();
            Map<TKey, Long> counts = new HashMap<>();
            for (Object[] result : results) {
                TKey key = (TKey) result[0];
                Long count = (Long) result[1];
                counts.put(key, count);
            }
            return counts;
        }, "countRelatedEntities");
    }

    /** {@inheritDoc} */
    @Override
    public <D> Optional<D> getByIdAndMap(TKey id, Function<T, D> mapper, Consumer<T> lazyInitializer) throws RepositoryException {
        if (id == null) throw new IllegalArgumentException("ID must not be null");
        if (mapper == null) throw new IllegalArgumentException("Mapper function must not be null");
        return Optional.ofNullable(executeInTransaction(session -> {
            T entity = session.get(entityClass, id);
            if (entity == null) return null;
            if (lazyInitializer != null) lazyInitializer.accept(entity);
            return mapper.apply(entity);
        }, "getByIdAndMap"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <D> List<D> getAllAndMap(int page, int size, String orderBy, boolean ascending, Function<T, D> mapper, String... fetchRelations) throws RepositoryException {
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper function must not be null");
        }
        return executeInTransaction(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);

            // Handle fetch relations
            if (fetchRelations != null && fetchRelations.length > 0) {
                for (String relation : fetchRelations) {
                    root.fetch(relation, JoinType.LEFT);
                }
            }

            cq.select(root);
            if (orderBy != null) {
                cq.orderBy(ascending ? cb.asc(root.get(orderBy)) : cb.desc(root.get(orderBy)));
            }

            Query<T> query = session.createQuery(cq);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            List<T> entities = query.list();
            return entities.stream().map(mapper).collect(Collectors.toList());
        }, "getAllAndMap");
    }

    /** {@inheritDoc} */
    @Override
    public <R> List<R> findRelatedEntities(Class<R> relatedEntityClass, String relationField, TKey entityId, int page, int size, String orderBy, boolean ascending) throws RepositoryException {
        return executeInTransaction(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<R> cq = cb.createQuery(relatedEntityClass);
            Root<R> root = cq.from(relatedEntityClass);
            Join<R, T> join = root.join(relationField);
            cq.select(root).where(cb.equal(join.get("id"), entityId));
            if (orderBy != null) {
                cq.orderBy(ascending ? cb.asc(root.get(orderBy)) : cb.desc(root.get(orderBy)));
            }
            Query<R> query = session.createQuery(cq);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.list();
        }, "findRelatedEntities");
    }

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
                        RepositoryMessages.format(RepositoryMessages.TRANSACTION_FAILED, operationName, entityClass.getSimpleName()), e
                );
            }
        } catch (Exception e) {
            throw new RepositoryException(
                    RepositoryMessages.format(RepositoryMessages.SESSION_OPEN_FAILED, operationName, entityClass.getSimpleName()), e
            );
        }
    }
}