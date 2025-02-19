package services.contracts;

import jakarta.persistence.criteria.JoinType;
import services.data.dto.employees.EmployeeCreateDto;
import services.data.dto.employees.EmployeeViewDto;
import services.data.dto.transportcompany.TransportCompanyCreateDto;
import services.data.dto.transportcompany.TransportCompanyViewDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Interface defining operations for managing transport companies.
 * Provides synchronous and asynchronous methods for creating transport companies.
 */
public interface ITransportCompanyService {

    public CompletableFuture<Void> createTransportCompanyAsync(TransportCompanyCreateDto dto);

    public CompletableFuture<Optional<TransportCompanyViewDto>> getTransportCompanyByIdAsync(long id);

    public CompletableFuture<List<TransportCompanyViewDto>> getAllTransportCompaniesAsync(int page,
                                                                                          int pageSize,
                                                                                          Map<String, JoinType> fetchRelations,
                                                                                          String orderBy,
                                                                                          boolean ascending);

    public CompletableFuture<Void> updateTransportCompanyAsync(TransportCompanyViewDto dto);

    public CompletableFuture<Void> deleteTransportCompanyAsync(long id);

    public CompletableFuture<List<EmployeeViewDto>> getAllEmployeesForACompanyAsync(long id);

    public CompletableFuture<Void> addEmployeeToCompanyAsync(EmployeeCreateDto dto, long id);
    public CompletableFuture<Void> removeEmployeeFromCompanyAsync(long employeeId, long id);


}
