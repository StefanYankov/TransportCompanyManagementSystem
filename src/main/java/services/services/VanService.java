package services.services;

import data.models.TransportCompany;
import data.models.transportservices.TransportPassengersService;
import data.models.vehicles.Van;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.common.Constants;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;
import services.data.dto.vehicles.VanCreateDTO;
import services.data.dto.vehicles.VanUpdateDTO;
import services.data.dto.vehicles.VanViewDTO;
import services.data.mapping.mappers.TransportPassengersServiceMapper;
import services.data.mapping.mappers.VanMapper;
import services.services.contracts.IVanService;

import java.util.*;
import java.util.stream.Collectors;

public class VanService implements IVanService {
    private static final Logger logger = LoggerFactory.getLogger(VanService.class);
    private final IGenericRepository<Van, Long> vanRepo;
    private final IGenericRepository<TransportCompany, Long> companyRepo;
    private final IGenericRepository<TransportPassengersService, Long> transportServiceRepo;
    private final VanMapper vanMapper;
    private final TransportPassengersServiceMapper transportServiceMapper;

    public VanService(IGenericRepository<Van, Long> vanRepo,
                      IGenericRepository<TransportCompany, Long> companyRepo,
                      IGenericRepository<TransportPassengersService, Long> transportServiceRepo,
                      VanMapper vanMapper,
                      TransportPassengersServiceMapper transportServiceMapper) {
        this.vanRepo = vanRepo;
        this.companyRepo = companyRepo;
        this.transportServiceRepo = transportServiceRepo;
        this.vanMapper = vanMapper;
        this.transportServiceMapper = transportServiceMapper;
    }

    public VanViewDTO create(VanCreateDTO dto) {
        if (dto == null) {
            logger.error("Cannot create {}: DTO is null", Constants.VAN);
            throw new IllegalArgumentException("VanCreateDTO must not be null");
        }
        logger.debug("Creating {} with DTO: {}", Constants.VAN, dto);
        try {
            Van entity = vanMapper.toEntity(dto);
            TransportCompany company = companyRepo.getById(dto.getTransportCompanyId())
                    .orElseThrow(() -> new RepositoryException("Transport company not found with ID: " + dto.getTransportCompanyId()));
            entity.setTransportCompany(company);
            Van created = vanRepo.create(entity);
            logger.info("{} created with ID: {}", Constants.VAN, created.getId());
            return vanMapper.toViewDTO(created);
        } catch (RepositoryException e) {
            logger.error("Failed to create {} with registration plate: {}, cause: {}", Constants.VAN, dto.getRegistrationPlate(), e.getMessage(), e);
            throw e;
        }
    }

    public VanViewDTO update(VanUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            logger.error("Cannot update {}: DTO or ID is null", Constants.VAN);
            throw new IllegalArgumentException("VanUpdateDTO and ID must not be null");
        }
        logger.debug("Updating {} with DTO: {}", Constants.VAN, dto);
        try {
            Van existing = vanRepo.getById(dto.getId())
                    .orElseThrow(() -> new RepositoryException("Van not found with ID: " + dto.getId()));
            vanMapper.toEntity(dto, existing);
            if (dto.getTransportCompanyId() != null) {
                TransportCompany company = companyRepo.getById(dto.getTransportCompanyId())
                        .orElseThrow(() -> new RepositoryException("Transport company not found with ID: " + dto.getTransportCompanyId()));
                existing.setTransportCompany(company);
            }
            VanViewDTO result = vanRepo.updateAndMap(existing, vanMapper::toViewDTO, null);
            logger.info("{} updated with ID: {}", Constants.VAN, result.getId());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to update {} with ID: {}, cause: {}", Constants.VAN, dto.getId(), e.getMessage(), e);
            throw e;
        }
    }

    public void delete(Long id) {
        if (id == null) {
            logger.error("Cannot delete {}: ID is null", Constants.VAN);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Deleting {} with ID: {}", Constants.VAN, id);
        try {
            Van entity = vanRepo.getById(id)
                    .orElseThrow(() -> new RepositoryException("Van not found with ID: " + id));
            vanRepo.delete(entity);
            logger.info("{} deleted with ID: {}", Constants.VAN, id);
        } catch (RepositoryException e) {
            logger.error("Failed to delete {} with ID: {}, cause: {}", Constants.VAN, id, e.getMessage(), e);
            throw e;
        }
    }

    public VanViewDTO getById(Long id) {
        if (id == null) {
            logger.error("Cannot retrieve {}: ID is null", Constants.VAN);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Retrieving {} with ID: {}", Constants.VAN, id);
        try {
            Optional<VanViewDTO> result = vanRepo.getByIdAndMap(id, vanMapper::toViewDTO, null);
            if (result.isEmpty()) {
                logger.warn("No {} found with ID: {}", Constants.VAN, id);
                return null;
            }
            logger.info("{} retrieved with ID: {}", Constants.VAN, id);
            return result.get();
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} with ID: {}, cause: {}", Constants.VAN, id, e.getMessage(), e);
            throw e;
        }
    }

    public List<VanViewDTO> getAll(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving all {}: page={}, size={}, orderBy={}, ascending={}", Constants.VAN, page, size, orderBy, ascending);
        try {
            List<VanViewDTO> result = vanRepo.getAllAndMap(page, size, orderBy, ascending, vanMapper::toViewDTO, null);
            logger.info("Retrieved {} {}", result.size(), Constants.VAN);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve all {}, cause: {}", Constants.VAN, e.getMessage(), e);
            throw e;
        }
    }

    public List<TransportPassengersServiceViewDTO> getTransportServicesForVan(Long vanId, int page, int size) {
        if (vanId == null) {
            logger.error("Cannot retrieve transport services: Van ID is null");
            throw new IllegalArgumentException("Van ID must not be null");
        }
        if (page < 0 || size <= 0) {
            logger.debug("Invalid pagination for {} with ID: {}, page: {}, size: {}, returning empty list", Constants.VAN, vanId, page, size);
            return Collections.emptyList();
        }
        logger.debug("Retrieving transport services for {} with ID: {}, page: {}, size: {}", Constants.VAN, vanId, page, size);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("vehicle.id", vanId);
            List<TransportPassengersService> transportServices = transportServiceRepo.findByCriteria(conditions, "startingDate", true, page, size);
            return transportServices.stream()
                    .map(transportServiceMapper::toViewDTO)
                    .collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve transport services for {} with ID: {}, cause: {}", Constants.VAN, vanId, e.getMessage(), e);
            throw e;
        }
    }

    public List<TransportPassengersServiceViewDTO> getActiveTransportServices(Long vanId, int page, int size) {
        if (vanId == null) {
            logger.error("Cannot retrieve active transport services: Van ID is null");
            throw new IllegalArgumentException("Van ID must not be null");
        }
        if (page < 0 || size <= 0) {
            logger.debug("Invalid pagination for {} with ID: {}, page: {}, size: {}, returning empty list", Constants.VAN, vanId, page, size);
            return Collections.emptyList();
        }
        logger.debug("Retrieving active transport services for {} with ID: {}, page: {}, size: {}", Constants.VAN, vanId, page, size);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("vehicle.id", vanId);
            conditions.put("delivered", false);
            List<TransportPassengersService> transportServices = transportServiceRepo.findByCriteria(conditions, "startingDate", true, page, size);
            return transportServices.stream()
                    .map(transportServiceMapper::toViewDTO)
                    .collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve active transport services for {} with ID: {}, cause: {}", Constants.VAN, vanId, e.getMessage(), e);
            throw e;
        }
    }

    public List<VanViewDTO> getVansByCompany(Long companyId, int page, int size) {
        if (companyId == null) {
            logger.error("Cannot retrieve vans: Company ID is null");
            throw new IllegalArgumentException("Company ID must not be null");
        }
        logger.debug("Retrieving vans for company with ID: {}, page: {}, size: {}", companyId, page, size);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("transportCompany.id", companyId);
            List<Van> vans = vanRepo.findByCriteria(conditions, "registrationPlate", true, page, size);
            return vans.stream()
                    .map(vanMapper::toViewDTO)
                    .collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve vans for company with ID: {}, cause: {}", companyId, e.getMessage(), e);
            throw e;
        }
    }
}