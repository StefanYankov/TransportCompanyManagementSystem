package data.repositories;

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
import data.repositories.exceptions.RepositoryException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.*;


public class GenericRepositoryTest {
    private SessionFactory sessionFactory;
    private ExecutorService executorService;
    private GenericRepository<TransportCompany, Long> companyRepo;
    private GenericRepository<Driver, Long> driverRepo;
    private GenericRepository<Qualification, Long> qualificationRepo;
    private GenericRepository<TransportCargoService, Long> serviceRepo;


    @BeforeEach
    void SetUp() {
        try{
            Configuration configuration = new Configuration();
            // Add all entity classes explicitly since no persistence.xml
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
            serviceRepo = new GenericRepository<>(sessionFactory, executorService, TransportCargoService.class);

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

            TransportCargoService transportCargoService = new TransportCargoService();
            transportCargoService.setTransportCompany(company);
            transportCargoService.setPrice(new BigDecimal("1000"));
            transportCargoService.setStartingDate(LocalDate.now());
            transportCargoService.setEndingDate(LocalDate.now().plusDays(1));
            transportCargoService.setWeightInKilograms(BigDecimal.valueOf(25));
            transportCargoService.setLengthInCentimeters(50);
            transportCargoService.setWidthInCentimeters(25);
            transportCargoService.setHeightInCentimeters(25);

            serviceRepo.create(transportCargoService);
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

    // CRUD Tests
    @Test
    void Create_WhenEntityIsValid_ShouldPersistAndReturnEntity() {
        TransportCompany company = new TransportCompany();
        company.setName("Swift Delivery");
        company.setAddress("456 Oak Ave");

        TransportCompany result = companyRepo.create(company);

        assertNotNull(result.getId(), "H2 generates ID via IDENTITY strategy");
        assertEquals("Swift Delivery", result.getName());
    }

    @Test
    void Create_WhenEntityIsNull_ShouldThrowException() {
        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> companyRepo.create(null),
                "Expected RepositoryException for null entity");

        assertTrue(exception.getMessage().contains("Cannot create null entity"));
    }

    @Test
    void Update_WhenEntityExists_ShouldUpdateAndReturnEntity() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        company.setAddress("789 Pine Rd");

        TransportCompany result = companyRepo.update(company);

        assertEquals("789 Pine Rd", result.getAddress());
        assertEquals(company.getId(), result.getId());
    }

    @Test
    void Delete_WhenEntityExists_ShouldRemoveEntity() {
        // Arrange
        TransportCompany company = new TransportCompany();
        company.setName("Test Transport");
        company.setAddress("456 Test Ave");

        // Create the entity and confirm it exists
        TransportCompany createdCompany = companyRepo.create(company);
        Long companyId = createdCompany.getId(); // Assuming getId() exists
        assertNotNull(companyRepo.getById(companyId), "Entity should exist before deletion");

        // Act
        companyRepo.delete(createdCompany);

        // Assert
        assertThrows(RepositoryException.class, () -> companyRepo.getById(companyId),
                "Entity should not be found after deletion");
    }

    @Test
    void DeleteAsync_WhenEntityExists_ShouldRemoveEntity() throws ExecutionException, InterruptedException {
        // Arrange
        TransportCompany company = new TransportCompany();
        company.setName("Async Test Transport");
        company.setAddress("789 Async Rd");

        TransportCompany createdCompany = companyRepo.create(company);
        Long companyId = createdCompany.getId();
        assertNotNull(companyRepo.getById(companyId), "Entity should exist before deletion");

        // Act
        CompletableFuture<Void> deleteFuture = companyRepo.deleteAsync(createdCompany);
        deleteFuture.get(); // Wait for completion

        // Assert
        assertThrows(RepositoryException.class, () -> companyRepo.getById(companyId),
                "Entity should not be found after async deletion");
    }

    @Test
    void GetById_WhenIdExists_ShouldReturnEntity() {
        // Arrange
        TransportCompany company = new TransportCompany();
        company.setName("Test Transport");
        company.setAddress("456 Test Ave");

        TransportCompany createdCompany = companyRepo.create(company);
        Long companyId = createdCompany.getId();
        System.out.println("Created company ID: " + companyId);
        assertNotNull(companyRepo.getById(companyId), "Entity should exist before deletion");

        // Act
        companyRepo.delete(createdCompany);

        // Assert
        assertThrows(RepositoryException.class, () -> {
            TransportCompany result = companyRepo.getById(companyId);
            System.out.println("Unexpected result after deletion: " + result);
        }, "Entity should not be found after deletion");
    }

    @Test
    void GetById_WhenIdDoesNotExist_ShouldThrowException() {
        // Arrange
        Long nonExistentId = 999L; // An ID unlikely to exist based on your setup

        // Act & Assert
        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> companyRepo.getById(nonExistentId),
                "GetById should throw RepositoryException for non-existent ID");

        // Optional: Verify exception message (if you want to be more specific)
        assertTrue(exception.getMessage().contains("not found"),
                "Exception message should indicate entity not found");
    }

    // Fetch Relations Test
    @Test
    void GetAll_WhenFetchingRelations_ShouldLoadRelatedData() {
        // Seed a Driver with a TransportCompany
        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setFamilyName("Doe");
        driver.setSalary(new java.math.BigDecimal("50000"));
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        driver.setTransportCompany(company);
        driverRepo.create(driver);

        List<TransportCompany> companies = companyRepo.getAll(0, 10, "name", true, "employees");

        assertFalse(companies.isEmpty());
        TransportCompany result = companies.getFirst();
        assertFalse(result.getEmployees().isEmpty(), "Employees should be eagerly fetched");
        assertEquals("John", result.getEmployees().iterator().next().getFirstName());
    }

    @Test
    void GetAll_WhenEntitiesExist_ShouldReturnPaginatedList() {
        TransportCompany company1 = new TransportCompany("Alpha Transport", "111 St");

        TransportCompany company2 = new TransportCompany("Beta Transport", "222 St");
        companyRepo.create(company1);
        companyRepo.create(company2);
        List<TransportCompany> companies = companyRepo.getAll(0, 1, "name", true);
        assertEquals(1, companies.size(), "Should return one entity per page");
        assertEquals("Alpha Transport", companies.getFirst().getName(), "First entity should be Alpha (ascending order)");
    }

    // Aggregation Test
    @Test
    void FindWithAggregation_WhenRelationExists_ShouldSortBySum() {
        // Seed TransportServices
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TransportCargoService service1 = new TransportCargoService();
        service1.setTransportCompany(company);
        service1.setPrice(new java.math.BigDecimal("1000"));
        TransportCargoService service2 = new TransportCargoService();
        service2.setTransportCompany(company);
        service2.setPrice(new java.math.BigDecimal("2000"));
        GenericRepository<TransportCargoService, Long> serviceRepo =
                new GenericRepository<>(sessionFactory, executorService, TransportCargoService.class);
        serviceRepo.create(service1);
        serviceRepo.create(service2);

        List<TransportCompany> companies = companyRepo.findWithAggregation("transportServices", "price", "id", true);

        assertFalse(companies.isEmpty());
        assertEquals("Fast Transport", companies.getFirst().getName());
        // H2 sums prices: 1000 + 2000 = 3000 (assuming single company for simplicity)
    }

    @Test
    void FindWithAggregation_WhenNoRelatedData_ShouldSortByGroupField() {
        // Test with Driver, no transportServices yet
        Driver driver2 = new Driver();
        driver2.setFirstName("Jane");
        driver2.setFamilyName("Smith");
        driver2.setSalary(new BigDecimal("60000"));
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        driver2.setTransportCompany(company);
        driverRepo.create(driver2);

        List<Driver> result = driverRepo.findWithAggregation("transportServices", "price", "id", true);

        assertEquals(2, result.size(), "H2 returns all drivers, even without services");
        assertEquals("Doe", result.get(0).getFamilyName(), "Sorted by ID when no services");
        assertEquals("Smith", result.get(1).getFamilyName());
    }

    @Test
    void FindWithAggregation_WhenInvalidRelation_ShouldThrowException() {
        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> companyRepo.findWithAggregation("invalidRelation", "price", "id", true),
                "H2 fails on unmapped aggregation relation");

        assertTrue(exception.getMessage().contains("Failed to find entities"));
    }

    @Test
    void FindByCriteria_WhenConditionsMatch_ShouldReturnEntities() {
        TransportCompany company = new TransportCompany("Match Co", "Match St");

        companyRepo.create(company);
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "Match Co");
        List<TransportCompany> results = companyRepo.findByCriteria(conditions, "name", true);
        assertEquals(1, results.size(), "Should return one matching entity");
        assertEquals("Match Co", results.getFirst().getName(), "Name should match the condition");
    }

    @Test
    void FindByCriteria_WhenMatchingName_ShouldReturnFilteredCompanies() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "Fast Transport");

        List<TransportCompany> result = companyRepo.findByCriteria(conditions, "name", true);

        assertEquals(1, result.size(), "Should find one matching company");
        assertEquals("Fast Transport", result.getFirst().getName());
    }

    @Test
    void FindByCriteria_WhenNoMatches_ShouldReturnEmptyList() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "NonExistent");

        List<TransportCompany> result = companyRepo.findByCriteria(conditions, null, true);

        assertTrue(result.isEmpty(), "Should return empty list for no matches");
    }

    @Test
    void FindByCriteria_WhenDuplicateEntriesExist_ShouldReturnAllMatches() {
        // Seed a duplicate name (assuming no unique constraint on name)
        TransportCompany duplicate = new TransportCompany();
        duplicate.setName("Fast Transport");
        duplicate.setAddress("789 Pine Rd");
        companyRepo.create(duplicate);

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "Fast Transport");

        List<TransportCompany> result = companyRepo.findByCriteria(conditions, "address", true);

        assertEquals(2, result.size(), "Should return both companies with same name");
        assertEquals("123 Main St", result.get(0).getAddress());
        assertEquals("789 Pine Rd", result.get(1).getAddress());
    }

    @Test
    void FindWithJoin_WhenQualificationMatches_ShouldReturnDrivers() {
        // Arrange
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();

        Qualification basicLicense = new Qualification();
        basicLicense.setName("Basic License");
        basicLicense.setDescription("For small vehicles");
        qualificationRepo.create(basicLicense);

        Driver janeSmith = new Driver();
        janeSmith.setFirstName("Jane");
        janeSmith.setFamilyName("Smith");
        janeSmith.setSalary(new BigDecimal("45000"));
        janeSmith.setTransportCompany(company);
        janeSmith.setQualifications(Set.of(basicLicense));
        driverRepo.create(janeSmith);

        // Act
        List<Driver> drivers = driverRepo.findWithJoin(
                "qualifications",
                "name",
                "Heavy Duty License",
                "familyName",
                true,
                true // Eagerly fetch qualifications
        );

        // Assert
        assertEquals(1, drivers.size(), "Should return one driver with matching qualification");
        assertEquals("Doe", drivers.getFirst().getFamilyName(), "Driver should be John Doe");
        assertTrue(drivers.getFirst().getQualifications().stream()
                        .anyMatch(q -> "Heavy Duty License".equals(q.getName())),
                "Driver should have Heavy Duty License qualification");
    }

    @Test
    void FindWithJoin_WhenNoMatches_ShouldReturnEmptyList() {
        // Arrange
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst(); // "Fast Transport" from SetUp

        // Add a driver with a different qualification to ensure no matches
        Qualification differentLicense = new Qualification();
        differentLicense.setName("Light Duty License");
        differentLicense.setDescription("For light vehicles");
        qualificationRepo.create(differentLicense);

        Driver otherDriver = new Driver();
        otherDriver.setFirstName("Alice");
        otherDriver.setFamilyName("Brown");
        otherDriver.setSalary(new BigDecimal("40000"));
        otherDriver.setTransportCompany(company);
        otherDriver.setQualifications(Set.of(differentLicense));
        driverRepo.create(otherDriver);

        // Act
        List<Driver> drivers = driverRepo.findWithJoin(
                "qualifications",
                "name",
                "NonExistent License", // No driver has this qualification
                "familyName",
                true,
                true // Eager fetch (though irrelevant since no results)
        );

        // Assert
        assertTrue(drivers.isEmpty(), "Should return an empty list when no drivers match the qualification");
    }

    @Test
    void FindWithJoin_WhenInvalidJoinRelation_ShouldThrowException() {
        // Arrange
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst(); // "Fast Transport" from SetUp
        Driver johnDoe = driverRepo.findByCriteria(Map.of("familyName", "Doe"), null, true).getFirst(); // John Doe from SetUp

        // Act & Assert
        RepositoryException exception = assertThrows(RepositoryException.class, () -> {
            driverRepo.findWithJoin(
                    "invalidField", // Non-existent field in Driver
                    "name",
                    "Heavy Duty License",
                    "familyName",
                    true,
                    true
            );
        }, "Should throw RepositoryException for invalid join field");

        assertTrue(exception.getMessage().contains("FIND_WITH_JOIN_FAILED"),
                "Exception message should indicate join failure");
    }

    // Edge Case Tests
    @Test
    void GetAll_WhenInvalidFetchRelation_ShouldThrowException() {
        // H2/Hibernate throws an exception if the fetch relation isn’t mapped
        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> companyRepo.getAll(0, 10, "name", true, "invalidRelation"),
                "Expected RepositoryException for invalid fetch");

        assertTrue(exception.getMessage().contains("Failed to retrieve all entities"));
    }

    @Test
    void FindByCriteria_WhenEmptyConditions_ShouldReturnAllEntities() {
        Map<String, Object> conditions = new HashMap<>(); // Empty conditions

        List<TransportCompany> result = companyRepo.findByCriteria(conditions, "name", true);

        assertEquals(1, result.size(), "Should return all companies when no conditions");
        assertEquals("Fast Transport", result.getFirst().getName());
    }

    // Async Test
    @Test
    void CreateAsync_WhenEntityIsValid_ShouldPersistAndReturnEntity() throws ExecutionException, InterruptedException {
        TransportCompany company = new TransportCompany();
        company.setName("Async Transport");
        company.setAddress("999 Async Lane");

        CompletableFuture<TransportCompany> future = companyRepo.createAsync(company);
        TransportCompany result = future.get(); // Blocks for test; avoid in production

        assertNotNull(result.getId());
        assertEquals("Async Transport", result.getName());
    }

    @Test
    void GetAllAsync_WhenFetchingRelations_ShouldLoadRelatedData() throws ExecutionException, InterruptedException {
        Driver driver = new Driver();
        driver.setFirstName("Jane");
        driver.setFamilyName("Smith");
        driver.setSalary(new java.math.BigDecimal("60000"));
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        driver.setTransportCompany(company);
        driverRepo.create(driver);

        CompletableFuture<List<TransportCompany>> future = companyRepo.getAllAsync(0, 10, "name", true, "employees");
        List<TransportCompany> companies = future.get();

        assertFalse(companies.isEmpty());
        TransportCompany result = companies.getFirst();
        assertFalse(result.getEmployees().isEmpty());
        assertEquals("Jane", result.getEmployees().iterator().next().getFirstName());
    }

    @Test
    void FindWithJoinAsync_WhenQualificationMatches_ShouldReturnDrivers() throws ExecutionException, InterruptedException {
        // Arrange
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst(); // "Fast Transport" from SetUp
        Driver johnDoe = driverRepo.findByCriteria(Map.of("familyName", "Doe"), null, true).getFirst(); // John Doe from SetUp

        Qualification basicLicense = new Qualification();
        basicLicense.setName("Basic License");
        basicLicense.setDescription("For small vehicles");
        qualificationRepo.create(basicLicense);

        Driver janeSmith = new Driver();
        janeSmith.setFirstName("Jane");
        janeSmith.setFamilyName("Smith");
        janeSmith.setSalary(new BigDecimal("45000"));
        janeSmith.setTransportCompany(company);
        janeSmith.setQualifications(Set.of(basicLicense));
        driverRepo.create(janeSmith);

        // Act
        CompletableFuture<List<Driver>> driversFuture = driverRepo.findWithJoinAsync(
                "qualifications",
                "name",
                "Heavy Duty License",
                "familyName",
                true,
                true // Eagerly fetch qualifications
        );
        List<Driver> drivers = driversFuture.get(); // Block and get the result

        // Assert
        assertEquals(1, drivers.size(), "Should return one driver with matching qualification");
        assertEquals("Doe", drivers.getFirst().getFamilyName(), "Driver should be John Doe");
        assertTrue(drivers.getFirst().getQualifications().stream()
                        .anyMatch(q -> "Heavy Duty License".equals(q.getName())),
                "Driver should have Heavy Duty License qualification");
    }

    @Test
    void FindWithAggregationAsync_WhenRelationExists_ShouldSortBySum() throws ExecutionException, InterruptedException {
        TransportCompany company2 = new TransportCompany();
        company2.setName("Slow Transport");
        company2.setAddress("999 Elm St");
        companyRepo.create(company2);

        CompletableFuture<List<TransportCompany>> future = companyRepo.findWithAggregationAsync("transportServices", "price", "id", true);
        List<TransportCompany> result = future.get();

        assertEquals(2, result.size());
        assertEquals("Slow Transport", result.get(0).getName(), "Async sorts with no services first");
        assertEquals("Fast Transport", result.get(1).getName());
    }

}
