package services.services.contracts;

import data.repositories.exceptions.RepositoryException;
import services.data.dto.employees.DispatcherCreateDTO;
import services.data.dto.employees.DispatcherUpdateDTO;
import services.data.dto.employees.DispatcherViewDTO;
import services.data.dto.employees.DriverViewDTO;

import java.util.List;

/**
 * Interface defining operations for managing {@link data.models.employee.Dispatcher} entities in the transport system.
 * Provides methods for CRUD operations and dispatcher-specific queries.
 * All methods may throw {@link RepositoryException} as an unchecked exception, documented below.
 */
public interface IDispatcherService {

    /**
     * Creates a new dispatcher based on the provided DTO.
     *
     * @param dto the data transfer object containing dispatcher creation details
     * @return a view DTO representing the created dispatcher
     * @throws IllegalArgumentException if the DTO is null
     * @throws RepositoryException if the dispatcher cannot be created (e.g., null DTO, database errors)
     */
    public DispatcherViewDTO create(DispatcherCreateDTO dto);

    /**
     * Updates an existing dispatcher based on the provided DTO.
     *
     * @param dto the data transfer object containing updated dispatcher details
     * @return a view DTO representing the updated dispatcher
     * @throws IllegalArgumentException if the DTO or its ID is null
     * @throws RepositoryException if the dispatcher is not found or update fails (e.g., optimistic locking conflict)
     */
    public DispatcherViewDTO update(DispatcherUpdateDTO dto);

    /**
     * Deletes a dispatcher by their ID.
     *
     * @param id the ID of the dispatcher to delete
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException if deletion fails (e.g., dispatcher not found, database errors)
     */
    public void delete(Long id);

    /**
     * Retrieves a dispatcher by their ID, optionally fetching specified relationships.
     *
     * @param id the ID of the dispatcher to retrieve
     * @param fetchRelations the relationships to eagerly fetch (e.g., "supervisedDrivers"); may be null or empty
     * @return a view DTO of the dispatcher if found, null otherwise
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException if retrieval fails (e.g., database errors)
     */
    public DispatcherViewDTO getById(Long id, String... fetchRelations);

    /**
     * Retrieves a paginated list of all dispatchers, optionally sorted and with specified relationships fetched.
     *
     * @param page the page number (0-based)
     * @param size the number of dispatchers per page
     * @param orderBy the field to sort by (e.g., "familyName"); may be null for no sorting
     * @param ascending true for ascending order, false for descending
     * @param fetchRelations the relationships to eagerly fetch (e.g., "supervisedDrivers"); may be null or empty
     * @return a list of dispatcher view DTOs
     * @throws RepositoryException if the query fails (e.g., invalid orderBy field)
     */
    public List<DispatcherViewDTO> getAll(int page, int size, String orderBy, boolean ascending, String... fetchRelations);

    /**
     * Retrieves drivers supervised by a specific dispatcher, fetching supervised drivers eagerly.
     *
     * @param dispatcherId the ID of the dispatcher
     * @return a list of driver view DTOs supervised by the dispatcher
     * @throws IllegalArgumentException if the dispatcher ID is null
     * @throws RepositoryException if the query fails (e.g., dispatcher not found)
     */
    public List<DriverViewDTO> getDriversByDispatcher(Long dispatcherId);

    /**
     * Retrieves a paginated list of dispatchers sorted by their salary.
     *
     * @param page the page number (0-based)
     * @param size the number of dispatchers per page
     * @param ascending true for ascending order, false for descending
     * @param fetchRelations the relationships to eagerly fetch (e.g., "supervisedDrivers"); may be null or empty
     * @return a list of dispatcher view DTOs sorted by salary
     * @throws RepositoryException if the query fails
     */
    public List<DispatcherViewDTO> getDispatchersSortedBySalary(int page, int size, boolean ascending, String... fetchRelations);
}