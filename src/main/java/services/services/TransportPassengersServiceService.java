package services.services;

import data.models.transportservices.TransportPassengersService;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.common.Constants;
import services.data.dto.transportservices.TransportPassengersServiceCreateDTO;
import services.data.dto.transportservices.TransportPassengersServiceUpdateDTO;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;
import services.data.mapping.mappers.TransportPassengersServiceMapper;
import services.services.contracts.ITransportPassengersServiceService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for managing {@link TransportPassengersService} entities,
 * exposing DTOs instead of entities.
 */
public class TransportPassengersServiceService implements ITransportPassengersServiceService {
    private static final Logger logger = LoggerFactory.getLogger(TransportPassengersServiceService.class);
    private final IGenericRepository<TransportPassengersService, Long> transportServiceRepo;
    private final TransportPassengersServiceMapper transportServiceMapper;

    public TransportPassengersServiceService(IGenericRepository<TransportPassengersService, Long> transportServiceRepo,
                                             TransportPassengersServiceMapper transportServiceMapper) {
        this.transportServiceRepo = transportServiceRepo;
        this.transportServiceMapper = transportServiceMapper;
    }

    /** {@inheritDoc} */
    @Override
    public TransportPassengersServiceViewDTO create(TransportPassengersServiceCreateDTO dto) {
        if (dto == null) {
            logger.error("Cannot create {}: DTO is null", Constants.TRANSPORT_PASSENGERS_SERVICE);
            throw new IllegalArgumentException("TransportPassengersServiceCreateDTO must not be null");
        }
        logger.debug("Creating {} with DTO: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, dto);
        try {
            TransportPassengersService entity = transportServiceMapper.toEntity(dto);
            TransportPassengersService created = transportServiceRepo.create(entity);
            logger.info("{} created with ID: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, created.getId());
            return transportServiceMapper.toViewDTO(created);
        } catch (RepositoryException e) {
            logger.error("Failed to create {}: numberOfPassengers={}, cause: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, dto.getNumberOfPassengers(), e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public TransportPassengersServiceViewDTO update(TransportPassengersServiceUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            logger.error("Cannot update {}: DTO or ID is null", Constants.TRANSPORT_PASSENGERS_SERVICE);
            throw new IllegalArgumentException("TransportPassengersServiceUpdateDTO and ID must not be null");
        }
        logger.debug("Updating {} with DTO: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, dto);
        try {
            TransportPassengersService existing = transportServiceRepo.getById(dto.getId(), "destination")
                    .orElseThrow(() -> new RepositoryException("TransportPassengersService not found with ID: " + dto.getId()));
            transportServiceMapper.toEntity(dto, existing);
            TransportPassengersServiceViewDTO result = transportServiceRepo.updateAndMap(existing, transportServiceMapper::toViewDTO, null);
            logger.info("{} updated with ID: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, result.getId());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to update {} with ID: {}, cause: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, dto.getId(), e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long id) {
        if (id == null) {
            logger.error("Cannot delete {}: ID is null", Constants.TRANSPORT_PASSENGERS_SERVICE);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Deleting {} with ID: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, id);
        try {
            TransportPassengersService entity = transportServiceRepo.getById(id)
                    .orElseThrow(() -> new RepositoryException("TransportPassengersService not found with ID: " + id));
            transportServiceRepo.delete(entity);
            logger.info("{} deleted with ID: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, id);
        } catch (RepositoryException e) {
            logger.error("Failed to delete {} with ID: {}, cause: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, id, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public TransportPassengersServiceViewDTO getById(Long id) {
        if (id == null) {
            logger.error("Cannot retrieve {}: ID is null", Constants.TRANSPORT_PASSENGERS_SERVICE);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Retrieving {} with ID: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, id);
        try {
            Optional<TransportPassengersServiceViewDTO> result = transportServiceRepo.getByIdAndMap(id, transportServiceMapper::toViewDTO, null);
            if (result.isEmpty()) {
                logger.warn("No {} found with ID: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, id);
                return null;
            }
            logger.info("{} retrieved with ID: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, id);
            return result.get();
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} with ID: {}, cause: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, id, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<TransportPassengersServiceViewDTO> getAll(int page, int size, String orderBy, boolean ascending, String filter) {
        logger.debug("Retrieving all {}: page={}, size={}, orderBy={}, ascending={}, filter={}", Constants.TRANSPORT_PASSENGERS_SERVICE, page, size, orderBy, ascending, filter);
        try {
            List<TransportPassengersServiceViewDTO> result = transportServiceRepo.getAllAndMap(
                    page, size, orderBy, ascending, transportServiceMapper::toViewDTO, "destination"
            );
            if (filter != null && !filter.trim().isEmpty()) {
                try {
                    int filterPassengers = Integer.parseInt(filter.trim());
                    result = result.stream()
                            .filter(s -> s.getNumberOfPassengers() == filterPassengers)
                            .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    logger.warn("Invalid filter value for numberOfPassengers: {}", filter);
                }
            }
            logger.info("Retrieved {} {}", result.size(), Constants.TRANSPORT_PASSENGERS_SERVICE);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve all {}, cause: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<TransportPassengersServiceViewDTO> getTransportsSortedByDestination(int page, int size, boolean ascending) {
        logger.debug("Retrieving {} sorted by destination, page={}, size={}, ascending={}", Constants.TRANSPORT_PASSENGERS_SERVICE, page, size, ascending);
        try {
            List<TransportPassengersService> services = transportServiceRepo.getAll(page, size, "destination", ascending, "destination");
            List<TransportPassengersServiceViewDTO> result = services.stream()
                    .map(transportServiceMapper::toViewDTO)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} {} sorted by destination", result.size(), Constants.TRANSPORT_PASSENGERS_SERVICE);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} sorted by destination, cause: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getTotalTransportCount() {
        logger.debug("Retrieving total count of {}", Constants.TRANSPORT_PASSENGERS_SERVICE);
        try {
            List<TransportPassengersService> services = transportServiceRepo.getAll(0, Integer.MAX_VALUE, null, true);
            int count = services.size();
            logger.info("Total count of {}: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, count);
            return count;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve total count of {}, cause: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<TransportPassengersServiceViewDTO> getActiveServices(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving active {}: page={}, size={}, orderBy={}, ascending={}", Constants.TRANSPORT_PASSENGERS_SERVICE, page, size, orderBy, ascending);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("startingDate", LocalDate.now());
            conditions.put("delivered", false);
            List<TransportPassengersService> services = transportServiceRepo.findByCriteria(conditions, orderBy, ascending, page, size, "destination");
            List<TransportPassengersServiceViewDTO> result = services.stream()
                    .map(transportServiceMapper::toViewDTO)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} active {}", result.size(), Constants.TRANSPORT_PASSENGERS_SERVICE);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve active {}, cause: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<TransportPassengersServiceViewDTO> getByDriver(Long driverId, int page, int size, String orderBy, boolean ascending) {
        if (driverId == null) {
            logger.error("Cannot retrieve {} by driver: Driver ID is null", Constants.TRANSPORT_PASSENGERS_SERVICE);
            throw new IllegalArgumentException("Driver ID must not be null");
        }
        logger.debug("Retrieving {} by driver ID: {}, page={}, size={}, orderBy={}, ascending={}", Constants.TRANSPORT_PASSENGERS_SERVICE, driverId, page, size, orderBy, ascending);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("driver.id", driverId);
            List<TransportPassengersService> services = transportServiceRepo.findByCriteria(conditions, orderBy, ascending, page, size, "destination");
            List<TransportPassengersServiceViewDTO> result = services.stream()
                    .map(transportServiceMapper::toViewDTO)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} {} for driver ID: {}", result.size(), Constants.TRANSPORT_PASSENGERS_SERVICE, driverId);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} by driver ID: {}, cause: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, driverId, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<TransportPassengersServiceViewDTO> getByClient(Long clientId, int page, int size, String orderBy, boolean ascending) {
        if (clientId == null) {
            logger.error("Cannot retrieve {} by client: Client ID is null", Constants.TRANSPORT_PASSENGERS_SERVICE);
            throw new IllegalArgumentException("Client ID must not be null");
        }
        logger.debug("Retrieving {} by client ID: {}, page={}, size={}, orderBy={}, ascending={}", Constants.TRANSPORT_PASSENGERS_SERVICE, clientId, page, size, orderBy, ascending);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("client.id", clientId);
            List<TransportPassengersService> services = transportServiceRepo.findByCriteria(conditions, orderBy, ascending, page, size, "destination");
            List<TransportPassengersServiceViewDTO> result = services.stream()
                    .map(transportServiceMapper::toViewDTO)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} {} for client ID: {}", result.size(), Constants.TRANSPORT_PASSENGERS_SERVICE, clientId);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} by client ID: {}, cause: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, clientId, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<TransportPassengersServiceViewDTO> getByCompany(Long companyId, int page, int size, String orderBy, boolean ascending) {
        if (companyId == null) {
            logger.error("Cannot retrieve {} by company: Company ID is null", Constants.TRANSPORT_PASSENGERS_SERVICE);
            throw new IllegalArgumentException("Company ID must not be null");
        }
        logger.debug("Retrieving {} by company ID: {}, page={}, size={}, orderBy={}, ascending={}", Constants.TRANSPORT_PASSENGERS_SERVICE, companyId, page, size, orderBy, ascending);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("transportCompany.id", companyId);
            List<TransportPassengersService> services = transportServiceRepo.findByCriteria(conditions, orderBy, ascending, page, size, "destination");
            List<TransportPassengersServiceViewDTO> result = services.stream()
                    .map(transportServiceMapper::toViewDTO)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} {} for company ID: {}", result.size(), Constants.TRANSPORT_PASSENGERS_SERVICE, companyId);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} by company ID: {}, cause: {}", Constants.TRANSPORT_PASSENGERS_SERVICE, companyId, e.getMessage(), e);
            throw e;
        }
    }
}