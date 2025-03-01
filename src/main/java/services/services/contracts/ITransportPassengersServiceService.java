package services.services.contracts;

import data.models.transportservices.TransportPassengersService;
import services.data.dto.transportservices.TransportPassengersServiceCreateDTO;
import services.data.dto.transportservices.TransportPassengersServiceUpdateDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for managing {@link TransportPassengersService} entities with CRUD operations.
 */
public interface ITransportPassengersServiceService {

    /**
     * Creates a new passenger transport service synchronously.
     *
     * @param dto the DTO containing data for the new passenger transport service
     * @return the created TransportPassengersService entity
     */
    public TransportPassengersService create(TransportPassengersServiceCreateDTO dto);

    /**
     * Updates an existing passenger transport service synchronously.
     *
     * @param dto the DTO containing updated data for the passenger transport service
     * @return the updated TransportPassengersService entity
     */
    public TransportPassengersService update(TransportPassengersServiceUpdateDTO dto);

    /**
     * Deletes a passenger transport service by its ID synchronously.
     *
     * @param id the ID of the passenger transport service to delete
     */
    public void delete(Long id);

    /**
     * Retrieves a passenger transport service by its ID synchronously.
     *
     * @param id the ID of the passenger transport service to retrieve
     * @return the TransportPassengersService entity, or null if not found
     */
    public TransportPassengersService getById(Long id);

    /**
     * Retrieves a list of all passenger transport service synchronously with pagination and sorting.
     *
     * @param page      the page number (0-based)
     * @param size      the number of entities per page
     * @param orderBy   the field to sort by (e.g., "name")
     * @param ascending true for ascending order, false for descending
     * @return a list of TransportCompany entities
     */
    public List<TransportPassengersService> getAll(int page, int size, String orderBy, boolean ascending);

    /**
     * Retrieves passenger transports sorted by destination synchronously.
     *
     * @param ascending true for ascending order by startingLocation, false for descending
     * @return a list of TransportPassengersService entities sorted by destination
     */
    public List<TransportPassengersService> getTransportsSortedByDestination(boolean ascending);

    /**
     * Saves all passenger transport data to a binary file synchronously.
     *
     * @param filePath the path to the binary file
     * @throws IOException if file writing fails
     */
    public void saveToBinaryFile(String filePath) throws IOException;

    /**
     * Loads passenger transport data from a binary file synchronously.
     *
     * @param filePath the path to the binary file
     * @throws IOException if file reading fails
     * @throws ClassNotFoundException if deserialization fails
     */
    public void loadFromBinaryFile(String filePath) throws IOException, ClassNotFoundException;

    /**
     * Saves all passenger transport data to a JSON file synchronously.
     *
     * @param filePath the path to the JSON file
     * @throws IOException if file writing fails
     */
    public void saveToJsonFile(String filePath) throws IOException;

    /**
     * Loads passenger transport data from a JSON file synchronously.
     *
     * @param filePath the path to the JSON file
     * @throws IOException if file reading fails
     */
    public void loadFromJsonFile(String filePath) throws IOException;

    /**
     * Calculates the total number of passenger transports synchronously.
     *
     * @return the total count of TransportPassengersService entities
     */
    public int getTotalTransportCount();

    /**
     * Calculates the total revenue from all passenger transports synchronously.
     *
     * @return the total revenue as a BigDecimal
     */
    public BigDecimal getTotalRevenue();


}