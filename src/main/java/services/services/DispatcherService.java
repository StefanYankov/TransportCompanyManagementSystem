package services.services;

import data.models.employee.Dispatcher;
import data.models.employee.Driver;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.common.Constants;
import services.data.dto.employees.DispatcherCreateDTO;
import services.data.dto.employees.DispatcherUpdateDTO;
import services.data.dto.employees.DispatcherViewDTO;
import services.data.dto.employees.DriverViewDTO;
import services.data.mapping.mappers.DispatcherMapper;
import services.data.mapping.mappers.DriverMapper;
import services.services.contracts.IDispatcherService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for managing {@link Dispatcher} entities, exposing DTOs instead of entities.
 */
public class DispatcherService implements IDispatcherService {
    private static final Logger logger = LoggerFactory.getLogger(DispatcherService.class);
    private final IGenericRepository<Dispatcher, Long> dispatcherRepo;
    private final IGenericRepository<Driver, Long> driverRepo;
    private final DispatcherMapper dispatcherMapper;
    private final DriverMapper driverMapper;

    public DispatcherService(IGenericRepository<Dispatcher, Long> dispatcherRepo,
                             IGenericRepository<Driver, Long> driverRepo,
                             DispatcherMapper dispatcherMapper,
                             DriverMapper driverMapper) {
        this.dispatcherRepo = dispatcherRepo;
        this.driverRepo = driverRepo;
        this.dispatcherMapper = dispatcherMapper;
        this.driverMapper = driverMapper;
    }

    /** {@inheritDoc} */
    @Override
    public DispatcherViewDTO create(DispatcherCreateDTO dto) {
        if (dto == null) {
            logger.error("Cannot create {}: DTO is null", Constants.DISPATCHER);
            throw new IllegalArgumentException("DispatcherCreateDTO must not be null");
        }
        logger.debug("Creating {} with DTO: {}", Constants.DISPATCHER, dto);
        try {
            Dispatcher dispatcher = dispatcherMapper.toEntity(dto);
            Dispatcher created = dispatcherRepo.create(dispatcher);
            logger.info("{} created with ID: {}", Constants.DISPATCHER, created.getId());
            return dispatcherMapper.toViewDTO(created);
        } catch (RepositoryException e) {
            logger.error("Failed to create {}: {} {}, cause: {}", Constants.DISPATCHER, dto.getFirstName(), dto.getFamilyName(), e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public DispatcherViewDTO update(DispatcherUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            logger.error("Cannot update {}: DTO or ID is null", Constants.DISPATCHER);
            throw new IllegalArgumentException("DispatcherUpdateDTO and ID must not be null");
        }
        logger.debug("Updating {} with DTO: {}", Constants.DISPATCHER, dto);
        try {
            Dispatcher existing = dispatcherRepo.getById(dto.getId(), "supervisedDrivers", "transportCompany")
                    .orElseThrow(() -> new RepositoryException("Dispatcher not found with ID: " + dto.getId()));
            dispatcherMapper.toEntity(dto, existing);
            DispatcherViewDTO result = dispatcherRepo.updateAndMap(existing, dispatcherMapper::toViewDTO, null);
            logger.info("{} updated with ID: {}", Constants.DISPATCHER, result.getId());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to update {} with ID: {}, cause: {}", Constants.DISPATCHER, dto.getId(), e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long id) {
        if (id == null) {
            logger.error("Cannot delete {}: ID is null", Constants.DISPATCHER);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Deleting {} with ID: {}", Constants.DISPATCHER, id);
        try {
            Dispatcher entity = dispatcherRepo.getById(id)
                    .orElseThrow(() -> new RepositoryException("Dispatcher not found with ID: " + id));
            dispatcherRepo.delete(entity);
            logger.info("{} deleted with ID: {}", Constants.DISPATCHER, id);
        } catch (RepositoryException e) {
            logger.error("Failed to delete {} with ID: {}, cause: {}", Constants.DISPATCHER, id, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public DispatcherViewDTO getById(Long id, String... fetchRelations) {
        if (id == null) {
            logger.error("Cannot retrieve {}: ID is null", Constants.DISPATCHER);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Retrieving {} with ID: {}, fetchRelations: {}", Constants.DISPATCHER, id, String.join(",", fetchRelations));
        try {
            Optional<Dispatcher> dispatcher = dispatcherRepo.getById(id, fetchRelations.length > 0 ? fetchRelations : new String[]{"supervisedDrivers"});
            if (dispatcher.isEmpty()) {
                logger.warn("No {} found with ID: {}", Constants.DISPATCHER, id);
                return null;
            }
            logger.info("{} retrieved with ID: {}", Constants.DISPATCHER, id);
            return dispatcherMapper.toViewDTO(dispatcher.get());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} with ID: {}, cause: {}", Constants.DISPATCHER, id, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<DispatcherViewDTO> getAll(int page, int size, String orderBy, boolean ascending, String... fetchRelations) {
        logger.debug("Retrieving all {}: page={}, size={}, orderBy={}, ascending={}, fetchRelations={}",
                Constants.DISPATCHER, page, size, orderBy, ascending, String.join(",", fetchRelations));
        try {
            List<Dispatcher> dispatchers = dispatcherRepo.getAll(page, size, orderBy, ascending,
                    fetchRelations.length > 0 ? fetchRelations : new String[]{"supervisedDrivers"});
            List<DispatcherViewDTO> result = dispatchers.stream().map(dispatcherMapper::toViewDTO).collect(Collectors.toList());
            logger.info("Retrieved {} {}", result.size(), Constants.DISPATCHER);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve all {}, cause: {}", Constants.DISPATCHER, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<DriverViewDTO> getDriversByDispatcher(Long dispatcherId) {
        if (dispatcherId == null) {
            logger.error("Cannot retrieve drivers: Dispatcher ID is null");
            throw new IllegalArgumentException("Dispatcher ID must not be null");
        }
        logger.debug("Retrieving drivers for dispatcher with ID: {}", dispatcherId);
        try {
            List<Driver> drivers = driverRepo.findWithJoin("dispatcher", "id", dispatcherId, "familyName", true, "dispatcher", "qualifications");
            List<DriverViewDTO> result = drivers.stream().map(driverMapper::toViewDTO).collect(Collectors.toList());
            logger.info("Retrieved {} drivers for dispatcher with ID: {}", result.size(), dispatcherId);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve drivers for dispatcher with ID: {}, cause: {}", dispatcherId, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<DispatcherViewDTO> getDispatchersSortedBySalary(int page, int size, boolean ascending, String... fetchRelations) {
        logger.debug("Retrieving {} sorted by salary, page={}, size={}, ascending={}, fetchRelations={}",
                Constants.DISPATCHER, page, size, ascending, String.join(",", fetchRelations));
        try {
            List<Dispatcher> dispatchers = dispatcherRepo.getAll(page, size, "salary", ascending,
                    fetchRelations.length > 0 ? fetchRelations : new String[]{"supervisedDrivers"});
            List<DispatcherViewDTO> result = dispatchers.stream().map(dispatcherMapper::toViewDTO).collect(Collectors.toList());
            logger.info("Retrieved {} {} sorted by salary", result.size(), Constants.DISPATCHER);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} sorted by salary, cause: {}", Constants.DISPATCHER, e.getMessage(), e);
            throw e;
        }
    }
}