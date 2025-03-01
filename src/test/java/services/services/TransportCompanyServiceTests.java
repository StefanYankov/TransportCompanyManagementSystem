package services.services;

import data.models.*;
import data.models.employee.*;
import data.models.transportservices.*;
import data.models.vehicles.*;
import data.repositories.GenericRepository;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.data.dto.companies.TransportCompanyCreateDTO;
import services.data.dto.companies.TransportCompanyUpdateDTO;
import services.data.dto.companies.TransportCompanyViewDTO;
import services.data.dto.employees.EmployeeViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.dto.vehicles.VehicleViewDTO;
import services.data.mapping.mappers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;

public class TransportCompanyServiceTests {
    private SessionFactory sessionFactory;
    private ExecutorService executorService;
    private IGenericRepository<Client, Long> clientRepo;
    private IGenericRepository<TransportService, Long> transportServiceRepo;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<Employee, Long> employeeRepo;
    private IGenericRepository<Driver, Long> driverRepo;
    private IGenericRepository<Destination, Long> destinationRepo;
    private IGenericRepository<Vehicle, Long> vehicleRepo;
    private TransportCompanyService service;
    private TransportCompanyMapper companyMapper;
    private VehicleMapper vehicleMapper;
    private DestinationMapper destinationMapper;
    private TransportServiceMapper transportServiceMapper;
    private EmployeeMapper employeeMapper;

    @BeforeEach
    void Setup() {
        try {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(TransportCompany.class);
            configuration.addAnnotatedClass(Client.class);
            configuration.addAnnotatedClass(Employee.class);
            configuration.addAnnotatedClass(Driver.class);
            configuration.addAnnotatedClass(Dispatcher.class);
            configuration.addAnnotatedClass(Qualification.class);
            configuration.addAnnotatedClass(Destination.class);
            configuration.addAnnotatedClass(TransportService.class);
            configuration.addAnnotatedClass(TransportCargoService.class);
            configuration.addAnnotatedClass(TransportPassengersService.class);
            configuration.addAnnotatedClass(Vehicle.class);
            configuration.addAnnotatedClass(TransportCargoVehicle.class);
            configuration.addAnnotatedClass(TransportPeopleVehicle.class);
            configuration.addAnnotatedClass(Truck.class);
            configuration.addAnnotatedClass(Bus.class);
            configuration.addAnnotatedClass(Van.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);


            companyRepo = new GenericRepository<>(sessionFactory,  TransportCompany.class);
            clientRepo = new GenericRepository<>(sessionFactory,  Client.class);
            employeeRepo = new GenericRepository<>(sessionFactory, Employee.class);
            transportServiceRepo = new GenericRepository<>(sessionFactory, TransportService.class);
            destinationRepo = new GenericRepository<>(sessionFactory, Destination.class);
            driverRepo = new GenericRepository<>(sessionFactory, Driver.class);
            vehicleRepo = new GenericRepository<>(sessionFactory, Vehicle.class);

            destinationMapper = new DestinationMapper();

            companyMapper = new TransportCompanyMapper();
            vehicleMapper = new VehicleMapper(companyRepo);
            transportServiceMapper = new TransportServiceMapper(companyRepo, clientRepo, driverRepo, destinationRepo, vehicleRepo);
            employeeMapper = new EmployeeMapper();
            service = new TransportCompanyService(companyRepo, employeeRepo, vehicleRepo, transportServiceRepo,
                    companyMapper,  employeeMapper,vehicleMapper, transportServiceMapper);
        } catch (Exception e) {
            fail("Failed to initialize SessionFactory: " + e.getMessage());
        }
    }

    @AfterEach
    void TearDown() {
        if (executorService != null) executorService.shutdown();
        if (sessionFactory != null) sessionFactory.close();
    }

    // Happy Path Tests
    @Test
    void create_ValidDTO_ShouldCreateCompany() {
        TransportCompanyCreateDTO dto = new TransportCompanyCreateDTO("Fast Transport", "123 Main St");
        TransportCompanyViewDTO result = service.create(dto);

        assertNotNull(result.getId());
        assertEquals("Fast Transport", result.getName());
        assertEquals("123 Main St", result.getAddress());
        assertNotNull(result.getEmployeeIds());
        assertTrue(result.getEmployeeIds().isEmpty());
    }

    @Test
    void update_FullUpdate_ShouldUpdateCompany() {
        TransportCompanyCreateDTO createDto = new TransportCompanyCreateDTO("Initial Co", "Initial Address");
        TransportCompanyViewDTO created = service.create(createDto);

        TransportCompanyUpdateDTO updateDto = new TransportCompanyUpdateDTO(created.getId(), "Updated Co", "Updated Address");
        TransportCompanyViewDTO result = service.update(updateDto);

        assertEquals("Updated Co", result.getName());
        assertEquals("Updated Address", result.getAddress());
    }

    @Test
    void update_PartialUpdate_ShouldPreserveUnchangedFields() {
        TransportCompanyCreateDTO createDto = new TransportCompanyCreateDTO("Initial Co", "Initial Address");
        TransportCompanyViewDTO created = service.create(createDto);

        TransportCompanyUpdateDTO updateDto = new TransportCompanyUpdateDTO(created.getId(), "Updated Co", null);
        TransportCompanyViewDTO result = service.update(updateDto);

        assertEquals("Updated Co", result.getName());
        assertEquals("Initial Address", result.getAddress());
    }

    @Test
    void delete_ExistingId_ShouldDeleteCompany() {
        TransportCompanyCreateDTO dto = new TransportCompanyCreateDTO("Temp Co", "Temp Address");
        TransportCompanyViewDTO created = service.create(dto);

        service.delete(created.getId());
        TransportCompanyViewDTO result = service.getById(created.getId());
        assertNull(result);
    }

    @Test
    void getById_ExistingId_ShouldReturnCompany() {
        TransportCompanyCreateDTO dto = new TransportCompanyCreateDTO("Test Co", "Test Address");
        TransportCompanyViewDTO created = service.create(dto);

        TransportCompanyViewDTO result = service.getById(created.getId());
        assertNotNull(result);
        assertEquals("Test Co", result.getName());
        assertNotNull(result.getEmployeeIds());
    }

    @Test
    void getAll_WithFilter_ShouldReturnFilteredList() {
        service.create(new TransportCompanyCreateDTO("Fast Transport", "123 Main St"));
        service.create(new TransportCompanyCreateDTO("Swift Logistics", "456 Oak Ave"));

        List<TransportCompanyViewDTO> result = service.getAll(0, 10, "name", true, "Fast");
        assertEquals(1, result.size());
        assertEquals("Fast Transport", result.getFirst().getName());
    }

    @Test
    void getAll_NoFilter_ShouldReturnAll() {
        service.create(new TransportCompanyCreateDTO("Fast Transport", "123 Main St"));
        service.create(new TransportCompanyCreateDTO("Swift Logistics", "456 Oak Ave"));

        List<TransportCompanyViewDTO> result = service.getAll(0, 10, "name", true, null);
        assertEquals(2, result.size());
    }

    @Test
    void findByCriteria_NameMatch_ShouldReturnMatchingCompanies() {
        service.create(new TransportCompanyCreateDTO("Fast Transport", "123 Main St"));
        service.create(new TransportCompanyCreateDTO("Swift Logistics", "456 Oak Ave"));

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "Fast Transport");
        List<TransportCompanyViewDTO> result = service.findByCriteria(conditions, "name", true);
        assertEquals(1, result.size());
        assertEquals("Fast Transport", result.getFirst().getName());
    }

    @Test
    void findByCriteria_AddressMatch_ShouldReturnMatchingCompanies() {
        service.create(new TransportCompanyCreateDTO("Fast Transport", "123 Main St"));
        service.create(new TransportCompanyCreateDTO("Swift Logistics", "456 Oak Ave"));

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("address", "456 Oak Ave");
        List<TransportCompanyViewDTO> result = service.findByCriteria(conditions, "name", true);
        assertEquals(1, result.size());
        assertEquals("Swift Logistics", result.getFirst().getName());
    }

    @Test
    void getEmployeesByCompany_WithEmployees_ShouldReturnEmployees() {
        TransportCompanyCreateDTO companyDto = new TransportCompanyCreateDTO("Fast Transport", "123 Main St");
        TransportCompanyViewDTO company = service.create(companyDto);

        Driver employee = new Driver();
        employee.setFirstName("John");
        employee.setFamilyName("Doe");
        employee.setSalary(new BigDecimal("50000"));
        employee.setTransportCompany(companyRepo.getById(company.getId()).get());
        employeeRepo.create(employee);

        List<EmployeeViewDTO> result = service.getEmployeesByCompany(company.getId());
        assertEquals(1, result.size());
        assertEquals("Doe", result.getFirst().getFamilyName());
        assertTrue(company.getEmployeeIds().contains(employee.getId()));
    }

    @Test
    void getVehiclesByCompany_WithVehicles_ShouldReturnVehicles() {
        TransportCompanyCreateDTO companyDto = new TransportCompanyCreateDTO("Fast Transport", "123 Main St");
        TransportCompanyViewDTO company = service.create(companyDto);

        Truck vehicle = new Truck();
        vehicle.setRegistrationPlate("AC1234CA");
        vehicle.setMaxCargoCapacityKg(1000.0);
        vehicle.setCurrentCargoCapacityKg(0.0);
        vehicle.setCargoType(CargoType.REGULAR);
        vehicle.setTruckType(TruckType.FLATBED);
        vehicle.setTransportCompany(companyRepo.getById(company.getId()).get());
        vehicleRepo.create(vehicle);

        List<VehicleViewDTO> result = service.getVehiclesByCompany(company.getId());
        assertEquals(1, result.size());
        assertEquals("AC1234CA", result.getFirst().getRegistrationPlate());
    }

    @Test
    void getTransportServicesByCompany_WithServices_ShouldReturnServices() {
        TransportCompanyCreateDTO companyDto = new TransportCompanyCreateDTO("Fast Transport", "123 Main St");
        TransportCompanyViewDTO company = service.create(companyDto);

        TransportCargoService service1 = new TransportCargoService();
        service1.setStartingDate(LocalDate.now());
        service1.setPrice(new BigDecimal("2000"));
        service1.setTransportCompany(companyRepo.getById(company.getId()).get());
        service1.setWeightInKilograms(BigDecimal.valueOf(25));
        service1.setLengthInCentimeters(50);
        service1.setWidthInCentimeters(25);
        service1.setHeightInCentimeters(25);
        transportServiceRepo.create(service1);

        List<TransportServiceViewDTO> result = service.getTransportServicesByCompany(company.getId());
        assertEquals(1, result.size());
        assertEquals(new BigDecimal("2000.00"), result.getFirst().getPrice());
    }

    @Test
    void getEmployeeCountsPerCompany_WithEmployees_ShouldReturnCounts() {
        TransportCompanyCreateDTO companyDto1 = new TransportCompanyCreateDTO("Fast Transport", "123 Main St");
        TransportCompanyViewDTO company1 = service.create(companyDto1);
        TransportCompanyCreateDTO companyDto2 = new TransportCompanyCreateDTO("Swift Logistics", "456 Oak Ave");
        TransportCompanyViewDTO company2 = service.create(companyDto2);

        Driver employee = new Driver();
        employee.setFirstName("John");
        employee.setFamilyName("Doe");
        employee.setSalary(new BigDecimal("50000"));
        employee.setTransportCompany(companyRepo.getById(company1.getId()).get());
        employeeRepo.create(employee);

        Map<Long, Integer> result = service.getEmployeeCountsPerCompany();
        assertEquals(2, result.size());
        assertEquals(1, result.get(company1.getId()));
        assertEquals(0, result.get(company2.getId()));
    }

    // Error Cases
    @Test
    void create_NullDTO_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.create(null));
    }

    @Test
    void update_NullDTO_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.update(null));
    }

    @Test
    void update_NonExistentId_ShouldThrowRepositoryException() {
        TransportCompanyUpdateDTO dto = new TransportCompanyUpdateDTO(999L, "NonExistent", "Nowhere");
        assertThrows(RepositoryException.class, () -> service.update(dto));
    }

    @Test
    void delete_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.delete(null));
    }


    @Test
    void getById_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getById(null));
    }

    @Test
    void getEmployeesByCompany_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getEmployeesByCompany(null));
    }

    // Edge Cases
    @Test
    void getAll_EmptyFilter_ShouldReturnAll() {
        service.create(new TransportCompanyCreateDTO("Fast Transport", "123 Main St"));
        service.create(new TransportCompanyCreateDTO("Swift Logistics", "456 Oak Ave"));

        List<TransportCompanyViewDTO> result = service.getAll(0, Integer.MAX_VALUE, "name", true, "");
        assertEquals(2, result.size());
    }

    @Test
    void getAll_InvalidPage_ShouldReturnEmptyList()  {
        service.create(new TransportCompanyCreateDTO("Test Co", "Test Address"));
        List<TransportCompanyViewDTO> result = service.getAll(1, 1, "name", true, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByCriteria_NoMatches_ShouldReturnEmptyList() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "NonExistent");
        List<TransportCompanyViewDTO> result = service.findByCriteria(conditions, "name", true);
        assertTrue(result.isEmpty());
    }

    @Test
    void getEmployeesByCompany_NoEmployees_ShouldReturnEmptyList() {
        TransportCompanyCreateDTO companyDto = new TransportCompanyCreateDTO("Fast Transport", "123 Main St");
        TransportCompanyViewDTO company = service.create(companyDto);

        List<EmployeeViewDTO> result = service.getEmployeesByCompany(company.getId());
        assertTrue(result.isEmpty());
    }
}