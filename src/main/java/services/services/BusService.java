package services.services;

import data.models.TransportCompany;
import data.models.transportservices.TransportPassengersService;
import data.models.vehicles.Bus;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.common.Constants;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;
import services.data.dto.vehicles.BusCreateDTO;
import services.data.dto.vehicles.BusUpdateDTO;
import services.data.dto.vehicles.BusViewDTO;
import services.data.mapping.mappers.BusMapper;
import services.data.mapping.mappers.TransportPassengersServiceMapper;
import services.services.contracts.IBusService;

import java.util.*;
import java.util.stream.Collectors;

public class BusService implements IBusService {
    private static final Logger logger = LoggerFactory.getLogger(BusService.class);
    private final IGenericRepository<Bus, Long> busRepo;
    private final IGenericRepository<TransportCompany, Long> companyRepo;
    private final IGenericRepository<TransportPassengersService, Long> transportServiceRepo;
    private final BusMapper busMapper;
    private final TransportPassengersServiceMapper transportServiceMapper;

    public BusService(IGenericRepository<Bus, Long> busRepo,
                      IGenericRepository<TransportCompany, Long> companyRepo,
                      IGenericRepository<TransportPassengersService, Long> transportServiceRepo,
                      BusMapper busMapper,
                      TransportPassengersServiceMapper transportServiceMapper) {
        this.busRepo = busRepo;
        this.companyRepo = companyRepo;
        this.transportServiceRepo = transportServiceRepo;
        this.busMapper = busMapper;
        this.transportServiceMapper = transportServiceMapper;
    }

    @Override
    public BusViewDTO create(BusCreateDTO dto) {
        if (dto == null) {
            logger.error("Cannot create {}: DTO is null", Constants.BUS);
            throw new IllegalArgumentException("BusCreateDTO must not be null");
        }
        logger.debug("Creating {} with DTO: {}", Constants.BUS, dto);
        try {
            Bus entity = busMapper.toEntity(dto);
            Bus created = busRepo.create(entity);
            logger.info("{} created with ID: {}", Constants.BUS, created.getId());
            return busMapper.toViewDTO(created);
        } catch (RepositoryException e) {
            logger.error("Failed to create {} with registration plate: {}, cause: {}", Constants.BUS, dto.getRegistrationPlate(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BusViewDTO update(BusUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            logger.error("Cannot update {}: DTO or ID is null", Constants.BUS);
            throw new IllegalArgumentException("BusUpdateDTO and ID must not be null");
        }
        logger.debug("Updating {} with DTO: {}", Constants.BUS, dto);
        try {
            Bus existing = busRepo.getById(dto.getId())
                    .orElseThrow(() -> new RepositoryException("Bus not found with ID: " + dto.getId()));
            busMapper.toEntity(dto, existing);
            BusViewDTO result = busRepo.updateAndMap(existing, busMapper::toViewDTO, null);
            logger.info("{} updated with ID: {}", Constants.BUS, result.getId());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to update {} with ID: {}, cause: {}", Constants.BUS, dto.getId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            logger.error("Cannot delete {}: ID is null", Constants.BUS);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Deleting {} with ID: {}", Constants.BUS, id);
        try {
            Bus entity = busRepo.getById(id)
                    .orElseThrow(() -> new RepositoryException("Bus not found with ID: " + id));
            busRepo.delete(entity);
            logger.info("{} deleted with ID: {}", Constants.BUS, id);
        } catch (RepositoryException e) {
            logger.error("Failed to delete {} with ID: {}, cause: {}", Constants.BUS, id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BusViewDTO getById(Long id) {
        if (id == null) {
            logger.error("Cannot retrieve {}: ID is null", Constants.BUS);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Retrieving {} with ID: {}", Constants.BUS, id);
        try {
            Optional<BusViewDTO> result = busRepo.getByIdAndMap(id, busMapper::toViewDTO, null);
            if (result.isEmpty()) {
                logger.warn("No {} found with ID: {}", Constants.BUS, id);
                return null;
            }
            logger.info("{} retrieved with ID: {}", Constants.BUS, id);
            return result.get();
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} with ID: {}, cause: {}", Constants.BUS, id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<BusViewDTO> getAll(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving all {}: page={}, size={}, orderBy={}, ascending={}", Constants.BUS, page, size, orderBy, ascending);
        try {
            List<BusViewDTO> result = busRepo.getAllAndMap(page, size, orderBy, ascending, busMapper::toViewDTO, null);
            logger.info("Retrieved {} {}", result.size(), Constants.BUS);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve all {}, cause: {}", Constants.BUS, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TransportPassengersServiceViewDTO> getTransportServicesForBus(Long busId, int page, int pageSize) {
        if (busId == null) {
            logger.error("Cannot retrieve transport services: Bus ID is null");
            throw new IllegalArgumentException("Bus ID must not be null");
        }
        if (page < 0 || pageSize <= 0) {
            logger.debug("Invalid pagination for {} with ID: {}, page: {}, pageSize: {}, returning empty list", Constants.BUS, busId, page, pageSize);
            return Collections.emptyList();
        }
        logger.debug("Retrieving transport services for {} with ID: {}, page: {}, pageSize: {}", Constants.BUS, busId, page, pageSize);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("vehicle.id", busId);
            List<TransportPassengersService> transportServices = transportServiceRepo.findByCriteria(conditions, "startingDate", true, page, pageSize);
            return transportServices.stream()
                    .map(transportServiceMapper::toViewDTO)
                    .collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve transport services for {} with ID: {}, cause: {}", Constants.BUS, busId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TransportPassengersServiceViewDTO> getActiveTransportServices(Long busId, int page, int pageSize) {
        if (busId == null) {
            logger.error("Cannot retrieve active transport services: Bus ID is null");
            throw new IllegalArgumentException("Bus ID must not be null");
        }
        if (page < 0 || pageSize <= 0) {
            logger.debug("Invalid pagination for {} with ID: {}, page: {}, pageSize: {}, returning empty list", Constants.BUS, busId, page, pageSize);
            return Collections.emptyList();
        }
        logger.debug("Retrieving active transport services for {} with ID: {}, page: {}, pageSize: {}", Constants.BUS, busId, page, pageSize);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("vehicle.id", busId);
            conditions.put("delivered", false);
            List<TransportPassengersService> transportServices = transportServiceRepo.findByCriteria(conditions, "startingDate", true, page, pageSize);
            return transportServices.stream()
                    .map(transportServiceMapper::toViewDTO)
                    .collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve active transport services for {} with ID: {}, cause: {}", Constants.BUS, busId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<BusViewDTO> getBusesByCompany(Long companyId, int page, int pageSize) {
        if (companyId == null) {
            logger.error("Cannot retrieve buses: Company ID is null");
            throw new IllegalArgumentException("Company ID must not be null");
        }
        logger.debug("Retrieving buses for company with ID: {}, page: {}, pageSize: {}", companyId, page, pageSize);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("transportCompany.id", companyId);
            List<Bus> buses = busRepo.findByCriteria(conditions, "registrationPlate", true, page, pageSize);
            return buses.stream()
                    .map(busMapper::toViewDTO)
                    .collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve buses for company with ID: {}, cause: {}", companyId, e.getMessage(), e);
            throw e;
        }
    }
}