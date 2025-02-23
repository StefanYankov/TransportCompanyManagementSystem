package services.services.contracts;

import data.models.transportservices.TransportCargoService;
import services.data.dto.transportservices.TransportCargoServiceCreateDTO;
import services.data.dto.transportservices.TransportCargoServiceUpdateDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing {@link TransportCargoService} entities with CRUD operations, sorting, and file I/O.
 */
public interface ITransportCargoServiceService {

    /**
     * Creates a new cargo transport service synchronously.
     *
     * @param dto the DTO containing data for the new cargo transport service
     * @return the created TransportCargoService entity
     */
    public TransportCargoService create(TransportCargoServiceCreateDTO dto);

    /**
     * Creates a new cargo transport service asynchronously.
     *
     * @param dto the DTO containing data for the new cargo transport service
     * @return a CompletableFuture resolving to the created TransportCargoService entity
     */
    public CompletableFuture<TransportCargoService> createAsync(TransportCargoServiceCreateDTO dto);

    /**
     * Updates an existing cargo transport service synchronously.
     *
     * @param dto the DTO containing updated data for the cargo transport service
     * @return the updated TransportCargoService entity
     */
    TransportCargoService update(TransportCargoServiceUpdateDTO dto);

    /**
     * Updates an existing cargo transport service asynchronously.
     *
     * @param dto the DTO containing updated data for the cargo transport service
     * @return a CompletableFuture resolving to the updated TransportCargoService entity
     */
    CompletableFuture<TransportCargoService> updateAsync(TransportCargoServiceUpdateDTO dto);

    /**
     * Deletes a cargo transport service by its ID synchronously.
     *
     * @param id the ID of the cargo transport service to delete
     */
    void delete(Long id);

    /**
     * Deletes a cargo transport service by its ID asynchronously.
     *
     * @param id the ID of the cargo transport service to delete
     * @return a CompletableFuture indicating when the deletion is complete
     */
    CompletableFuture<Void> deleteAsync(Long id);

    /**
     * Retrieves a cargo transport service by its ID synchronously.
     *
     * @param id the ID of the cargo transport service to retrieve
     * @return the TransportCargoService entity, or null if not found
     */
    TransportCargoService getById(Long id);

    /**
     * Retrieves a list of all cargo transport services synchronously with pagination and sorting.
     *
     * @param page the page number (0-based)
     * @param size the number of entities per page
     * @param orderBy the field to sort by (e.g., "startingDate")
     * @param ascending true for ascending order, false for descending
     * @return a list of TransportCargoService entities
     */
    List<TransportCargoService> getAll(int page, int size, String orderBy, boolean ascending);

    /**
     * Retrieves cargo transports sorted by destination synchronously.
     *
     * @param ascending true for ascending order by startingLocation, false for descending
     * @return a list of TransportCargoService entities sorted by destination
     */
    List<TransportCargoService> getTransportsSortedByDestination(boolean ascending);

    /**
     * Saves all cargo transport data to a binary file synchronously.
     *
     * @param filePath the path to the binary file
     * @throws IOException if file writing fails
     */
    public void saveToBinaryFile(String filePath) throws IOException;

    /**
     * Loads cargo transport data from a binary file synchronously.
     *
     * @param filePath the path to the binary file
     * @throws IOException if file reading fails
     * @throws ClassNotFoundException if deserialization fails
     */
    public void loadFromBinaryFile(String filePath) throws IOException, ClassNotFoundException;

    /**
     * Saves all cargo transport data to a JSON file synchronously.
     *
     * @param filePath the path to the JSON file
     * @throws IOException if file writing fails
     */
    public void saveToJsonFile(String filePath) throws IOException;

    /**
     * Loads cargo transport data from a JSON file synchronously.
     *
     * @param filePath the path to the JSON file
     * @throws IOException if file reading fails
     */
    public void loadFromJsonFile(String filePath) throws IOException;

    /**
     * Calculates the total number of cargo transports synchronously.
     *
     * @return the total count of TransportCargoService entities
     */
    public int getTotalTransportCount();

    /**
     * Calculates the total revenue from all cargo transports synchronously.
     *
     * @return the total revenue as a BigDecimal
     */
    public BigDecimal getTotalRevenue();
}