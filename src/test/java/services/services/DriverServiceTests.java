package services.services;

import data.models.Client;
import data.models.TransportCompany;
import data.models.employee.Dispatcher;
import data.models.employee.Driver;
import data.models.employee.Employee;
import data.models.employee.Qualification;
import data.models.transportservices.Destination;
import data.models.transportservices.TransportCargoService;
import data.models.transportservices.TransportPassengersService;
import data.models.transportservices.TransportService;
import data.models.vehicles.*;
import data.repositories.GenericRepository;
import data.repositories.IGenericRepository;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.data.dto.employees.DriverCreateDTO;
import services.data.dto.employees.DriverViewDTO;
import services.data.mapping.mappers.DriverMapper;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.hibernate.cfg.Configuration;
import services.services.contracts.IDriverService;

import static org.junit.jupiter.api.Assertions.*;

class DriverServiceTests {
    private SessionFactory sessionFactory;
    private ExecutorService executorService;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<Driver, Long> driverRepo;
    private IGenericRepository<Qualification, Long> qualificationRepo;
    private IGenericRepository<Dispatcher, Long> dispatcherRepo;
    private IGenericRepository<TransportCargoService, Long> transportCargoRepo;
    private IGenericRepository<TransportPassengersService, Long> transportPassengerRepo;
    private DriverMapper driverMapper;
    private IDriverService driverService;


    @BeforeEach
    void SetUp() {
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
            executorService = Executors.newFixedThreadPool(2);

            // Initialize repositories for TransportCompany and Driver
            companyRepo = new GenericRepository<>(sessionFactory, executorService, TransportCompany.class);
            driverRepo = new GenericRepository<>(sessionFactory, executorService, Driver.class);
            qualificationRepo = new GenericRepository<>(sessionFactory, executorService, Qualification.class);
            dispatcherRepo = new GenericRepository<>(sessionFactory, executorService, Dispatcher.class);
            transportCargoRepo = new GenericRepository<>(sessionFactory, executorService, TransportCargoService.class);
            transportPassengerRepo = new GenericRepository<>(sessionFactory, executorService, TransportPassengersService.class);

            // Initialize mapper and service
            driverMapper = new DriverMapper();
            driverService = new DriverService(driverRepo,
                    driverMapper,
                    companyRepo,
                    dispatcherRepo,
                    transportCargoRepo,
                    transportPassengerRepo,
                    qualificationRepo);

            // Seed initial data
            TransportCompany company = new TransportCompany();
            company.setName("Fast Transport");
            company.setAddress("123 Main St");
            companyRepo.create(company);

            Qualification qualification = new Qualification();
            qualification.setName("Heavy Duty License");
            qualification.setDescription("For large vehicles");
            qualificationRepo.create(qualification);

            Driver driver = new Driver();
            driver.setFirstName("John");
            driver.setFamilyName("Doe");
            driver.setSalary(new BigDecimal("50000"));
            driver.setTransportCompany(company);
            driver.setQualifications(Set.of(qualification));
            driverRepo.create(driver);

        } catch (Exception e) {
            fail("Failed to initialize SessionFactory: " + e.getMessage());
        }
    }

    @AfterEach
    void TearDown() {
        // H2 drops tables automatically with create-drop, but we close resources manually
        if (executorService != null) {
            executorService.shutdown();
        }
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

//    @Test
//    public void CreateDriver_WhenValidDriverCreateDTO_ShouldCreateDriver()  {
//        // Arrange: A new driver DTO
//        DriverCreateDTO createDTO = new DriverCreateDTO();
//        createDTO.setFirstName("Jane");
//        createDTO.setFamilyName("Smith");
//        createDTO.setSalary(new BigDecimal("60000"));
//        createDTO.setTransportCompanyId(1L); // Assuming transport company with ID 1 exists
//        createDTO.setQualificationIds(Set.of(1L)); // Assuming qualification with ID 1 exists
//
//        // Act: Create driver is called
//        DriverViewDTO result = driverService.create(createDTO);
//
//        // Assert: The driver is created in the database
//        assertNotNull(result);
//        assertEquals("Jane", result.getFirstName());
//        assertEquals("Smith", result.getFamilyName());
//        assertNotNull(result.getId(), "Driver ID should not be null after creation");
//    }
//
//    @Test
//    public void CreateDriverAsync_WhenValidDriverCreateDTO_ShouldCreateDriverAsync() throws ExecutionException, InterruptedException {
//        // Arrange: A new driver DTO with required dependencies
//        DriverCreateDTO createDTO = new DriverCreateDTO();
//        createDTO.setFirstName("JaneAsync");
//        createDTO.setFamilyName("SmithAsync");
//        createDTO.setSalary(new BigDecimal("60000"));
//        createDTO.setTransportCompanyId(1L); // Using the company we seeded earlier
//        createDTO.setQualificationIds(Set.of(1L)); // Using the qualification we seeded earlier
//
//        // Act: Create the new driver asynchronously
//        CompletableFuture<DriverViewDTO> resultAsync = driverService.createAsync(createDTO);
//        DriverViewDTO newDriver = resultAsync.get();  // Wait for the result
//
//        // Assert: Verify that the new driver was created and the returned data is correct
//        assertNotNull(newDriver);  // Ensure the result is not null
//        assertEquals("JaneAsync", newDriver.getFirstName());
//        assertEquals("SmithAsync", newDriver.getFamilyName());
//        assertEquals(new BigDecimal("60000"), newDriver.getSalary());
//        assertEquals(1L, newDriver.getTransportCompanyId());
//
//        Set<Long> qualificationIds = newDriver.getDriverQualificationIds().stream()
//                .map(Long::valueOf)
//                .collect(Collectors.toSet());
//        assertEquals(createDTO.getQualificationIds(), qualificationIds);
//
//        assertNotNull(newDriver.getId(), "Driver ID should not be null after creation");
//    }
//

//    @Test
//    void updateDriverTest() {
//        // Given: An existing driver
//        Driver existingDriver = driverRepo.getById(1L).orElseThrow();
//        DriverUpdateDTO updateDTO = new DriverUpdateDTO();
//        updateDTO.setId(existingDriver.getId());
//        updateDTO.setFirstName("Updated Name");
//
//        // When: The driver is updated
//        DriverViewDTO updatedDriver = driverService.update(updateDTO);
//
//        // Then: The driver's name should be updated
//        assertEquals("Updated Name", updatedDriver.getFirstName());
//    }
//
//    @Test
//    void updateDriverAsyncTest() throws ExecutionException, InterruptedException {
//        // Given: An existing driver
//        CompletableFuture<Driver> existingDriver = driverRepo.getByIdAsync(1L);
//        DriverUpdateDTO updateDTO = new DriverUpdateDTO();
//        updateDTO.setId(existingDriver.getId());
//        updateDTO.setFirstName("Updated Async");
//
//        // When: The driver is updated asynchronously
//        CompletableFuture<DriverViewDTO> future = driverService.updateAsync(updateDTO);
//
//        // Then: The driver's name should be updated asynchronously
//        DriverViewDTO updatedDriver = future.get();
//        assertEquals("Updated Async", updatedDriver.getFirstName());
//    }
//
//    @Test
//    void updateDriverNotFoundTest() {
//        // Given: A non-existing driver
//        DriverUpdateDTO updateDTO = new DriverUpdateDTO();
//        updateDTO.setId(9999L); // ID that doesn't exist
//
//        // When: Update is called
//        assertThrows(RepositoryException.class, () -> driverService.update(updateDTO));
//    }
}