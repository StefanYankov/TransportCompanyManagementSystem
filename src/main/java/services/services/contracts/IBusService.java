package services.services.contracts;

import data.models.vehicles.Bus;
import services.data.dto.vehicles.BusCreateDTO;
import services.data.dto.vehicles.BusUpdateDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing {@link Bus} entities with CRUD operations.
 */
public interface IBusService {

    /**
     * Creates a new bus synchronously.
     *
     * @param dto the DTO containing data for the new bus
     * @return the created Bus entity
     */
    public Bus create(BusCreateDTO dto);

    /**
     * Creates a new bus asynchronously.
     *
     * @param dto the DTO containing data for the new bus
     * @return a CompletableFuture resolving to the created Bus entity
     */
    public CompletableFuture<Bus> createAsync(BusCreateDTO dto);

    /**
     * Updates an existing bus synchronously.
     *
     * @param dto the DTO containing updated data for the bus
     * @return the updated Bus entity
     */
    public Bus update(BusUpdateDTO dto);

    /**
     * Updates an existing bus asynchronously.
     *
     * @param dto the DTO containing updated data for the bus
     * @return a CompletableFuture resolving to the updated Bus entity
     */
    public CompletableFuture<Bus> updateAsync(BusUpdateDTO dto);

    /**
     * Deletes a bus by its ID synchronously.
     *
     * @param id the ID of the bus to delete
     */
    public void delete(Long id);

    /**
     * Deletes a bus by its ID asynchronously.
     *
     * @param id the ID of the bus to delete
     * @return a CompletableFuture indicating when the deletion is complete
     */
    public CompletableFuture<Void> deleteAsync(Long id);

    /**
     * Retrieves a bus by its ID synchronously.
     *
     * @param id the ID of the bus to retrieve
     * @return the Bus entity, or null if not found
     */
    public Bus getById(Long id);

    /**
     * Retrieves a list of all buses synchronously with pagination and sorting.
     *
     * @param page the page number (0-based)
     * @param size the number of entities per page
     * @param orderBy the field to sort by (e.g., "registrationPlate")
     * @param ascending true for ascending order, false for descending
     * @return a list of Bus entities
     */
    public List<Bus> getAll(int page, int size, String orderBy, boolean ascending);
}