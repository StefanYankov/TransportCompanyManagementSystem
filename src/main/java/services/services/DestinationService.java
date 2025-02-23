package services.services;

import data.models.transportservices.Destination;
import data.repositories.IGenericRepository;
import services.data.mapping.mappers.DestinationMapper;
import services.data.dto.transportservices.DestinationCreateDTO;
import services.data.dto.transportservices.DestinationUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.services.contracts.IDestinationService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service implementation for managing {@link Destination} entities.
 */
public class DestinationService implements IDestinationService {
    private static final Logger logger = LoggerFactory.getLogger(DestinationService.class);
    private final IGenericRepository<Destination, Long> destinationRepo;
    private final DestinationMapper mapper;

    public DestinationService(IGenericRepository<Destination, Long> destinationRepo, DestinationMapper mapper) {
        this.destinationRepo = destinationRepo;
        this.mapper = mapper;
    }

    /** {@inheritDoc} */
    @Override
    public Destination create(DestinationCreateDTO dto) {
        logger.debug("Creating destination with DTO: {}", dto);
        Destination entity = mapper.toEntity(dto);
        Destination result = destinationRepo.create(entity);
        logger.info("Destination created with ID: {}", result.getId());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Destination> createAsync(DestinationCreateDTO dto) {
        logger.debug("Async creating destination with DTO: {}", dto);
        Destination entity = mapper.toEntity(dto);
        return destinationRepo.createAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to create destination asynchronously", throwable);
                    } else {
                        logger.info("Destination created asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public Destination update(DestinationUpdateDTO dto) {
        logger.debug("Updating destination with DTO: {}", dto);
        Destination entity = mapper.toEntity(dto);
        Destination result = destinationRepo.update(entity);
        logger.info("Destination updated with ID: {}", result.getId());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Destination> updateAsync(DestinationUpdateDTO dto) {
        logger.debug("Async updating destination with DTO: {}", dto);
        Destination entity = mapper.toEntity(dto);
        return destinationRepo.updateAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to update destination asynchronously", throwable);
                    } else {
                        logger.info("Destination updated asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long id) {
        logger.debug("Deleting destination with ID: {}", id);
        Destination entity = destinationRepo.getById(id);
        if (entity != null) {
            destinationRepo.delete(entity);
            logger.info("Destination deleted with ID: {}", id);
        } else {
            logger.warn("No destination found to delete with ID: {}", id);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Void> deleteAsync(Long id) {
        logger.debug("Async deleting destination with ID: {}", id);
        return destinationRepo.getByIdAsync(id)
                .thenAccept(entity -> {
                    if (entity != null) {
                        destinationRepo.deleteAsync(entity)
                                .whenComplete((v, throwable) -> {
                                    if (throwable != null) {
                                        logger.error("Failed to delete destination asynchronously", throwable);
                                    } else {
                                        logger.info("Destination deleted asynchronously with ID: {}", id);
                                    }
                                });
                    } else {
                        logger.warn("No destination found to delete asynchronously with ID: {}", id);
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public Destination getById(Long id) {
        logger.debug("Retrieving destination with ID: {}", id);
        Destination result = destinationRepo.getById(id);
        if (result != null) {
            logger.info("Destination retrieved with ID: {}", id);
        } else {
            logger.warn("No destination found with ID: {}", id);
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<Destination> getAll(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving all destinations, page: {}, size: {}, orderBy: {}, ascending: {}", page, size, orderBy, ascending);
        List<Destination> result = destinationRepo.getAll(page, size, orderBy, ascending);
        logger.info("Retrieved {} destinations", result.size());
        return result;
    }

    /** {@inheritDoc} */
    public CompletableFuture<List<Destination>> getAllAsync(int page, int size) {
        return destinationRepo.getAllAsync(page, size, "startingLocation", true);
    }
}