package services.services;

import data.models.TransportCompany;
import data.models.vehicles.Van;
import data.repositories.IGenericRepository;
import services.data.mapping.mappers.VanMapper;
import services.data.dto.vehicles.VanCreateDTO;
import services.data.dto.vehicles.VanUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service implementation for managing {@link Van} entities.
 */
public class VanService implements IVanService {
    private static final Logger logger = LoggerFactory.getLogger(VanService.class);
    private final IGenericRepository<Van, Long> vanRepo;
    private final VanMapper mapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;

    public VanService(IGenericRepository<Van, Long> vanRepo, VanMapper mapper,
                      IGenericRepository<TransportCompany, Long> companyRepo) {
        this.vanRepo = vanRepo;
        this.mapper = mapper;
        this.companyRepo = companyRepo;
    }

    /** {@inheritDoc} */
    @Override
    public Van create(VanCreateDTO dto) {
        logger.debug("Creating van with DTO: {}", dto);
        Van entity = mapper.toEntity(dto);
        Van result = vanRepo.create(entity);
        logger.info("Van created with ID: {}", result.getId());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Van> createAsync(VanCreateDTO dto) {
        logger.debug("Async creating van with DTO: {}", dto);
        Van entity = mapper.toEntity(dto);
        return vanRepo.createAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to create van asynchronously", throwable);
                    } else {
                        logger.info("Van created asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public Van update(VanUpdateDTO dto) {
        logger.debug("Updating van with DTO: {}", dto);
        Van entity = mapper.toEntity(dto);
        Van result = vanRepo.update(entity);
        logger.info("Van updated with ID: {}", result.getId());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Van> updateAsync(VanUpdateDTO dto) {
        logger.debug("Async updating van with DTO: {}", dto);
        Van entity = mapper.toEntity(dto);
        return vanRepo.updateAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to update van asynchronously", throwable);
                    } else {
                        logger.info("Van updated asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long id) {
        logger.debug("Deleting van with ID: {}", id);
        Van entity = vanRepo.getById(id);
        if (entity != null) {
            vanRepo.delete(entity);
            logger.info("Van deleted with ID: {}", id);
        } else {
            logger.warn("No van found to delete with ID: {}", id);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Void> deleteAsync(Long id) {
        logger.debug("Async deleting van with ID: {}", id);
        return vanRepo.getByIdAsync(id)
                .thenAccept(entity -> {
                    if (entity != null) {
                        vanRepo.deleteAsync(entity)
                                .whenComplete((v, throwable) -> {
                                    if (throwable != null) {
                                        logger.error("Failed to delete van asynchronously", throwable);
                                    } else {
                                        logger.info("Van deleted asynchronously with ID: {}", id);
                                    }
                                });
                    } else {
                        logger.warn("No van found to delete asynchronously with ID: {}", id);
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public Van getById(Long id) {
        logger.debug("Retrieving van with ID: {}", id);
        Van result = vanRepo.getById(id);
        if (result != null) {
            logger.info("Van retrieved with ID: {}", id);
        } else {
            logger.warn("No van found with ID: {}", id);
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<Van> getAll(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving all vans, page: {}, size: {}, orderBy: {}, ascending: {}", page, size, orderBy, ascending);
        List<Van> result = vanRepo.getAll(page, size, orderBy, ascending);
        logger.info("Retrieved {} vans", result.size());
        return result;
    }
}