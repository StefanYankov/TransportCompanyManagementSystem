package services.services.contracts;

import data.repositories.exceptions.RepositoryException;
import services.data.dto.employees.QualificationCreateDTO;
import services.data.dto.employees.QualificationUpdateDTO;
import services.data.dto.employees.QualificationViewDTO;
import services.data.dto.employees.DriverViewDTO;

import java.util.List;
import java.util.Map;

/**
 * Interface defining operations for managing {@link data.models.employee.Qualification} entities in the transport system.
 * Provides methods for CRUD operations and qualification-specific queries.
 * All methods may throw {@link RepositoryException} as an unchecked exception, documented below.
 */
public interface IQualificationService {

    /**
     * Creates a new qualification based on the provided DTO.
     *
     * @param dto the data transfer object containing qualification creation details
     * @return a view DTO representing the created qualification
     * @throws IllegalArgumentException if the DTO is null
     * @throws RepositoryException if the qualification cannot be created (e.g., duplicate name, database errors)
     */
    public QualificationViewDTO create(QualificationCreateDTO dto);

    /**
     * Updates an existing qualification based on the provided DTO.
     *
     * @param dto the data transfer object containing updated qualification details
     * @return a view DTO representing the updated qualification
     * @throws IllegalArgumentException if the DTO or its ID is null
     * @throws RepositoryException if the qualification is not found or update fails (e.g., duplicate name, optimistic locking conflict)
     */
    public QualificationViewDTO update(QualificationUpdateDTO dto);

    /**
     * Deletes a qualification by its ID.
     *
     * @param id the ID of the qualification to delete
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException if deletion fails (e.g., qualification not found, database errors)
     */
    public void delete(Long id);

    /**
     * Retrieves a qualification by its ID.
     *
     * @param id the ID of the qualification to retrieve
     * @return a view DTO of the qualification if found, null otherwise
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException if retrieval fails (e.g., database errors)
     */
    public QualificationViewDTO getById(Long id);

    /**
     * Retrieves a paginated list of all qualifications, optionally sorted and filtered.
     *
     * @param page the page number (0-based)
     * @param size the number of qualifications per page
     * @param orderBy the field to sort by (e.g., "name"); may be null for no sorting
     * @param ascending true for ascending order, false for descending
     * @param filter a string to filter qualifications by name or description; may be null or empty for no filtering
     * @return a list of qualification view DTOs
     * @throws RepositoryException if the query fails (e.g., invalid orderBy field)
     */
    public List<QualificationViewDTO> getAll(int page, int size, String orderBy, boolean ascending, String filter);

    /**
     * Retrieves qualifications matching the specified criteria, optionally sorted.
     *
     * @param conditions a map of field names to values for filtering (e.g., "name" -> "Heavy Duty License")
     * @param orderBy the field to sort by (e.g., "name"); may be null for no sorting
     * @param ascending true for ascending order, false for descending
     * @return a list of qualification view DTOs matching the criteria
     * @throws RepositoryException if the query fails (e.g., invalid condition field)
     */
    public List<QualificationViewDTO> findByCriteria(Map<String, Object> conditions, String orderBy, boolean ascending);

    /**
     * Retrieves a map of qualification IDs to lists of drivers possessing each qualification.
     *
     * @return a map where keys are qualification IDs and values are lists of driver view DTOs
     * @throws RepositoryException if the query fails (e.g., database errors)
     */
    public Map<Long, List<DriverViewDTO>> getDriversByQualification();
}