package services.services;

import data.models.TransportCompany;
import data.models.transportservices.TransportCargoService;
import data.models.transportservices.TransportPassengersService;

import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.common.Constants;

import services.data.dto.companies.TransportCompanyCreateDTO;
import services.data.dto.companies.TransportCompanyUpdateDTO;
import services.data.dto.companies.TransportCompanyViewDTO;
import services.data.mapping.mappers.TransportCompanyMapper;
import services.services.contracts.ITransportCompanyService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Service class responsible for handling operations related to transport companies and their employees.
 * This class supports asynchronous operations.
 */
public class TransportCompanyService implements ITransportCompanyService {
    private static final Logger logger = LoggerFactory.getLogger(TransportCompanyService.class);
    private final IGenericRepository<TransportCompany, Long> companyRepo;
    private final IGenericRepository<TransportCargoService, Long> cargoRepo;
    private final IGenericRepository<TransportPassengersService, Long> passengersRepo;
    private final TransportCompanyMapper mapper;

    /**
     * Service for managing transport company entity, handling business logic and coordinating repository operations
     *
     * @param companyRepo    repository for transport company related database operations
     * @param cargoRepo      repository for managing transport cargo service
     * @param passengersRepo repository for managing transport passenger service
     * @param mapper         utility for mapping TransportCompany entities to DTOs and vice versa
     **/
    public TransportCompanyService(IGenericRepository<TransportCompany, Long> companyRepo,
                                   IGenericRepository<TransportCargoService, Long> cargoRepo,
                                   IGenericRepository<TransportPassengersService, Long> passengersRepo,
                                   TransportCompanyMapper mapper) {
        this.companyRepo = companyRepo;
        this.cargoRepo = cargoRepo;
        this.passengersRepo = passengersRepo;
        this.mapper = mapper;
    }

    // ## CRUD Operations

    /**
     * {@inheritDoc}
     */
    public TransportCompanyViewDTO create(TransportCompanyCreateDTO dto) {
        try {
            // Your existing logic to convert DTO to entity and call the repository
            TransportCompany entity = mapper.toEntity(dto);
            companyRepo.create(entity); // Repository call

            return mapper.toViewDTO(entity); // Convert back to DTO and return
        } catch (RepositoryException e) {
            // Log the exception that happened at the repository layer
            logger.error("Failed to create transport company: {}. Cause: {}", dto.getCompanyName(), e.getMessage());

            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    public TransportCompanyViewDTO update(TransportCompanyUpdateDTO dto) {
        logger.debug("Updating {} with DTO: {}", Constants.TRANSPORT_COMPANY, dto);
        if (dto.getId() == 0) {
            throw new IllegalArgumentException("ID must be provided for update");
        }

        // Fetch the existing entity
        TransportCompany existingEntity = companyRepo.getById(dto.getId());
        if (existingEntity == null) {
            throw new RepositoryException("TransportCompany with ID " + dto.getId() + " not found");
        }

        // Map DTO to entity, preserving the version
        TransportCompany updatedEntity = mapper.toEntity(dto);
        updatedEntity.setVersion(existingEntity.getVersion()); // Preserve the version

        // Merge and update
        TransportCompany result = companyRepo.update(updatedEntity);
        logger.info("{} updated with ID: {}", Constants.TRANSPORT_COMPANY, result.getId());

        return mapper.toViewDTO(result);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(Long id) {
        logger.debug("Deleting {} with ID: {}", Constants.TRANSPORT_COMPANY, id);
        TransportCompany entity = companyRepo.getById(id);
        if (entity != null) {
            companyRepo.delete(entity);
            logger.info("{} deleted with ID: {}", Constants.TRANSPORT_COMPANY, id);
        } else {
            logger.warn("No {} found to delete with ID: {}", Constants.TRANSPORT_COMPANY, id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransportCompanyViewDTO getById(Long id) {
        logger.debug("Retrieving transport company with ID: {}", id);
        TransportCompany entity = companyRepo.getById(id);

        if (entity != null) {
            logger.info("Transport company retrieved with ID: {}", id);
        } else {
            logger.warn("No transport company found with ID: {}", id);
        }

        TransportCompanyViewDTO result = mapper.toViewDTO(entity);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransportCompanyViewDTO> getAll(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving all transport companies, page: {}, size: {}, orderBy: {}, ascending: {}", page, size, orderBy, ascending);
        List<TransportCompany> entities = companyRepo.getAll(page, size, orderBy, ascending);
        logger.info("Retrieved {} transport companies", entities.size());

        List<TransportCompanyViewDTO> output = new ArrayList<>();

        for (TransportCompany entity : entities) {
            logger.debug("Mapping entity with ID: {} to DTO", entity.getId());
            output.add(mapper.toViewDTO(entity));
            logger.debug("Mapped entity with ID: {} to DTO", entity.getId());

        }
        return output;
    }

    // ## Operations required by specification

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransportCompany> getCompaniesSortedByRevenue(boolean ascending) {
        logger.debug("Retrieving companies sorted by revenue, ascending: {}", ascending);
        // Uses IGenericRepository’s findWithAggregation for revenue sorting
        List<TransportCompany> result = companyRepo.findWithAggregation("transportServices", "price", "id", ascending);
        logger.info("Retrieved {} companies sorted by revenue", result.size());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getRevenueForPeriod(Long companyId, LocalDate startDate, LocalDate endDate) {
        logger.debug("Calculating revenue for company ID: {} from {} to {}", companyId, startDate, endDate);

        // Define conditions to filter transport services by company and date range
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("transportCompany.id", companyId);

        // Fetch cargo transports for the company
        List<TransportCargoService> cargoServices = cargoRepo.findByCriteria(conditions, "startingDate", true)
                .stream()
                .filter(service -> isWithinDateRange(service.getStartingDate(), startDate, endDate))
                .toList();

        // Fetch passenger transports for the company
        List<TransportPassengersService> passengerServices = passengersRepo.findByCriteria(conditions, "startingDate", true)
                .stream()
                .filter(service -> isWithinDateRange(service.getStartingDate(), startDate, endDate))
                .toList();

        // Calculate total revenue by summing prices from both cargo and passenger services
        BigDecimal cargoRevenue = cargoServices.stream()
                .map(TransportCargoService::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal passengerRevenue = passengerServices.stream()
                .map(TransportPassengersService::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRevenue = cargoRevenue.add(passengerRevenue);
        logger.info("Revenue for company ID {} from {} to {}: {}", companyId, startDate, endDate, totalRevenue);
        return totalRevenue;
    }


    // ## Async CRUD operations ##

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<TransportCompanyViewDTO> createAsync(TransportCompanyCreateDTO dto) {
        logger.debug("Async creating TransportCompany with DTO: {}", dto);

        // Convert DTO to entity
        TransportCompany entity = mapper.toEntity(dto);

        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return companyRepo.create(entity);
                    } catch (RepositoryException e) {
                        // Log the exception that happened during the async operation
                        logger.error("Failed to {} company asynchronously due to error: {}", Constants.TRANSPORT_COMPANY, e.getMessage(), e);
                        // Rethrow the exception
                        throw e;
                    }
                })
                .thenApply(result -> {
                    logger.info("{} created asynchronously with ID: {}", Constants.TRANSPORT_COMPANY, result.getId());
                    return mapper.toViewDTO(result);
                }).exceptionally(throwable -> {
                    logger.error("Failed to create TransportCompanyViewDTO asynchronously", throwable);
                    throw new RepositoryException("Failed to create entity of type TransportCompany", throwable);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Void> deleteAsync(Long id) {
        logger.debug("Async deleting {} with ID: {}", Constants.TRANSPORT_COMPANY, id);

        return CompletableFuture.runAsync(() -> {
                    TransportCompany entity = companyRepo.getById(id); // Synchronous call within async block
                    if (entity != null) {
                        companyRepo.delete(entity); // Synchronous delete
                        logger.info("{} deleted asynchronously with ID: {}", Constants.TRANSPORT_COMPANY, entity.getId());
                    } else {
                        logger.warn("No {} found to delete asynchronously with ID: {}", Constants.TRANSPORT_COMPANY, id);
                    }
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to delete {} asynchronously with ID: {}", Constants.TRANSPORT_COMPANY, id, throwable);
                    throw new RepositoryException("Failed to delete entity of type TransportCompany with ID " + id, throwable);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<TransportCompanyViewDTO> updateAsync(TransportCompanyUpdateDTO dto) {
        logger.debug("Async updating {} with DTO: {}", Constants.TRANSPORT_COMPANY, dto);
        TransportCompany entity = mapper.toEntity(dto);

        return companyRepo.updateAsync(entity)
                .thenApply(result -> {
                    if (result != null) {
                        logger.info("{} updated asynchronously with ID: {}", Constants.TRANSPORT_COMPANY, result.getId());
                        // Mapping to TransportCompanyViewDTO and returning
                        return mapper.toViewDTO(result);
                    } else {
                        // Handle case where update fails or returns null, can also throw an exception if needed
                        logger.error("Failed to update {} asynchronously, no result returned", Constants.TRANSPORT_COMPANY);
                        throw new RepositoryException("Failed to update entity of type TransportCompany");
                    }
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to update {} asynchronously", Constants.TRANSPORT_COMPANY, throwable);
                    // Return null or a default DTO if an error occurs
                    throw new RepositoryException("Failed to update entity of type TransportCompany", throwable);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<TransportCompanyViewDTO> getByIdAsync(Long id) {
        logger.debug("Async retrieving {} with ID: {}", Constants.TRANSPORT_COMPANY, id);
        return companyRepo.getByIdAsync(id)
                .thenApply(entity -> {
                    if (entity != null) {
                        logger.info("Transport company retrieved asynchronously with ID: {}", id);
                    } else {
                        logger.warn("No transport company found asynchronously with ID: {}", id);
                    }
                    return mapper.toViewDTO(entity);
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to retrieve {} asynchronously with ID: {}", Constants.TRANSPORT_COMPANY, id, throwable);
                    throw new RepositoryException("Failed to retrieve entity of type TransportCompany with ID " + id, throwable);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<List<TransportCompanyViewDTO>> getAllAsync(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Async retrieving all transport companies, page: {}, size: {}, orderBy: {}, ascending: {}", page, size, orderBy, ascending);
        return companyRepo.getAllAsync(page, size, orderBy, ascending)
                .thenApply(entities -> {
                    logger.info("Retrieved {} transport companies asynchronously", entities.size());
                    return entities.stream().map(mapper::toViewDTO).toList();
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to retrieve all transport companies asynchronously", throwable);
                    throw new RepositoryException("Failed to retrieve all entities of type TransportCompany", throwable);
                });
    }


    // ## Utility methods ##

    /**
     * Helper method to check if a date falls within a specified range (inclusive).
     *
     * @param date      the date to check
     * @param startDate the start of the range (inclusive)
     * @param endDate   the end of the range (inclusive)
     * @return true if the date is within the range, false otherwise
     */
    private boolean isWithinDateRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return (date != null) && !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}