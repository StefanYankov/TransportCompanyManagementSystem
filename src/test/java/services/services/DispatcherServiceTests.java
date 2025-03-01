package services.services;

import data.models.*;
import data.models.employee.*;
import data.models.transportservices.Destination;
import data.models.transportservices.TransportCargoService;
import data.models.transportservices.TransportPassengersService;
import data.models.transportservices.TransportService;
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
import services.data.dto.employees.*;
import services.data.mapping.mappers.DispatcherMapper;
import services.data.mapping.mappers.DriverMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DispatcherServiceTests {
    private SessionFactory sessionFactory;
    private IGenericRepository<Dispatcher, Long> dispatcherRepo;
    private IGenericRepository<Driver, Long> driverRepo;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<Qualification, Long> qualificationRepo;
    private DispatcherService dispatcherService;
    private DispatcherMapper dispatcherMapper;
    private DriverMapper driverMapper;

    @BeforeEach
    void setup() {
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

            dispatcherRepo = new GenericRepository<>(sessionFactory, Dispatcher.class);
            driverRepo = new GenericRepository<>(sessionFactory, Driver.class);
            companyRepo = new GenericRepository<>(sessionFactory, TransportCompany.class);
            qualificationRepo = new GenericRepository<>(sessionFactory, Qualification.class);

            dispatcherMapper = new DispatcherMapper(companyRepo, driverRepo);
            driverMapper = new DriverMapper(companyRepo, dispatcherRepo, qualificationRepo);
            dispatcherService = new DispatcherService(dispatcherRepo, driverRepo, dispatcherMapper, driverMapper);

            TransportCompany company = new TransportCompany("Fast Transport", "123 Main St");
            companyRepo.create(company);

            Qualification qualification = new Qualification("Heavy Duty License", "For large vehicles");
            qualificationRepo.create(qualification);

            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setFirstName("Dispatch");
            dispatcher.setFamilyName("One");
            dispatcher.setSalary(new BigDecimal("40000"));
            dispatcher.setTransportCompany(company);
            dispatcherRepo.create(dispatcher);
        } catch (Exception e) {
            fail("Failed to initialize SessionFactory: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null) sessionFactory.close();
    }

    // Happy Path Tests
    @Test
    void create_ValidDTO_ShouldCreateDispatcher() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        DispatcherCreateDTO dto = new DispatcherCreateDTO("John", "Doe", new BigDecimal("50000"), company.getId(), Set.of());
        DispatcherViewDTO result = dispatcherService.create(dto);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getFamilyName());
        assertEquals(company.getId(), result.getTransportCompanyId());
    }

    @Test
    void update_FullUpdate_ShouldUpdateAllFields() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = new Driver();
        driver.setFirstName("Jane");
        driver.setFamilyName("Smith");
        driver.setSalary(new BigDecimal("60000"));
        driver.setTransportCompany(company);
        driver = driverRepo.create(driver);

        DispatcherCreateDTO createDto = new DispatcherCreateDTO("Initial", "Disp", new BigDecimal("40000"), company.getId(), Set.of());
        DispatcherViewDTO created = dispatcherService.create(createDto);

        DispatcherUpdateDTO updateDto = new DispatcherUpdateDTO(created.getId(), "Updated", "Name", new BigDecimal("45000"), company.getId(), Set.of(driver.getId()));
        DispatcherViewDTO result = dispatcherService.update(updateDto);

        assertEquals("Updated", result.getFirstName());
        assertEquals("Name", result.getFamilyName());
        assertEquals(new BigDecimal("45000"), result.getSalary());
        assertEquals(company.getId(), result.getTransportCompanyId());
        assertEquals(Set.of(driver.getId()), result.getSupervisedDriverIds());
    }
    @Test
    void update_PartialUpdate_ShouldPreserveUnchangedFields() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        DispatcherCreateDTO createDto = new DispatcherCreateDTO("Initial", "Disp", new BigDecimal("40000"), company.getId(), Set.of());
        DispatcherViewDTO created = dispatcherService.create(createDto);

        DispatcherUpdateDTO updateDto = new DispatcherUpdateDTO(created.getId(), "Updated", null, null, null, null);
        DispatcherViewDTO result = dispatcherService.update(updateDto);

        assertEquals("Updated", result.getFirstName());
        assertEquals("Disp", result.getFamilyName());
        assertEquals(new BigDecimal("40000.00"), result.getSalary());
        assertEquals(company.getId(), result.getTransportCompanyId());
        assertTrue(result.getSupervisedDriverIds().isEmpty());
    }

    @Test
    void getById_ExistingId_ShouldReturnDispatcher() {
        Dispatcher dispatcher = dispatcherRepo.getAll(0, 1, null, true).getFirst();
        DispatcherViewDTO result = dispatcherService.getById(dispatcher.getId(), "supervisedDrivers");

        assertNotNull(result);
        assertEquals("Dispatch", result.getFirstName());
        assertEquals("One", result.getFamilyName());
    }

    @Test
    void getAll_ValidPagination_ShouldReturnDispatchers() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        dispatcherService.create(new DispatcherCreateDTO("Alice", "Smith", new BigDecimal("41000"), company.getId(), Set.of()));
        dispatcherService.create(new DispatcherCreateDTO("Bob", "Jones", new BigDecimal("42000"), company.getId(), Set.of()));

        List<DispatcherViewDTO> result = dispatcherService.getAll(0, 3, "familyName", true, "supervisedDrivers");
        assertEquals(3, result.size());
        assertEquals("Jones", result.get(0).getFamilyName());
        assertEquals("One", result.get(1).getFamilyName());
        assertEquals("Smith", result.get(2).getFamilyName());
    }

    @Test
    void getDriversByDispatcher_ValidId_ShouldReturnDrivers() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Dispatcher dispatcher = dispatcherRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = new Driver();
        driver.setFirstName("Jane");
        driver.setFamilyName("Smith");
        driver.setSalary(new BigDecimal("60000"));
        driver.setTransportCompany(company);
        driver.setDispatcher(dispatcher);
        driverRepo.create(driver);

        List<DriverViewDTO> result = dispatcherService.getDriversByDispatcher(dispatcher.getId());
        assertEquals(1, result.size());
        assertEquals("Smith", result.getFirst().getFamilyName());
    }

    @Test
    void getDispatchersSortedBySalary_Paginated_ShouldReturnSortedList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        dispatcherService.create(new DispatcherCreateDTO("Alice", "Smith", new BigDecimal("41000"), company.getId(), Set.of()));
        dispatcherService.create(new DispatcherCreateDTO("Bob", "Jones", new BigDecimal("42000"), company.getId(), Set.of()));

        List<DispatcherViewDTO> result = dispatcherService.getDispatchersSortedBySalary(0, 3, true, "supervisedDrivers");
        assertEquals(3, result.size());
        assertEquals(new BigDecimal("40000.00"), result.get(0).getSalary());
        assertEquals(new BigDecimal("41000.00"), result.get(1).getSalary());
        assertEquals(new BigDecimal("42000.00"), result.get(2).getSalary());
    }

    // Error Cases
    @Test
    void create_NullDTO_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> dispatcherService.create(null));
    }

    @Test
    void update_NullDTO_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> dispatcherService.update(null));
    }


    @Test
    void update_NonExistentId_ShouldThrowRepositoryException() {
        DispatcherUpdateDTO dto = new DispatcherUpdateDTO(999L, "Updated", "Name", new BigDecimal("45000"), 1L, Set.of());
        assertThrows(RepositoryException.class, () -> dispatcherService.update(dto));
    }

    @Test
    void delete_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> dispatcherService.delete(null));
    }

    @Test
    void delete_NonExistentId_ShouldThrowRepositoryException() {
        assertThrows(RepositoryException.class, () -> dispatcherService.delete(999L));
    }

    @Test
    void getById_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> dispatcherService.getById(null, "supervisedDrivers"));
    }

    @Test
    void getDriversByDispatcher_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> dispatcherService.getDriversByDispatcher(null));
    }

    // Edge Cases
    @Test
    void getById_NonExistentId_ShouldReturnNull() {
        DispatcherViewDTO result = dispatcherService.getById(999L, "supervisedDrivers");
        assertNull(result);
    }

    @Test
    void getAll_InvalidPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        dispatcherService.create(new DispatcherCreateDTO("Alice", "Smith", new BigDecimal("41000"), company.getId(), Set.of()));

        List<DispatcherViewDTO> result = dispatcherService.getAll(2, 1, "familyName", true, "supervisedDrivers");
        assertTrue(result.isEmpty());
    }

    @Test
    void getDispatchersSortedBySalary_NoDispatchers_ShouldReturnEmptyList() {
        List<Dispatcher> existing = dispatcherRepo.getAll(0, Integer.MAX_VALUE, null, true);
        existing.forEach(dispatcherRepo::delete);

        List<DispatcherViewDTO> result = dispatcherService.getDispatchersSortedBySalary(0, 10, true, "supervisedDrivers");
        assertTrue(result.isEmpty());
    }

    @Test
    void getDriversByDispatcher_NoDrivers_ShouldReturnEmptyList() {
        Dispatcher dispatcher = dispatcherRepo.getAll(0, 1, null, true).getFirst();
        List<DriverViewDTO> result = dispatcherService.getDriversByDispatcher(dispatcher.getId());
        assertTrue(result.isEmpty());
    }
}