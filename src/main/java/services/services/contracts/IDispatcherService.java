package services.services.contracts;

import data.models.employee.Dispatcher;
import services.data.dto.employees.DispatcherCreateDTO;
import services.data.dto.employees.DispatcherUpdateDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing {@link Dispatcher} entities with CRUD operations.
 */
public interface IDispatcherService {

    /**
     * Creates a new dispatcher synchronously.
     *
     * @param dto the DTO containing data for the new dispatcher
     * @return the created Dispatcher entity
     */
    public Dispatcher create(DispatcherCreateDTO dto);

    /**
     * Creates a new dispatcher asynchronously.
     *
     * @param dto the DTO containing data for the new dispatcher
     * @return a CompletableFuture resolving to the created Dispatcher entity
     */
    public CompletableFuture<Dispatcher> createAsync(DispatcherCreateDTO dto);

    /**
     * Updates an existing dispatcher synchronously.
     *
     * @param dto the DTO containing updated data for the dispatcher
     * @return the updated Dispatcher entity
     */
    public Dispatcher update(DispatcherUpdateDTO dto);

    /**
     * Updates an existing dispatcher asynchronously.
     *
     * @param dto the DTO containing updated data for the dispatcher
     * @return a CompletableFuture resolving to the updated Dispatcher entity
     */
    public CompletableFuture<Dispatcher> updateAsync(DispatcherUpdateDTO dto);

    /**
     * Deletes a dispatcher by its ID synchronously.
     *
     * @param id the ID of the dispatcher to delete
     */
    public void delete(Long id);

    /**
     * Deletes a dispatcher by its ID asynchronously.
     *
     * @param id the ID of the dispatcher to delete
     * @return a CompletableFuture indicating when the deletion is complete
     */
    public CompletableFuture<Void> deleteAsync(Long id);

    /**
     * Retrieves a dispatcher by its ID synchronously.
     *
     * @param id the ID of the dispatcher to retrieve
     * @return the Dispatcher entity, or null if not found
     */
    public Dispatcher getById(Long id);

    /**
     * Retrieves a list of all dispatchers synchronously with pagination and sorting.
     *
     * @param page the page number (0-based)
     * @param size the number of entities per page
     * @param orderBy the field to sort by (e.g., "familyName")
     * @param ascending true for ascending order, false for descending
     * @return a list of Dispatcher entities
     */
    public List<Dispatcher> getAll(int page, int size, String orderBy, boolean ascending);
}