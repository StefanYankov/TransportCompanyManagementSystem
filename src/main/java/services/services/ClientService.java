package services.services;

import data.models.Client;
import data.repositories.IGenericRepository;
import services.common.Constants;
import services.data.dto.clients.ClientCreateDTO;
import services.data.dto.clients.ClientUpdateDTO;
import services.data.mapping.mappers.ClientMapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.services.contracts.IClientService;

public class ClientService implements IClientService {
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final IGenericRepository<Client, Long> clientRepo;
    private final ClientMapper mapper;

    public ClientService(IGenericRepository<Client, Long> clientRepo, ClientMapper mapper) {
        this.clientRepo = clientRepo;
        this.mapper = mapper;
    }

    /** {@inheritDoc} */
    @Override
    public Client create(ClientCreateDTO dto) {
        logger.debug("Creating {} with DTO: {}",Constants.CLIENT, dto);
        Client entity = mapper.toEntity(dto);
        Client result = clientRepo.create(entity);
        logger.info("{} created with ID: {}",Constants.CLIENT, result.getId());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Client> createAsync(ClientCreateDTO dto) {
        logger.debug("Async creating {} with DTO: {}",Constants.CLIENT, dto);
        Client entity = mapper.toEntity(dto);
        return clientRepo.createAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to create {} asynchronously",Constants.CLIENT, throwable);
                    } else {
                        logger.info("{} created asynchronously with ID: {}",Constants.CLIENT, result.getId());
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public Client update(ClientUpdateDTO dto) {
        logger.debug("Updating client with DTO: {}", dto);
        Client entity = mapper.toEntity(dto);
        Client result = clientRepo.update(entity);
        logger.info("{} updated with ID: {}",Constants.CLIENT, result.getId());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Client> updateAsync(ClientUpdateDTO dto) {
        logger.debug("Async updating {} with DTO: {}",Constants.CLIENT, dto);
        Client entity = mapper.toEntity(dto);
        return clientRepo.updateAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to update {} asynchronously",Constants.CLIENT, throwable);
                    } else {
                        logger.info("{} updated asynchronously with ID: {}",Constants.CLIENT, result.getId());
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long id) {
        logger.debug("Deleting {} with ID: {}",Constants.CLIENT, id);
        Client entity = clientRepo.getById(id);
        if (entity != null) {
            clientRepo.delete(entity);
            logger.info("{} deleted with ID: {}",Constants.CLIENT, id);
        } else {
            logger.warn("No {} found to delete with ID: {}",Constants.CLIENT, id);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Void> deleteAsync(Long id) {
        logger.debug("Async deleting client with ID: {}", id);
        return clientRepo.getByIdAsync(id)
                .thenAccept(entity -> {
                    if (entity != null) {
                        clientRepo.deleteAsync(entity)
                                .whenComplete((v, throwable) -> {
                                    if (throwable != null) {
                                        logger.error("Failed to delete {} asynchronously",Constants.CLIENT, throwable);
                                    } else {
                                        logger.info("{} deleted asynchronously with ID: {}",Constants.CLIENT, id);
                                    }
                                });
                    } else {
                        logger.warn("No {} found to delete asynchronously with ID: {}",Constants.CLIENT, id);
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public Client getById(Long id) {
        logger.debug("Retrieving {} with ID: {}",Constants.CLIENT, id);
        Client result = clientRepo.getById(id);
        if (result != null) {
            logger.info("{} retrieved with ID: {}", Constants.CLIENT, id);
        } else {
            logger.warn("No {} found with ID: {}",Constants.CLIENT, id);
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<Client> getAll(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving all clients, page: {}, size: {}, orderBy: {}, ascending: {}", page, size, orderBy, ascending);
        List<Client> result = clientRepo.getAll(page, size, orderBy, ascending);
        logger.info("Retrieved {} clients", result.size());
        return result;
    }
}