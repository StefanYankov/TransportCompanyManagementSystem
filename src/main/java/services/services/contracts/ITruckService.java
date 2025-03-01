package services.services.contracts;

import data.repositories.exceptions.RepositoryException;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.dto.vehicles.TruckCreateDTO;
import services.data.dto.vehicles.TruckUpdateDTO;
import services.data.dto.vehicles.TruckViewDTO;

import java.util.List;

/**
 * Service Interface defining operations for managing {@link data.models.vehicles.Truck} entities in the transport system.
 * Provides CRUD operations and truck-specific transport service queries.
 * All methods may throw {@link RepositoryException} as an unchecked exception.
 */
public interface ITruckService {
    /**
     * Creates a new truck entity based on the provided DTO.
     *
     * @param dto The DTO containing truck creation data.
     * @return A view DTO representing the created truck.
     * @throws IllegalArgumentException if the DTO is null.
     * @throws RepositoryException      if there’s a database error or the transport company isn’t found.
     */
    TruckViewDTO create(TruckCreateDTO dto);

    /**
     * Updates an existing truck entity based on the provided DTO.
     *
     * @param dto The DTO containing updated truck data.
     * @return A view DTO representing the updated truck.
     * @throws IllegalArgumentException if the DTO or its ID is null.
     * @throws RepositoryException      if the truck or transport company isn’t found or there’s a database error.
     */
    TruckViewDTO update(TruckUpdateDTO dto);

    /**
     * Deletes a truck entity by its ID.
     *
     * @param id The ID of the truck to delete.
     * @throws IllegalArgumentException if the ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    void delete(Long id);

    /**
     * Retrieves a truck entity by its ID.
     *
     * @param id The ID of the truck to retrieve.
     * @return A view DTO representing the truck, or null if not found.
     * @throws IllegalArgumentException if the ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    TruckViewDTO getById(Long id);

    /**
     * Retrieves a paginated list of all truck entities.
     *
     * @param page      The page number (0-based).
     * @param size      The number of items per page.
     * @param orderBy   The field to order by (e.g., "registrationPlate").
     * @param ascending Whether to sort in ascending order.
     * @return A list of view DTOs representing the trucks.
     * @throws RepositoryException if there’s a database error.
     */
    List<TruckViewDTO> getAll(int page, int size, String orderBy, boolean ascending);

    /**
     * Retrieves a paginated list of transport services associated with a truck.
     *
     * @param truckId The ID of the truck.
     * @param page    The page number (0-based).
     * @param size    The number of items per page.
     * @return A list of transport service view DTOs.
     * @throws IllegalArgumentException if the truck ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    List<? extends TransportServiceViewDTO> getTransportServicesForTruck(Long truckId, int page, int size);

    /**
     * Retrieves a paginated list of active (non-delivered) transport services associated with a truck.
     *
     * @param truckId The ID of the truck.
     * @param page    The page number (0-based).
     * @param size    The number of items per page.
     * @return A list of transport service view DTOs.
     * @throws IllegalArgumentException if the truck ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    List<? extends TransportServiceViewDTO> getActiveTransportServices(Long truckId, int page, int size);

    /**
     * Retrieves a paginated list of trucks belonging to a specific transport company.
     *
     * @param companyId The ID of the transport company.
     * @param page      The page number (0-based).
     * @param size      The number of items per page.
     * @return A list of view DTOs representing the trucks.
     * @throws IllegalArgumentException if the company ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    List<TruckViewDTO> getTrucksByCompany(Long companyId, int page, int size);

    /**
     * Retrieves a paginated list of trucks of a specific truck type.
     *
     * @param truckType The type of truck (e.g., "BOX", "FLATBED").
     * @param page      The page number (0-based).
     * @param size      The number of items per page.
     * @param orderBy   The field to order by (e.g., "registrationPlate").
     * @param ascending Whether to sort in ascending order.
     * @return A list of view DTOs representing the trucks.
     * @throws IllegalArgumentException if the truck type is null.
     * @throws RepositoryException      if there’s a database error.
     */
    List<TruckViewDTO> getAllByTruckType(String truckType, int page, int size, String orderBy, boolean ascending);
}