package services.services;

import data.models.transportservices.Destination;
import data.models.transportservices.TransportService;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.common.Constants;
import services.data.dto.transportservices.DestinationCreateDTO;
import services.data.dto.transportservices.DestinationUpdateDTO;
import services.data.dto.transportservices.DestinationViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.mapping.mappers.DestinationMapper;
import services.data.mapping.mappers.TransportServiceMapper;
import services.services.contracts.IDestinationService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing {@link Destination} entities, exposing DTOs instead of entities.
 */
public class DestinationService implements IDestinationService {
    private static final Logger logger = LoggerFactory.getLogger(DestinationService.class);
    private final IGenericRepository<Destination, Long> destinationRepo;
    private final IGenericRepository<TransportService, Long> transportServiceRepo;
    private final DestinationMapper destinationMapper;
    private final TransportServiceMapper transportServiceMapper;

    /**
     * Constructs a new DestinationService with the specified dependencies.
     *
     * @param destinationRepo      repository for destination-related database operations
     * @param transportServiceRepo repository for transport service-related database operations
     * @param destinationMapper    utility for mapping Destination entities to DTOs and vice versa
     * @param transportServiceMapper utility for mapping TransportService entities to DTOs
     */
    public DestinationService(IGenericRepository<Destination, Long> destinationRepo,
                              IGenericRepository<TransportService, Long> transportServiceRepo,
                              DestinationMapper destinationMapper,
                              TransportServiceMapper transportServiceMapper) {
        this.destinationRepo = destinationRepo;
        this.transportServiceRepo = transportServiceRepo;
        this.destinationMapper = destinationMapper;
        this.transportServiceMapper = transportServiceMapper;
    }

    /** {@inheritDoc} */
    @Override
    public DestinationViewDTO create(DestinationCreateDTO dto) {
        if (dto == null) {
            logger.error("Cannot create {}: DTO is null", Constants.DESTINATION);
            throw new IllegalArgumentException("DestinationCreateDTO must not be null");
        }
        logger.debug("Creating {} with DTO: {}", Constants.DESTINATION, dto);
        try {
            Destination entity = destinationMapper.toEntity(dto);
            Destination created = destinationRepo.create(entity);
            logger.info("{} created with ID: {}", Constants.DESTINATION, created.getId());
            return destinationMapper.toViewDTO(created);
        } catch (RepositoryException e) {
            logger.error("Failed to create {}: {} to {}, cause: {}", Constants.DESTINATION, dto.getStartingLocation(), dto.getEndingLocation(), e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public DestinationViewDTO update(DestinationUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            logger.error("Cannot update {}: DTO or ID is null", Constants.DESTINATION);
            throw new IllegalArgumentException("DestinationUpdateDTO and ID must not be null");
        }
        logger.debug("Updating {} with DTO: {}", Constants.DESTINATION, dto);
        try {
            Destination existing = destinationRepo.getById(dto.getId())
                    .orElseThrow(() -> new RepositoryException("Destination not found with ID: " + dto.getId()));
            destinationMapper.toEntity(dto, existing);
            DestinationViewDTO result = destinationRepo.updateAndMap(existing, destinationMapper::toViewDTO, null);
            logger.info("{} updated with ID: {}", Constants.DESTINATION, result.getId());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to update {} with ID: {}, cause: {}", Constants.DESTINATION, dto.getId(), e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long id) {
        if (id == null) {
            logger.error("Cannot delete {}: ID is null", Constants.DESTINATION);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Deleting {} with ID: {}", Constants.DESTINATION, id);
        try {
            Destination entity = destinationRepo.getById(id)
                    .orElseThrow(() -> new RepositoryException("Destination not found with ID: " + id));
            destinationRepo.delete(entity);
            logger.info("{} deleted with ID: {}", Constants.DESTINATION, id);
        } catch (RepositoryException e) {
            logger.error("Failed to delete {} with ID: {}, cause: {}", Constants.DESTINATION, id, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public DestinationViewDTO getById(Long id) {
        if (id == null) {
            logger.error("Cannot retrieve {}: ID is null", Constants.DESTINATION);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Retrieving {} with ID: {}", Constants.DESTINATION, id);
        try {
            Optional<DestinationViewDTO> result = destinationRepo.getByIdAndMap(id, destinationMapper::toViewDTO, null);
            if (result.isEmpty()) {
                logger.warn("No {} found with ID: {}", Constants.DESTINATION, id);
                return null;
            }
            logger.info("{} retrieved with ID: {}", Constants.DESTINATION, id);
            return result.get();
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} with ID: {}, cause: {}", Constants.DESTINATION, id, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<DestinationViewDTO> getAll(int page, int size, String orderBy, boolean ascending, String filter) {
        logger.debug("Retrieving all {}: page={}, size={}, orderBy={}, ascending={}, filter={}", Constants.DESTINATION, page, size, orderBy, ascending, filter);
        try {
            List<DestinationViewDTO> result = destinationRepo.getAllAndMap(page, size, orderBy, ascending, destinationMapper::toViewDTO, null);
            if (filter != null && !filter.trim().isEmpty()) {
                String filterLower = filter.trim().toLowerCase();
                result = result.stream()
                        .filter(d -> d.getStartingLocation().toLowerCase().contains(filterLower) ||
                                d.getEndingLocation().toLowerCase().contains(filterLower))
                        .collect(Collectors.toList());
            }
            logger.info("Retrieved {} {}", result.size(), Constants.DESTINATION);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve all {}, cause: {}", Constants.DESTINATION, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<DestinationViewDTO> findByCriteria(Map<String, Object> conditions, String orderBy, boolean ascending) {
        logger.debug("Finding {} by criteria: conditions={}, orderBy={}, ascending={}", Constants.DESTINATION, conditions, orderBy, ascending);
        try {
            List<DestinationViewDTO> result = destinationRepo.findByCriteria(conditions, orderBy, ascending)
                    .stream()
                    .map(destinationMapper::toViewDTO)
                    .collect(Collectors.toList());
            logger.info("Found {} {} matching criteria", result.size(), Constants.DESTINATION);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to find {} by criteria, cause: {}", Constants.DESTINATION, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Map<Long, List<TransportServiceViewDTO>> getTransportServicesByDestination() {
        logger.debug("Retrieving transport services by {}", Constants.DESTINATION);
        try {
            List<Destination> destinations = destinationRepo.getAll(0, Integer.MAX_VALUE, "startingLocation", true);
            Map<Long, List<TransportServiceViewDTO>> result = new HashMap<>();
            for (Destination dest : destinations) {
                List<TransportService> services = transportServiceRepo.findWithJoin("destination", "id", dest.getId(), "startingDate", true, "destination");
                result.put(dest.getId(), services.stream().map(transportServiceMapper::toViewDTO).collect(Collectors.toList()));
            }
            logger.info("Retrieved transport services for {} destinations", result.size());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve transport services by {}, cause: {}", Constants.DESTINATION, e.getMessage(), e);
            throw e;
        }
    }
}