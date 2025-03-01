package services.services;

import data.models.Client;
import data.models.transportservices.TransportService;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.common.Constants;
import services.data.dto.clients.ClientCreateDTO;
import services.data.dto.clients.ClientUpdateDTO;
import services.data.dto.clients.ClientViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.mapping.mappers.ClientMapper;
import services.data.mapping.mappers.TransportServiceMapper;
import services.services.contracts.IClientService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing {@link Client} entities, exposing DTOs instead of entities.
 */
public class ClientService implements IClientService {
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final IGenericRepository<Client, Long> clientRepo;
    private final IGenericRepository<TransportService, Long> transportServiceRepo;
    private final ClientMapper clientMapper;
    private final TransportServiceMapper transportServiceMapper;

    /**
     * Constructs a new ClientService with the specified dependencies.
     *
     * @param clientRepo             repository for client-related database operations
     * @param transportServiceRepo   repository for transport service-related database operations
     * @param clientMapper           utility for mapping Client entities to DTOs and vice versa
     * @param transportServiceMapper utility for mapping TransportService entities to DTOs
     */
    public ClientService(IGenericRepository<Client, Long> clientRepo,
                         IGenericRepository<TransportService, Long> transportServiceRepo,
                         ClientMapper clientMapper,
                         TransportServiceMapper transportServiceMapper) {
        this.clientRepo = clientRepo;
        this.transportServiceRepo = transportServiceRepo;
        this.clientMapper = clientMapper;
        this.transportServiceMapper = transportServiceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientViewDTO create(ClientCreateDTO dto) {
        if (dto == null) {
            logger.error("Cannot create {}: DTO is null", Constants.CLIENT);
            throw new IllegalArgumentException("ClientCreateDTO must not be null");
        }
        logger.debug("Creating {} with DTO: {}", Constants.CLIENT, dto);
        try {
            Client entity = clientMapper.toEntity(dto);
            Client created = clientRepo.create(entity);
            logger.info("{} created with ID: {}", Constants.CLIENT, created.getId());
            return clientMapper.toViewDTO(created);
        } catch (RepositoryException e) {
            logger.error("Failed to create {}: {}, cause: {}", Constants.CLIENT, dto.getName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientViewDTO update(ClientUpdateDTO dto) {
        if (dto == null) {
            logger.error("Cannot update {}: DTO is null", Constants.CLIENT);
            throw new IllegalArgumentException("ClientUpdateDTO must not be null");
        }
        if (dto.getId() == null) {
            logger.error("Cannot update {}: ID is null in DTO", Constants.CLIENT);
            throw new IllegalArgumentException("Client ID must not be null for update");
        }
        logger.debug("Updating {} with DTO: {}", Constants.CLIENT, dto);
        try {
            Client existing = clientRepo.getById(dto.getId())
                    .orElseThrow(() -> new RepositoryException("Client not found with ID: " + dto.getId()));
            if (dto.getName() != null) existing.setName(dto.getName());
            if (dto.getTelephone() != null) existing.setTelephone(dto.getTelephone());
            if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
            ClientViewDTO result = clientRepo.updateAndMap(existing, clientMapper::toViewDTO, null);
            logger.info("{} updated with ID: {}", Constants.CLIENT, result.getId());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to update {} with ID: {}, cause: {}", Constants.CLIENT, dto.getId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        if (id == null) {
            logger.error("Cannot delete {}: ID is null", Constants.CLIENT);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Deleting {} with ID: {}", Constants.CLIENT, id);
        try {
            Client entity = clientRepo.getById(id)
                    .orElseThrow(() -> new RepositoryException("Client not found with ID: " + id));
            clientRepo.delete(entity);
            logger.info("{} deleted with ID: {}", Constants.CLIENT, id);
        } catch (RepositoryException e) {
            logger.error("Failed to delete {} with ID: {}, cause: {}", Constants.CLIENT, id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientViewDTO getById(Long id) {
        if (id == null) throw new IllegalArgumentException("ID must not be null");
        logger.debug("Retrieving {} with ID: {}", Constants.CLIENT, id);
        try {
            Optional<ClientViewDTO> result = clientRepo.getByIdAndMap(id, clientMapper::toViewDTO, null);
            if (result.isEmpty()) {
                logger.warn("No {} found with ID: {}", Constants.CLIENT, id);
                return null;
            }
            logger.info("{} retrieved with ID: {}", Constants.CLIENT, id);
            return result.get();
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} with ID: {}, cause: {}", Constants.CLIENT, id, e.getMessage(), e);
            throw e;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ClientViewDTO> getAll(int page, int size, String orderBy, boolean ascending, String filter) {
        logger.debug("Retrieving all {}: page={}, size={}, orderBy={}, ascending={}, filter={}", Constants.CLIENT, page, size, orderBy, ascending, filter);
        try {
            List<ClientViewDTO> result = clientRepo.getAllAndMap(page, size, orderBy, ascending, clientMapper::toViewDTO, null);
            if (filter != null && !filter.trim().isEmpty()) {
                String filterLower = filter.trim().toLowerCase();
                result = result.stream()
                        .filter(c -> c.getName().toLowerCase().contains(filterLower) ||
                                c.getTelephone().toLowerCase().contains(filterLower) ||
                                c.getEmail().toLowerCase().contains(filterLower))
                        .collect(Collectors.toList());
            }
            logger.info("Retrieved {} {}", result.size(), Constants.CLIENT);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve all {}, cause: {}", Constants.CLIENT, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ClientViewDTO> findByCriteria(Map<String, Object> conditions, String orderBy, boolean ascending) {
        logger.debug("Finding {} by criteria: conditions={}, orderBy={}, ascending={}", Constants.CLIENT, conditions, orderBy, ascending);
        try {
            List<ClientViewDTO> result = clientRepo.findByCriteria(conditions, orderBy, ascending)
                    .stream()
                    .map(clientMapper::toViewDTO)
                    .collect(Collectors.toList());
            logger.info("Found {} {} matching criteria", result.size(), Constants.CLIENT);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to find {} by criteria, cause: {}", Constants.CLIENT, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransportServiceViewDTO> getTransportServicesByClient(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID must not be null");
        }
        logger.debug("Retrieving transport services for {} with ID: {}", Constants.CLIENT, clientId);
        try {
            List<TransportService> services = transportServiceRepo.findWithJoin("client", "id", clientId, "startingDate", true, "client");
            logger.info("Retrieved {} transport services for {} with ID: {}", services.size(), Constants.CLIENT, clientId);
            return services.stream().map(transportServiceMapper::toViewDTO).collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve transport services for {} with ID: {}, cause: {}", Constants.CLIENT, clientId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, Integer> getTransportServiceCountsPerClient() {
        logger.debug("Retrieving transport service counts per {}", Constants.CLIENT);
        try {
            List<Client> clients = clientRepo.getAll(0, Integer.MAX_VALUE, "name", true);
            Map<Long, Integer> result = new HashMap<>();
            for (Client client : clients) {
                List<TransportService> services = transportServiceRepo.findWithJoin("client", "id", client.getId(), "startingDate", true);
                result.put(client.getId(), services.size());
            }
            logger.info("Retrieved transport service counts for {} clients", result.size());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve transport service counts per {}, cause: {}", Constants.CLIENT, e.getMessage(), e);
            throw e;
        }
    }
}