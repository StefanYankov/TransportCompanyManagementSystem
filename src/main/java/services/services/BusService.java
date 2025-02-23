package services.services;

import data.models.TransportCompany;
import data.models.vehicles.Bus;
import data.repositories.IGenericRepository;
import services.data.mapping.mappers.BusMapper;
import services.data.dto.vehicles.BusCreateDTO;
import services.data.dto.vehicles.BusUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.services.contracts.IBusService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service implementation for managing {@link Bus} entities.
 */
public class BusService implements IBusService {
    private static final Logger logger = LoggerFactory.getLogger(BusService.class);
    private final IGenericRepository<Bus, Long> busRepo;
    private final BusMapper mapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;

    public BusService(IGenericRepository<Bus, Long> busRepo, BusMapper mapper,
                      IGenericRepository<TransportCompany, Long> companyRepo) {
        this.busRepo = busRepo;
        this.mapper = mapper;
        this.companyRepo = companyRepo;
    }

    /** {@inheritDoc} */
    @Override
    public Bus create(BusCreateDTO dto) {
        logger.debug("Creating bus with DTO: {}", dto);
        Bus entity = mapper.toEntity(dto);
        Bus result = busRepo.create(entity);
        logger.info("Bus created with ID: {}", result.getId());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Bus> createAsync(BusCreateDTO dto) {
        logger.debug("Async creating bus with DTO: {}", dto);
        Bus entity = mapper.toEntity(dto);
        return busRepo.createAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to create bus asynchronously", throwable);
                    } else {
                        logger.info("Bus created asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public Bus update(BusUpdateDTO dto) {
        logger.debug("Updating bus with DTO: {}", dto);
        Bus entity = mapper.toEntity(dto);
        Bus result = busRepo.update(entity);
        logger.info("Bus updated with ID: {}", result.getId());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Bus> updateAsync(BusUpdateDTO dto) {
        logger.debug("Async updating bus with DTO: {}", dto);
        Bus entity = mapper.toEntity(dto);
        return busRepo.updateAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to update bus asynchronously", throwable);
                    } else {
                        logger.info("Bus updated asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long id) {
        logger.debug("Deleting bus with ID: {}", id);
        Bus entity = busRepo.getById(id);
        if (entity != null) {
            busRepo.delete(entity);
            logger.info("Bus deleted with ID: {}", id);
        } else {
            logger.warn("No bus found to delete with ID: {}", id);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Void> deleteAsync(Long id) {
        logger.debug("Async deleting bus with ID: {}", id);
        return busRepo.getByIdAsync(id)
                .thenAccept(entity -> {
                    if (entity != null) {
                        busRepo.deleteAsync(entity)
                                .whenComplete((v, throwable) -> {
                                    if (throwable != null) {
                                        logger.error("Failed to delete bus asynchronously", throwable);
                                    } else {
                                        logger.info("Bus deleted asynchronously with ID: {}", id);
                                    }
                                });
                    } else {
                        logger.warn("No bus found to delete asynchronously with ID: {}", id);
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public Bus getById(Long id) {
        logger.debug("Retrieving bus with ID: {}", id);
        Bus result = busRepo.getById(id);
        if (result != null) {
            logger.info("Bus retrieved with ID: {}", id);
        } else {
            logger.warn("No bus found with ID: {}", id);
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<Bus> getAll(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving all buses, page: {}, size: {}, orderBy: {}, ascending: {}", page, size, orderBy, ascending);
        List<Bus> result = busRepo.getAll(page, size, orderBy, ascending);
        logger.info("Retrieved {} buses", result.size());
        return result;
    }
}