package services.services;

import data.models.Client;
import data.models.TransportCompany;
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
import services.data.dto.employees.DriverViewDTO;
import services.data.dto.employees.QualificationCreateDTO;
import services.data.dto.employees.QualificationUpdateDTO;
import services.data.dto.employees.QualificationViewDTO;
import services.data.mapping.mappers.DriverMapper;
import services.data.mapping.mappers.QualificationMapper;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;

public class QualificationServiceTests {
    private SessionFactory sessionFactory;
    private ExecutorService executorService;
    private IGenericRepository<Qualification, Long> qualificationRepo;
    private IGenericRepository<Driver, Long> driverRepo;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<Dispatcher, Long> dispatcherRepo;
    private QualificationService service;
    private QualificationMapper qualificationMapper;
    private DriverMapper driverMapper;

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
            qualificationMapper = new QualificationMapper();

            qualificationRepo = new GenericRepository<>(sessionFactory,  Qualification.class);
            driverRepo = new GenericRepository<>(sessionFactory, Driver.class);
            companyRepo = new GenericRepository<>(sessionFactory, TransportCompany.class);
            dispatcherRepo = new GenericRepository<>(sessionFactory, Dispatcher.class);
            driverMapper = new DriverMapper(companyRepo,dispatcherRepo,qualificationRepo);

            service = new QualificationService(qualificationRepo, driverRepo, qualificationMapper, driverMapper);

            TransportCompany company = new TransportCompany("Fast Transport", "123 Main St");
            companyRepo.create(company);
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
    void create_ValidDTO_ShouldCreateQualification() {
        QualificationCreateDTO dto = new QualificationCreateDTO("Heavy Duty License", "For large vehicles");
        QualificationViewDTO result = service.create(dto);

        assertNotNull(result.getId());
        assertEquals("Heavy Duty License", result.getName());
        assertEquals("For large vehicles", result.getDescription());
    }

    @Test
    void update_FullUpdate_ShouldUpdateQualification() {
        QualificationCreateDTO createDto = new QualificationCreateDTO("Initial", "Initial desc");
        QualificationViewDTO created = service.create(createDto);

        QualificationUpdateDTO updateDto = new QualificationUpdateDTO(created.getId(), "Updated", "Updated desc");
        QualificationViewDTO result = service.update(updateDto);

        assertEquals("Updated", result.getName());
        assertEquals("Updated desc", result.getDescription());
    }

    @Test
    void update_PartialUpdate_ShouldPreserveUnchangedFields() {
        QualificationCreateDTO createDto = new QualificationCreateDTO("Initial", "Initial desc");
        QualificationViewDTO created = service.create(createDto);

        QualificationUpdateDTO updateDto = new QualificationUpdateDTO(created.getId(), "Updated", null);
        QualificationViewDTO result = service.update(updateDto);

        assertEquals("Updated", result.getName());
        assertEquals("Initial desc", result.getDescription());
    }

    @Test
    void delete_ExistingId_ShouldDeleteQualification() {
        QualificationCreateDTO dto = new QualificationCreateDTO("Temp", "Temp desc");
        QualificationViewDTO created = service.create(dto);

        service.delete(created.getId());
        QualificationViewDTO result = service.getById(created.getId());
        assertNull(result);
    }

    @Test
    void delete_QualificationWithDrivers_ShouldDeleteAndRemoveFromDrivers() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();

        // Create two qualifications to test retention of others
        QualificationCreateDTO qual1Dto = new QualificationCreateDTO("Heavy Duty License", "For large vehicles");
        QualificationViewDTO qual1 = service.create(qual1Dto);
        QualificationCreateDTO qual2Dto = new QualificationCreateDTO("Light Duty License", "For small vehicles");
        QualificationViewDTO qual2 = service.create(qual2Dto);

        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setFamilyName("Doe");
        driver.setSalary(new BigDecimal("50000"));
        driver.setTransportCompany(company);
        driver.setQualifications(Set.of(
                qualificationRepo.getById(qual1.getId()).get(),
                qualificationRepo.getById(qual2.getId()).get()
        ));
        driverRepo.create(driver);

        // Verify initial state
        Driver preDeleteDriver = driverRepo.getById(driver.getId(), "qualifications").get();
        assertEquals(2, preDeleteDriver.getQualifications().size(), "Driver should have 2 qualifications initially");

        // Delete one qualification
        service.delete(qual1.getId());
        assertNull(service.getById(qual1.getId()), "Qualification should be deleted");

        // Fetch driver fresh to reflect database state
        Driver updatedDriver = driverRepo.getById(driver.getId(), "qualifications").get();
        assertEquals(1, updatedDriver.getQualifications().size(), "Driver should have 1 qualification after deletion");
        assertTrue(updatedDriver.getQualifications().stream().anyMatch(q -> q.getId().equals(qual2.getId())),
                "Driver should retain the remaining qualification");
    }

    @Test
    void getById_ExistingId_ShouldReturnQualification() {
        QualificationCreateDTO dto = new QualificationCreateDTO("Test", "Test desc");
        QualificationViewDTO created = service.create(dto);

        QualificationViewDTO result = service.getById(created.getId());
        assertNotNull(result);
        assertEquals("Test", result.getName());
    }

    @Test
    void getAll_WithFilter_ShouldReturnFilteredList() {
        service.create(new QualificationCreateDTO("Heavy Duty License", "For large vehicles"));
        service.create(new QualificationCreateDTO("Light Duty License", "For small vehicles"));

        List<QualificationViewDTO> result = service.getAll(0, 10, "name", true, "Heavy");
        assertEquals(1, result.size());
        assertEquals("Heavy Duty License", result.getFirst().getName());
    }

    @Test
    void findByCriteria_NameMatch_ShouldReturnMatchingQualifications() {
        service.create(new QualificationCreateDTO("Heavy Duty License", "For large vehicles"));
        service.create(new QualificationCreateDTO("Light Duty License", "For small vehicles"));

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "Heavy Duty License");
        List<QualificationViewDTO> result = service.findByCriteria(conditions, "name", true);
        assertEquals(1, result.size());
        assertEquals("Heavy Duty License", result.getFirst().getName());
    }

    @Test
    void getDriversByQualification_WithDrivers_ShouldReturnDriversPerQualification() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        QualificationCreateDTO qualDto1 = new QualificationCreateDTO("Heavy Duty License", "For large vehicles");
        QualificationViewDTO qual1 = service.create(qualDto1);
        QualificationCreateDTO qualDto2 = new QualificationCreateDTO("Light Duty License", "For small vehicles");
        QualificationViewDTO qual2 = service.create(qualDto2);

        Driver driver1 = new Driver();
        driver1.setFirstName("John");
        driver1.setFamilyName("Doe");
        driver1.setSalary(new BigDecimal("50000"));
        driver1.setTransportCompany(company);
        driver1.setQualifications(Set.of(qualificationRepo.getById(qual1.getId()).get()));
        driverRepo.create(driver1);

        Driver driver2 = new Driver();
        driver2.setFirstName("Jane");
        driver2.setFamilyName("Smith");
        driver2.setSalary(new BigDecimal("60000"));
        driver2.setTransportCompany(company);
        driver2.setQualifications(Set.of(qualificationRepo.getById(qual2.getId()).get()));
        driverRepo.create(driver2);

        Map<Long, List<DriverViewDTO>> result = service.getDriversByQualification();
        assertEquals(2, result.size());
        assertEquals(1, result.get(qual1.getId()).size());
        assertEquals("Doe", result.get(qual1.getId()).getFirst().getFamilyName());
        assertEquals(1, result.get(qual2.getId()).size());
        assertEquals("Smith", result.get(qual2.getId()).getFirst().getFamilyName());
    }

    @Test
    void getAll_Pagination_ShouldReturnPaginatedList() {
        service.create(new QualificationCreateDTO("Heavy Duty License", "For large vehicles"));
        service.create(new QualificationCreateDTO("Light Duty License", "For small vehicles"));
        service.create(new QualificationCreateDTO("Medium Duty License", "For medium vehicles"));

        List<QualificationViewDTO> result = service.getAll(0, 2, "name", true, null);
        assertEquals(2, result.size());
        assertEquals("Heavy Duty License", result.get(0).getName());
        assertEquals("Light Duty License", result.get(1).getName());
    }

    // Error Cases
    @Test
    void create_NullDTO_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.create(null));
    }

    @Test
    void create_DuplicateName_ShouldThrowRepositoryException() {
        service.create(new QualificationCreateDTO("Unique", "Desc1"));
        QualificationCreateDTO dto = new QualificationCreateDTO("Unique", "Desc2");
        assertThrows(RepositoryException.class, () -> service.create(dto));
    }

    @Test
    void update_NullDTO_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.update(null));
    }

    @Test
    void update_NonExistentId_ShouldThrowRepositoryException() {
        QualificationUpdateDTO dto = new QualificationUpdateDTO(999L, "NonExistent", "Desc");
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

    // Edge Cases
    @Test
    void getAll_EmptyFilter_ShouldReturnAll() {
        service.create(new QualificationCreateDTO("Heavy Duty License", "For large vehicles"));
        service.create(new QualificationCreateDTO("Light Duty License", "For small vehicles"));

        List<QualificationViewDTO> result = service.getAll(0, Integer.MAX_VALUE, "name", true, "");
        assertEquals(2, result.size());
    }

    @Test
    void getAll_InvalidOrderBy_ShouldThrowRepositoryException() {
        assertThrows(RepositoryException.class, () -> service.getAll(0, 10, "invalidField", true, null));
    }

    @Test
    void findByCriteria_NoMatches_ShouldReturnEmptyList() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "NonExistent");
        List<QualificationViewDTO> result = service.findByCriteria(conditions, "name", true);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByCriteria_EmptyConditions_ShouldReturnAll() {
        service.create(new QualificationCreateDTO("Heavy Duty License", "For large vehicles"));
        service.create(new QualificationCreateDTO("Light Duty License", "For small vehicles"));

        List<QualificationViewDTO> result = service.findByCriteria(new HashMap<>(), "name", true);
        assertEquals(2, result.size());
    }

    @Test
    void getDriversByQualification_NoDrivers_ShouldReturnEmptyLists() {
        service.create(new QualificationCreateDTO("Heavy Duty License", "For large vehicles"));
        service.create(new QualificationCreateDTO("Light Duty License", "For small vehicles"));

        Map<Long, List<DriverViewDTO>> result = service.getDriversByQualification();
        assertEquals(2, result.size());
        result.values().forEach(list -> assertTrue(list.isEmpty()));
    }

    @Test
    void getById_NonExistentId_ShouldReturnNull()  {
        QualificationViewDTO result = service.getById(999L);
        assertNull(result);
    }

    @Test
    void update_DuplicateName_ShouldThrowConstraintViolation() {
        service.create(new QualificationCreateDTO("Unique", "Desc1"));
        QualificationCreateDTO createDto = new QualificationCreateDTO("Initial", "Desc2");
        QualificationViewDTO created = service.create(createDto);

        QualificationUpdateDTO updateDto = new QualificationUpdateDTO(created.getId(), "Unique", "Updated desc");
        assertThrows(RepositoryException.class, () -> service.update(updateDto),
                "Expected ConstraintViolationException for duplicate qualification name");
    }
}