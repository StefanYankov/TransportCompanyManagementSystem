package services.services.contracts;

import data.repositories.exceptions.RepositoryException;
import services.data.dto.transportservices.TransportCargoServiceCreateDTO;
import services.data.dto.transportservices.TransportCargoServiceUpdateDTO;
import services.data.dto.transportservices.TransportCargoServiceViewDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Interface defining operations for managing TransportCargoService entities in the transport system.
 * Provides CRUD operations and cargo-specific queries.
 * All methods may throw RepositoryException as an unchecked exception.
 */
public interface ITransportCargoServiceService {

    /**
     * Creates a new cargo transport service based on the provided DTO.
     *
     * @param dto The DTO containing cargo service creation data.
     * @return A view DTO representing the created cargo service.
     * @throws IllegalArgumentException if the DTO is null.
     * @throws RepositoryException if creation fails (e.g., invalid references, database errors).
     */
    TransportCargoServiceViewDTO create(TransportCargoServiceCreateDTO dto);

    /**
     * Updates an existing cargo transport service based on the provided DTO.
     *
     * @param dto The DTO containing updated cargo service data.
     * @return A view DTO representing the updated cargo service.
     * @throws IllegalArgumentException if the DTO or its ID is null.
     * @throws RepositoryException if the service isn’t found or update fails (e.g., database errors).
     */
    TransportCargoServiceViewDTO update(TransportCargoServiceUpdateDTO dto);

    /**
     * Deletes a cargo transport service by its ID.
     *
     * @param id The ID of the cargo service to delete.
     * @throws IllegalArgumentException if the ID is null.
     * @throws RepositoryException if deletion fails (e.g., service not found, database errors).
     */
    void delete(Long id);

    /**
     * Retrieves a cargo transport service by its ID.
     *
     * @param id The ID of the cargo service to retrieve.
     * @return A view DTO representing the cargo service, or null if not found.
     * @throws IllegalArgumentException if the ID is null.
     * @throws RepositoryException if retrieval fails (e.g., database errors).
     */
    TransportCargoServiceViewDTO getById(Long id);

    /**
     * Retrieves a paginated list of all cargo transport services.
     *
     * @param page The page number (0-based).
     * @param size The number of items per page.
     * @param orderBy The field to sort by (e.g., "startingDate").
     * @param ascending Whether to sort in ascending order.
     * @return A list of cargo service view DTOs.
     * @throws RepositoryException if the query fails (e.g., database errors).
     */
    List<TransportCargoServiceViewDTO> getAll(int page, int size, String orderBy, boolean ascending);

    /**
     * Retrieves cargo transport services by transport company ID.
     *
     * @param companyId The ID of the transport company.
     * @param page The page number (0-based).
     * @param size The number of items per page.
     * @return A list of cargo service view DTOs.
     * @throws IllegalArgumentException if the company ID is null.
     * @throws RepositoryException if the query fails (e.g., database errors).
     */
    List<TransportCargoServiceViewDTO> getByCompany(Long companyId, int page, int size);

    /**
     * Retrieves cargo transport services by client ID.
     *
     * @param clientId The ID of the client.
     * @param page The page number (0-based).
     * @param size The number of items per page.
     * @return A list of cargo service view DTOs.
     * @throws IllegalArgumentException if the client ID is null.
     * @throws RepositoryException if the query fails (e.g., database errors).
     */
    List<TransportCargoServiceViewDTO> getByClient(Long clientId, int page, int size);

    /**
     * Retrieves cargo transport services by driver ID.
     *
     * @param driverId The ID of the driver.
     * @param page The page number (0-based).
     * @param size The number of items per page.
     * @return A list of cargo service view DTOs.
     * @throws IllegalArgumentException if the driver ID is null.
     * @throws RepositoryException if the query fails (e.g., database errors).
     */
    List<TransportCargoServiceViewDTO> getByDriver(Long driverId, int page, int size);

    /**
     * Retrieves active (non-delivered) cargo transport services.
     *
     * @param page The page number (0-based).
     * @param size The number of items per page.
     * @return A list of active cargo service view DTOs.
     * @throws RepositoryException if the query fails (e.g., database errors).
     */
    List<TransportCargoServiceViewDTO> getActiveServices(int page, int size);

}