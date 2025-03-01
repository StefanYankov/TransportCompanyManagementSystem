package services.services.contracts;

import data.repositories.exceptions.RepositoryException;
import services.data.dto.clients.ClientCreateDTO;
import services.data.dto.clients.ClientUpdateDTO;
import services.data.dto.clients.ClientViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Interface defining operations for managing {@link data.models.Client} entities in the transport system.
 * Provides methods for CRUD operations, client-specific queries, and retrieval of related transport services.
 * All methods may throw {@link RepositoryException} as an unchecked exception, documented below.
 */
public interface IClientService {

    /**
     * Creates a new client based on the provided DTO.
     *
     * @param dto the data transfer object containing client creation details
     * @return a view DTO representing the created client
     * @throws IllegalArgumentException if the DTO is null
     * @throws RepositoryException if the client cannot be created (e.g., database errors)
     */
    public ClientViewDTO create(ClientCreateDTO dto);

    /**
     * Updates an existing client based on the provided DTO.
     *
     * @param dto the data transfer object containing updated client details
     * @return a view DTO representing the updated client
     * @throws IllegalArgumentException if the DTO or its ID is null
     * @throws RepositoryException if the client is not found or update fails (e.g., database errors)
     */
    public ClientViewDTO update(ClientUpdateDTO dto);

    /**
     * Deletes a client by its ID.
     *
     * @param id the ID of the client to delete
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException if deletion fails (e.g., client not found, database errors)
     */
    public void delete(Long id);

    /**
     * Retrieves a client by its ID.
     *
     * @param id the ID of the client to retrieve
     * @return a view DTO of the client if found, null otherwise
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException if retrieval fails (e.g., database errors)
     */
    public ClientViewDTO getById(Long id);


    /**
     * Retrieves a paginated list of all clients, optionally filtered and sorted.
     *
     * @param page      the page number (0-based)
     * @param size      the number of clients per page
     * @param orderBy   the field to sort by (e.g., "name"); may be null for no sorting
     * @param ascending true for ascending order, false for descending
     * @param filter    an optional filter string to match against name, telephone, or email; may be null
     * @return a list of client view DTOs
     * @throws RepositoryException if the query fails (e.g., invalid orderBy field)
     */
    public List<ClientViewDTO> getAll(int page, int size, String orderBy, boolean ascending, String filter);

    /**
     * Finds clients matching the given criteria, optionally sorted.
     *
     * @param conditions map of field names to values for filtering (e.g., {"name": "John Doe"})
     * @param orderBy    the field to sort by; may be null for no sorting
     * @param ascending  true for ascending order, false for descending
     * @return a list of client view DTOs matching the criteria
     * @throws RepositoryException if the query fails (e.g., invalid field name)
     */
    public List<ClientViewDTO> findByCriteria(Map<String, Object> conditions, String orderBy, boolean ascending);


    /**
     * Retrieves the transport services associated with a specific client by its ID.
     *
     * @param clientId the ID of the client
     * @return a list of transport service view DTOs associated with the client
     * @throws IllegalArgumentException if the client ID is null
     * @throws RepositoryException if the query fails (e.g., client not found, database errors)
     */
    public List<TransportServiceViewDTO> getTransportServicesByClient(Long clientId);

    /**
     * Retrieves the total number of transport services per client.
     *
     * @return a map where keys are client IDs and values are the count of transport services
     * @throws RepositoryException if the query fails (e.g., database errors)
     */
    public Map<Long, Integer> getTransportServiceCountsPerClient();
}