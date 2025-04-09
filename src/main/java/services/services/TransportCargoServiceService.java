package services.services;

import data.models.transportservices.TransportCargoService;
import data.models.TransportCompany;
import data.models.Client;
import data.models.employee.Driver;
import data.models.transportservices.Destination;
import data.models.vehicles.Vehicle;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.common.Constants;
import services.data.dto.transportservices.TransportCargoServiceCreateDTO;
import services.data.dto.transportservices.TransportCargoServiceUpdateDTO;
import services.data.dto.transportservices.TransportCargoServiceViewDTO;
import services.data.mapping.mappers.TransportCargoServiceMapper;
import services.services.contracts.ITransportCargoServiceService;

import java.util.*;
import java.util.stream.Collectors;

public class TransportCargoServiceService implements ITransportCargoServiceService {
    private static final Logger logger = LoggerFactory.getLogger(TransportCargoServiceService.class);
    private final IGenericRepository<TransportCargoService, Long> cargoServiceRepo;
    private final TransportCargoServiceMapper mapper;

    public TransportCargoServiceService(IGenericRepository<TransportCargoService, Long> cargoServiceRepo,
                                        IGenericRepository<TransportCompany, Long> companyRepo,
                                        IGenericRepository<Client, Long> clientRepo,
                                        IGenericRepository<Driver, Long> driverRepo,
                                        IGenericRepository<Destination, Long> destinationRepo,
                                        IGenericRepository<Vehicle, Long> vehicleRepo) {
        this.cargoServiceRepo = cargoServiceRepo;
        this.mapper = new TransportCargoServiceMapper(companyRepo, clientRepo, driverRepo, destinationRepo, vehicleRepo);
    }

    @Override
    public TransportCargoServiceViewDTO create(TransportCargoServiceCreateDTO dto) {
        if (dto == null) {
            logger.error("Cannot create {}: DTO is null", Constants.TRANSPORT_CARGO_SERVICE);
            throw new IllegalArgumentException("TransportCargoServiceCreateDTO must not be null");
        }
        logger.debug("Creating {} with DTO: {}", Constants.TRANSPORT_CARGO_SERVICE, dto);
        try {
            TransportCargoService entity = mapper.toEntity(dto);
            TransportCargoService created = cargoServiceRepo.create(entity);
            logger.info("{} created with ID: {}", Constants.TRANSPORT_CARGO_SERVICE, created.getId());
            return mapper.toViewDTO(created);
        } catch (RepositoryException e) {
            logger.error("Failed to create {}: startingDate={}, cause: {}", Constants.TRANSPORT_CARGO_SERVICE, dto.getStartingDate(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public TransportCargoServiceViewDTO update(TransportCargoServiceUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            logger.error("Cannot update {}: DTO or ID is null", Constants.TRANSPORT_CARGO_SERVICE);
            throw new IllegalArgumentException("TransportCargoServiceUpdateDTO and ID must not be null");
        }
        logger.debug("Updating {} with DTO: {}", Constants.TRANSPORT_CARGO_SERVICE, dto);
        try {
            TransportCargoService existing = cargoServiceRepo.getById(dto.getId())
                    .orElseThrow(() -> new RepositoryException("TransportCargoService not found with ID: " + dto.getId()));
            TransportCargoService updatedEntity = mapper.toEntity(dto);
            // Preserve version for optimistic locking
            updatedEntity.setVersion(existing.getVersion());
            TransportCargoServiceViewDTO result = cargoServiceRepo.updateAndMap(updatedEntity, mapper::toViewDTO, null);
            logger.info("{} updated with ID: {}", Constants.TRANSPORT_CARGO_SERVICE, result.getId());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to update {} with ID: {}, cause: {}", Constants.TRANSPORT_CARGO_SERVICE, dto.getId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            logger.error("Cannot delete {}: ID is null", Constants.TRANSPORT_CARGO_SERVICE);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Deleting {} with ID: {}", Constants.TRANSPORT_CARGO_SERVICE, id);
        try {
            TransportCargoService entity = cargoServiceRepo.getById(id)
                    .orElseThrow(() -> new RepositoryException("TransportCargoService not found with ID: " + id));
            cargoServiceRepo.delete(entity);
            logger.info("{} deleted with ID: {}", Constants.TRANSPORT_CARGO_SERVICE, id);
        } catch (RepositoryException e) {
            logger.error("Failed to delete {} with ID: {}, cause: {}", Constants.TRANSPORT_CARGO_SERVICE, id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public TransportCargoServiceViewDTO getById(Long id) {
        if (id == null) {
            logger.error("Cannot retrieve {}: ID is null", Constants.TRANSPORT_CARGO_SERVICE);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Retrieving {} with ID: {}", Constants.TRANSPORT_CARGO_SERVICE, id);
        try {
            Optional<TransportCargoServiceViewDTO> result = cargoServiceRepo.getByIdAndMap(id, mapper::toViewDTO, null);
            if (result.isEmpty()) {
                logger.warn("No {} found with ID: {}", Constants.TRANSPORT_CARGO_SERVICE, id);
                return null;
            }
            logger.info("{} retrieved with ID: {}", Constants.TRANSPORT_CARGO_SERVICE, id);
            return result.get();
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} with ID: {}, cause: {}", Constants.TRANSPORT_CARGO_SERVICE, id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TransportCargoServiceViewDTO> getAll(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving all {}: page={}, size={}, orderBy={}, ascending={}", Constants.TRANSPORT_CARGO_SERVICE, page, size, orderBy, ascending);
        try {
            List<TransportCargoServiceViewDTO> result = cargoServiceRepo.getAllAndMap(page, size, orderBy, ascending, mapper::toViewDTO, null);
            logger.info("Retrieved {} {}", result.size(), Constants.TRANSPORT_CARGO_SERVICE);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve all {}, cause: {}", Constants.TRANSPORT_CARGO_SERVICE, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TransportCargoServiceViewDTO> getByCompany(Long companyId, int page, int size) {
        if (companyId == null) {
            logger.error("Cannot retrieve {}: Company ID is null", Constants.TRANSPORT_CARGO_SERVICE);
            throw new IllegalArgumentException("Company ID must not be null");
        }
        if (page < 0 || size <= 0) {
            logger.debug("Invalid pagination for {} by company ID: {}, page: {}, size: {}, returning empty list",
                    Constants.TRANSPORT_CARGO_SERVICE, companyId, page, size);
            return Collections.emptyList();
        }
        logger.debug("Retrieving {} by company ID: {}, page: {}, size: {}", Constants.TRANSPORT_CARGO_SERVICE, companyId, page, size);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("transportCompany.id", companyId);
            List<TransportCargoService> services = cargoServiceRepo.findByCriteria(conditions, "startingDate", true, page, size);
            return services.stream().map(mapper::toViewDTO).collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} by company ID: {}, cause: {}", Constants.TRANSPORT_CARGO_SERVICE, companyId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TransportCargoServiceViewDTO> getByClient(Long clientId, int page, int size) {
        if (clientId == null) {
            logger.error("Cannot retrieve {}: Client ID is null", Constants.TRANSPORT_CARGO_SERVICE);
            throw new IllegalArgumentException("Client ID must not be null");
        }
        if (page < 0 || size <= 0) {
            logger.debug("Invalid pagination for {} by client ID: {}, page: {}, size: {}, returning empty list",
                    Constants.TRANSPORT_CARGO_SERVICE, clientId, page, size);
            return Collections.emptyList();
        }
        logger.debug("Retrieving {} by client ID: {}, page: {}, size: {}", Constants.TRANSPORT_CARGO_SERVICE, clientId, page, size);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("client.id", clientId);
            List<TransportCargoService> services = cargoServiceRepo.findByCriteria(conditions, "startingDate", true, page, size);
            return services.stream().map(mapper::toViewDTO).collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} by client ID: {}, cause: {}", Constants.TRANSPORT_CARGO_SERVICE, clientId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TransportCargoServiceViewDTO> getByDriver(Long driverId, int page, int size) {
        if (driverId == null) {
            logger.error("Cannot retrieve {}: Driver ID is null", Constants.TRANSPORT_CARGO_SERVICE);
            throw new IllegalArgumentException("Driver ID must not be null");
        }
        if (page < 0 || size <= 0) {
            logger.debug("Invalid pagination for {} by driver ID: {}, page: {}, size: {}, returning empty list",
                    Constants.TRANSPORT_CARGO_SERVICE, driverId, page, size);
            return Collections.emptyList();
        }
        logger.debug("Retrieving {} by driver ID: {}, page: {}, size: {}", Constants.TRANSPORT_CARGO_SERVICE, driverId, page, size);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("driver.id", driverId);
            List<TransportCargoService> services = cargoServiceRepo.findByCriteria(conditions, "startingDate", true, page, size);
            return services.stream().map(mapper::toViewDTO).collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} by driver ID: {}, cause: {}", Constants.TRANSPORT_CARGO_SERVICE, driverId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TransportCargoServiceViewDTO> getActiveServices(int page, int size) {
        if (page < 0 || size <= 0) {
            logger.debug("Invalid pagination for active {}: page: {}, size: {}, returning empty list",
                    Constants.TRANSPORT_CARGO_SERVICE, page, size);
            return Collections.emptyList();
        }
        logger.debug("Retrieving active {}: page={}, size={}", Constants.TRANSPORT_CARGO_SERVICE, page, size);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("delivered", false);
            List<TransportCargoService> services = cargoServiceRepo.findByCriteria(conditions, "startingDate", true, page, size);
            return services.stream().map(mapper::toViewDTO).collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve active {}, cause: {}", Constants.TRANSPORT_CARGO_SERVICE, e.getMessage(), e);
            throw e;
        }
    }

}