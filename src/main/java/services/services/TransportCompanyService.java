package services.services;

import data.models.Client;
import data.models.employee.Employee;
import data.models.TransportCompany;
import data.models.transportservices.TransportService;
import data.models.vehicles.Vehicle;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.common.Constants;
import services.data.dto.clients.ClientViewDTO;
import services.data.dto.companies.TransportCompanyCreateDTO;
import services.data.dto.companies.TransportCompanyUpdateDTO;
import services.data.dto.companies.TransportCompanyViewDTO;
import services.data.dto.employees.EmployeeViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.dto.vehicles.VehicleViewDTO;
import services.data.mapping.mappers.*;
import services.services.contracts.ITransportCompanyService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class TransportCompanyService implements ITransportCompanyService {
    private static final Logger logger = LoggerFactory.getLogger(TransportCompanyService.class);
    private final IGenericRepository<TransportCompany, Long> companyRepo;
    private final IGenericRepository<Employee, Long> employeeRepo;
    private final IGenericRepository<Vehicle, Long> vehicleRepo;
    private final IGenericRepository<TransportService, Long> transportServiceRepo;
    private final TransportCompanyMapper companyMapper;
    private final EmployeeMapper employeeMapper;
    private final VehicleMapper vehicleMapper;
    private final TransportServiceMapper transportServiceMapper;
    private final ClientMapper clientMapper;

    public TransportCompanyService(IGenericRepository<TransportCompany, Long> companyRepo,
                                   IGenericRepository<Employee, Long> employeeRepo,
                                   IGenericRepository<Vehicle, Long> vehicleRepo,
                                   IGenericRepository<TransportService, Long> transportServiceRepo,
                                   TransportCompanyMapper companyMapper,
                                   EmployeeMapper employeeMapper,
                                   VehicleMapper vehicleMapper,
                                   TransportServiceMapper transportServiceMapper,
                                   ClientMapper clientMapper) {
        this.companyRepo = companyRepo;
        this.employeeRepo = employeeRepo;
        this.vehicleRepo = vehicleRepo;
        this.transportServiceRepo = transportServiceRepo;
        this.companyMapper = companyMapper;
        this.employeeMapper = employeeMapper;
        this.vehicleMapper = vehicleMapper;
        this.transportServiceMapper = transportServiceMapper;
        this.clientMapper = clientMapper;
    }

    @Override
    public TransportCompanyViewDTO create(TransportCompanyCreateDTO dto) {
        if (dto == null) {
            logger.error("Cannot create {}: DTO is null", Constants.TRANSPORT_COMPANY);
            throw new IllegalArgumentException("TransportCompanyCreateDTO must not be null");
        }
        logger.debug("Creating {} with DTO: {}", Constants.TRANSPORT_COMPANY, dto);
        try {
            TransportCompany entity = companyMapper.toEntity(dto);
            TransportCompany created = companyRepo.create(entity);
            logger.info("{} created with ID: {}", Constants.TRANSPORT_COMPANY, created.getId());
            return companyMapper.toViewDTO(created);
        } catch (RepositoryException e) {
            logger.error("Failed to create {}: {}, cause: {}", Constants.TRANSPORT_COMPANY, dto.getName(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public TransportCompanyViewDTO update(TransportCompanyUpdateDTO dto) {
        if (dto == null) {
            logger.error("Cannot update {}: DTO is null", Constants.TRANSPORT_COMPANY);
            throw new IllegalArgumentException("TransportCompanyUpdateDTO must not be null");
        }
        if (dto.getId() == null || dto.getId() == 0) {
            logger.error("Cannot update {}: ID is null or unset in DTO", Constants.TRANSPORT_COMPANY);
            throw new IllegalArgumentException("TransportCompany ID must not be null for update");
        }
        logger.debug("Updating {} with DTO: {}", Constants.TRANSPORT_COMPANY, dto);
        try {
            TransportCompany existing = companyRepo.getById(dto.getId(), "employees")
                    .orElseThrow(() -> new RepositoryException("TransportCompany not found with ID: " + dto.getId()));
            companyMapper.toEntity(dto, existing);
            TransportCompanyViewDTO result = companyRepo.updateAndMap(existing, companyMapper::toViewDTO, null);
            logger.info("{} updated with ID: {}", Constants.TRANSPORT_COMPANY, result.getId());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to update {} with ID: {}, cause: {}", Constants.TRANSPORT_COMPANY, dto.getId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            logger.error("Cannot delete {}: ID is null", Constants.TRANSPORT_COMPANY);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Deleting {} with ID: {}", Constants.TRANSPORT_COMPANY, id);
        try {
            TransportCompany entity = companyRepo.getById(id)
                    .orElseThrow(() -> new RepositoryException("TransportCompany not found with ID: " + id));
            companyRepo.delete(entity);
            logger.info("{} deleted with ID: {}", Constants.TRANSPORT_COMPANY, id);
        } catch (RepositoryException e) {
            logger.error("Failed to delete {} with ID: {}, cause: {}", Constants.TRANSPORT_COMPANY, id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public TransportCompanyViewDTO getById(Long id) {
        if (id == null) {
            logger.error("Cannot retrieve {}: ID is null", Constants.TRANSPORT_COMPANY);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Retrieving {} with ID: {}", Constants.TRANSPORT_COMPANY, id);
        try {
            Optional<TransportCompany> result = companyRepo.getById(id, "employees");
            if (result.isEmpty()) {
                logger.warn("No {} found with ID: {}", Constants.TRANSPORT_COMPANY, id);
                return null;
            }
            logger.info("{} retrieved with ID: {}", Constants.TRANSPORT_COMPANY, id);
            return companyMapper.toViewDTO(result.get());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} with ID: {}, cause: {}", Constants.TRANSPORT_COMPANY, id, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<TransportCompanyViewDTO> getAll(int page, int size, String orderBy, boolean ascending, String filter) {
        logger.debug("Retrieving all {}: page={}, size={}, orderBy={}, ascending={}, filter={}", Constants.TRANSPORT_COMPANY, page, size, orderBy, ascending, filter);
        try {
            List<TransportCompanyViewDTO> result = companyRepo.getAllAndMap(
                    page, size, orderBy, ascending, companyMapper::toViewDTO, "employees"
            );
            if (filter != null && !filter.trim().isEmpty()) {
                String filterLower = filter.trim().toLowerCase();
                result = result.stream()
                        .filter(c -> c.getName().toLowerCase().contains(filterLower) ||
                                c.getAddress().toLowerCase().contains(filterLower))
                        .collect(Collectors.toList());
            }
            logger.info("Retrieved {} {}", result.size(), Constants.TRANSPORT_COMPANY);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve all {}, cause: {}", Constants.TRANSPORT_COMPANY, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<TransportCompanyViewDTO> getCompaniesBetweenRevenue(BigDecimal minRevenue, BigDecimal maxRevenue) {
        if (minRevenue == null || maxRevenue == null) {
            logger.error("Cannot retrieve companies: Revenue range is null");
            throw new IllegalArgumentException("Revenue range must not be null");
        }
        if (minRevenue.compareTo(maxRevenue) > 0) {
            logger.error("Invalid revenue range: minRevenue > maxRevenue");
            throw new IllegalArgumentException("minRevenue must be less than or equal to maxRevenue");
        }
        logger.debug("Retrieving companies with revenue between {} and {}", minRevenue, maxRevenue);
        try {
            List<TransportCompany> allCompanies = companyRepo.getAll(0, Integer.MAX_VALUE, "name", true, "employees");
            List<TransportCompanyViewDTO> result = allCompanies.stream()
                    .filter(company -> {
                        BigDecimal revenue = getTotalRevenue(company.getId());
                        return revenue.compareTo(minRevenue) >= 0 && revenue.compareTo(maxRevenue) <= 0;
                    })
                    .map(companyMapper::toViewDTO)
                    .collect(Collectors.toList());
            logger.info("Found {} companies with revenue between {} and {}", result.size(), minRevenue, maxRevenue);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve companies with revenue between {} and {}, cause: {}", minRevenue, maxRevenue, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TransportCompanyViewDTO> findByCriteria(Map<String, Object> conditions, String orderBy, boolean ascending) {
        logger.debug("Finding {} by criteria: conditions={}, orderBy={}, ascending={}", Constants.TRANSPORT_COMPANY, conditions, orderBy, ascending);
        try {
            List<TransportCompany> companies = companyRepo.findByCriteria(conditions, orderBy, ascending, "employees");
            List<TransportCompanyViewDTO> result = companies.stream()
                    .map(companyMapper::toViewDTO)
                    .collect(Collectors.toList());
            logger.info("Found {} {} matching criteria", result.size(), Constants.TRANSPORT_COMPANY);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to find {} by criteria, cause: {}", Constants.TRANSPORT_COMPANY, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<EmployeeViewDTO> getEmployeesByCompany(Long companyId) {
        if (companyId == null) {
            logger.error("Cannot retrieve employees: Company ID is null");
            throw new IllegalArgumentException("Company ID must not be null");
        }
        logger.debug("Retrieving employees for company with ID: {}", companyId);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("transportCompany.id", companyId);
            List<Employee> employees = employeeRepo.findByCriteria(conditions, "familyName", true, "transportCompany");
            logger.info("Retrieved {} employees for company with ID: {}", employees.size(), companyId);
            return employees.stream()
                    .map(employeeMapper::toViewDTO)
                    .collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve employees for company with ID: {}, cause: {}", companyId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<VehicleViewDTO> getVehiclesByCompany(Long companyId) {
        if (companyId == null) {
            logger.error("Cannot retrieve vehicles: Company ID is null");
            throw new IllegalArgumentException("Company ID must not be null");
        }
        logger.debug("Retrieving vehicles for {} with ID: {}", Constants.TRANSPORT_COMPANY, companyId);
        try {
            List<Vehicle> vehicles = vehicleRepo.findWithJoin("transportCompany", "id", companyId, "registrationPlate", true);
            logger.info("Retrieved {} vehicles for {} with ID: {}", vehicles.size(), Constants.TRANSPORT_COMPANY, companyId);
            return vehicles.stream().map(vehicleMapper::toViewDTO).collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve vehicles for {} with ID: {}, cause: {}", Constants.TRANSPORT_COMPANY, companyId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TransportServiceViewDTO> getTransportServicesByCompany(Long companyId) {
        if (companyId == null) {
            logger.error("Cannot retrieve transport services: Company ID is null");
            throw new IllegalArgumentException("Company ID must not be null");
        }
        logger.debug("Retrieving transport services for {} with ID: {}", Constants.TRANSPORT_COMPANY, companyId);
        try {
            List<TransportService> services = transportServiceRepo.findWithJoin("transportCompany", "id", companyId, "startingDate", true, "transportCompany", "client");
            logger.info("Retrieved {} transport services for {} with ID: {}", services.size(), Constants.TRANSPORT_COMPANY, companyId);
            return services.stream().map(transportServiceMapper::toViewDTO).collect(Collectors.toList());
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve transport services for {} with ID: {}, cause: {}", Constants.TRANSPORT_COMPANY, companyId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Map<Long, Integer> getEmployeeCountsPerCompany() {
        logger.debug("Retrieving employee counts per {}", Constants.TRANSPORT_COMPANY);
        try {
            List<TransportCompany> companies = companyRepo.getAll(0, Integer.MAX_VALUE, "name", true, null);
            Map<Long, Integer> result = new HashMap<>();
            for (TransportCompany company : companies) {
                Map<String, Object> conditions = new HashMap<>();
                conditions.put("transportCompany.id", company.getId());
                List<Employee> employees = employeeRepo.findByCriteria(conditions, "familyName", true);
                result.put(company.getId(), employees.size());
            }
            logger.info("Retrieved employee counts for {} companies", result.size());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve employee counts per {}, cause: {}", Constants.TRANSPORT_COMPANY, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BigDecimal getTotalRevenue(Long companyId) {
        if (companyId == null) {
            logger.error("Cannot retrieve total revenue: Company ID is null");
            throw new IllegalArgumentException("Company ID must not be null");
        }
        logger.debug("Retrieving total revenue for company with ID: {}", companyId);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("transportCompany.id", companyId);
            List<TransportService> services = transportServiceRepo.findByCriteria(conditions, null, true);
            BigDecimal totalRevenue = services.stream()
                    .map(TransportService::getPrice)
                    .filter(Objects::nonNull) // Handle null prices
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            logger.info("Total revenue for company with ID {}: {}", companyId, totalRevenue);
            return totalRevenue;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve total revenue for company with ID: {}, cause: {}", companyId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public int getTotalTransportCount(Long companyId) {
        if (companyId == null) {
            logger.error("Cannot retrieve total transport count: Company ID is null");
            throw new IllegalArgumentException("Company ID must not be null");
        }
        logger.debug("Retrieving total transport count for company with ID: {}", companyId);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("transportCompany.id", companyId);
            List<TransportService> services = transportServiceRepo.findByCriteria(conditions, null, true);
            int count = services.size();
            logger.info("Total transport count for company with ID {}: {}", companyId, count);
            return count;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve total transport count for company with ID: {}, cause: {}", companyId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ClientViewDTO> getAllClientsForCompany(Long companyId) {
        if (companyId == null) {
            logger.error("Cannot retrieve clients: Company ID is null");
            throw new IllegalArgumentException("Company ID must not be null");
        }
        logger.debug("Retrieving all clients for company with ID: {}", companyId);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("transportCompany.id", companyId);
            List<TransportService> services = transportServiceRepo.findByCriteria(conditions, null, true, "client");
            Set<Client> clients = services.stream()
                    .map(TransportService::getClient)
                    .filter(Objects::nonNull) // Handle null clients
                    .collect(Collectors.toSet());
            List<ClientViewDTO> clientViewDTOs = clients.stream()
                    .map(clientMapper::toViewDTO)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} clients for company with ID: {}", clientViewDTOs.size(), companyId);
            return clientViewDTOs;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve clients for company with ID: {}, cause: {}", companyId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ClientViewDTO> getAllClientsForCompany(Long companyId, boolean paid) {
        if (companyId == null) {
            logger.error("Cannot retrieve clients: Company ID is null");
            throw new IllegalArgumentException("Company ID must not be null");
        }
        logger.debug("Retrieving all clients for company with ID: {}, paid: {}", companyId, paid);
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("transportCompany.id", companyId);
            List<TransportService> services = transportServiceRepo.findByCriteria(conditions, null, true, "client");
            Set<Client> clients = services.stream()
                    .filter(service -> service.isPaid() == paid)
                    .map(TransportService::getClient)
                    .filter(Objects::nonNull) // Handle null clients
                    .collect(Collectors.toSet());
            List<ClientViewDTO> clientViewDTOs = clients.stream()
                    .map(clientMapper::toViewDTO)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} clients for company with ID: {}, paid: {}", clientViewDTOs.size(), companyId, paid);
            return clientViewDTOs;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve clients for company with ID: {}, paid: {}, cause: {}", companyId, paid, e.getMessage(), e);
            throw e;
        }
    }
}