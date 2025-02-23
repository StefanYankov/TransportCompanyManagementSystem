package services.services.contracts;

import data.models.TransportCompany;

import services.data.dto.companies.TransportCompanyCreateDTO;
import services.data.dto.companies.TransportCompanyUpdateDTO;
import services.data.dto.companies.TransportCompanyViewDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing {@link TransportCompany} entities with CRUD operations.
 */
public interface ITransportCompanyService {

    /**
     * Creates a new transport company synchronously.
     *
     * @param dto the DTO containing data for the new transport company
     * @return the created {@link TransportCompanyViewDTO} entity
     */
    public TransportCompanyViewDTO create(TransportCompanyCreateDTO dto);

    public CompletableFuture<TransportCompanyViewDTO> createAsync(TransportCompanyCreateDTO dto);

    /**
     * Updates an existing transport company synchronously.
     *
     * @param dto the DTO containing updated data for the transport company
     * @return the updated TransportCompany entity
     */
    public TransportCompanyViewDTO update(TransportCompanyUpdateDTO dto);

    /**
     * Updates an existing transport company asynchronously.
     *
     * @param dto the {@link CompletableFuture} DTO containing updated data for the transport company
     * @return the updated TransportCompany entity
     */
    public CompletableFuture<TransportCompanyViewDTO> updateAsync(TransportCompanyUpdateDTO dto);

    /**
     * Deletes a transport company by its ID synchronously.
     *
     * @param id the ID of the transport company to delete
     */
    public void delete(Long id);

    /**
     * Retrieves a transport company by its ID synchronously and returns its ViewDTO representation.
     *
     * @param id the ID of the transport company to retrieve
     * @return the TransportCompanyViewDTO representing the transport company, or null if not found
     */
    public TransportCompanyViewDTO getById(Long id);

    /**
     * Retrieves a list of all transport companies synchronously, with pagination and sorting,
     * and returns the ViewDTO representations of the transport companies.
     *
     * @param page      the page number (0-based)
     * @param size      the number of entities per page
     * @param orderBy   the field to sort by (e.g., "name")
     * @param ascending true for ascending order, false for descending
     * @return a list of TransportCompanyViewDTO objects representing the transport companies
     */
    public List<TransportCompanyViewDTO> getAll(int page, int size, String orderBy, boolean ascending);


    /**
     * Retrieves companies sorted by total revenue synchronously from their transport services.
     *
     * @param ascending true for ascending order, false for descending
     * @return a list of TransportCompany entities sorted by revenue
     */
    public List<TransportCompany> getCompaniesSortedByRevenue(boolean ascending);

    /**
     * Calculates the total revenue for the company over a specified period synchronously.
     *
     * @param companyId the ID of the company
     * @param startDate the start date of the period (inclusive)
     * @param endDate the end date of the period (inclusive)
     * @return the total revenue as a BigDecimal
     */
    public BigDecimal getRevenueForPeriod(Long companyId, LocalDate startDate, LocalDate endDate);


    public CompletableFuture<Void> deleteAsync(Long id);
    public CompletableFuture<TransportCompanyViewDTO> getByIdAsync(Long id);
    public CompletableFuture<List<TransportCompanyViewDTO>> getAllAsync(int page, int size, String orderBy, boolean ascending);
}