package services.services.contracts;

import data.models.vehicles.Bus;
import data.repositories.exceptions.RepositoryException;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.dto.vehicles.BusCreateDTO;
import services.data.dto.vehicles.BusUpdateDTO;
import services.data.dto.vehicles.BusViewDTO;

import java.util.List;

/**
 * Service Interface defining operations for managing {@link Bus} entities in the transport system.
 * Provides CRUD operations and bus-specific transport service queries.
 * All methods may throw {@link RepositoryException} as an unchecked exception.
 */
public interface IBusService {
    /**
     * Creates a new bus entity based on the provided DTO.
     *
     * @param dto The DTO containing bus creation data.
     * @return A view DTO representing the created bus.
     * @throws IllegalArgumentException if the DTO is null.
     * @throws RepositoryException      if there’s a database error or the transport company isn’t found.
     */
    BusViewDTO create(BusCreateDTO dto);

    /**
     * Updates an existing bus entity based on the provided DTO.
     *
     * @param dto The DTO containing updated bus data.
     * @return A view DTO representing the updated bus.
     * @throws IllegalArgumentException if the DTO or its ID is null.
     * @throws RepositoryException      if the bus or transport company isn’t found or there’s a database error.
     */
    BusViewDTO update(BusUpdateDTO dto);

    /**
     * Deletes a bus entity by its ID.
     *
     * @param id The ID of the bus to delete.
     * @throws IllegalArgumentException if the ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    void delete(Long id);

    /**
     * Retrieves a bus entity by its ID.
     *
     * @param id The ID of the bus to retrieve.
     * @return A view DTO representing the bus, or null if not found.
     * @throws IllegalArgumentException if the ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    BusViewDTO getById(Long id);

    /**
     * Retrieves a paginated list of all bus entities.
     *
     * @param page      The page number (0-based).
     * @param size      The number of items per page.
     * @param orderBy   The field to order by.
     * @param ascending Whether to sort in ascending order.
     * @return A list of view DTOs representing the buses.
     * @throws RepositoryException if there’s a database error.
     */
    List<BusViewDTO> getAll(int page, int size, String orderBy, boolean ascending);

    /**
     * Retrieves a paginated list of transport services associated with a bus.
     *
     * @param busId The ID of the bus.
     * @param page  The page number (0-based).
     * @param size  The number of items per page.
     * @return A list of transport service view DTOs.
     * @throws IllegalArgumentException if the bus ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    List<? extends TransportServiceViewDTO> getTransportServicesForBus(Long busId, int page, int size);

    /**
     * Retrieves a paginated list of active (non-delivered) transport services associated with a bus.
     *
     * @param busId The ID of the bus.
     * @param page  The page number (0-based).
     * @param size  The number of items per page.
     * @return A list of transport service view DTOs.
     * @throws IllegalArgumentException if the bus ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    List<? extends TransportServiceViewDTO> getActiveTransportServices(Long busId, int page, int size);

    /**
     * Retrieves a paginated list of buses belonging to a specific transport company.
     *
     * @param companyId The ID of the transport company.
     * @param page      The page number (0-based).
     * @param size      The number of items per page.
     * @return A list of view DTOs representing the buses.
     * @throws IllegalArgumentException if the company ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    List<BusViewDTO> getBusesByCompany(Long companyId, int page, int size);
}