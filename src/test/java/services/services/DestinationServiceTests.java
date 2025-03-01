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
import services.data.dto.transportservices.DestinationCreateDTO;
import services.data.dto.transportservices.DestinationUpdateDTO;
import services.data.dto.transportservices.DestinationViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.mapping.mappers.DestinationMapper;
import services.data.mapping.mappers.TransportServiceMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;

public class DestinationServiceTests {
    private SessionFactory sessionFactory;
    private ExecutorService executorService;
    private IGenericRepository<Client, Long> clientRepo;
    private IGenericRepository<TransportService, Long> transportServiceRepo;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<Driver, Long> driverRepo;
    private IGenericRepository<Destination, Long> destinationRepo;
    private IGenericRepository<Vehicle, Long> vehicleRepo;
    private DestinationService service;
    private DestinationMapper destinationMapper;
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
            destinationRepo = new GenericRepository<>(sessionFactory, Destination.class);
            transportServiceRepo = new GenericRepository<>(sessionFactory, TransportService.class);
            driverRepo = new GenericRepository<>(sessionFactory, Driver.class);
            vehicleRepo = new GenericRepository<>(sessionFactory, Vehicle.class);

            destinationMapper = new DestinationMapper();
            transportServiceMapper = new TransportServiceMapper(companyRepo, clientRepo, driverRepo, destinationRepo, vehicleRepo);

            service = new DestinationService(destinationRepo, transportServiceRepo, destinationMapper, transportServiceMapper);

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
    void create_ValidDTO_ShouldCreateDestination() {
        DestinationCreateDTO dto = new DestinationCreateDTO("New York", "Los Angeles");
        DestinationViewDTO result = service.create(dto);

        assertNotNull(result.getId());
        assertEquals("New York", result.getStartingLocation());
        assertEquals("Los Angeles", result.getEndingLocation());
    }
    @Test
    void update_FullUpdate_ShouldUpdateDestination() {
        DestinationCreateDTO createDto = new DestinationCreateDTO("Initial Start", "Initial End");
        DestinationViewDTO created = service.create(createDto);

        DestinationUpdateDTO updateDto = new DestinationUpdateDTO(created.getId(), "Updated Start", "Updated End");
        DestinationViewDTO result = service.update(updateDto);

        assertEquals("Updated Start", result.getStartingLocation());
        assertEquals("Updated End", result.getEndingLocation());
    }

    @Test
    void update_PartialUpdate_ShouldPreserveUnchangedFields(){
        DestinationCreateDTO createDto = new DestinationCreateDTO("Initial Start", "Initial End");
        DestinationViewDTO created = service.create(createDto);

        DestinationUpdateDTO updateDto = new DestinationUpdateDTO(created.getId(), "Updated Start", null);
        DestinationViewDTO result = service.update(updateDto);

        assertEquals("Updated Start", result.getStartingLocation());
        assertEquals("Initial End", result.getEndingLocation());
    }

    @Test
    void delete_ExistingId_ShouldDeleteDestination() {
        DestinationCreateDTO dto = new DestinationCreateDTO("Temp Start", "Temp End");
        DestinationViewDTO created = service.create(dto);

        service.delete(created.getId());
        DestinationViewDTO result = service.getById(created.getId());
        assertNull(result);
    }

    @Test
    void delete_DestinationWithServices_ShouldDeleteAndNullifyServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        DestinationCreateDTO dto = new DestinationCreateDTO("New York", "Los Angeles");
        DestinationViewDTO dest = service.create(dto);

        TransportCargoService serviceEntity = new TransportCargoService();
        serviceEntity.setTransportCompany(company);
        serviceEntity.setPrice(new BigDecimal("2000"));
        serviceEntity.setStartingDate(LocalDate.now());
        serviceEntity.setDestination(destinationRepo.getById(dest.getId()).get());
        serviceEntity.setWeightInKilograms(BigDecimal.valueOf(25));
        serviceEntity.setLengthInCentimeters(50);
        serviceEntity.setWidthInCentimeters(25);
        serviceEntity.setHeightInCentimeters(25);
        transportServiceRepo.create(serviceEntity);

        service.delete(dest.getId());
        assertNull(service.getById(dest.getId()), "Destination should be deleted");

        TransportService updatedService = transportServiceRepo.getById(serviceEntity.getId()).get();
        assertNull(updatedService.getDestination(), "Transport service destination should be null after deletion");
    }

    @Test
    void getById_ExistingId_ShouldReturnDestination() {
        DestinationCreateDTO dto = new DestinationCreateDTO("Test Start", "Test End");
        DestinationViewDTO created = service.create(dto);

        DestinationViewDTO result = service.getById(created.getId());
        assertNotNull(result);
        assertEquals("Test Start", result.getStartingLocation());
    }

    @Test
    void getAll_WithFilter_ShouldReturnFilteredList() {
        service.create(new DestinationCreateDTO("New York", "Los Angeles"));
        service.create(new DestinationCreateDTO("Chicago", "Boston"));

        List<DestinationViewDTO> result = service.getAll(0, 10, "startingLocation", true, "New");
        assertEquals(1, result.size());
        assertEquals("New York", result.getFirst().getStartingLocation());
    }

    @Test
    void getAll_NoFilter_ShouldReturnAll() {
        service.create(new DestinationCreateDTO("New York", "Los Angeles"));
        service.create(new DestinationCreateDTO("Chicago", "Boston"));

        List<DestinationViewDTO> result = service.getAll(0, 10, "startingLocation", true, null);
        assertEquals(2, result.size());
    }

    @Test
    void findByCriteria_LocationMatch_ShouldReturnMatchingDestinations() {
        service.create(new DestinationCreateDTO("New York", "Los Angeles"));
        service.create(new DestinationCreateDTO("Chicago", "Boston"));

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("startingLocation", "New York");
        List<DestinationViewDTO> result = service.findByCriteria(conditions, "startingLocation", true);
        assertEquals(1, result.size());
        assertEquals("New York", result.getFirst().getStartingLocation());
    }

    @Test
    void findByCriteria_EndLocationMatch_ShouldReturnMatchingDestinations() {
        service.create(new DestinationCreateDTO("New York", "Los Angeles"));
        service.create(new DestinationCreateDTO("Chicago", "Boston"));

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("endingLocation", "Boston");
        List<DestinationViewDTO> result = service.findByCriteria(conditions, "startingLocation", true);
        assertEquals(1, result.size());
        assertEquals("Chicago", result.getFirst().getStartingLocation());
    }

    @Test
    void getTransportServicesByDestination_WithServices_ShouldReturnServicesPerDestination() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        DestinationCreateDTO destDto1 = new DestinationCreateDTO("New York", "Los Angeles");
        DestinationViewDTO dest1 = service.create(destDto1);
        DestinationCreateDTO destDto2 = new DestinationCreateDTO("Chicago", "Boston");
        DestinationViewDTO dest2 = service.create(destDto2);

        TransportCargoService service1 = new TransportCargoService();
        service1.setStartingDate(LocalDate.now());
        service1.setPrice(new BigDecimal("2000"));
        service1.setTransportCompany(company);
        service1.setDestination(destinationRepo.getById(dest1.getId()).get());
        service1.setWeightInKilograms(BigDecimal.valueOf(25));
        service1.setLengthInCentimeters(50);
        service1.setWidthInCentimeters(25);
        service1.setHeightInCentimeters(25);
        transportServiceRepo.create(service1);

        TransportPassengersService service2 = new TransportPassengersService();
        service2.setStartingDate(LocalDate.now());
        service2.setPrice(new BigDecimal("1500"));
        service2.setTransportCompany(company);
        service2.setDestination(destinationRepo.getById(dest2.getId()).get());
        service2.setNumberOfPassengers(20);
        transportServiceRepo.create(service2);

        Map<Long, List<TransportServiceViewDTO>> result = service.getTransportServicesByDestination();
        assertEquals(2, result.size());
        assertEquals(1, result.get(dest1.getId()).size());
        assertEquals(new BigDecimal("2000.00"), result.get(dest1.getId()).getFirst().getPrice());
        assertEquals(1, result.get(dest2.getId()).size());
        assertEquals(new BigDecimal("1500.00"), result.get(dest2.getId()).getFirst().getPrice());
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
        DestinationUpdateDTO dto = new DestinationUpdateDTO(null, "Updated Start", "Updated End");
        assertThrows(IllegalArgumentException.class, () -> service.update(dto));
    }

    @Test
    void update_NonExistentId_ShouldThrowRepositoryException() {
        DestinationUpdateDTO dto = new DestinationUpdateDTO(999L, "NonExistent Start", "NonExistent End");
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

    // Edge Cases

    @Test
    void getById_NonExistentId_ShouldReturnNull() {
        DestinationViewDTO result = service.getById(999L);
        assertNull(result);
    }

    @Test
    void getAll_EmptyFilter_ShouldReturnAll() {
        service.create(new DestinationCreateDTO("New York", "Los Angeles"));
        service.create(new DestinationCreateDTO("Chicago", "Boston"));

        List<DestinationViewDTO> result = service.getAll(0, Integer.MAX_VALUE, "startingLocation", true, "");
        assertEquals(2, result.size());
    }

    @Test
    void getAll_InvalidPage_ShouldReturnEmptyList() {
        service.create(new DestinationCreateDTO("Test Start", "Test End"));
        List<DestinationViewDTO> result = service.getAll(1, 1, "startingLocation", true, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_Pagination_ShouldReturnPaginatedList() {
        service.create(new DestinationCreateDTO("New York", "Los Angeles"));
        service.create(new DestinationCreateDTO("Chicago", "Boston"));
        service.create(new DestinationCreateDTO("Miami", "Seattle"));

        List<DestinationViewDTO> result = service.getAll(0, 2, "startingLocation", true, null);
        assertEquals(2, result.size());
        assertEquals("Chicago", result.get(0).getStartingLocation());
        assertEquals("Miami", result.get(1).getStartingLocation());
    }

    @Test
    void getAll_InvalidOrderBy_ShouldThrowRepositoryException() {
        assertThrows(RepositoryException.class, () -> service.getAll(0, 10, "invalidField", true, null));
    }

    @Test
    void findByCriteria_NoMatches_ShouldReturnEmptyList() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("startingLocation", "NonExistent");
        List<DestinationViewDTO> result = service.findByCriteria(conditions, "startingLocation", true);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByCriteria_EmptyConditions_ShouldReturnAll() {
        service.create(new DestinationCreateDTO("New York", "Los Angeles"));
        service.create(new DestinationCreateDTO("Chicago", "Boston"));

        List<DestinationViewDTO> result = service.findByCriteria(new HashMap<>(), "startingLocation", true);
        assertEquals(2, result.size());
    }

    @Test
    void getTransportServicesByDestination_NoServices_ShouldReturnEmptyLists() {
        service.create(new DestinationCreateDTO("New York", "Los Angeles"));
        service.create(new DestinationCreateDTO("Chicago", "Boston"));

        Map<Long, List<TransportServiceViewDTO>> result = service.getTransportServicesByDestination();
        assertEquals(2, result.size());
        result.values().forEach(list -> assertTrue(list.isEmpty()));
    }
}