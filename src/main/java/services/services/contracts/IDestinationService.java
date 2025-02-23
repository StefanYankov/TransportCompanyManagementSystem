package services.services.contracts;

import data.models.transportservices.Destination;
import services.data.dto.transportservices.DestinationCreateDTO;
import services.data.dto.transportservices.DestinationUpdateDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing {@link Destination} entities with CRUD operations.
 */
public interface IDestinationService {

    /**
     * Creates a new destination synchronously.
     *
     * @param dto the DTO containing data for the new destination
     * @return the created Destination entity
     */
    public Destination create(DestinationCreateDTO dto);

    /**
     * Creates a new destination asynchronously.
     *
     * @param dto the DTO containing data for the new destination
     * @return a CompletableFuture resolving to the created Destination entity
     */
    public CompletableFuture<Destination> createAsync(DestinationCreateDTO dto);

    /**
     * Updates an existing destination synchronously.
     *
     * @param dto the DTO containing updated data for the destination
     * @return the updated Destination entity
     */
    public Destination update(DestinationUpdateDTO dto);

    /**
     * Updates an existing destination asynchronously.
     *
     * @param dto the DTO containing updated data for the destination
     * @return a CompletableFuture resolving to the updated Destination entity
     */
    public CompletableFuture<Destination> updateAsync(DestinationUpdateDTO dto);

    /**
     * Deletes a destination by its ID synchronously.
     *
     * @param id the ID of the destination to delete
     */
    public void delete(Long id);

    /**
     * Deletes a destination by its ID asynchronously.
     *
     * @param id the ID of the destination to delete
     * @return a CompletableFuture indicating when the deletion is complete
     */
    public CompletableFuture<Void> deleteAsync(Long id);

    /**
     * Retrieves a destination by its ID synchronously.
     *
     * @param id the ID of the destination to retrieve
     * @return the Destination entity, or null if not found
     */
    public Destination getById(Long id);

    /**
     * Retrieves a list of all destinations synchronously with pagination and sorting.
     *
     * @param page the page number (0-based)
     * @param size the number of entities per page
     * @param orderBy the field to sort by (e.g., "startingLocation")
     * @param ascending true for ascending order, false for descending
     * @return a list of Destination entities
     */
    public List<Destination> getAll(int page, int size, String orderBy, boolean ascending);
}