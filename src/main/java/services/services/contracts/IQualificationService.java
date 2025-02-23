package services.services.contracts;

import data.models.employee.Qualification;
import services.data.dto.employees.QualificationCreateDTO;
import services.data.dto.employees.QualificationUpdateDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing {@link Qualification} entities with CRUD operations.
 */
public interface IQualificationService {

    /**
     * Creates a new qualification synchronously.
     *
     * @param dto the DTO containing data for the new qualification
     * @return the created Qualification entity
     */
    public Qualification create(QualificationCreateDTO dto);

    /**
     * Creates a new qualification asynchronously.
     *
     * @param dto the DTO containing data for the new qualification
     * @return a CompletableFuture resolving to the created Qualification entity
     */
    public CompletableFuture<Qualification> createAsync(QualificationCreateDTO dto);

    /**
     * Updates an existing qualification synchronously.
     *
     * @param dto the DTO containing updated data for the qualification
     * @return the updated Qualification entity
     */
    public Qualification update(QualificationUpdateDTO dto);

    /**
     * Updates an existing qualification asynchronously.
     *
     * @param dto the DTO containing updated data for the qualification
     * @return a CompletableFuture resolving to the updated Qualification entity
     */
    public CompletableFuture<Qualification> updateAsync(QualificationUpdateDTO dto);

    /**
     * Deletes a qualification by its ID synchronously.
     *
     * @param id the ID of the qualification to delete
     */
    public void delete(Long id);

    /**
     * Deletes a qualification by its ID asynchronously.
     *
     * @param id the ID of the qualification to delete
     * @return a CompletableFuture indicating when the deletion is complete
     */
    public CompletableFuture<Void> deleteAsync(Long id);

    /**
     * Retrieves a qualification by its ID synchronously.
     *
     * @param id the ID of the qualification to retrieve
     * @return the Qualification entity, or null if not found
     */
    public Qualification getById(Long id);

    /**
     * Retrieves a list of all qualifications synchronously with pagination and sorting.
     *
     * @param page the page number (0-based)
     * @param size the number of entities per page
     * @param orderBy the field to sort by (e.g., "name")
     * @param ascending true for ascending order, false for descending
     * @return a list of Qualification entities
     */
    public List<Qualification> getAll(int page, int size, String orderBy, boolean ascending);
}