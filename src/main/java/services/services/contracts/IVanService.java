package services.services.contracts;

import data.repositories.exceptions.RepositoryException;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;
import services.data.dto.vehicles.VanCreateDTO;
import services.data.dto.vehicles.VanUpdateDTO;
import services.data.dto.vehicles.VanViewDTO;

import java.util.List;

/**
 * Service Interface defining operations for managing {@link data.models.vehicles.Van} entities in the transport system.
 * Provides CRUD operations and van-specific transport service queries for passenger services.
 * All methods may throw {@link RepositoryException} as an unchecked exception.
 */
public interface IVanService {
    /**
     * Creates a new van entity based on the provided DTO.
     *
     * @param dto The DTO containing van creation data.
     * @return A view DTO representing the created van.
     * @throws IllegalArgumentException if the DTO is null.
     * @throws RepositoryException      if there’s a database error or the transport company isn’t found.
     */
    public VanViewDTO create(VanCreateDTO dto);

    /**
     * Updates an existing van entity based on the provided DTO.
     *
     * @param dto The DTO containing updated van data.
     * @return A view DTO representing the updated van.
     * @throws IllegalArgumentException if the DTO or its ID is null.
     * @throws RepositoryException      if the van or transport company isn’t found or there’s a database error.
     */
    public VanViewDTO update(VanUpdateDTO dto);

    /**
     * Deletes a van entity by its ID.
     *
     * @param id The ID of the van to delete.
     * @throws IllegalArgumentException if the ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    public void delete(Long id);

    /**
     * Retrieves a van entity by its ID.
     *
     * @param id The ID of the van to retrieve.
     * @return A view DTO representing the van, or null if not found.
     * @throws IllegalArgumentException if the ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    public VanViewDTO getById(Long id);

    /**
     * Retrieves a paginated list of all van entities.
     *
     * @param page      The page number (0-based).
     * @param size      The number of items per page.
     * @param orderBy   The field to order by (e.g., "registrationPlate").
     * @param ascending Whether to sort in ascending order.
     * @return A list of view DTOs representing the vans.
     * @throws RepositoryException if there’s a database error.
     */
    public List<VanViewDTO> getAll(int page, int size, String orderBy, boolean ascending);

    /**
     * Retrieves a paginated list of passenger transport services associated with a van.
     *
     * @param vanId The ID of the van.
     * @param page  The page number (0-based).
     * @param size  The number of items per page.
     * @return A list of passenger service view DTOs.
     * @throws IllegalArgumentException if the van ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    public List<TransportPassengersServiceViewDTO> getTransportServicesForVan(Long vanId, int page, int size);

    /**
     * Retrieves a paginated list of active (non-delivered) passenger transport services associated with a van.
     *
     * @param vanId The ID of the van.
     * @param page  The page number (0-based).
     * @param size  The number of items per page.
     * @return A list of passenger service view DTOs.
     * @throws IllegalArgumentException if the van ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    public List<TransportPassengersServiceViewDTO> getActiveTransportServices(Long vanId, int page, int size);

    /**
     * Retrieves a paginated list of vans belonging to a specific transport company.
     *
     * @param companyId The ID of the transport company.
     * @param page      The page number (0-based).
     * @param size      The number of items per page.
     * @return A list of view DTOs representing the vans.
     * @throws IllegalArgumentException if the company ID is null.
     * @throws RepositoryException      if there’s a database error.
     */
    public List<VanViewDTO> getVansByCompany(Long companyId, int page, int size);
}