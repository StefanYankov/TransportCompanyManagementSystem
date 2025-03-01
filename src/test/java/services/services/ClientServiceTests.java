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
import services.data.dto.clients.ClientCreateDTO;
import services.data.dto.clients.ClientUpdateDTO;
import services.data.dto.clients.ClientViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.mapping.mappers.ClientMapper;
import services.data.mapping.mappers.TransportServiceMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;

public class ClientServiceTests {
    private SessionFactory sessionFactory;
    private ExecutorService executorService;
    private IGenericRepository<Client, Long> clientRepo;
    private IGenericRepository<TransportService, Long> transportServiceRepo;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<Driver, Long> driverRepo;
    private IGenericRepository<Destination, Long> destinationRepo;
    private IGenericRepository<Vehicle, Long> vehicleRepo;
    private ClientService service;
    private ClientMapper clientMapper;
    private TransportServiceMapper transportServiceMapper;

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
            transportServiceRepo = new GenericRepository<>(sessionFactory,  TransportService.class);
            driverRepo = new GenericRepository<>(sessionFactory, Driver.class);
            vehicleRepo = new GenericRepository<>(sessionFactory, Vehicle.class);

            clientMapper = new ClientMapper();
            transportServiceMapper = new TransportServiceMapper(companyRepo, clientRepo, driverRepo, destinationRepo, vehicleRepo);

            service = new ClientService(clientRepo, transportServiceRepo, clientMapper, transportServiceMapper);

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
    void create_ValidDTO_ShouldCreateClient() {
        ClientCreateDTO dto = new ClientCreateDTO("John Doe", "1234567890", "john.doe@example.com");
        ClientViewDTO result = service.create(dto);

        assertNotNull(result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("1234567890", result.getTelephone());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void update_FullUpdate_ShouldUpdateClient() {
        ClientCreateDTO createDto = new ClientCreateDTO("Initial Name", "1111111111", "initial@example.com");
        ClientViewDTO created = service.create(createDto);

        ClientUpdateDTO updateDto = new ClientUpdateDTO(created.getId(), "Updated Name", "2222222222", "updated@example.com");
        ClientViewDTO result = service.update(updateDto);

        assertEquals("Updated Name", result.getName());
        assertEquals("2222222222", result.getTelephone());
        assertEquals("updated@example.com", result.getEmail());
    }


    @Test
    void delete_ExistingId_ShouldDeleteClient() {
        ClientCreateDTO dto = new ClientCreateDTO("Temp Name", "1234567890", "temp@example.com");
        ClientViewDTO created = service.create(dto);

        service.delete(created.getId());
        ClientViewDTO result = service.getById(created.getId());
        assertNull(result);
    }

    @Test
    void getById_ExistingId_ShouldReturnClient() {
        ClientCreateDTO dto = new ClientCreateDTO("Test Name", "1234567890", "test@example.com");
        ClientViewDTO created = service.create(dto);

        ClientViewDTO result = service.getById(created.getId());
        assertNotNull(result);
        assertEquals("Test Name", result.getName());
    }

    @Test
    void getAll_WithFilter_ShouldReturnFilteredList() {
        service.create(new ClientCreateDTO("John Doe", "1234567890", "john.doe@example.com"));
        service.create(new ClientCreateDTO("Jane Smith", "0987654321", "jane.smith@example.com"));

        List<ClientViewDTO> result = service.getAll(0, 10, "name", true, "John");
        assertEquals(1, result.size());
        assertEquals("John Doe", result.getFirst().getName());
    }

    @Test
    void findByCriteria_NameMatch_ShouldReturnMatchingClients() {
        service.create(new ClientCreateDTO("John Doe", "1234567890", "john.doe@example.com"));
        service.create(new ClientCreateDTO("Jane Smith", "0987654321", "jane.smith@example.com"));

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "John Doe");
        List<ClientViewDTO> result = service.findByCriteria(conditions, "name", true);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.getFirst().getName());
    }

    @Test
    void getTransportServicesByClient_WithServices_ShouldReturnServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        ClientCreateDTO clientDto = new ClientCreateDTO("John Doe", "1234567890", "john.doe@example.com");
        ClientViewDTO client = service.create(clientDto);

        TransportCargoService service1 = new TransportCargoService();
        service1.setStartingDate(LocalDate.now());
        service1.setPrice(new BigDecimal("2000"));
        service1.setTransportCompany(company);
        service1.setClient(clientRepo.getById(client.getId()).get());
        service1.setWeightInKilograms(BigDecimal.valueOf(25));
        service1.setLengthInCentimeters(50);
        service1.setWidthInCentimeters(25);
        service1.setHeightInCentimeters(25);
        transportServiceRepo.create(service1);

        List<TransportServiceViewDTO> result = service.getTransportServicesByClient(client.getId());
        assertEquals(1, result.size());
        assertEquals(new BigDecimal("2000.00"), result.getFirst().getPrice());
    }

    @Test
    void getTransportServiceCountsPerClient_WithServices_ShouldReturnCounts() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        ClientCreateDTO clientDto1 = new ClientCreateDTO("John Doe", "1234567890", "john.doe@example.com");
        ClientViewDTO client1 = service.create(clientDto1);
        ClientCreateDTO clientDto2 = new ClientCreateDTO("Jane Smith", "0987654321", "jane.smith@example.com");
        ClientViewDTO client2 = service.create(clientDto2);

        TransportCargoService service1 = new TransportCargoService();
        service1.setStartingDate(LocalDate.now());
        service1.setPrice(new BigDecimal("2000"));
        service1.setTransportCompany(company);
        service1.setClient(clientRepo.getById(client1.getId()).get());
        service1.setWeightInKilograms(BigDecimal.valueOf(25));
        service1.setLengthInCentimeters(50);
        service1.setWidthInCentimeters(25);
        service1.setHeightInCentimeters(25);
        transportServiceRepo.create(service1);

        Map<Long, Integer> result = service.getTransportServiceCountsPerClient();
        assertEquals(2, result.size());
        assertEquals(1, result.get(client1.getId()));
        assertEquals(0, result.get(client2.getId()));
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
        ClientUpdateDTO dto = new ClientUpdateDTO(999L, "NonExistent", "1234567890", "nonexistent@example.com");
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
    void getTransportServicesByClient_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getTransportServicesByClient(null));
    }

    // Edge Cases
    @Test
    void getAll_EmptyFilter_ShouldReturnAll() {
        service.create(new ClientCreateDTO("John Doe", "1234567890", "john.doe@example.com"));
        service.create(new ClientCreateDTO("Jane Smith", "0987654321", "jane.smith@example.com"));

        List<ClientViewDTO> result = service.getAll(0, Integer.MAX_VALUE, "name", true, "");
        assertEquals(2, result.size());
    }

    @Test
    void findByCriteria_NoMatches_ShouldReturnEmptyList() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "NonExistent");
        List<ClientViewDTO> result = service.findByCriteria(conditions, "name", true);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransportServicesByClient_NoServices_ShouldReturnEmptyList() {
        ClientCreateDTO clientDto = new ClientCreateDTO("John Doe", "1234567890", "john.doe@example.com");
        ClientViewDTO client = service.create(clientDto);

        List<TransportServiceViewDTO> result = service.getTransportServicesByClient(client.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransportServiceCountsPerClient_NoClients_ShouldReturnEmptyMap() {
        Map<Long, Integer> result = service.getTransportServiceCountsPerClient();
        assertTrue(result.isEmpty());
    }
}