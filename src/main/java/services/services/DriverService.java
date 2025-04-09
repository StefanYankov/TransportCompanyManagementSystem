package services.services;

import data.models.TransportCompany;
import data.models.employee.Dispatcher;
import data.models.employee.Driver;
import data.models.employee.Qualification;
import data.models.transportservices.TransportCargoService;
import data.models.transportservices.TransportPassengersService;
import data.models.transportservices.TransportService;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.common.Constants;
import services.data.dto.employees.DriverCreateDTO;
import services.data.dto.employees.DriverUpdateDTO;
import services.data.dto.employees.DriverViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.mapping.mappers.DriverMapper;
import services.data.mapping.mappers.TransportCargoServiceMapper;
import services.data.mapping.mappers.TransportPassengersServiceMapper;
import services.services.contracts.IDriverService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing {@link Driver} entities, exposing DTOs instead of entities.
 * Uses fetchRelations to eagerly load relationships in the repository layer, avoiding Hibernate leakage.
 */
public class DriverService implements IDriverService {
    private static final Logger logger = LoggerFactory.getLogger(DriverService.class);
    private final IGenericRepository<Driver, Long> driverRepo;
    private final IGenericRepository<TransportCargoService, Long> cargoRepo;
    private final IGenericRepository<TransportPassengersService, Long> passengersRepo;
    private final IGenericRepository<TransportCompany, Long> companyRepo;
    private final IGenericRepository<Dispatcher, Long> dispatcherRepo;
    private final IGenericRepository<Qualification, Long> qualificationRepo;
    private final DriverMapper driverMapper;
    private final TransportCargoServiceMapper cargoServiceMapper;
    private final TransportPassengersServiceMapper passengersServiceMapper;

    public DriverService(IGenericRepository<Driver, Long> driverRepo,
                         IGenericRepository<TransportCompany, Long> companyRepo,
                         IGenericRepository<Dispatcher, Long> dispatcherRepo,
                         IGenericRepository<TransportCargoService, Long> cargoRepo,
                         IGenericRepository<TransportPassengersService, Long> passengersRepo,
                         IGenericRepository<Qualification, Long> qualificationRepo,
                         DriverMapper driverMapper,
                         TransportCargoServiceMapper cargoServiceMapper,
                         TransportPassengersServiceMapper passengersServiceMapper) {
        this.driverRepo = driverRepo;
        this.companyRepo = companyRepo;
        this.dispatcherRepo = dispatcherRepo;
        this.cargoRepo = cargoRepo;
        this.passengersRepo = passengersRepo;
        this.qualificationRepo = qualificationRepo;
        this.driverMapper = driverMapper;
        this.cargoServiceMapper = cargoServiceMapper;
        this.passengersServiceMapper = passengersServiceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DriverViewDTO create(DriverCreateDTO dto) {
        if (dto == null) {
            logger.error("Cannot create {}: DTO is null", Constants.DRIVER);
            throw new IllegalArgumentException("DriverCreateDTO must not be null");
        }
        logger.debug("Creating {} with DTO: {}", Constants.DRIVER, dto);
        try {
            Driver driver = driverMapper.toEntity(dto);
            Driver created = driverRepo.create(driver);
            logger.info("{} created with ID: {}", Constants.DRIVER, created.getId());
            return driverMapper.toViewDTO(created);
        } catch (RepositoryException e) {
            logger.error("Failed to create {}: {} {}, cause: {}", Constants.DRIVER, dto.getFirstName(), dto.getFamilyName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DriverViewDTO update(DriverUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            logger.error("Cannot update {}: DTO or ID is null", Constants.DRIVER);
            throw new IllegalArgumentException("DriverUpdateDTO and ID must not be null");
        }
        logger.debug("Updating {} with DTO: {}", Constants.DRIVER, dto);
        try {
            // Fetch with qualifications and transportCompany to avoid LazyInitializationException
            Driver existing = driverRepo.getById(dto.getId(), "qualifications", "transportCompany")
                    .orElseThrow(() -> new RepositoryException("Driver not found with ID: " + dto.getId()));
            driverMapper.toEntity(dto, existing);
            Driver updated = driverRepo.update(existing);
            logger.info("{} updated with ID: {}", Constants.DRIVER, updated.getId());
            return driverMapper.toViewDTO(updated);
        } catch (RepositoryException e) {
            logger.error("Failed to update {} with ID: {}, cause: {}", Constants.DRIVER, dto.getId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        if (id == null) {
            logger.error("Cannot delete {}: ID is null", Constants.DRIVER);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Deleting {} with ID: {}", Constants.DRIVER, id);
        try {
            Driver entity = driverRepo.getById(id)
                    .orElseThrow(() -> new RepositoryException("Driver not found with ID: " + id));
            driverRepo.delete(entity);
            logger.info("{} deleted with ID: {}", Constants.DRIVER, id);
        } catch (RepositoryException e) {
            logger.error("Failed to delete {} with ID: {}, cause: {}", Constants.DRIVER, id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DriverViewDTO getById(Long id, String... fetchRelations) {
        if (id == null) {
            logger.error("Cannot retrieve {}: ID is null", Constants.DRIVER);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Retrieving {} with ID: {}, fetchRelations: {}", Constants.DRIVER, id, String.join(",", fetchRelations));
        try {
            // Ensure qualifications are fetched for ViewDTO
            Optional<Driver> driver = driverRepo.getById(id, fetchRelations.length > 0 ? fetchRelations : new String[]{"qualifications"});
            if (driver.isEmpty()) {
                logger.warn("No {} found with ID: {}", Constants.DRIVER, id);
                return null;
            }
            logger.info("{} retrieved with ID: {}", Constants.DRIVER, id);
            return driverMapper.toViewDTO(driver.get());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} with ID: {}, cause: {}", Constants.DRIVER, id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DriverViewDTO> getAll(int page, int size, String orderBy, boolean ascending, String... fetchRelations) {
        logger.debug("Retrieving all {}: page={}, size={}, orderBy={}, ascending={}, fetchRelations={}",
                Constants.DRIVER, page, size, orderBy, ascending, String.join(",", fetchRelations));
        try {
            // Fetch qualifications by default if not specified
            List<Driver> drivers = driverRepo.getAll(page, size, orderBy, ascending,
                    fetchRelations.length > 0 ? fetchRelations : new String[]{"qualifications"});
            List<DriverViewDTO> result = drivers.stream().map(driverMapper::toViewDTO).collect(Collectors.toList());
            logger.info("Retrieved {} {}", result.size(), Constants.DRIVER);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve all {}, cause: {}", Constants.DRIVER, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DriverViewDTO> getDriversByQualification(String qualificationName) {
        logger.debug("Retrieving {} with qualification: {}", Constants.DRIVER, qualificationName);
        try {
            // Already fetches qualifications via join
            List<Driver> result = driverRepo.findWithJoin("qualifications", "name", qualificationName, "familyName", true, "qualifications");
            logger.info("Retrieved {} {} with qualification {}", result.size(), Constants.DRIVER, qualificationName);
            return result.stream().map(driverMapper::toViewDTO).collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} with qualification {}, cause: {}", Constants.DRIVER, qualificationName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DriverViewDTO> getDriversSortedBySalary(boolean ascending, String... fetchRelations) {
        logger.debug("Retrieving {} sorted by salary, ascending: {}, fetchRelations: {}",
                Constants.DRIVER, ascending, String.join(",", fetchRelations));
        try {
            List<Driver> drivers = driverRepo.getAll(0, Integer.MAX_VALUE, "salary", ascending,
                    fetchRelations.length > 0 ? fetchRelations : new String[]{"qualifications"});
            List<DriverViewDTO> result = drivers.stream().map(driverMapper::toViewDTO).collect(Collectors.toList());
            logger.info("Retrieved {} {} sorted by salary", result.size(), Constants.DRIVER);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} sorted by salary, cause: {}", Constants.DRIVER, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, Integer> getDriverTransportCounts() {
        logger.debug("Retrieving transport counts for all {}", Constants.DRIVER);
        try {
            List<Driver> drivers = driverRepo.getAll(0, Integer.MAX_VALUE, "id", true);
            Map<Long, Integer> counts = new HashMap<>();
            for (Driver driver : drivers) {
                Map<String, Object> conditions = new HashMap<>();
                conditions.put("driver.id", driver.getId());
                int cargoCount = cargoRepo.findByCriteria(conditions, null, true).size();
                int passengerCount = passengersRepo.findByCriteria(conditions, null, true).size();
                counts.put(driver.getId(), cargoCount + passengerCount);
            }
            logger.info("Retrieved transport counts for {} {}", counts.size(), Constants.DRIVER);
            return counts;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve transport counts for {}, cause: {}", Constants.DRIVER, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getRevenueByDriver(Long driverId) {
        logger.debug("Calculating revenue for {} ID: {}", Constants.DRIVER, driverId);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("driver.id", driverId);
            List<TransportCargoService> cargoServices = cargoRepo.findByCriteria(conditions, "startingDate", true);
            List<TransportPassengersService> passengerServices = passengersRepo.findByCriteria(conditions, "startingDate", true);
            BigDecimal cargoRevenue = cargoServices.stream()
                    .map(TransportCargoService::getPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal passengerRevenue = passengerServices.stream()
                    .map(TransportPassengersService::getPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalRevenue = cargoRevenue.add(passengerRevenue);
            logger.info("Revenue calculated for {} ID {}: {}", Constants.DRIVER, driverId, totalRevenue);
            return totalRevenue;
        } catch (RepositoryException e) {
            logger.error("Failed to calculate revenue for {} ID: {}, cause: {}", Constants.DRIVER, driverId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, Integer> getDriverTripCounts(boolean orderByCount, boolean ascending, int page, int size) {
        logger.debug("Retrieving trip counts for all drivers with orderByCount: {}, ascending: {}, page: {}, size: {}",
                orderByCount, ascending, page, size);
        try {
            Map<Long, Long> counts = driverRepo.countRelatedEntities(TransportService.class, "transportServices",
                    "id", "id", page, size, orderByCount ? "count" : "id", ascending);
            return counts.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve driver trip counts, cause: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DriverViewDTO> getDriversByDispatcher(Long dispatcherId) {
        if (dispatcherId == null) {
            logger.error("Cannot retrieve drivers: Dispatcher ID is null");
            throw new IllegalArgumentException("Dispatcher ID must not be null");
        }
        logger.debug("Retrieving drivers for dispatcher with ID: {}", dispatcherId);
        try {
            List<Driver> result = driverRepo.findWithJoin("dispatcher", "id", dispatcherId, "familyName", true, "dispatcher", "qualifications");
            logger.info("Retrieved {} drivers for dispatcher with ID: {}", result.size(), dispatcherId);
            return result.stream().map(driverMapper::toViewDTO).collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve drivers for dispatcher with ID: {}, cause: {}", dispatcherId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DriverViewDTO> getDriversByCompany(Long companyId, int page, int size, String orderBy, boolean ascending, String... fetchRelations) {
        if (companyId == null) {
            logger.error("Cannot retrieve drivers: Company ID is null");
            throw new IllegalArgumentException("Company ID must not be null");
        }
        logger.debug("Retrieving drivers for company with ID: {}, page: {}, size: {}, orderBy: {}, ascending: {}, fetchRelations: {}",
                companyId, page, size, orderBy, ascending, String.join(",", fetchRelations));
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("transportCompany.id", companyId);
            List<Driver> drivers = driverRepo.findByCriteria(conditions, orderBy, ascending,
                    fetchRelations.length > 0 ? fetchRelations : new String[]{"qualifications"});
            List<DriverViewDTO> result = drivers.stream()
                    .map(driverMapper::toViewDTO)
                    .skip((long) page * size)
                    .limit(size)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} drivers for company with ID: {}", result.size(), companyId);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve drivers for company with ID: {}, cause: {}", companyId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransportServiceViewDTO> getTransportServicesForDriver(Long driverId, int page, int size) {
        if (driverId == null) {
            logger.error("Cannot retrieve transport services: Driver ID is null");
            throw new IllegalArgumentException("Driver ID must not be null");
        }
        if (page < 0 || size <= 0) {
            logger.debug("Invalid pagination for {} with ID: {}, page: {}, size: {}, returning empty list", Constants.DRIVER, driverId, page, size);
            return Collections.emptyList();
        }
        logger.debug("Retrieving transport services for {} with ID: {}, page: {}, size: {}", Constants.DRIVER, driverId, page, size);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("driver.id", driverId);
            List<TransportCargoService> cargoServices = cargoRepo.findByCriteria(conditions, "startingDate", true, page, size);
            List<TransportPassengersService> passengerServices = passengersRepo.findByCriteria(conditions, "startingDate", true, page, size);

            List<TransportServiceViewDTO> combinedServices = new ArrayList<>();
            combinedServices.addAll(cargoServices.stream()
                    .map(cargoServiceMapper::toViewDTO)
                    .toList());
            combinedServices.addAll(passengerServices.stream()
                    .map(passengersServiceMapper::toViewDTO)
                    .toList());

            // Apply pagination (already handled by repository, but kept for clarity)
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, combinedServices.size());
            List<TransportServiceViewDTO> paginatedServices = fromIndex < combinedServices.size() ?
                    combinedServices.subList(fromIndex, toIndex) :
                    Collections.emptyList();

            logger.info("Retrieved {} transport services for {} with ID: {}", paginatedServices.size(), Constants.DRIVER, driverId);
            return paginatedServices;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve transport services for {} with ID: {}, cause: {}", Constants.DRIVER, driverId, e.getMessage());
            throw e;
        }
    }
}