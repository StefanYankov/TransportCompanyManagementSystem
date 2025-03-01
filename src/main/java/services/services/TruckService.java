package services.services;

import data.models.TransportCompany;
import data.models.transportservices.TransportCargoService;
import data.models.vehicles.Truck;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.common.Constants;
import services.data.dto.transportservices.TransportCargoServiceViewDTO;
import services.data.dto.vehicles.TruckCreateDTO;
import services.data.dto.vehicles.TruckUpdateDTO;
import services.data.dto.vehicles.TruckViewDTO;
import services.data.mapping.mappers.TransportCargoServiceMapper;
import services.data.mapping.mappers.TruckMapper;
import services.services.contracts.ITruckService;

import java.util.*;
import java.util.stream.Collectors;

public class TruckService implements ITruckService {
    private static final Logger logger = LoggerFactory.getLogger(TruckService.class);
    private final IGenericRepository<Truck, Long> truckRepo;
    private final IGenericRepository<TransportCompany, Long> companyRepo;
    private final IGenericRepository<TransportCargoService, Long> transportServiceRepo;
    private final TruckMapper truckMapper;
    private final TransportCargoServiceMapper transportServiceMapper;

    public TruckService(IGenericRepository<Truck, Long> truckRepo,
                        IGenericRepository<TransportCompany, Long> companyRepo,
                        IGenericRepository<TransportCargoService, Long> transportServiceRepo,
                        TruckMapper truckMapper,
                        TransportCargoServiceMapper transportServiceMapper) {
        this.truckRepo = truckRepo;
        this.companyRepo = companyRepo;
        this.transportServiceRepo = transportServiceRepo;
        this.truckMapper = truckMapper;
        this.transportServiceMapper = transportServiceMapper;
    }

    public TruckViewDTO create(TruckCreateDTO dto) {
        if (dto == null) {
            logger.error("Cannot create {}: DTO is null", Constants.TRUCK);
            throw new IllegalArgumentException("TruckCreateDTO must not be null");
        }
        logger.debug("Creating {} with DTO: {}", Constants.TRUCK, dto);
        try {
            Truck entity = truckMapper.toEntity(dto);
            Truck created = truckRepo.create(entity);
            logger.info("{} created with ID: {}", Constants.TRUCK, created.getId());
            return truckMapper.toViewDTO(created);
        } catch (RepositoryException e) {
            logger.error("Failed to create {} with registration plate: {}, cause: {}", Constants.TRUCK, dto.getRegistrationPlate(), e.getMessage(), e);
            throw e;
        }
    }

    public TruckViewDTO update(TruckUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            logger.error("Cannot update {}: DTO or ID is null", Constants.TRUCK);
            throw new IllegalArgumentException("TruckUpdateDTO and ID must not be null");
        }
        logger.debug("Updating {} with DTO: {}", Constants.TRUCK, dto);
        try {
            Truck existing = truckRepo.getById(dto.getId())
                    .orElseThrow(() -> new RepositoryException("Truck not found with ID: " + dto.getId()));
            truckMapper.toEntity(dto, existing);
            TruckViewDTO result = truckRepo.updateAndMap(existing, truckMapper::toViewDTO, null);
            logger.info("{} updated with ID: {}", Constants.TRUCK, result.getId());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to update {} with ID: {}, cause: {}", Constants.TRUCK, dto.getId(), e.getMessage(), e);
            throw e;
        }
    }

    public void delete(Long id) {
        if (id == null) {
            logger.error("Cannot delete {}: ID is null", Constants.TRUCK);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Deleting {} with ID: {}", Constants.TRUCK, id);
        try {
            Truck entity = truckRepo.getById(id)
                    .orElseThrow(() -> new RepositoryException("Truck not found with ID: " + id));
            truckRepo.delete(entity);
            logger.info("{} deleted with ID: {}", Constants.TRUCK, id);
        } catch (RepositoryException e) {
            logger.error("Failed to delete {} with ID: {}, cause: {}", Constants.TRUCK, id, e.getMessage(), e);
            throw e;
        }
    }

    public TruckViewDTO getById(Long id) {
        if (id == null) {
            logger.error("Cannot retrieve {}: ID is null", Constants.TRUCK);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Retrieving {} with ID: {}", Constants.TRUCK, id);
        try {
            Optional<TruckViewDTO> result = truckRepo.getByIdAndMap(id, truckMapper::toViewDTO, null);
            if (result.isEmpty()) {
                logger.warn("No {} found with ID: {}", Constants.TRUCK, id);
                return null;
            }
            logger.info("{} retrieved with ID: {}", Constants.TRUCK, id);
            return result.get();
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} with ID: {}, cause: {}", Constants.TRUCK, id, e.getMessage(), e);
            throw e;
        }
    }

    public List<TruckViewDTO> getAll(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving all {}: page={}, size={}, orderBy={}, ascending={}", Constants.TRUCK, page, size, orderBy, ascending);
        try {
            List<TruckViewDTO> result = truckRepo.getAllAndMap(page, size, orderBy, ascending, truckMapper::toViewDTO, null);
            logger.info("Retrieved {} {}", result.size(), Constants.TRUCK);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve all {}, cause: {}", Constants.TRUCK, e.getMessage(), e);
            throw e;
        }
    }

    public List<TransportCargoServiceViewDTO> getTransportServicesForTruck(Long truckId, int page, int size) {
        if (truckId == null) {
            logger.error("Cannot retrieve transport services: Truck ID is null");
            throw new IllegalArgumentException("Truck ID must not be null");
        }
        if (page < 0 || size <= 0) {
            logger.debug("Invalid pagination for {} with ID: {}, page: {}, size: {}, returning empty list", Constants.TRUCK, truckId, page, size);
            return Collections.emptyList();
        }
        logger.debug("Retrieving transport services for {} with ID: {}, page: {}, size: {}", Constants.TRUCK, truckId, page, size);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("vehicle.id", truckId);
            List<TransportCargoService> transportServices = transportServiceRepo.findByCriteria(conditions, "startingDate", true, page, size);
            return transportServices.stream()
                    .map(transportServiceMapper::toViewDTO)
                    .collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve transport services for {} with ID: {}, cause: {}", Constants.TRUCK, truckId, e.getMessage(), e);
            throw e;
        }
    }

    public List<TransportCargoServiceViewDTO> getActiveTransportServices(Long truckId, int page, int size) {
        if (truckId == null) {
            logger.error("Cannot retrieve active transport services: Truck ID is null");
            throw new IllegalArgumentException("Truck ID must not be null");
        }
        if (page < 0 || size <= 0) {
            logger.debug("Invalid pagination for {} with ID: {}, page: {}, size: {}, returning empty list", Constants.TRUCK, truckId, page, size);
            return Collections.emptyList();
        }
        logger.debug("Retrieving active transport services for {} with ID: {}, page: {}, size: {}", Constants.TRUCK, truckId, page, size);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("vehicle.id", truckId);
            conditions.put("delivered", false);
            List<TransportCargoService> transportServices = transportServiceRepo.findByCriteria(conditions, "startingDate", true, page, size);
            return transportServices.stream()
                    .map(transportServiceMapper::toViewDTO)
                    .collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve active transport services for {} with ID: {}, cause: {}", Constants.TRUCK, truckId, e.getMessage(), e);
            throw e;
        }
    }

    public List<TruckViewDTO> getTrucksByCompany(Long companyId, int page, int size) {
        if (companyId == null) {
            logger.error("Cannot retrieve trucks: Company ID is null");
            throw new IllegalArgumentException("Company ID must not be null");
        }
        logger.debug("Retrieving trucks for company with ID: {}, page: {}, size: {}", companyId, page, size);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("transportCompany.id", companyId);
            List<Truck> trucks = truckRepo.findByCriteria(conditions, "registrationPlate", true, page, size);
            return trucks.stream()
                    .map(truckMapper::toViewDTO)
                    .collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve trucks for company with ID: {}, cause: {}", companyId, e.getMessage(), e);
            throw e;
        }
    }

    public List<TruckViewDTO> getAllByTruckType(String truckType, int page, int size, String orderBy, boolean ascending) {
        if (truckType == null) {
            logger.error("Cannot retrieve trucks: Truck type is null");
            throw new IllegalArgumentException("Truck type must not be null");
        }
        if (page < 0 || size <= 0) {
            logger.debug("Invalid pagination for trucks of type {}, page: {}, size: {}, returning empty list", truckType, page, size);
            return Collections.emptyList();
        }
        logger.debug("Retrieving trucks of type {}: page={}, size={}, orderBy={}, ascending={}", truckType, page, size, orderBy, ascending);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("truckType", truckType);
            List<Truck> trucks = truckRepo.findByCriteria(conditions, orderBy, ascending, page, size);
            List<TruckViewDTO> result = trucks.stream()
                    .map(truckMapper::toViewDTO)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} trucks of type {}", result.size(), truckType);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve trucks of type {}, cause: {}", truckType, e.getMessage(), e);
            throw e;
        }
    }
}