package data.repositories;

import data.models.*;
import data.models.employee.*;
import data.models.transportservices.*;
import data.models.vehicles.*;
import data.repositories.exceptions.RepositoryException;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GenericRepositoryTest {
    private SessionFactory sessionFactory;
    private GenericRepository<TransportCompany, Long> companyRepo;
    private GenericRepository<Driver, Long> driverRepo;
    private GenericRepository<Qualification, Long> qualificationRepo;
    private GenericRepository<TransportCargoService, Long> serviceRepo;
    private GenericRepository<Client, Long> clientRepo;

    @BeforeEach
    public void setupRepositoriesAndSeedData() {
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

            companyRepo = new GenericRepository<>(sessionFactory, TransportCompany.class);
            driverRepo = new GenericRepository<>(sessionFactory, Driver.class);
            qualificationRepo = new GenericRepository<>(sessionFactory, Qualification.class);
            serviceRepo = new GenericRepository<>(sessionFactory, TransportCargoService.class);
            clientRepo = new GenericRepository<>(sessionFactory, Client.class);

            TransportCompany company = new TransportCompany("Fast Transport", "123 Main St");
            companyRepo.create(company);

            Qualification qualification = new Qualification("Heavy Duty License", "For large vehicles");
            qualificationRepo.create(qualification);

            Client client = new Client();
            client.setName("Starter client");
            client.setTelephone("0888888888");
            client.setEmail("starter@gmail.com");
            clientRepo.create(client);

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
    void tearDown() {
        // H2 create-drop handles cleanup
    }

    @AfterAll
    void closeResources() {
        if (sessionFactory != null) sessionFactory.close();
    }

    // CRUD Tests
    @Test
    void create_WhenEntityIsValid_ShouldPersistAndReturnEntity() {
        TransportCompany company = new TransportCompany("Starter", "Troyan");
        TransportCompany result = companyRepo.create(company);
        assertNotNull(result.getId());
        assertEquals("Starter", result.getName());
    }

    @Test
    void create_WhenEntityIsNull_ShouldThrowException() {
        RepositoryException exception = assertThrows(RepositoryException.class, () -> companyRepo.create(null));
        assertTrue(exception.getMessage().contains("Cannot create null entity"));
    }

    @Test
    void update_WhenEntityExists_ShouldUpdateAndReturnEntity() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        company.setAddress("789 Pine Rd");
        TransportCompany result = companyRepo.update(company);
        assertEquals("789 Pine Rd", result.getAddress());
        assertEquals(company.getId(), result.getId());
    }

    @Test
    void update_WhenEntityIsNull_ShouldThrowRepositoryException() {
        RepositoryException exception = assertThrows(RepositoryException.class, () -> companyRepo.update(null));
        assertTrue(exception.getMessage().contains("Cannot update null entity"));
    }

    @Test
    void delete_WhenEntityExists_ShouldRemoveEntity() {
        TransportCompany company = new TransportCompany("Temp Co", "Temp St");
        TransportCompany created = companyRepo.create(company);
        companyRepo.delete(created);
        Optional<TransportCompany> result = companyRepo.getById(created.getId());
        assertFalse(result.isPresent());
    }

    @Test
    void delete_WhenEntityIsNull_ShouldThrowRepositoryException() {
        RepositoryException exception = assertThrows(RepositoryException.class, () -> companyRepo.delete(null));
        assertTrue(exception.getMessage().contains("Cannot delete null entity"));
    }

    @Test
    void getById_WhenIdExists_ShouldReturnEntity() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Optional<TransportCompany> result = companyRepo.getById(company.getId());
        assertTrue(result.isPresent());
        assertEquals("Fast Transport", result.get().getName());
    }

    @Test
    void getById_WhenIdDoesNotExist_ShouldReturnEmptyOptional() {
        Optional<TransportCompany> result = companyRepo.getById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void getById_WhenIdIsNull_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> companyRepo.getById(null));
    }

    @Test
    void getByIdWithFetchRelations_WhenIdExistsAndRelationsSpecified_ShouldFetchRelations() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Qualification qualification = qualificationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();

        Optional<Driver> result = driverRepo.getById(driver.getId(), "qualifications", "transportCompany");
        assertTrue(result.isPresent());
        Driver fetchedDriver = result.get();
        assertTrue(Hibernate.isInitialized(fetchedDriver.getQualifications()), "Qualifications should be initialized");
        assertEquals(1, fetchedDriver.getQualifications().size());
        assertTrue(fetchedDriver.getQualifications().contains(qualification)); // Now passes
        assertTrue(Hibernate.isInitialized(fetchedDriver.getTransportCompany()), "TransportCompany should be initialized");
        assertEquals(company.getId(), fetchedDriver.getTransportCompany().getId());
    }

    @Test
    void getByIdWithFetchRelations_WhenIdExistsAndNoRelationsSpecified_ShouldReturnEntityWithoutFetching() {
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        Optional<Driver> result = driverRepo.getById(driver.getId()); // No fetchRelations
        assertTrue(result.isPresent());
        Driver fetchedDriver = result.get();
        assertFalse(Hibernate.isInitialized(fetchedDriver.getQualifications()), "Qualifications should not be initialized");
        assertFalse(Hibernate.isInitialized(fetchedDriver.getTransportCompany()), "TransportCompany should not be initialized");
    }

    @Test
    void getByIdWithFetchRelations_WhenIdIsNull_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> driverRepo.getById(null, "qualifications"));
    }

    @Test
    void getByIdWithFetchRelations_WhenInvalidRelationSpecified_ShouldThrowRepositoryException() {
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> driverRepo.getById(driver.getId(), "invalidRelation"));

        assertTrue(exception.getCause().getMessage().contains("Failed to getByIdWithFetch entity"),
                "Exception should indicate fetch failure");
    }

    @Test
    void getByIdWithFetchRelations_WhenIdDoesNotExist_ShouldReturnEmptyOptional() {
        Optional<Driver> result = driverRepo.getById(999L, "qualifications");
        assertFalse(result.isPresent());
    }


    @Test
    void getByIdAndMap_WhenLazyInitializerUsed_ShouldInitializeCollections() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Optional<TransportCompany> result = companyRepo.getByIdAndMap(
                company.getId(),
                c -> c,
                c -> Hibernate.initialize(c.getEmployees())
        );
        assertTrue(result.isPresent());
        assertTrue(Hibernate.isInitialized(result.get().getEmployees()));
        assertFalse(result.get().getEmployees().isEmpty());
    }

    @Test
    void getAll_WhenFetchingRelations_ShouldLoadRelatedData() {
        List<TransportCompany> companies = companyRepo.getAll(0, 10, "name", true, "employees");
        assertFalse(companies.isEmpty());
        TransportCompany company = companies.getFirst();
        assertFalse(company.getEmployees().isEmpty());
        assertEquals("John", company.getEmployees().iterator().next().getFirstName());
    }

    @Test
    void getAll_WhenEntitiesExist_ShouldReturnPaginatedList() {
        TransportCompany company = new TransportCompany("Beta Transport", "222 St");
        companyRepo.create(company);
        List<TransportCompany> companies = companyRepo.getAll(0, 1, "name", true);
        assertEquals(1, companies.size());
        assertEquals("Beta Transport", companies.getFirst().getName());
    }

    @Test
    void getAll_WhenInvalidFetchRelation_ShouldThrowRepositoryException() {
        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> companyRepo.getAll(0, 10, "name", true, "invalidRelation"));
        assertTrue(exception.getMessage().contains("Failed to open session for getAll"));
    }

    @Test
    void findWithAggregation_WhenRelationExists_ShouldSortBySum() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TransportCargoService service = new TransportCargoService();
        service.setTransportCompany(company);
        service.setPrice(new BigDecimal("2000"));
        service.setStartingDate(LocalDate.now());
        service.setWeightInKilograms(BigDecimal.valueOf(25));
        service.setLengthInCentimeters(50);
        service.setWidthInCentimeters(25);
        service.setHeightInCentimeters(25);
        serviceRepo.create(service);

        List<TransportCompany> companies = companyRepo.findWithAggregation("transportServices", "price", "id", true);
        assertFalse(companies.isEmpty());
        assertEquals("Fast Transport", companies.getFirst().getName());
    }

    @Test
    void findByCriteria_WithFetchRelations_ShouldLoadRelatedData() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Qualification qualification = qualificationRepo.getAll(0, 1, null, true).getFirst();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("transportCompany.id", company.getId());

        List<Driver> drivers = driverRepo.findByCriteria(conditions, "familyName", true, "qualifications");
        assertEquals(1, drivers.size());
        assertEquals("Doe", drivers.getFirst().getFamilyName());
        assertFalse(drivers.getFirst().getQualifications().isEmpty());
        assertTrue(drivers.getFirst().getQualifications().stream()
                .anyMatch(q -> q.getId().equals(qualification.getId())));
    }


    @Test
    void findWithAggregation_WhenNoRelatedData_ShouldSortByGroupField() {
        TransportCompany company2 = new TransportCompany("Slow Transport", "999 Elm St");
        companyRepo.create(company2);
        List<TransportCompany> result = companyRepo.findWithAggregation("transportServices", "price", "id", true);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId()); // Assuming IDs start at 1
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void findWithAggregation_InvalidRelation_ShouldThrowRepositoryException() {
        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> companyRepo.findWithAggregation("invalidRelation", "price", "id", true));
        assertInstanceOf(RepositoryException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Failed to findWithAggregation entity"));
    }


    @Test
    void findByCriteria_WhenConditionsMatch_ShouldReturnEntities() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "Fast Transport");
        List<TransportCompany> result = companyRepo.findByCriteria(conditions, "name", true);
        assertEquals(1, result.size());
        assertEquals("Fast Transport", result.getFirst().getName());
    }


    @Test
    void findWithJoin_WhenQualificationMatches_ShouldReturnDrivers() {
        Qualification qualification = qualificationRepo.getAll(0, 1, null, true).getFirst();
        String qualificationName = qualification.getName();
        List<Driver> drivers = driverRepo.findWithJoin("qualifications", "name", qualificationName, "familyName", true, "qualifications");
        assertEquals(1, drivers.size());
        assertEquals("Doe", drivers.getFirst().getFamilyName());
        assertFalse(drivers.getFirst().getQualifications().isEmpty()); // Verify qualifications are loaded
    }

    @Test
    void getByIdAndMap_WhenIdExists_ShouldMapEntity() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Optional<String> result = companyRepo.getByIdAndMap(company.getId(), TransportCompany::getName, null);
        assertTrue(result.isPresent());
        assertEquals("Fast Transport", result.get());
    }

    @Test
    void getByIdAndMap_WhenIdDoesNotExist_ShouldReturnEmptyOptional() {
        Optional<String> result = companyRepo.getByIdAndMap(999L, TransportCompany::getName, null);
        assertFalse(result.isPresent());
    }

    @Test
    void getByIdAndMap_WhenIdIsNull_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> companyRepo.getByIdAndMap(null, TransportCompany::getName, null));
    }

    @Test
    void getByIdAndMap_WhenMapperIsNull_ShouldThrowIllegalArgumentException() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        assertThrows(IllegalArgumentException.class, () -> companyRepo.getByIdAndMap(company.getId(), null, null));
    }


    @Test
    void getAllAndMap_WhenEntitiesExist_ShouldMapEntities() {
        TransportCompany company2 = new TransportCompany("Beta Transport", "222 St");
        companyRepo.create(company2);
        List<String> results = companyRepo.getAllAndMap(0, 10, "name", true, TransportCompany::getName, null);
        assertEquals(2, results.size());
        assertEquals("Beta Transport", results.get(0));
        assertEquals("Fast Transport", results.get(1));
    }

    @Test
    void findRelatedEntities_QualificationToDrivers_ShouldReturnSortedDrivers() {
        Qualification qualification = qualificationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver2 = new Driver();
        driver2.setFirstName("Jane");
        driver2.setFamilyName("Smith");
        driver2.setSalary(new BigDecimal("60000"));
        driver2.setTransportCompany(companyRepo.getAll(0, 1, null, true).getFirst());
        driver2.setQualifications(Set.of(qualification));
        driverRepo.create(driver2);

        List<Driver> drivers = qualificationRepo.findRelatedEntities(Driver.class, "qualifications", qualification.getId(), 0, 10, "familyName", true);
        assertEquals(2, drivers.size());
        assertEquals("Doe", drivers.getFirst().getFamilyName());
        assertEquals("Smith", drivers.get(1).getFamilyName());
    }

    @Test
    void findRelatedEntities_InvalidRelationField_ShouldThrowRepositoryException() {
        Qualification qualification = qualificationRepo.getAll(0, 1, null, true).getFirst();
        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> qualificationRepo.findRelatedEntities(Driver.class, "invalidField", qualification.getId(), 0, 10, "familyName", true));
        assertInstanceOf(RepositoryException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Failed to findRelatedEntities entity of type Qualification"));
    }

    @Test
    void findRelatedEntities_InvalidOrderByField_ShouldThrowRepositoryException() {
        Qualification qualification = qualificationRepo.getAll(0, 1, null, true).getFirst();
        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> qualificationRepo.findRelatedEntities(Driver.class, "qualifications", qualification.getId(), 0, 10, "invalidField", true));
        assertInstanceOf(RepositoryException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Failed to findRelatedEntities entity of type Qualification"));
    }

    @Test
    void updateAndMap_WhenEntityExists_ShouldUpdateAndMap() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        company.setAddress("New Address");
        String result = companyRepo.updateAndMap(company, TransportCompany::getAddress, null);
        assertEquals("New Address", result);
    }

    @Test
    void countRelatedEntities_WhenDriversHaveServices_ShouldReturnCounts() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        TransportCargoService service = new TransportCargoService();
        service.setTransportCompany(company);
        service.setDriver(driver);
        service.setPrice(new BigDecimal("2000"));
        service.setStartingDate(LocalDate.now());
        service.setWeightInKilograms(BigDecimal.valueOf(25));
        service.setLengthInCentimeters(50);
        service.setWidthInCentimeters(25);
        service.setHeightInCentimeters(25);
        serviceRepo.create(service);

        Map<Long, Long> counts = driverRepo.countRelatedEntities(TransportService.class, "transportServices", "id", "id", 0, 10, "count", true);
        assertEquals(1, counts.size());
        assertEquals(1L, counts.get(driver.getId()));
    }

    @Test
    void countRelatedEntities_WhenNoServices_ShouldReturnZeroCounts() {
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        Map<Long, Long> counts = driverRepo.countRelatedEntities(TransportService.class, "transportServices", "id", "id", 0, 10, "id", true);
        assertEquals(1, counts.size());
        assertEquals(0L, counts.get(driver.getId()));
    }

    @Test
    void getAll_WhenPageIsNegative_ShouldReturnEmptyList() {
        List<TransportCompany> result = companyRepo.getAll(-1, 10, "name", true);
        assertTrue(result.isEmpty(), "Negative page should return an empty list");
    }

    @Test
    void getAll_WhenSizeIsZero_ShouldReturnEmptyList() {
        List<TransportCompany> result = companyRepo.getAll(0, 0, "name", true);
        assertTrue(result.isEmpty(), "Zero size should return an empty list");
    }

    @Test
    void getAll_WhenNoFetchRelations_ShouldLeaveCollectionsUninitialized() {
        List<TransportCompany> companies = companyRepo.getAll(0, 10, "name", true); // No fetchRelations
        TransportCompany company = companies.getFirst();
        assertFalse(Hibernate.isInitialized(company.getEmployees()), "Employees collection should be uninitialized without fetchRelations");
    }

    @Test
    void findByCriteria_WhenNoFetchRelations_ShouldLeaveCollectionsUninitialized() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "Fast Transport");
        List<TransportCompany> companies = companyRepo.findByCriteria(conditions, "name", true); // No fetchRelations
        TransportCompany company = companies.getFirst();
        assertFalse(Hibernate.isInitialized(company.getEmployees()), "Employees collection should be uninitialized without fetchRelations");
    }

    @Test
    void findWithJoin_WhenNestedJoinMatches_ShouldReturnDrivers() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        List<Driver> drivers = driverRepo.findWithJoin("transportCompany", "name", "Fast Transport", "familyName", true, "transportCompany");
        assertEquals(1, drivers.size());
        assertEquals("Doe", drivers.getFirst().getFamilyName());
        assertEquals("Fast Transport", drivers.getFirst().getTransportCompany().getName());
    }

    @Test
    void findWithAggregation_WhenInvalidAggregationField_ShouldThrowRepositoryException() {
        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> companyRepo.findWithAggregation("transportServices", "invalidField", "id", true));
        assertTrue(exception.getCause().getMessage().contains("Failed to findWithAggregation entity"),
                "Invalid aggregation field should throw an exception");
    }
}
