package services.services.contracts;

import data.repositories.exceptions.RepositoryException;
import services.data.dto.transportservices.DestinationCreateDTO;
import services.data.dto.transportservices.DestinationUpdateDTO;
import services.data.dto.transportservices.DestinationViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;

import java.util.List;
import java.util.Map;

/**
 * Interface defining operations for managing {@link data.models.transportservices.Destination} entities in the transport system.
 * Provides methods for CRUD operations, destination-specific queries, and retrieval of related transport services.
 * All methods may throw {@link RepositoryException} as an unchecked exception, documented below.
 */
public interface IDestinationService {

    /**
     * Creates a new destination based on the provided DTO.
     *
     * @param dto the data transfer object containing destination creation details
     * @return a view DTO representing the created destination
     * @throws IllegalArgumentException if the DTO is null
     * @throws RepositoryException if the destination cannot be created (e.g., database errors)
     */
    public DestinationViewDTO create(DestinationCreateDTO dto);

    /**
     * Updates an existing destination based on the provided DTO.
     *
     * @param dto the data transfer object containing updated destination details
     * @return a view DTO representing the updated destination
     * @throws IllegalArgumentException if the DTO or its ID is null
     * @throws RepositoryException if the destination is not found or update fails (e.g., database errors)
     */
    public DestinationViewDTO update(DestinationUpdateDTO dto);

    /**
     * Deletes a destination by its ID.
     *
     * @param id the ID of the destination to delete
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException if deletion fails (e.g., destination not found, database errors)
     */
    public void delete(Long id);

    /**
     * Retrieves a destination by its ID.
     *
     * @param id the ID of the destination to retrieve
     * @return a view DTO of the destination if found, null otherwise
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException if retrieval fails (e.g., database errors)
     */
    public DestinationViewDTO getById(Long id);

    /**
     * Retrieves a paginated list of all destinations, optionally filtered and sorted.
     *
     * @param page      the page number (0-based)
     * @param size      the number of destinations per page
     * @param orderBy   the field to sort by (e.g., "startingLocation"); may be null for no sorting
     * @param ascending true for ascending order, false for descending
     * @param filter    an optional filter string to match against startingLocation or endingLocation; may be null
     * @return a list of destination view DTOs
     * @throws RepositoryException if the query fails (e.g., invalid orderBy field)
     */
    public List<DestinationViewDTO> getAll(int page, int size, String orderBy, boolean ascending, String filter);

    /**
     * Finds destinations matching the given criteria, optionally sorted.
     *
     * @param conditions map of field names to values for filtering (e.g., {"startingLocation": "New York"})
     * @param orderBy    the field to sort by; may be null for no sorting
     * @param ascending  true for ascending order, false for descending
     * @return a list of destination view DTOs matching the criteria
     * @throws RepositoryException if the query fails (e.g., invalid field name)
     */
    public List<DestinationViewDTO> findByCriteria(Map<String, Object> conditions, String orderBy, boolean ascending);

    /**
     * Retrieves a list of transport services associated with each destination.
     *
     * @return a map where keys are destination IDs and values are lists of transport service view DTOs
     * @throws RepositoryException if the query fails (e.g., database errors)
     */
    public Map<Long, List<TransportServiceViewDTO>> getTransportServicesByDestination();

}