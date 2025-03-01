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
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;
import services.data.dto.vehicles.VanCreateDTO;
import services.data.dto.vehicles.VanUpdateDTO;
import services.data.dto.vehicles.VanViewDTO;
import services.data.mapping.mappers.TransportPassengersServiceMapper;
import services.data.mapping.mappers.VanMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class VanServiceTests {
    private SessionFactory sessionFactory;
    private IGenericRepository<Van, Long> vanRepo;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<TransportPassengersService, Long> transportServiceRepo;
    private IGenericRepository<Driver, Long> driverRepo;
    private IGenericRepository<Client, Long> clientRepo;
    private IGenericRepository<Destination, Long> destinationRepo;
    private IGenericRepository<Vehicle, Long> vehicleRepo;
    private VanService service;
    private VanMapper vanMapper;
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
            vanRepo = new GenericRepository<>(sessionFactory, Van.class);
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
            vanMapper = new VanMapper(companyRepo);
            transportServiceMapper = new TransportPassengersServiceMapper(companyRepo, clientRepo, driverRepo, vehicleRepo, destinationRepo);
            service = new VanService(vanRepo, companyRepo, transportServiceRepo, vanMapper, transportServiceMapper);
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
    void create_ValidDTO_ShouldCreateVan() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        VanCreateDTO dto = new VanCreateDTO("VN1234VN", company.getId(), 8, true);
        VanViewDTO result = service.create(dto);

        assertNotNull(result.getId());
        assertEquals("VN1234VN", result.getRegistrationPlate());
        assertEquals(company.getId(), result.getTransportCompanyId());
        assertEquals(8, result.getMaxPassengerCapacity());
        assertTrue(result.getHasPassengerOverheadStorage());
    }

    @Test
    void update_FullUpdate_ShouldUpdateVan() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        VanCreateDTO createDto = new VanCreateDTO("VN1234VN", company.getId(), 8, true);
        VanViewDTO created = service.create(createDto);

        VanUpdateDTO updateDto = new VanUpdateDTO(created.getId(), "VN5678VN", company.getId(), 10, false);
        VanViewDTO result = service.update(updateDto);

        assertEquals("VN5678VN", result.getRegistrationPlate());
        assertEquals(company.getId(), result.getTransportCompanyId());
        assertEquals(10, result.getMaxPassengerCapacity());
        assertFalse(result.getHasPassengerOverheadStorage());
    }

    @Test
    void update_PartialChanges_ShouldPreserveUnchangedFields() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        VanCreateDTO createDto = new VanCreateDTO("VN1234VN", company.getId(), 8, true);
        VanViewDTO created = service.create(createDto);

        VanUpdateDTO updateDto = new VanUpdateDTO();
        updateDto.setId(created.getId());
        updateDto.setRegistrationPlate("VN5678VN");

        VanViewDTO result = service.update(updateDto);

        assertEquals("VN5678VN", result.getRegistrationPlate());
        assertEquals(company.getId(), result.getTransportCompanyId());
        assertEquals(8, result.getMaxPassengerCapacity());
        assertTrue(result.getHasPassengerOverheadStorage());
    }

    @Test
    void delete_ExistingId_ShouldDeleteVan() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        VanCreateDTO dto = new VanCreateDTO("VN1234VN", company.getId(), 8, true);
        VanViewDTO created = service.create(dto);

        service.delete(created.getId());
        VanViewDTO result = service.getById(created.getId());
        assertNull(result);
    }

    @Test
    void getById_ExistingId_ShouldReturnVan() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        VanCreateDTO dto = new VanCreateDTO("VN1234VN", company.getId(), 8, true);
        VanViewDTO created = service.create(dto);

        VanViewDTO result = service.getById(created.getId());
        assertNotNull(result);
        assertEquals("VN1234VN", result.getRegistrationPlate());
        assertEquals(8, result.getMaxPassengerCapacity());
        assertTrue(result.getHasPassengerOverheadStorage());
    }

    @Test
    void getAll_ValidPagination_ShouldReturnVans() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        service.create(new VanCreateDTO("VAN1", company.getId(), 6, true));
        service.create(new VanCreateDTO("VAN2", company.getId(), 8, false));
        service.create(new VanCreateDTO("VAN3", company.getId(), 10, true));

        List<VanViewDTO> result = service.getAll(0, 2, "registrationPlate", true);
        assertEquals(2, result.size());
        assertEquals("VAN1", result.get(0).getRegistrationPlate());
        assertEquals("VAN2", result.get(1).getRegistrationPlate());
    }

    @Test
    void getTransportServicesForVan_ValidVanId_ShouldReturnPassengerServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = createDriver(company);

        VanViewDTO van = service.create(new VanCreateDTO("VN1234VN", company.getId(), 8, true));

        TransportPassengersService serviceEntity = new TransportPassengersService();
        serviceEntity.setTransportCompany(company);
        serviceEntity.setClient(client);
        serviceEntity.setDestination(destination);
        serviceEntity.setPrice(new BigDecimal("1000.00"));
        serviceEntity.setStartingDate(LocalDate.now());
        serviceEntity.setEndingDate(LocalDate.now().plusDays(1));
        serviceEntity.setVehicle(vanRepo.getById(van.getId()).get());
        serviceEntity.setDriver(driver);
        serviceEntity.setNumberOfPassengers(6);
        transportServiceRepo.create(serviceEntity);

        List<TransportPassengersServiceViewDTO> services = service.getTransportServicesForVan(van.getId(), 0, 10);
        assertEquals(1, services.size());
        TransportPassengersServiceViewDTO result = services.getFirst();
        assertEquals(new BigDecimal("1000.00"), result.getPrice());
        assertEquals(van.getId(), result.getVehicleId());
        assertEquals(6, result.getNumberOfPassengers());
    }

    @Test
    void getActiveTransportServices_ValidVanId_ShouldReturnActivePassengerServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = createDriver(company);

        VanViewDTO van = service.create(new VanCreateDTO("VN1234VN", company.getId(), 8, true));

        TransportPassengersService activeService = new TransportPassengersService();
        activeService.setTransportCompany(company);
        activeService.setClient(client);
        activeService.setDestination(destination);
        activeService.setPrice(new BigDecimal("1000.00"));
        activeService.setStartingDate(LocalDate.now());
        activeService.setEndingDate(LocalDate.now().plusDays(1));
        activeService.setVehicle(vanRepo.getById(van.getId()).get());
        activeService.setDriver(driver);
        activeService.setDelivered(false);
        activeService.setNumberOfPassengers(6);
        transportServiceRepo.create(activeService);

        TransportPassengersService deliveredService = new TransportPassengersService();
        deliveredService.setTransportCompany(company);
        deliveredService.setClient(client);
        deliveredService.setDestination(destination);
        deliveredService.setPrice(new BigDecimal("1500.00"));
        deliveredService.setStartingDate(LocalDate.now());
        deliveredService.setEndingDate(LocalDate.now().plusDays(1));
        deliveredService.setVehicle(vanRepo.getById(van.getId()).get());
        deliveredService.setDriver(driver);
        deliveredService.setDelivered(true);
        deliveredService.setNumberOfPassengers(7);
        transportServiceRepo.create(deliveredService);

        List<TransportPassengersServiceViewDTO> services = service.getActiveTransportServices(van.getId(), 0, 10);
        assertEquals(1, services.size());
        TransportPassengersServiceViewDTO result = services.getFirst();
        assertEquals(new BigDecimal("1000.00"), result.getPrice());
        assertFalse(result.isDelivered());
        assertEquals(6, result.getNumberOfPassengers());
    }

    @Test
    void getVansByCompany_ValidCompanyId_ShouldReturnVans() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        service.create(new VanCreateDTO("CMP1", company.getId(), 6, true));
        service.create(new VanCreateDTO("CMP2", company.getId(), 8, false));

        List<VanViewDTO> result = service.getVansByCompany(company.getId(), 0, 2);
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
        VanCreateDTO dto = new VanCreateDTO("VN1234VN", 999L, 8, true);
        assertThrows(RepositoryException.class, () -> service.create(dto));
    }

    @Test
    void update_NullDTO_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.update(null));
    }

    @Test
    void update_NullId_ShouldThrowIllegalArgumentException() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        VanUpdateDTO dto = new VanUpdateDTO(null, "VN5678VN", company.getId(), 10, false);
        assertThrows(IllegalArgumentException.class, () -> service.update(dto));
    }

    @Test
    void update_NonExistentId_ShouldThrowRepositoryException() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        VanUpdateDTO dto = new VanUpdateDTO(999L, "VN5678VN", company.getId(), 10, false);
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
    void getTransportServicesForVan_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getTransportServicesForVan(null, 0, 10));
    }

    @Test
    void getActiveTransportServices_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getActiveTransportServices(null, 0, 10));
    }

    @Test
    void getVansByCompany_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getVansByCompany(null, 0, 10));
    }

    @Test
    void getAll_InvalidOrderBy_ShouldThrowRepositoryException() {
        assertThrows(RepositoryException.class, () -> service.getAll(0, 10, "invalidField", true));
    }

    // Edge Cases
    @Test
    void getById_NonExistentId_ShouldReturnNull() {
        VanViewDTO result = service.getById(999L);
        assertNull(result);
    }

    @Test
    void getAll_InvalidPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        service.create(new VanCreateDTO("VAN1", company.getId(), 6, true));
        List<VanViewDTO> result = service.getAll(1, 1, "registrationPlate", true);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransportServicesForVan_NoServices_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        VanViewDTO van = service.create(new VanCreateDTO("VN1234VN", company.getId(), 8, true));
        List<TransportPassengersServiceViewDTO> services = service.getTransportServicesForVan(van.getId(), 0, 10);
        assertTrue(services.isEmpty());
    }

    @Test
    void getTransportServicesForVan_Pagination_ShouldReturnPaginatedList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = createDriver(company);

        VanViewDTO van = service.create(new VanCreateDTO("VN1234VN", company.getId(), 8, true));

        TransportPassengersService service1 = new TransportPassengersService();
        service1.setTransportCompany(company);
        service1.setClient(client);
        service1.setDestination(destination);
        service1.setPrice(new BigDecimal("1000.00"));
        service1.setStartingDate(LocalDate.now());
        service1.setEndingDate(LocalDate.now().plusDays(1));
        service1.setVehicle(vanRepo.getById(van.getId()).get());
        service1.setDriver(driver);
        service1.setNumberOfPassengers(6);
        transportServiceRepo.create(service1);

        TransportPassengersService service2 = new TransportPassengersService();
        service2.setTransportCompany(company);
        service2.setClient(client);
        service2.setDestination(destination);
        service2.setPrice(new BigDecimal("1500.00"));
        service2.setStartingDate(LocalDate.now().plusDays(1));
        service2.setEndingDate(LocalDate.now().plusDays(2));
        service2.setVehicle(vanRepo.getById(van.getId()).get());
        service2.setDriver(driver);
        service2.setNumberOfPassengers(7);
        transportServiceRepo.create(service2);

        List<TransportPassengersServiceViewDTO> servicesPage1 = service.getTransportServicesForVan(van.getId(), 0, 1);
        assertEquals(1, servicesPage1.size());
        assertEquals(new BigDecimal("1000.00"), servicesPage1.getFirst().getPrice());

        List<TransportPassengersServiceViewDTO> servicesPage2 = service.getTransportServicesForVan(van.getId(), 1, 1);
        assertEquals(1, servicesPage2.size());
        assertEquals(new BigDecimal("1500.00"), servicesPage2.getFirst().getPrice());
    }

    @Test
    void getActiveTransportServices_NoActiveServices_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = createDriver(company);

        VanViewDTO van = service.create(new VanCreateDTO("VN1234VN", company.getId(), 8, true));
        TransportPassengersService deliveredService = new TransportPassengersService();
        deliveredService.setTransportCompany(company);
        deliveredService.setClient(client);
        deliveredService.setDestination(destination);
        deliveredService.setPrice(new BigDecimal("1500.00"));
        deliveredService.setStartingDate(LocalDate.now());
        deliveredService.setEndingDate(LocalDate.now().plusDays(1));
        deliveredService.setVehicle(vanRepo.getById(van.getId()).get());
        deliveredService.setDriver(driver);
        deliveredService.setDelivered(true);
        deliveredService.setNumberOfPassengers(7);
        transportServiceRepo.create(deliveredService);

        List<TransportPassengersServiceViewDTO> services = service.getActiveTransportServices(van.getId(), 0, 10);
        assertTrue(services.isEmpty());
    }

    @Test
    void getVansByCompany_NoVans_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        List<VanViewDTO> result = service.getVansByCompany(company.getId(), 0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransportServicesForVan_InvalidPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = createDriver(company);

        VanViewDTO van = service.create(new VanCreateDTO("VN1234VN", company.getId(), 8, true));
        TransportPassengersService serviceEntity = new TransportPassengersService();
        serviceEntity.setTransportCompany(company);
        serviceEntity.setClient(client);
        serviceEntity.setDestination(destination);
        serviceEntity.setPrice(new BigDecimal("1000.00"));
        serviceEntity.setStartingDate(LocalDate.now());
        serviceEntity.setEndingDate(LocalDate.now().plusDays(1));
        serviceEntity.setVehicle(vanRepo.getById(van.getId()).get());
        serviceEntity.setDriver(driver);
        serviceEntity.setNumberOfPassengers(6);
        transportServiceRepo.create(serviceEntity);

        List<TransportPassengersServiceViewDTO> services = service.getTransportServicesForVan(van.getId(), -1, 10);
        assertTrue(services.isEmpty());
    }

    @Test
    void getTransportServicesForVan_InvalidPageSize_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        VanViewDTO van = service.create(new VanCreateDTO("VN1234VN", company.getId(), 8, true));
        List<TransportPassengersServiceViewDTO> services = service.getTransportServicesForVan(van.getId(), 0, 0);
        assertTrue(services.isEmpty());
    }

    @Test
    void getActiveTransportServices_EmptyPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = createDriver(company);

        VanViewDTO van = service.create(new VanCreateDTO("VN1234VN", company.getId(), 8, true));
        TransportPassengersService activeService = new TransportPassengersService();
        activeService.setTransportCompany(company);
        activeService.setClient(client);
        activeService.setDestination(destination);
        activeService.setPrice(new BigDecimal("1000.00"));
        activeService.setStartingDate(LocalDate.now());
        activeService.setEndingDate(LocalDate.now().plusDays(1));
        activeService.setVehicle(vanRepo.getById(van.getId()).get());
        activeService.setDriver(driver);
        activeService.setDelivered(false);
        activeService.setNumberOfPassengers(6);
        transportServiceRepo.create(activeService);

        List<TransportPassengersServiceViewDTO> services = service.getActiveTransportServices(van.getId(), 1, 1);
        assertTrue(services.isEmpty());
    }

    // Helper method
    private Driver createDriver(TransportCompany company) {
        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setFamilyName("Doe");
        driver.setSalary(new BigDecimal("50000"));
        driver.setTransportCompany(company);
        driverRepo.create(driver);
        return driver;
    }
}