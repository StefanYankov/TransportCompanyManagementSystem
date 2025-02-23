package services.services.contracts;

import data.models.Client;
import services.data.dto.clients.ClientCreateDTO;
import services.data.dto.clients.ClientUpdateDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing {@link Client} entities with CRUD operations.
 */
public interface IClientService {

    /**
     * Creates a new client synchronously.
     *
     * @param dto the DTO containing data for the new client
     * @return the created Client entity
     */
    public Client create(ClientCreateDTO dto);

    /**
     * Creates a new client asynchronously.
     *
     * @param dto the DTO containing data for the new client
     * @return a CompletableFuture resolving to the created Client entity
     */
    public CompletableFuture<Client> createAsync(ClientCreateDTO dto);

    /**
     * Updates an existing client synchronously.
     *
     * @param dto the DTO containing updated data for the client
     * @return the updated Client entity
     */
    public Client update(ClientUpdateDTO dto);

    /**
     * Updates an existing client asynchronously.
     *
     * @param dto the DTO containing updated data for the client
     * @return a CompletableFuture resolving to the updated Client entity
     */
    public CompletableFuture<Client> updateAsync(ClientUpdateDTO dto);

    /**
     * Deletes a client by its ID synchronously.
     *
     * @param id the ID of the client to delete
     */
    public void delete(Long id);

    /**
     * Deletes a client by its ID asynchronously.
     *
     * @param id the ID of the client to delete
     * @return a CompletableFuture indicating when the deletion is complete
     */
    public CompletableFuture<Void> deleteAsync(Long id);

    /**
     * Retrieves a client by its ID synchronously.
     *
     * @param id the ID of the client to retrieve
     * @return the Client entity, or null if not found
     */
    public Client getById(Long id);

    /**
     * Retrieves a list of all clients synchronously with pagination and sorting.
     *
     * @param page      the page number (0-based)
     * @param size      the number of entities per page
     * @param orderBy   the field to sort by (e.g., "name")
     * @param ascending true for ascending order, false for descending
     * @return a list of Client entities
     */
    public List<Client> getAll(int page, int size, String orderBy, boolean ascending);
}