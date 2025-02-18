package services;

import data.models.TransportCompany;
import data.models.employee.Employee;
import data.repositories.IGenericRepository;
import services.data.dto.CreateTransportCompanyInputModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * Service class responsible for handling operations related to transport companies and their employees.
 * This class supports asynchronous operations.
 */
public class TransportCompanyService {
    private final IGenericRepository<Employee, Long> employeeRepository;
    private final IGenericRepository<TransportCompany, Long> companyRepository;


    public TransportCompanyService(IGenericRepository<TransportCompany, Long> companyRepository,
                                   IGenericRepository<Employee, Long> employeeRepository) {
        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
    }

    public void CreateTransportCompany(CreateTransportCompanyInputModel inputModel) {
        TransportCompany transportCompanyModel = new TransportCompany();
        transportCompanyModel.setName(inputModel.getCompanyName());
        transportCompanyModel.setAddress(inputModel.getAddress());

        this.companyRepository.add(transportCompanyModel);
    }

    public CompletableFuture<Void> createTransportCompanyAsync(CreateTransportCompanyInputModel inputModel) {
        TransportCompany transportCompanyModel = new TransportCompany();
        transportCompanyModel.setName(inputModel.getCompanyName());
        transportCompanyModel.setAddress(inputModel.getAddress());

        return CompletableFuture.runAsync(() -> companyRepository.addAsync(transportCompanyModel));

    }

    /**
     * Asynchronously retrieves a transport company by its ID.
     *
     * @param companyId The ID of the transport company to retrieve.
     * @return A CompletableFuture containing the transport company, if found.
     */
    public CompletableFuture<TransportCompany> getCompanyByIdAsync(Long companyId) {
        return companyRepository.getByIdAsync(companyId)
                .thenApply(optionalCompany -> optionalCompany.orElseThrow(() -> new RuntimeException("Company not found")));
    }

    public CompletableFuture<List<TransportCompany>> getCompaniesByNameAsynch(String companyName, String orderBy, boolean ascending) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", companyName);
        return companyRepository.findByCriteriaAsync(conditions, orderBy, true);
    }
}
