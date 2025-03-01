package services.services.contracts;

import data.repositories.exceptions.RepositoryException;
import services.data.dto.companies.TransportCompanyCreateDTO;
import services.data.dto.companies.TransportCompanyUpdateDTO;
import services.data.dto.companies.TransportCompanyViewDTO;
import services.data.dto.employees.EmployeeViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.dto.vehicles.VehicleViewDTO;

import java.util.List;
import java.util.Map;

/**
 * Interface defining operations for managing {@link data.models.TransportCompany} entities in the transport system.
 * Provides methods for CRUD operations, company-specific queries, and retrieval of related entities (employees, vehicles, transport services).
 * All methods may throw {@link RepositoryException} as an unchecked exception, documented below.
 */
public interface ITransportCompanyService {

    /**
     * Creates a new transport company based on the provided DTO.
     *
     * @param dto the data transfer object containing transport company creation details
     * @return a view DTO representing the created transport company
     * @throws IllegalArgumentException if the DTO is null
     * @throws RepositoryException      if the transport company cannot be created (e.g., database errors)
     */
    public TransportCompanyViewDTO create(TransportCompanyCreateDTO dto);

    /**
     * Updates an existing transport company based on the provided DTO.
     *
     * @param dto the data transfer object containing updated transport company details
     * @return a view DTO representing the updated transport company
     * @throws IllegalArgumentException if the DTO or its ID is null
     * @throws RepositoryException      if the transport company is not found or update fails (e.g., database errors)
     */
    public TransportCompanyViewDTO update(TransportCompanyUpdateDTO dto);

    /**
     * Deletes a transport company by its ID.
     *
     * @param id the ID of the transport company to delete
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException      if deletion fails (e.g., transport company not found, database errors)
     */
    public void delete(Long id);

    /**
     * Retrieves a transport company by its ID.
     *
     * @param id the ID of the transport company to retrieve
     * @return a view DTO of the transport company if found, null otherwise
     * @throws IllegalArgumentException if the ID is null
     * @throws RepositoryException      if retrieval fails (e.g., database errors)
     */
    public TransportCompanyViewDTO getById(Long id);

    /**
     * Retrieves a paginated list of all transport companies, optionally filtered and sorted.
     *
     * @param page      the page number (0-based)
     * @param size      the number of transport companies per page
     * @param orderBy   the field to sort by (e.g., "companyName"); may be null for no sorting
     * @param ascending true for ascending order, false for descending
     * @param filter    an optional filter string to match against companyName or address; may be null
     * @return a list of transport company view DTOs
     * @throws RepositoryException if the query fails (e.g., invalid orderBy field)
     */
    public List<TransportCompanyViewDTO> getAll(int page, int size, String orderBy, boolean ascending, String filter);

    /**
     * Finds transport companies matching the given criteria, optionally sorted.
     *
     * @param conditions map of field names to values for filtering (e.g., {"companyName": "Fast Transport"})
     * @param orderBy    the field to sort by; may be null for no sorting
     * @param ascending  true for ascending order, false for descending
     * @return a list of transport company view DTOs matching the criteria
     * @throws RepositoryException if the query fails (e.g., invalid field name)
     */
    public List<TransportCompanyViewDTO> findByCriteria(Map<String, Object> conditions, String orderBy, boolean ascending);

    /**
     * Retrieves the employees associated with a specific transport company by its ID.
     *
     * @param companyId the ID of the transport company
     * @return a list of employee view DTOs associated with the company
     * @throws IllegalArgumentException if the company ID is null
     * @throws RepositoryException      if the query fails (e.g., company not found, database errors)
     */
    public List<EmployeeViewDTO> getEmployeesByCompany(Long companyId);

    /**
     * Retrieves the vehicles associated with a specific transport company by its ID.
     *
     * @param companyId the ID of the transport company
     * @return a list of vehicle view DTOs associated with the company
     * @throws IllegalArgumentException if the company ID is null
     * @throws RepositoryException      if the query fails (e.g., company not found, database errors)
     */
    public List<VehicleViewDTO> getVehiclesByCompany(Long companyId);


    /**
     * Retrieves the transport services associated with a specific transport company by its ID.
     *
     * @param companyId the ID of the transport company
     * @return a list of transport service view DTOs associated with the company
     * @throws IllegalArgumentException if the company ID is null
     * @throws RepositoryException      if the query fails (e.g., company not found, database errors)
     */
    public List<TransportServiceViewDTO> getTransportServicesByCompany(Long companyId);

    /**
     * Retrieves the count of employees per transport company.
     *
     * @return a map where keys are company IDs and values are the count of employees
     * @throws RepositoryException if the query fails (e.g., database errors)
     */
    public Map<Long, Integer> getEmployeeCountsPerCompany();
}