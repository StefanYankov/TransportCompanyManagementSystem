package services.services;

import data.models.*;
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
import data.repositories.exceptions.RepositoryException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.data.dto.transportservices.TransportPassengersServiceCreateDTO;
import services.data.dto.transportservices.TransportPassengersServiceUpdateDTO;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;
import services.data.mapping.mappers.TransportPassengersServiceMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TransportPassengersServiceServiceTests {
    private SessionFactory sessionFactory;
    private IGenericRepository<TransportPassengersService, Long> transportServiceRepo;
    private IGenericRepository<Driver, Long> driverRepo;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<Client, Long> clientRepo;
    private IGenericRepository<Destination, Long> destinationRepo;
    private TransportPassengersServiceService service;
    private TransportPassengersServiceMapper transportServiceMapper;

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

            transportServiceRepo = new GenericRepository<>(sessionFactory, TransportPassengersService.class);
            driverRepo = new GenericRepository<>(sessionFactory, Driver.class);
            companyRepo = new GenericRepository<>(sessionFactory, TransportCompany.class);
            clientRepo = new GenericRepository<>(sessionFactory, Client.class);
            destinationRepo = new GenericRepository<>(sessionFactory, Destination.class);

            transportServiceMapper = new TransportPassengersServiceMapper(companyRepo, clientRepo, driverRepo, null, destinationRepo);
            service = new TransportPassengersServiceService(transportServiceRepo, transportServiceMapper);

            TransportCompany company = new TransportCompany("Fast Transport", "123 Main St");
            companyRepo.create(company);

            Destination destination = new Destination();
            destination.setStartingLocation("City A");
            destination.setEndingLocation("City B");
            destinationRepo.create(destination);
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
    void create_ValidDTO_ShouldCreateService() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TransportPassengersServiceCreateDTO dto = new TransportPassengersServiceCreateDTO();
        dto.setTransportCompanyId(company.getId());
        dto.setStartingDate(LocalDate.now());
        dto.setPrice(new BigDecimal("1500"));
        dto.setNumberOfPassengers(20);

        TransportPassengersServiceViewDTO result = service.create(dto);

        assertNotNull(result.getId());
        assertEquals(20, result.getNumberOfPassengers());
        assertEquals(new BigDecimal("1500"), result.getPrice());
    }

    @Test
    void update_FullUpdate_ShouldUpdateService() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TransportPassengersServiceCreateDTO createDto = new TransportPassengersServiceCreateDTO();
        createDto.setTransportCompanyId(company.getId());
        createDto.setStartingDate(LocalDate.now());
        createDto.setPrice(new BigDecimal("1500"));
        createDto.setNumberOfPassengers(20);
        TransportPassengersServiceViewDTO created = service.create(createDto);

        TransportPassengersServiceUpdateDTO updateDto = new TransportPassengersServiceUpdateDTO();
        updateDto.setId(created.getId());
        updateDto.setTransportCompanyId(company.getId());
        updateDto.setStartingDate(LocalDate.now().plusDays(1));
        updateDto.setPrice(new BigDecimal("2000"));
        updateDto.setNumberOfPassengers(25);
        TransportPassengersServiceViewDTO result = service.update(updateDto);

        assertEquals(25, result.getNumberOfPassengers());
        assertEquals(new BigDecimal("2000"), result.getPrice());
    }

    @Test
    void update_PartialUpdate_ShouldPreserveUnchangedFields() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TransportPassengersServiceCreateDTO createDto = new TransportPassengersServiceCreateDTO();
        createDto.setTransportCompanyId(company.getId());
        createDto.setStartingDate(LocalDate.now());
        createDto.setPrice(new BigDecimal("1500"));
        createDto.setNumberOfPassengers(20);
        TransportPassengersServiceViewDTO created = service.create(createDto);

        TransportPassengersServiceUpdateDTO updateDto = new TransportPassengersServiceUpdateDTO();
        updateDto.setId(created.getId());
        updateDto.setStartingDate(LocalDate.now()); // Preserve startingDate explicitly
        updateDto.setNumberOfPassengers(30);
        TransportPassengersServiceViewDTO result = service.update(updateDto);

        assertEquals(30, result.getNumberOfPassengers());
        assertEquals(new BigDecimal("1500.00"), result.getPrice());
    }

    @Test
    void delete_ExistingId_ShouldDeleteService() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TransportPassengersServiceCreateDTO dto = new TransportPassengersServiceCreateDTO();
        dto.setTransportCompanyId(company.getId());
        dto.setStartingDate(LocalDate.now());
        dto.setPrice(new BigDecimal("1500"));
        dto.setNumberOfPassengers(20);
        TransportPassengersServiceViewDTO created = service.create(dto);

        service.delete(created.getId());
        TransportPassengersServiceViewDTO result = service.getById(created.getId());
        assertNull(result);
    }

    @Test
    void getById_ExistingId_ShouldReturnService() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TransportPassengersServiceCreateDTO dto = new TransportPassengersServiceCreateDTO();
        dto.setTransportCompanyId(company.getId());
        dto.setStartingDate(LocalDate.now());
        dto.setPrice(new BigDecimal("1500"));
        dto.setNumberOfPassengers(20);
        TransportPassengersServiceViewDTO created = service.create(dto);

        TransportPassengersServiceViewDTO result = service.getById(created.getId());
        assertNotNull(result);
        assertEquals(20, result.getNumberOfPassengers());
    }

    @Test
    void getAll_WithFilter_ShouldReturnFilteredList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();

        TransportPassengersServiceCreateDTO dto1 = new TransportPassengersServiceCreateDTO();
        dto1.setTransportCompanyId(company.getId());
        dto1.setStartingDate(LocalDate.now());
        dto1.setPrice(new BigDecimal("1500"));
        dto1.setNumberOfPassengers(20);
        service.create(dto1);

        TransportPassengersServiceCreateDTO dto2 = new TransportPassengersServiceCreateDTO();
        dto2.setTransportCompanyId(company.getId());
        dto2.setStartingDate(LocalDate.now());
        dto2.setPrice(new BigDecimal("2000"));
        dto2.setNumberOfPassengers(30);
        service.create(dto2);

        List<TransportPassengersServiceViewDTO> result = service.getAll(0, 10, "startingDate", true, "20");
        assertEquals(1, result.size());
        assertEquals(20, result.getFirst().getNumberOfPassengers());
    }

    @Test
    void getTransportsSortedByDestination_Sorted_ShouldReturnSortedList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Destination dest1 = destinationRepo.getAll(0, 1, null, true).getFirst();
        Destination dest2 = new Destination();
        dest2.setStartingLocation("City X");
        dest2.setEndingLocation("City Z");
        destinationRepo.create(dest2);

        TransportPassengersServiceCreateDTO dto1 = new TransportPassengersServiceCreateDTO();
        dto1.setTransportCompanyId(company.getId());
        dto1.setDestinationId(dest1.getId());
        dto1.setStartingDate(LocalDate.now());
        dto1.setPrice(new BigDecimal("1500"));
        dto1.setNumberOfPassengers(20);
        service.create(dto1);

        TransportPassengersServiceCreateDTO dto2 = new TransportPassengersServiceCreateDTO();
        dto2.setTransportCompanyId(company.getId());
        dto2.setDestinationId(dest2.getId());
        dto2.setStartingDate(LocalDate.now());
        dto2.setPrice(new BigDecimal("2000"));
        dto2.setNumberOfPassengers(30);
        service.create(dto2);

        List<TransportPassengersServiceViewDTO> result = service.getTransportsSortedByDestination(0, 10, true);
        assertEquals(2, result.size());
        assertEquals(dest1.getId(), result.get(0).getDestinationId());
        assertEquals(dest2.getId(), result.get(1).getDestinationId());
    }

    @Test
    void getTotalTransportCount_WithServices_ShouldReturnCount() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();

        TransportPassengersServiceCreateDTO dto1 = new TransportPassengersServiceCreateDTO();
        dto1.setTransportCompanyId(company.getId());
        dto1.setStartingDate(LocalDate.now());
        dto1.setPrice(new BigDecimal("1500"));
        dto1.setNumberOfPassengers(20);
        service.create(dto1);

        TransportPassengersServiceCreateDTO dto2 = new TransportPassengersServiceCreateDTO();
        dto2.setTransportCompanyId(company.getId());
        dto2.setStartingDate(LocalDate.now());
        dto2.setPrice(new BigDecimal("2000"));
        dto2.setNumberOfPassengers(30);
        service.create(dto2);

        int count = service.getTotalTransportCount();
        assertEquals(2, count);
    }

    @Test
    void getActiveServices_WithActive_ShouldReturnActiveServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();

        TransportPassengersServiceCreateDTO dto1 = new TransportPassengersServiceCreateDTO();
        dto1.setTransportCompanyId(company.getId());
        dto1.setStartingDate(LocalDate.now());
        dto1.setPrice(new BigDecimal("1500"));
        dto1.setNumberOfPassengers(20);
        service.create(dto1);

        TransportPassengersServiceCreateDTO dto2 = new TransportPassengersServiceCreateDTO();
        dto2.setTransportCompanyId(company.getId());
        dto2.setStartingDate(LocalDate.now().minusDays(1));
        dto2.setPrice(new BigDecimal("2000"));
        dto2.setNumberOfPassengers(30);
        service.create(dto2);

        List<TransportPassengersServiceViewDTO> result = service.getActiveServices(0, 10, "startingDate", true);
        assertEquals(1, result.size());
        assertEquals(LocalDate.now(), result.getFirst().getStartingDate());
    }

    @Test
    void getByDriver_WithDriver_ShouldReturnServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setFamilyName("Doe");
        driver.setSalary(new BigDecimal("50000"));
        driver.setTransportCompany(company);
        driverRepo.create(driver);

        TransportPassengersServiceCreateDTO dto1 = new TransportPassengersServiceCreateDTO();
        dto1.setTransportCompanyId(company.getId());
        dto1.setDriverId(driver.getId());
        dto1.setStartingDate(LocalDate.now());
        dto1.setPrice(new BigDecimal("1500"));
        dto1.setNumberOfPassengers(20);
        service.create(dto1);

        TransportPassengersServiceCreateDTO dto2 = new TransportPassengersServiceCreateDTO();
        dto2.setTransportCompanyId(company.getId());
        dto2.setStartingDate(LocalDate.now());
        dto2.setPrice(new BigDecimal("2000"));
        dto2.setNumberOfPassengers(30);
        service.create(dto2);

        List<TransportPassengersServiceViewDTO> result = service.getByDriver(driver.getId(), 0, 10, "startingDate", true);
        assertEquals(1, result.size());
        assertEquals(driver.getId(), result.getFirst().getDriverId());
    }

    @Test
    void getByClient_WithClient_ShouldReturnServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = new Client();
        client.setName("Client A");
        client.setTelephone("1234567890");
        client.setEmail("clientA@example.com");
        clientRepo.create(client);

        TransportPassengersServiceCreateDTO dto1 = new TransportPassengersServiceCreateDTO();
        dto1.setTransportCompanyId(company.getId());
        dto1.setClientId(client.getId());
        dto1.setStartingDate(LocalDate.now());
        dto1.setPrice(new BigDecimal("1500"));
        dto1.setNumberOfPassengers(20);
        service.create(dto1);

        TransportPassengersServiceCreateDTO dto2 = new TransportPassengersServiceCreateDTO();
        dto2.setTransportCompanyId(company.getId());
        dto2.setStartingDate(LocalDate.now());
        dto2.setPrice(new BigDecimal("2000"));
        dto2.setNumberOfPassengers(30);
        service.create(dto2);

        List<TransportPassengersServiceViewDTO> result = service.getByClient(client.getId(), 0, 10, "startingDate", true);
        assertEquals(1, result.size());
        assertEquals(client.getId(), result.getFirst().getClientId());
    }

    @Test
    void getByCompany_WithCompany_ShouldReturnServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TransportCompany company2 = new TransportCompany("Swift Logistics", "456 Oak Ave");
        companyRepo.create(company2);

        TransportPassengersServiceCreateDTO dto1 = new TransportPassengersServiceCreateDTO();
        dto1.setTransportCompanyId(company.getId());
        dto1.setStartingDate(LocalDate.now());
        dto1.setPrice(new BigDecimal("1500"));
        dto1.setNumberOfPassengers(20);
        service.create(dto1);

        TransportPassengersServiceCreateDTO dto2 = new TransportPassengersServiceCreateDTO();
        dto2.setTransportCompanyId(company2.getId());
        dto2.setStartingDate(LocalDate.now());
        dto2.setPrice(new BigDecimal("2000"));
        dto2.setNumberOfPassengers(30);
        service.create(dto2);

        List<TransportPassengersServiceViewDTO> result = service.getByCompany(company.getId(), 0, 10, "startingDate", true);
        assertEquals(1, result.size());
        assertEquals(company.getId(), result.getFirst().getTransportCompanyId());
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
    void update_NullId_ShouldThrowIllegalArgumentException() {
        TransportPassengersServiceUpdateDTO dto = new TransportPassengersServiceUpdateDTO();
        dto.setNumberOfPassengers(30);
        assertThrows(IllegalArgumentException.class, () -> service.update(dto));
    }

    @Test
    void update_NonExistentId_ShouldThrowRepositoryException() {
        TransportPassengersServiceUpdateDTO dto = new TransportPassengersServiceUpdateDTO();
        dto.setId(999L);
        dto.setNumberOfPassengers(30);
        assertThrows(RepositoryException.class, () -> service.update(dto));
    }

    @Test
    void delete_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.delete(null));
    }

    @Test
    void delete_NonExistentId_ShouldThrowRepositoryException() {
        assertThrows(RepositoryException.class, () -> service.delete(999L));
    }

    @Test
    void getById_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getById(null));
    }

    @Test
    void getByDriver_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getByDriver(null, 0, 10, "startingDate", true));
    }

    @Test
    void getByClient_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getByClient(null, 0, 10, "startingDate", true));
    }

    @Test
    void getByCompany_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getByCompany(null, 0, 10, "startingDate", true));
    }

    // Edge Cases
    @Test
    void getById_NonExistentId_ShouldReturnNull() {
        TransportPassengersServiceViewDTO result = service.getById(999L);
        assertNull(result);
    }

    @Test
    void getAll_EmptyFilter_ShouldReturnAll() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();

        TransportPassengersServiceCreateDTO dto1 = new TransportPassengersServiceCreateDTO();
        dto1.setTransportCompanyId(company.getId());
        dto1.setStartingDate(LocalDate.now());
        dto1.setPrice(new BigDecimal("1500"));
        dto1.setNumberOfPassengers(20);
        service.create(dto1);

        TransportPassengersServiceCreateDTO dto2 = new TransportPassengersServiceCreateDTO();
        dto2.setTransportCompanyId(company.getId());
        dto2.setStartingDate(LocalDate.now());
        dto2.setPrice(new BigDecimal("2000"));
        dto2.setNumberOfPassengers(30);
        service.create(dto2);

        List<TransportPassengersServiceViewDTO> result = service.getAll(0, Integer.MAX_VALUE, "startingDate", true, "");
        assertEquals(2, result.size());
    }

    @Test
    void getAll_InvalidPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TransportPassengersServiceCreateDTO dto = new TransportPassengersServiceCreateDTO();
        dto.setTransportCompanyId(company.getId());
        dto.setStartingDate(LocalDate.now());
        dto.setPrice(new BigDecimal("1500"));
        dto.setNumberOfPassengers(20);
        service.create(dto);

        List<TransportPassengersServiceViewDTO> result = service.getAll(1, 1, "startingDate", true, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransportsSortedByDestination_NoServices_ShouldReturnEmptyList() {
        List<TransportPassengersServiceViewDTO> result = service.getTransportsSortedByDestination(0, 10, true);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTotalTransportCount_NoServices_ShouldReturnZero() {
        int count = service.getTotalTransportCount();
        assertEquals(0, count);
    }

    @Test
    void getActiveServices_NoActive_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TransportPassengersServiceCreateDTO dto = new TransportPassengersServiceCreateDTO();
        dto.setTransportCompanyId(company.getId());
        dto.setStartingDate(LocalDate.now().minusDays(1));
        dto.setPrice(new BigDecimal("1500"));
        dto.setNumberOfPassengers(20);
        service.create(dto);

        List<TransportPassengersServiceViewDTO> result = service.getActiveServices(0, 10, "startingDate", true);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByDriver_NoServices_ShouldReturnEmptyList() {
        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setFamilyName("Doe");
        driver.setSalary(new BigDecimal("50000"));
        driverRepo.create(driver);

        List<TransportPassengersServiceViewDTO> result = service.getByDriver(driver.getId(), 0, 10, "startingDate", true);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByClient_NoServices_ShouldReturnEmptyList() {
        Client client = new Client();
        client.setName("Client A");
        client.setTelephone("1234567890");
        client.setEmail("clientA@example.com");
        clientRepo.create(client);

        List<TransportPassengersServiceViewDTO> result = service.getByClient(client.getId(), 0, 10, "startingDate", true);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByCompany_NoServices_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        List<TransportPassengersServiceViewDTO> result = service.getByCompany(company.getId(), 0, 10, "startingDate", true);
        assertTrue(result.isEmpty());
    }
}