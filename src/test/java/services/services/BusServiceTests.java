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
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.dto.vehicles.BusCreateDTO;
import services.data.dto.vehicles.BusUpdateDTO;
import services.data.dto.vehicles.BusViewDTO;
import services.data.mapping.mappers.BusMapper;
import services.data.mapping.mappers.TransportPassengersServiceMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BusServiceTests {
    private SessionFactory sessionFactory;
    private IGenericRepository<Bus, Long> busRepo;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<TransportPassengersService, Long> transportServiceRepo;
    private IGenericRepository<Driver, Long> driverRepo;
    private IGenericRepository<Client, Long> clientRepo;
    private IGenericRepository<Destination, Long> destinationRepo;
    private BusService service;
    private BusMapper busMapper;
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

            // Initialize repositories
            companyRepo = new GenericRepository<>(sessionFactory, TransportCompany.class);
            busRepo = new GenericRepository<>(sessionFactory, Bus.class);
            transportServiceRepo = new GenericRepository<>(sessionFactory, TransportPassengersService.class);
            driverRepo = new GenericRepository<>(sessionFactory, Driver.class);
            clientRepo = new GenericRepository<>(sessionFactory, Client.class);
            destinationRepo = new GenericRepository<>(sessionFactory, Destination.class);

            // Seed data
            TransportCompany company = new TransportCompany("Fast Transport", "123 Main St");
            companyRepo.create(company);

            Client client = new Client();
            client.setName("Test Client");
            client.setTelephone("1234567890");
            client.setEmail("test@client.com");
            clientRepo.create(client);

            Destination destination = new Destination();
            destination.setStartingLocation("City A");
            destination.setEndingLocation("City B");
            destinationRepo.create(destination);

            // Initialize mappers
            busMapper = new BusMapper(companyRepo);
            transportServiceMapper = new TransportPassengersServiceMapper();

            service = new BusService(busRepo, companyRepo, transportServiceRepo, busMapper, transportServiceMapper);
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
    void create_ValidDTO_ShouldCreateBus() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        BusCreateDTO dto = new BusCreateDTO("CA1234AC", company.getId(), 50, true, new BigDecimal("500.00"));
        BusViewDTO result = service.create(dto);

        assertNotNull(result.getId());
        assertEquals("CA1234AC", result.getRegistrationPlate());
        assertEquals(company.getId(), result.getTransportCompanyId());
        assertEquals(50, result.getMaxPassengerCapacity());
        assertTrue(result.getHasRestroom());
        assertEquals(new BigDecimal("500.00"), result.getLuggageCapacity());
    }

    @Test
    void update_FullUpdate_ShouldUpdateBus() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        BusCreateDTO createDto = new BusCreateDTO("CA1234AC_OLD", company.getId(), 30, false, new BigDecimal("300.00"));
        BusViewDTO created = service.create(createDto);

        BusUpdateDTO updateDto = new BusUpdateDTO(created.getId(), "CA1234AC_NEW", company.getId(), 35, true, new BigDecimal("350.00"));
        BusViewDTO result = service.update(updateDto);

        assertEquals("CA1234AC_NEW", result.getRegistrationPlate());
        assertEquals(35, result.getMaxPassengerCapacity());
        assertTrue(result.getHasRestroom());
        assertEquals(new BigDecimal("350.00"), result.getLuggageCapacity());
    }

    @Test
    void update_PartialChanges_ShouldPreserveUnchangedFields() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        BusCreateDTO createDto = new BusCreateDTO("CA1234AC", company.getId(), 50, true, new BigDecimal("500.00"));
        BusViewDTO created = service.create(createDto);

        // Only update registrationPlate, leave others unchanged
        BusUpdateDTO updateDto = new BusUpdateDTO();
        updateDto.setId(created.getId());
        updateDto.setRegistrationPlate("CA5678AC");
        // Explicitly not setting transportCompanyId, maxPassengerCapacity, hasRestroom, luggageCapacity

        BusViewDTO result = service.update(updateDto);

        assertEquals("CA5678AC", result.getRegistrationPlate());
        assertEquals(company.getId(), result.getTransportCompanyId()); // Unchanged
        assertEquals(50, result.getMaxPassengerCapacity()); // Unchanged
        assertTrue(result.getHasRestroom()); // Unchanged from true
        assertEquals(new BigDecimal("500.00"), result.getLuggageCapacity()); // Unchanged
    }

    @Test
    void delete_ExistingId_ShouldDeleteBus() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        BusCreateDTO dto = new BusCreateDTO("DEL123", company.getId(), 40, true, new BigDecimal("400.00"));
        BusViewDTO created = service.create(dto);

        service.delete(created.getId());
        BusViewDTO result = service.getById(created.getId());
        assertNull(result);
    }

    @Test
    void getById_ExistingId_ShouldReturnBus() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        BusCreateDTO dto = new BusCreateDTO("CA1234AC_GET", company.getId(), 50, false, new BigDecimal("500.00"));
        BusViewDTO created = service.create(dto);

        BusViewDTO result = service.getById(created.getId());
        assertNotNull(result);
        assertEquals("CA1234AC_GET", result.getRegistrationPlate());
        assertEquals(50, result.getMaxPassengerCapacity());
    }

    @Test
    void getAll_ValidPagination_ShouldReturnBuses() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        service.create(new BusCreateDTO("BUS1", company.getId(), 30, true, new BigDecimal("300.00")));
        service.create(new BusCreateDTO("BUS2", company.getId(), 40, false, new BigDecimal("400.00")));
        service.create(new BusCreateDTO("BUS3", company.getId(), 50, true, new BigDecimal("500.00")));

        List<BusViewDTO> result = service.getAll(0, 2, "registrationPlate", true);
        assertEquals(2, result.size());
        assertEquals("BUS1", result.get(0).getRegistrationPlate());
        assertEquals("BUS2", result.get(1).getRegistrationPlate());
    }



    @Test
    void getTransportServicesForBus_ValidBusId_ShouldReturnServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setFamilyName("Doe");
        driver.setSalary(new BigDecimal("50000"));
        driver.setTransportCompany(company);
        driverRepo.create(driver);

        BusCreateDTO busDto = new BusCreateDTO("CA1234AC", company.getId(), 50, true, new BigDecimal("500.00"));
        BusViewDTO bus = service.create(busDto);

        TransportPassengersService serviceEntity = new TransportPassengersService();
        serviceEntity.setTransportCompany(company);
        serviceEntity.setPrice(new BigDecimal("1000.00"));
        serviceEntity.setStartingDate(LocalDate.now());
        serviceEntity.setVehicle(busRepo.getById(bus.getId()).get());
        serviceEntity.setDriver(driver);
        serviceEntity.setNumberOfPassengers(20);
        transportServiceRepo.create(serviceEntity);

        List<? extends TransportServiceViewDTO> services = service.getTransportServicesForBus(bus.getId(), 0, 10);
        assertEquals(1, services.size());
        assertEquals(new BigDecimal("1000.00"), services.getFirst().getPrice());
        assertEquals(bus.getId(), services.getFirst().getVehicleId());
    }

    @Test
    void getActiveTransportServices_ValidBusId_ShouldReturnActiveServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setFamilyName("Doe");
        driver.setSalary(new BigDecimal("50000"));
        driver.setTransportCompany(company);
        driverRepo.create(driver);

        BusCreateDTO busDto = new BusCreateDTO("CA1234AC", company.getId(), 50, true, new BigDecimal("500.00"));
        BusViewDTO bus = service.create(busDto);

        TransportPassengersService activeService = new TransportPassengersService();
        activeService.setTransportCompany(company);
        activeService.setPrice(new BigDecimal("1000.00"));
        activeService.setStartingDate(LocalDate.now());
        activeService.setVehicle(busRepo.getById(bus.getId()).get());
        activeService.setDriver(driver);
        activeService.setDelivered(false);
        activeService.setNumberOfPassengers(20);
        transportServiceRepo.create(activeService);

        TransportPassengersService deliveredService = new TransportPassengersService();
        deliveredService.setTransportCompany(company);
        deliveredService.setPrice(new BigDecimal("1500.00"));
        deliveredService.setStartingDate(LocalDate.now());
        deliveredService.setVehicle(busRepo.getById(bus.getId()).get());
        deliveredService.setDriver(driver);
        deliveredService.setDelivered(true);
        deliveredService.setNumberOfPassengers(25);
        transportServiceRepo.create(deliveredService);

        List<? extends TransportServiceViewDTO> services = service.getActiveTransportServices(bus.getId(), 0, 10);
        assertEquals(1, services.size());
        assertEquals(new BigDecimal("1000.00"), services.getFirst().getPrice());
        assertFalse(services.getFirst().isDelivered());
    }

    @Test
    void getBusesByCompany_ValidCompanyId_ShouldReturnBuses() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        service.create(new BusCreateDTO("CMP1", company.getId(), 30, true, new BigDecimal("300.00")));
        service.create(new BusCreateDTO("CMP2", company.getId(), 40, false, new BigDecimal("400.00")));

        List<BusViewDTO> result = service.getBusesByCompany(company.getId(), 0, 2);
        assertEquals(2, result.size());
        assertEquals("CMP1", result.get(0).getRegistrationPlate());
        assertEquals("CMP2", result.get(1).getRegistrationPlate());
    }

    // Error Cases
    @Test
    void create_NullDTO_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.create(null));
    }

    @Test
    void create_InvalidCompanyId_ShouldThrowRepositoryException() {
        BusCreateDTO dto = new BusCreateDTO("CA1234AC_INV", 999L, 50, true, new BigDecimal("500.00"));
        assertThrows(RepositoryException.class, () -> service.create(dto));
    }

    @Test
    void update_NullDTO_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.update(null));
    }

    @Test
    void update_NullId_ShouldThrowIllegalArgumentException() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        BusUpdateDTO dto = new BusUpdateDTO(null, "CA1234AC_UPD", company.getId(), 50, true, new BigDecimal("500.00"));
        assertThrows(IllegalArgumentException.class, () -> service.update(dto));
    }

    @Test
    void update_NonExistentId_ShouldThrowRepositoryException() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        BusUpdateDTO dto = new BusUpdateDTO(999L, "CA1234AC_UPD", company.getId(), 50, true, new BigDecimal("500.00"));
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
    void getTransportServicesForBus_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getTransportServicesForBus(null, 0, 10));
    }

    @Test
    void getActiveTransportServices_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getActiveTransportServices(null, 0, 10));
    }

    @Test
    void getBusesByCompany_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getBusesByCompany(null, 0, 10));
    }

    @Test
    void getAll_InvalidOrderBy_ShouldThrowRepositoryException() {
        assertThrows(RepositoryException.class, () -> service.getAll(0, 10, "invalidField", true));
    }

    // Edge Cases
    @Test
    void getById_NonExistentId_ShouldReturnNull() {
        BusViewDTO result = service.getById(999L);
        assertNull(result);
    }

    @Test
    void getAll_InvalidPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        service.create(new BusCreateDTO("BUS1", company.getId(), 30, true, new BigDecimal("300.00")));
        List<BusViewDTO> result = service.getAll(1, 1, "registrationPlate", true);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransportServicesForBus_NoServices_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        BusViewDTO bus = service.create(new BusCreateDTO("CA1234AC", company.getId(), 50, true, new BigDecimal("500.00")));
        List<? extends TransportServiceViewDTO> services = service.getTransportServicesForBus(bus.getId(), 0, 10);
        assertTrue(services.isEmpty());
    }

    @Test
    void getTransportServicesForBus_Pagination_ShouldReturnPaginatedList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setFamilyName("Doe");
        driver.setSalary(new BigDecimal("50000"));
        driver.setTransportCompany(company);
        driverRepo.create(driver);

        BusViewDTO bus = service.create(new BusCreateDTO("CA1234AC", company.getId(), 50, true, new BigDecimal("500.00")));

        TransportPassengersService service1 = new TransportPassengersService();
        service1.setTransportCompany(company);
        service1.setPrice(new BigDecimal("1000.00"));
        service1.setStartingDate(LocalDate.now());
        service1.setVehicle(busRepo.getById(bus.getId()).get());
        service1.setDriver(driver);
        service1.setNumberOfPassengers(20);
        transportServiceRepo.create(service1);

        TransportPassengersService service2 = new TransportPassengersService();
        service2.setTransportCompany(company);
        service2.setPrice(new BigDecimal("1500.00"));
        service2.setStartingDate(LocalDate.now().plusDays(1));
        service2.setVehicle(busRepo.getById(bus.getId()).get());
        service2.setDriver(driver);
        service2.setNumberOfPassengers(25);
        transportServiceRepo.create(service2);

        List<? extends TransportServiceViewDTO> servicesPage1 = service.getTransportServicesForBus(bus.getId(), 0, 1);
        assertEquals(1, servicesPage1.size());
        assertEquals(new BigDecimal("1000.00"), servicesPage1.getFirst().getPrice());

        List<? extends TransportServiceViewDTO> servicesPage2 = service.getTransportServicesForBus(bus.getId(), 1, 1);
        assertEquals(1, servicesPage2.size());
        assertEquals(new BigDecimal("1500.00"), servicesPage2.getFirst().getPrice());
    }

    @Test
    void getActiveTransportServices_NoActiveServices_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setFamilyName("Doe");
        driver.setSalary(new BigDecimal("50000"));
        driver.setTransportCompany(company);
        driverRepo.create(driver);

        BusViewDTO bus = service.create(new BusCreateDTO("CA1234AC", company.getId(), 50, true, new BigDecimal("500.00")));
        TransportPassengersService deliveredService = new TransportPassengersService();
        deliveredService.setTransportCompany(company);
        deliveredService.setPrice(new BigDecimal("1500.00"));
        deliveredService.setStartingDate(LocalDate.now());
        deliveredService.setVehicle(busRepo.getById(bus.getId()).get());
        deliveredService.setDriver(driver);
        deliveredService.setDelivered(true);
        deliveredService.setNumberOfPassengers(25);
        transportServiceRepo.create(deliveredService);

        List<? extends TransportServiceViewDTO> services = service.getActiveTransportServices(bus.getId(), 0, 10);
        assertTrue(services.isEmpty());
    }

    @Test
    void getBusesByCompany_NoBuses_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        List<BusViewDTO> result = service.getBusesByCompany(company.getId(), 0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransportServicesForBus_InvalidPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setFamilyName("Doe");
        driver.setSalary(new BigDecimal("50000"));
        driver.setTransportCompany(company);
        driverRepo.create(driver);

        BusViewDTO bus = service.create(new BusCreateDTO("CA1234AC", company.getId(), 50, true, new BigDecimal("500.00")));
        TransportPassengersService service1 = new TransportPassengersService();
        service1.setTransportCompany(company);
        service1.setPrice(new BigDecimal("1000.00"));
        service1.setStartingDate(LocalDate.now());
        service1.setVehicle(busRepo.getById(bus.getId()).get());
        service1.setDriver(driver);
        service1.setNumberOfPassengers(20);
        transportServiceRepo.create(service1);

        List<? extends TransportServiceViewDTO> services = service.getTransportServicesForBus(bus.getId(), -1, 10);
        assertTrue(services.isEmpty(), "Negative page should return empty list");
    }

    @Test
    void getTransportServicesForBus_InvalidPageSize_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        BusViewDTO bus = service.create(new BusCreateDTO("CA1234AC", company.getId(), 50, true, new BigDecimal("500.00")));
        List<? extends TransportServiceViewDTO> services = service.getTransportServicesForBus(bus.getId(), 0, 0);
        assertTrue(services.isEmpty(), "Non-positive pageSize should return empty list");
    }

    @Test
    void getActiveTransportServices_EmptyPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setFamilyName("Doe");
        driver.setSalary(new BigDecimal("50000"));
        driver.setTransportCompany(company);
        driverRepo.create(driver);

        BusViewDTO bus = service.create(new BusCreateDTO("CA1234AC", company.getId(), 50, true, new BigDecimal("500.00")));
        TransportPassengersService activeService = new TransportPassengersService();
        activeService.setTransportCompany(company);
        activeService.setPrice(new BigDecimal("1000.00"));
        activeService.setStartingDate(LocalDate.now());
        activeService.setVehicle(busRepo.getById(bus.getId()).get());
        activeService.setDriver(driver);
        activeService.setDelivered(false);
        activeService.setNumberOfPassengers(20);
        transportServiceRepo.create(activeService);

        List<? extends TransportServiceViewDTO> services = service.getActiveTransportServices(bus.getId(), 1, 1);
        assertTrue(services.isEmpty(), "Page beyond data should return empty list");
    }

}