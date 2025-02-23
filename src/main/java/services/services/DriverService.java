package services.services;

import data.models.TransportCompany;
import data.models.employee.Dispatcher;
import data.models.employee.Driver;
import data.models.employee.Qualification;
import data.models.transportservices.TransportCargoService;
import data.models.transportservices.TransportPassengersService;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.dto.employees.DriverCreateDTO;
import services.data.dto.employees.DriverUpdateDTO;
import services.data.dto.employees.DriverViewDTO;
import services.data.dto.employees.QualificationViewDTO;
import services.data.mapping.mappers.DriverMapper;
import services.services.contracts.IDriverService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * Service implementation for managing {@link Driver} entities, exposing DTOs instead of entities.
 */
public class DriverService implements IDriverService {
    private static final Logger logger = LoggerFactory.getLogger(DriverService.class);
    private final IGenericRepository<Driver, Long> driverRepo;
    private final IGenericRepository<TransportCargoService, Long> cargoRepo;
    private final IGenericRepository<TransportPassengersService, Long> passengersRepo;
    private final DriverMapper mapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;
    private final IGenericRepository<Dispatcher, Long> dispatcherRepo;
    private final IGenericRepository<Qualification, Long> qualificationRepository;

    public DriverService(IGenericRepository<Driver, Long> driverRepo,
                         DriverMapper mapper,
                         IGenericRepository<TransportCompany, Long> companyRepo,
                         IGenericRepository<Dispatcher, Long> dispatcherRepo,
                         IGenericRepository<TransportCargoService, Long> cargoRepo,
                         IGenericRepository<TransportPassengersService, Long> passengersRepo,
                         IGenericRepository<Qualification, Long> qualificationRepository) {
        this.driverRepo = driverRepo;
        this.mapper = mapper;
        this.companyRepo = companyRepo;
        this.dispatcherRepo = dispatcherRepo;
        this.cargoRepo = cargoRepo;
        this.passengersRepo = passengersRepo;
        this.qualificationRepository = qualificationRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DriverViewDTO create(DriverCreateDTO dto) {
        logger.debug("Creating driver with DTO: {}", dto);

        // Convert DTO to entity
        Driver entity = mapper.toEntity(dto);

        // Set qualifications to the driver entity
        for (var qualificationDto : dto.getQualifications()) {
            Qualification qualification = qualificationRepository.getById(qualificationDto.getId());
            entity.getQualifications().add(qualification);
        }

        // Persist driver
        Driver result = driverRepo.create(entity);

        logger.info("Driver created with ID: {}", result.getId());
        return mapper.toViewDTO(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<DriverViewDTO> createAsync(DriverCreateDTO dto) {
        logger.debug("Async creating driver with DTO: {}", dto);

        return CompletableFuture.supplyAsync(() -> {

                    // Convert DTO to entity
                    Driver entity = mapper.toEntity(dto);

                    // Set qualifications to the driver entity
                    for (var qualificationDto : dto.getQualifications()) {
                        Qualification qualification = qualificationRepository.getById(qualificationDto.getId());
                        entity.getQualifications().add(qualification);
                    }

                    return entity;
                }).thenCompose(driverRepo::createAsync)
                .thenApply(mapper::toViewDTO)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to create driver asynchronously", throwable);
                    } else {
                        logger.info("Driver created asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DriverViewDTO update(DriverUpdateDTO dto) {
        logger.debug("Updating driver with DTO: {}", dto);

        // Fetch existing driver
        Driver existingDriver = driverRepo.getById(dto.getId());
        if (existingDriver == null) {
            logger.error("Driver with ID {} not found during update", dto.getId());
            throw new RepositoryException("Driver not found with ID: " + dto.getId());
        }

        // Map simple fields using ModelMapper
        mapper.toEntity(dto);  // This maps fields like name, licenseNumber, etc. from the DTO to the entity.


        // Step 2: Manually handle qualifications
        Set<Qualification> updatedQualifications = dto.getQualificationIds().stream()
                .map(id -> qualificationRepository.getById(id)) // Fetch the qualification objects by their IDs
                .filter(Objects::nonNull) // Filter out any nulls if the qualification ID is invalid
                .collect(Collectors.toSet());
        existingDriver.setQualifications(updatedQualifications); // Set the qualifications on the existing driver


        // Step 3: Persist the updated entity
        Driver result = driverRepo.update(existingDriver); // Update the entity in the repository
        logger.info("Driver updated with ID: {}", result.getId());

        // Step 4: Return the updated DriverViewDTO
        return mapper.toViewDTO(result); // Convert the updated entity to a view DTO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<DriverViewDTO> updateAsync(DriverUpdateDTO dto) {
        logger.debug("Async updating driver with DTO: {}", dto);

        // Fetch the driver from the repo asynchronously
        return CompletableFuture.supplyAsync(() -> {
                    Driver existingDriver = driverRepo.getById(dto.getId());
                    if (existingDriver == null) {
                        logger.error("Driver with ID {} not found during update", dto.getId());
                        throw new RepositoryException("Driver not found with ID: " + dto.getId());
                    }
                    return existingDriver;
                })
                .thenApply(existingDriver -> {
                    // Map the DTO to the existing entity (simple fields)
                    mapper.toEntity(dto);

                    // Handle qualifications (manual part)
                    Set<Qualification> updatedQualifications = dto.getQualificationIds().stream()
                            .map(id -> qualificationRepository.getById(id))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                    existingDriver.setQualifications(updatedQualifications);

                    // Save and return the result
                    Driver result = driverRepo.update(existingDriver);
                    return mapper.toViewDTO(result); // Map to View DTO for response
                })
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to update driver asynchronously", throwable);
                    } else {
                        logger.info("Driver updated asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        logger.debug("Deleting driver with ID: {}", id);
        Driver entity = driverRepo.getById(id);
        if (entity != null) {
            driverRepo.delete(entity);
            logger.info("Driver deleted with ID: {}", id);
        } else {
            logger.warn("No driver found to delete with ID: {}", id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Void> deleteAsync(Long id) {
        logger.debug("Async deleting driver with ID: {}", id);
        return driverRepo.getByIdAsync(id)
                .thenAccept(entity -> {
                    if (entity != null) {
                        driverRepo.deleteAsync(entity)
                                .whenComplete((v, throwable) -> {
                                    if (throwable != null) {
                                        logger.error("Failed to delete driver asynchronously", throwable);
                                    } else {
                                        logger.info("Driver deleted asynchronously with ID: {}", id);
                                    }
                                });
                    } else {
                        logger.warn("No driver found to delete asynchronously with ID: {}", id);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DriverViewDTO getById(Long id) {
        logger.debug("Retrieving driver with ID: {}", id);
        Driver result = driverRepo.getById(id);
        if (result != null) {
            logger.info("Driver retrieved with ID: {}", id);
            return mapper.toViewDTO(result);
        } else {
            logger.warn("No driver found with ID: {}", id);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DriverViewDTO> getAll(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving all drivers, page: {}, size: {}, orderBy: {}, ascending: {}", page, size, orderBy, ascending);
        List<Driver> drivers = driverRepo.getAll(page, size, orderBy, ascending);
        logger.info("Retrieved {} drivers", drivers.size());
        return drivers.stream()
                .map(mapper::toViewDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DriverViewDTO> getDriversByQualification(String qualificationName) {
        logger.debug("Retrieving drivers with qualification: {}", qualificationName);
        List<Driver> result = driverRepo.findWithJoin(
                "qualifications",  // Join field
                "name",            // Join condition field
                qualificationName, // Join condition value
                "familyName",      // Order by
                true,              // Ascending
                true               // Eager fetch qualifications
        );
        logger.info("Retrieved {} drivers with qualification {}", result.size(), qualificationName);
        return result.stream()
                .map(mapper::toViewDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DriverViewDTO> getDriversSortedBySalary(boolean ascending) {
        logger.debug("Retrieving drivers sorted by salary, ascending: {}", ascending);
        List<Driver> result = driverRepo.getAll(0, Integer.MAX_VALUE, "salary", ascending);
        logger.info("Retrieved {} drivers sorted by salary", result.size());
        return result.stream()
                .map(mapper::toViewDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, Integer> getDriverTransportCounts() {
        logger.debug("Retrieving transport counts for all drivers");
        List<Driver> drivers = driverRepo.getAll(0, Integer.MAX_VALUE, "id", true);
        Map<Long, Integer> counts = drivers.stream()
                .collect(Collectors.toMap(
                        Driver::getId,
                        driver -> driverRepo.findWithAggregation("transportServices", "id", "id", true)
                                .stream()
                                .filter(ts -> ts.getId().equals(driver.getId()))
                                .mapToInt(ts -> 1)
                                .sum()
                ));
        logger.info("Retrieved transport counts for {} drivers", counts.size());
        return counts;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getRevenueByDriver(Long driverId) {
        logger.debug("Calculating revenue for driver ID: {}", driverId);

        // Define conditions to filter transport services by driver
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("driver.id", driverId);

        // Step 1: Fetch all cargo transport services where the driver matches the given driverId
        List<TransportCargoService> cargoServices = cargoRepo.findByCriteria(conditions, "startingDate", true);

        // Step 2: Fetch all passenger transport services where the driver matches the given driverId
        List<TransportPassengersService> passengerServices = passengersRepo.findByCriteria(conditions, "startingDate", true);

        // Step 3: Calculate revenue from cargo services by summing their prices
        BigDecimal cargoRevenue = cargoServices.stream()
                .map(TransportCargoService::getPrice) // Get the price field from each cargo service
                .filter(Objects::nonNull) // Exclude null prices to avoid NullPointerException
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum all prices, starting from zero

        // Step 4: Calculate revenue from passenger services by summing their prices
        BigDecimal passengerRevenue = passengerServices.stream()
                .map(TransportPassengersService::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Step 5: Combine cargo and passenger revenue to get the total revenue for the driver
        BigDecimal totalRevenue = cargoRevenue.add(passengerRevenue);

        logger.info("Revenue for driver ID {}: {}", driverId, totalRevenue);
        return totalRevenue;
    }
}