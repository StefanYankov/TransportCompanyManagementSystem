package services.services;

import data.models.vehicles.Van;
import services.data.dto.vehicles.VanCreateDTO;
import services.data.dto.vehicles.VanUpdateDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing {@link Van} entities with CRUD operations.
 */
public interface IVanService {

    /**
     * Creates a new van synchronously.
     *
     * @param dto the DTO containing data for the new van
     * @return the created Van entity
     */
    public Van create(VanCreateDTO dto);

    /**
     * Creates a new van asynchronously.
     *
     * @param dto the DTO containing data for the new van
     * @return a CompletableFuture resolving to the created Van entity
     */
    public CompletableFuture<Van> createAsync(VanCreateDTO dto);

    /**
     * Updates an existing van synchronously.
     *
     * @param dto the DTO containing updated data for the van
     * @return the updated Van entity
     */
    public Van update(VanUpdateDTO dto);

    /**
     * Updates an existing van asynchronously.
     *
     * @param dto the DTO containing updated data for the van
     * @return a CompletableFuture resolving to the updated Van entity
     */
    public CompletableFuture<Van> updateAsync(VanUpdateDTO dto);

    /**
     * Deletes a van by its ID synchronously.
     *
     * @param id the ID of the van to delete
     */
    public void delete(Long id);

    /**
     * Deletes a van by its ID asynchronously.
     *
     * @param id the ID of the van to delete
     * @return a CompletableFuture indicating when the deletion is complete
     */
    public CompletableFuture<Void> deleteAsync(Long id);

    /**
     * Retrieves a van by its ID synchronously.
     *
     * @param id the ID of the van to retrieve
     * @return the Van entity, or null if not found
     */
    public Van getById(Long id);

    /**
     * Retrieves a list of all vans synchronously with pagination and sorting.
     *
     * @param page the page number (0-based)
     * @param size the number of entities per page
     * @param orderBy the field to sort by (e.g., "registrationPlate")
     * @param ascending true for ascending order, false for descending
     * @return a list of Van entities
     */
    public List<Van> getAll(int page, int size, String orderBy, boolean ascending);
}