package services.services.contracts;

import data.repositories.exceptions.RepositoryException;
import services.data.dto.transportservices.TransportPassengersServiceCreateDTO;
import services.data.dto.transportservices.TransportPassengersServiceUpdateDTO;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;

import java.util.List;

/**
 * Service contract for managing {@link data.models.transportservices.TransportPassengersService} entities,
 * providing CRUD operations and specialized queries exposed through DTOs.
 */
public interface ITransportPassengersServiceService {

    /**
     * Creates a new transport passengers service based on the provided DTO.
     *
     * @param dto the DTO containing data for the new transport passengers service
     * @return a view DTO representing the created transport passengers service
     * @throws IllegalArgumentException if the DTO is null
     * @throws RepositoryException if the creation fails due to database constraints or errors
     */
    TransportPassengersServiceViewDTO create(TransportPassengersServiceCreateDTO dto);

    /**
     * Updates an existing transport passengers service based on the provided DTO.
     *
     * @param dto the DTO containing updated data for the transport passengers service
     * @return a view DTO representing the updated transport passengers service
     * @throws IllegalArgumentException if the DTO or its ID is null
     * @throws RepositoryException if the service is not found or update fails
     */
    TransportPassengersServiceViewDTO update(TransportPassengersServiceUpdateDTO dto);

    /**
     * Deletes a transport passengers service by its ID.
     *
     * @param id the ID of the transport passengers service to delete
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException if the service is not found or deletion fails
     */
    void delete(Long id);

    /**
     * Retrieves a transport passengers service by its ID.
     *
     * @param id the ID of the transport passengers service to retrieve
     * @return a view DTO representing the transport passengers service, or null if not found
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException if retrieval fails due to database errors
     */
    TransportPassengersServiceViewDTO getById(Long id);

    /**
     * Retrieves a paginated, sorted list of all transport passengers services with optional filtering.
     *
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @param orderBy the field to sort by (e.g., "startingDate")
     * @param ascending true for ascending order, false for descending
     * @param filter an optional filter string to match against service attributes (e.g., description)
     * @return a list of view DTOs representing the transport passengers services
     * @throws RepositoryException if retrieval fails due to database errors
     */
    List<TransportPassengersServiceViewDTO> getAll(int page, int size, String orderBy, boolean ascending, String filter);

    /**
     * Retrieves transport passengers services sorted by destination ending location.
     *
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @param ascending true for ascending order, false for descending
     * @return a list of view DTOs representing the transport passengers services sorted by destination
     * @throws RepositoryException if retrieval fails due to database errors
     */
    List<TransportPassengersServiceViewDTO> getTransportsSortedByDestination(int page, int size, boolean ascending);

    /**
     * Retrieves the total count of transport passengers services.
     *
     * @return the total number of transport passengers services
     * @throws RepositoryException if retrieval fails due to database errors
     */
    int getTotalTransportCount();

    /**
     * Retrieves currently active transport passengers services (e.g., ongoing based on dates).
     *
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @param orderBy the field to sort by (e.g., "startingDate")
     * @param ascending true for ascending order, false for descending
     * @return a list of view DTOs representing active transport passengers services
     * @throws RepositoryException if retrieval fails due to database errors
     */
    List<TransportPassengersServiceViewDTO> getActiveServices(int page, int size, String orderBy, boolean ascending);

    /**
     * Retrieves transport passengers services associated with a specific driver.
     *
     * @param driverId the ID of the driver
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @param orderBy the field to sort by (e.g., "startingDate")
     * @param ascending true for ascending order, false for descending
     * @return a list of view DTOs representing the transport passengers services for the driver
     * @throws IllegalArgumentException if the driver ID is null
     * @throws RepositoryException if retrieval fails due to database errors
     */
    List<TransportPassengersServiceViewDTO> getByDriver(Long driverId, int page, int size, String orderBy, boolean ascending);

    /**
     * Retrieves transport passengers services associated with a specific client.
     *
     * @param clientId the ID of the client
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @param orderBy the field to sort by (e.g., "startingDate")
     * @param ascending true for ascending order, false for descending
     * @return a list of view DTOs representing the transport passengers services for the client
     * @throws IllegalArgumentException if the client ID is null
     * @throws RepositoryException if retrieval fails due to database errors
     */
    List<TransportPassengersServiceViewDTO> getByClient(Long clientId, int page, int size, String orderBy, boolean ascending);

    /**
     * Retrieves transport passengers services associated with a specific company.
     *
     * @param companyId the ID of the company
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @param orderBy the field to sort by (e.g., "startingDate")
     * @param ascending true for ascending order, false for descending
     * @return a list of view DTOs representing the transport passengers services for the company
     * @throws IllegalArgumentException if the company ID is null
     * @throws RepositoryException if retrieval fails due to database errors
     */
    List<TransportPassengersServiceViewDTO> getByCompany(Long companyId, int page, int size, String orderBy, boolean ascending);
}