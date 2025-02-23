package services.services;

import data.models.TransportCompany;
import data.models.employee.Dispatcher;
import data.repositories.IGenericRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.dto.employees.DispatcherCreateDTO;
import services.data.dto.employees.DispatcherUpdateDTO;
import services.data.mapping.mappers.DispatcherMapper;
import services.services.contracts.IDispatcherService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service implementation for managing {@link Dispatcher} entities.
 */
public class DispatcherService implements IDispatcherService {
    private static final Logger logger = LoggerFactory.getLogger(DispatcherService.class);
    private final IGenericRepository<Dispatcher, Long> dispatcherRepo;
    private final DispatcherMapper mapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;

    public DispatcherService(IGenericRepository<Dispatcher, Long> dispatcherRepo, DispatcherMapper mapper,
                             IGenericRepository<TransportCompany, Long> companyRepo) {
        this.dispatcherRepo = dispatcherRepo;
        this.mapper = mapper;
        this.companyRepo = companyRepo;
    }

    /** {@inheritDoc} */
    @Override
    public Dispatcher create(DispatcherCreateDTO dto) {
        logger.debug("Creating dispatcher with DTO: {}", dto);
        Dispatcher entity = mapper.toEntity(dto);
        Dispatcher result = dispatcherRepo.create(entity);
        logger.info("Dispatcher created with ID: {}", result.getId());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Dispatcher> createAsync(DispatcherCreateDTO dto) {
        logger.debug("Async creating dispatcher with DTO: {}", dto);
        Dispatcher entity = mapper.toEntity(dto);
        return dispatcherRepo.createAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to create dispatcher asynchronously", throwable);
                    } else {
                        logger.info("Dispatcher created asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public Dispatcher update(DispatcherUpdateDTO dto) {
        logger.debug("Updating dispatcher with DTO: {}", dto);
        Dispatcher entity = mapper.toEntity(dto);
        Dispatcher result = dispatcherRepo.update(entity);
        logger.info("Dispatcher updated with ID: {}", result.getId());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Dispatcher> updateAsync(DispatcherUpdateDTO dto) {
        logger.debug("Async updating dispatcher with DTO: {}", dto);
        Dispatcher entity = mapper.toEntity(dto);
        return dispatcherRepo.updateAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to update dispatcher asynchronously", throwable);
                    } else {
                        logger.info("Dispatcher updated asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long id) {
        logger.debug("Deleting dispatcher with ID: {}", id);
        Dispatcher entity = dispatcherRepo.getById(id);
        if (entity != null) {
            dispatcherRepo.delete(entity);
            logger.info("Dispatcher deleted with ID: {}", id);
        } else {
            logger.warn("No dispatcher found to delete with ID: {}", id);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Void> deleteAsync(Long id) {
        logger.debug("Async deleting dispatcher with ID: {}", id);
        return dispatcherRepo.getByIdAsync(id)
                .thenAccept(entity -> {
                    if (entity != null) {
                        dispatcherRepo.deleteAsync(entity)
                                .whenComplete((v, throwable) -> {
                                    if (throwable != null) {
                                        logger.error("Failed to delete dispatcher asynchronously", throwable);
                                    } else {
                                        logger.info("Dispatcher deleted asynchronously with ID: {}", id);
                                    }
                                });
                    } else {
                        logger.warn("No dispatcher found to delete asynchronously with ID: {}", id);
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public Dispatcher getById(Long id) {
        logger.debug("Retrieving dispatcher with ID: {}", id);
        Dispatcher result = dispatcherRepo.getById(id);
        if (result != null) {
            logger.info("Dispatcher retrieved with ID: {}", id);
        } else {
            logger.warn("No dispatcher found with ID: {}", id);
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<Dispatcher> getAll(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving all dispatchers, page: {}, size: {}, orderBy: {}, ascending: {}", page, size, orderBy, ascending);
        List<Dispatcher> result = dispatcherRepo.getAll(page, size, orderBy, ascending);
        logger.info("Retrieved {} dispatchers", result.size());
        return result;
    }
}