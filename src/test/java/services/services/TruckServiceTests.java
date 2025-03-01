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
import services.data.dto.transportservices.TransportCargoServiceViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.dto.vehicles.TruckCreateDTO;
import services.data.dto.vehicles.TruckUpdateDTO;
import services.data.dto.vehicles.TruckViewDTO;
import services.data.mapping.mappers.TransportCargoServiceMapper;
import services.data.mapping.mappers.TruckMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TruckServiceTests {
    private SessionFactory sessionFactory;
    private IGenericRepository<Vehicle, Long> vehicleRepo;
    private IGenericRepository<Truck, Long> truckRepo;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<TransportCargoService, Long> transportServiceRepo;
    private IGenericRepository<Driver, Long> driverRepo;
    private IGenericRepository<Client, Long> clientRepo;
    private IGenericRepository<Destination, Long> destinationRepo;
    private TruckService service;
    private TruckMapper truckMapper;
    private TransportCargoServiceMapper transportServiceMapper;

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
            vehicleRepo = new GenericRepository<>(sessionFactory, Vehicle.class);
            truckRepo = new GenericRepository<>(sessionFactory, Truck.class);
            transportServiceRepo = new GenericRepository<>(sessionFactory, TransportCargoService.class);
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
            truckMapper = new TruckMapper(companyRepo);
            transportServiceMapper = new TransportCargoServiceMapper(companyRepo,clientRepo,driverRepo,destinationRepo, vehicleRepo);

            service = new TruckService(truckRepo, companyRepo, transportServiceRepo, truckMapper, transportServiceMapper);
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
    void create_ValidDTO_ShouldCreateTruck() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TruckCreateDTO dto = new TruckCreateDTO("TX1234TX", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX);
        TruckViewDTO result = service.create(dto);

        assertNotNull(result.getId());
        assertEquals("TX1234TX", result.getRegistrationPlate());
        assertEquals(company.getId(), result.getTransportCompanyId());
        assertEquals(10000.0, result.getMaxCargoCapacityKg());
        assertEquals(0.0, result.getCurrentCargoCapacityKg());
        assertEquals(CargoType.REGULAR, result.getCargoType());
        assertEquals(TruckType.BOX, result.getTruckType());
    }

    @Test
    void update_FullUpdate_ShouldUpdateTruck() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TruckCreateDTO createDto = new TruckCreateDTO("TX1234TX", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX);
        TruckViewDTO created = service.create(createDto);

        TruckUpdateDTO updateDto = new TruckUpdateDTO(created.getId(), "TX5678TX", company.getId(), 12000.0, 500.0, CargoType.SPECIAL, TruckType.FLATBED);
        TruckViewDTO result = service.update(updateDto);

        assertEquals("TX5678TX", result.getRegistrationPlate());
        assertEquals(company.getId(), result.getTransportCompanyId());
        assertEquals(12000.0, result.getMaxCargoCapacityKg());
        assertEquals(500.0, result.getCurrentCargoCapacityKg());
        assertEquals(CargoType.SPECIAL, result.getCargoType());
        assertEquals(TruckType.FLATBED, result.getTruckType());
    }

    @Test
    void update_PartialChanges_ShouldPreserveUnchangedFields() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TruckCreateDTO createDto = new TruckCreateDTO("TX1234TX", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX);
        TruckViewDTO created = service.create(createDto);

        TruckUpdateDTO updateDto = new TruckUpdateDTO();
        updateDto.setId(created.getId());
        updateDto.setRegistrationPlate("TX5678TX");

        TruckViewDTO result = service.update(updateDto);

        assertEquals("TX5678TX", result.getRegistrationPlate());
        assertEquals(company.getId(), result.getTransportCompanyId());
        assertEquals(10000.0, result.getMaxCargoCapacityKg());
        assertEquals(0.0, result.getCurrentCargoCapacityKg());
        assertEquals(CargoType.REGULAR, result.getCargoType());
        assertEquals(TruckType.BOX, result.getTruckType());
    }

    @Test
    void delete_ExistingId_ShouldDeleteTruck() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TruckCreateDTO dto = new TruckCreateDTO("TX1234TX", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX);
        TruckViewDTO created = service.create(dto);

        service.delete(created.getId());
        TruckViewDTO result = service.getById(created.getId());
        assertNull(result);
    }

    @Test
    void getById_ExistingId_ShouldReturnTruck() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TruckCreateDTO dto = new TruckCreateDTO("TX1234TX", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX);
        TruckViewDTO created = service.create(dto);

        TruckViewDTO result = service.getById(created.getId());
        assertNotNull(result);
        assertEquals("TX1234TX", result.getRegistrationPlate());
        assertEquals(10000.0, result.getMaxCargoCapacityKg());
    }

    @Test
    void getAll_ValidPagination_ShouldReturnTrucks() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        service.create(new TruckCreateDTO("TRUCK1", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX));
        service.create(new TruckCreateDTO("TRUCK2", company.getId(), 12000.0, 0.0, CargoType.SPECIAL, TruckType.FLATBED));
        service.create(new TruckCreateDTO("TRUCK3", company.getId(), 15000.0, 0.0, CargoType.REGULAR, TruckType.REFRIGERATED));

        List<TruckViewDTO> result = service.getAll(0, 2, "registrationPlate", true);
        assertEquals(2, result.size());
        assertEquals("TRUCK1", result.get(0).getRegistrationPlate());
        assertEquals("TRUCK2", result.get(1).getRegistrationPlate());
    }

    @Test
    void getTransportServicesForTruck_ValidTruckId_ShouldReturnCargoServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = createDriver(company);

        TruckViewDTO truck = service.create(new TruckCreateDTO("TX1234TX", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX));

        TransportCargoService serviceEntity = new TransportCargoService();
        serviceEntity.setTransportCompany(company);
        serviceEntity.setClient(client);
        serviceEntity.setDestination(destination);
        serviceEntity.setPrice(new BigDecimal("2000.00"));
        serviceEntity.setStartingDate(LocalDate.now());
        serviceEntity.setEndingDate(LocalDate.now().plusDays(1));
        serviceEntity.setVehicle(truckRepo.getById(truck.getId()).get());
        serviceEntity.setDriver(driver);
        serviceEntity.setWeightInKilograms(new BigDecimal("5000.00"));
        serviceEntity.setLengthInCentimeters(200);
        serviceEntity.setWidthInCentimeters(150);
        serviceEntity.setHeightInCentimeters(100);
        transportServiceRepo.create(serviceEntity);

        List<? extends TransportServiceViewDTO> services = service.getTransportServicesForTruck(truck.getId(), 0, 10);
        assertEquals(1, services.size());
        TransportCargoServiceViewDTO result = (TransportCargoServiceViewDTO) services.getFirst();
        assertEquals(new BigDecimal("2000.00"), result.getPrice());
        assertEquals(truck.getId(), result.getVehicleId());
        assertEquals(new BigDecimal("5000.00"), result.getWeightInKilograms());
        assertEquals(200, result.getLengthInCentimeters());
        assertEquals(150, result.getWidthInCentimeters());
        assertEquals(100, result.getHeightInCentimeters());
    }

    @Test
    void getActiveTransportServices_ValidTruckId_ShouldReturnActiveCargoServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = createDriver(company);

        TruckViewDTO truck = service.create(new TruckCreateDTO("TX1234TX", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX));

        TransportCargoService activeService = new TransportCargoService();
        activeService.setTransportCompany(company);
        activeService.setClient(client);
        activeService.setDestination(destination);
        activeService.setPrice(new BigDecimal("2000.00"));
        activeService.setStartingDate(LocalDate.now());
        activeService.setEndingDate(LocalDate.now().plusDays(1));
        activeService.setVehicle(truckRepo.getById(truck.getId()).get());
        activeService.setDriver(driver);
        activeService.setDelivered(false);
        activeService.setWeightInKilograms(new BigDecimal("5000.00"));
        activeService.setLengthInCentimeters(200);
        activeService.setWidthInCentimeters(150);
        activeService.setHeightInCentimeters(100);
        transportServiceRepo.create(activeService);

        TransportCargoService deliveredService = new TransportCargoService();
        deliveredService.setTransportCompany(company);
        deliveredService.setClient(client);
        deliveredService.setDestination(destination);
        deliveredService.setPrice(new BigDecimal("2500.00"));
        deliveredService.setStartingDate(LocalDate.now());
        deliveredService.setEndingDate(LocalDate.now().plusDays(1));
        deliveredService.setVehicle(truckRepo.getById(truck.getId()).get());
        deliveredService.setDriver(driver);
        deliveredService.setDelivered(true);
        deliveredService.setWeightInKilograms(new BigDecimal("6000.00"));
        deliveredService.setLengthInCentimeters(220);
        deliveredService.setWidthInCentimeters(160);
        deliveredService.setHeightInCentimeters(110);
        transportServiceRepo.create(deliveredService);

        List<? extends TransportServiceViewDTO> services = service.getActiveTransportServices(truck.getId(), 0, 10);
        assertEquals(1, services.size());
        TransportCargoServiceViewDTO result = (TransportCargoServiceViewDTO) services.getFirst();
        assertEquals(new BigDecimal("2000.00"), result.getPrice());
        assertFalse(result.isDelivered());
        assertEquals(new BigDecimal("5000.00"), result.getWeightInKilograms());
    }

    @Test
    void getTrucksByCompany_ValidCompanyId_ShouldReturnTrucks() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        service.create(new TruckCreateDTO("CMP1", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX));
        service.create(new TruckCreateDTO("CMP2", company.getId(), 12000.0, 0.0, CargoType.SPECIAL, TruckType.FLATBED));

        List<TruckViewDTO> result = service.getTrucksByCompany(company.getId(), 0, 2);
        assertEquals(2, result.size());
        assertEquals("CMP1", result.get(0).getRegistrationPlate());
        assertEquals("CMP2", result.get(1).getRegistrationPlate());
    }

    @Test
    void getAllByTruckType_ValidType_ShouldReturnTrucks() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        service.create(new TruckCreateDTO("BOX1", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX));
        service.create(new TruckCreateDTO("BOX2", company.getId(), 12000.0, 0.0, CargoType.SPECIAL, TruckType.BOX));
        service.create(new TruckCreateDTO("FLAT1", company.getId(), 15000.0, 0.0, CargoType.REGULAR, TruckType.FLATBED));

        List<TruckViewDTO> result = service.getAllByTruckType("BOX", 0, 10, "registrationPlate", true);
        assertEquals(2, result.size());
        assertEquals("BOX1", result.get(0).getRegistrationPlate());
        assertEquals("BOX2", result.get(1).getRegistrationPlate());
    }

    // Error Cases
    @Test
    void create_NullDTO_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.create(null));
    }

    @Test
    void create_InvalidCompanyId_ShouldThrowRepositoryException() {
        TruckCreateDTO dto = new TruckCreateDTO("TX1234TX", 999L, 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX);
        assertThrows(RepositoryException.class, () -> service.create(dto));
    }

    @Test
    void update_NullDTO_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.update(null));
    }

    @Test
    void update_NullId_ShouldThrowIllegalArgumentException() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TruckUpdateDTO dto = new TruckUpdateDTO(null, "TX5678TX", company.getId(), 12000.0, 500.0, CargoType.SPECIAL, TruckType.FLATBED);
        assertThrows(IllegalArgumentException.class, () -> service.update(dto));
    }

    @Test
    void update_NonExistentId_ShouldThrowRepositoryException() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TruckUpdateDTO dto = new TruckUpdateDTO(999L, "TX5678TX", company.getId(), 12000.0, 500.0, CargoType.SPECIAL, TruckType.FLATBED);
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
    void getTransportServicesForTruck_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getTransportServicesForTruck(null, 0, 10));
    }

    @Test
    void getActiveTransportServices_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getActiveTransportServices(null, 0, 10));
    }

    @Test
    void getTrucksByCompany_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getTrucksByCompany(null, 0, 10));
    }

    @Test
    void getAllByTruckType_NullType_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getAllByTruckType(null, 0, 10, "registrationPlate", true));
    }

    @Test
    void getAll_InvalidOrderBy_ShouldThrowRepositoryException() {
        assertThrows(RepositoryException.class, () -> service.getAll(0, 10, "invalidField", true));
    }

    @Test
    void getAllByTruckType_InvalidOrderBy_ShouldThrowRepositoryException() {
        assertThrows(RepositoryException.class, () -> service.getAllByTruckType("BOX", 0, 10, "invalidField", true));
    }

    // Edge Cases
    @Test
    void getById_NonExistentId_ShouldReturnNull() {
        TruckViewDTO result = service.getById(999L);
        assertNull(result);
    }

    @Test
    void getAll_InvalidPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        service.create(new TruckCreateDTO("TRUCK1", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX));
        List<TruckViewDTO> result = service.getAll(1, 1, "registrationPlate", true);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransportServicesForTruck_NoServices_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TruckViewDTO truck = service.create(new TruckCreateDTO("TX1234TX", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX));
        List<? extends TransportServiceViewDTO> services = service.getTransportServicesForTruck(truck.getId(), 0, 10);
        assertTrue(services.isEmpty());
    }

    @Test
    void getTransportServicesForTruck_Pagination_ShouldReturnPaginatedList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = createDriver(company);

        TruckViewDTO truck = service.create(new TruckCreateDTO("TX1234TX", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX));

        TransportCargoService service1 = new TransportCargoService();
        service1.setTransportCompany(company);
        service1.setClient(client);
        service1.setDestination(destination);
        service1.setPrice(new BigDecimal("2000.00"));
        service1.setStartingDate(LocalDate.now());
        service1.setEndingDate(LocalDate.now().plusDays(1));
        service1.setVehicle(truckRepo.getById(truck.getId()).get());
        service1.setDriver(driver);
        service1.setWeightInKilograms(new BigDecimal("5000.00"));
        service1.setLengthInCentimeters(200);
        service1.setWidthInCentimeters(150);
        service1.setHeightInCentimeters(100);
        transportServiceRepo.create(service1);

        TransportCargoService service2 = new TransportCargoService();
        service2.setTransportCompany(company);
        service2.setClient(client);
        service2.setDestination(destination);
        service2.setPrice(new BigDecimal("2500.00"));
        service2.setStartingDate(LocalDate.now().plusDays(1));
        service2.setEndingDate(LocalDate.now().plusDays(2));
        service2.setVehicle(truckRepo.getById(truck.getId()).get());
        service2.setDriver(driver);
        service2.setWeightInKilograms(new BigDecimal("6000.00"));
        service2.setLengthInCentimeters(220);
        service2.setWidthInCentimeters(160);
        service2.setHeightInCentimeters(110);
        transportServiceRepo.create(service2);

        List<? extends TransportServiceViewDTO> servicesPage1 = service.getTransportServicesForTruck(truck.getId(), 0, 1);
        assertEquals(1, servicesPage1.size());
        assertEquals(new BigDecimal("2000.00"), servicesPage1.getFirst().getPrice());

        List<? extends TransportServiceViewDTO> servicesPage2 = service.getTransportServicesForTruck(truck.getId(), 1, 1);
        assertEquals(1, servicesPage2.size());
        assertEquals(new BigDecimal("2500.00"), servicesPage2.getFirst().getPrice());
    }

    @Test
    void getActiveTransportServices_NoActiveServices_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = createDriver(company);

        TruckViewDTO truck = service.create(new TruckCreateDTO("TX1234TX", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX));
        TransportCargoService deliveredService = new TransportCargoService();
        deliveredService.setTransportCompany(company);
        deliveredService.setClient(client);
        deliveredService.setDestination(destination);
        deliveredService.setPrice(new BigDecimal("2500.00"));
        deliveredService.setStartingDate(LocalDate.now());
        deliveredService.setEndingDate(LocalDate.now().plusDays(1));
        deliveredService.setVehicle(truckRepo.getById(truck.getId()).get());
        deliveredService.setDriver(driver);
        deliveredService.setDelivered(true);
        deliveredService.setWeightInKilograms(new BigDecimal("6000.00"));
        deliveredService.setLengthInCentimeters(220);
        deliveredService.setWidthInCentimeters(160);
        deliveredService.setHeightInCentimeters(110);
        transportServiceRepo.create(deliveredService);

        List<? extends TransportServiceViewDTO> services = service.getActiveTransportServices(truck.getId(), 0, 10);
        assertTrue(services.isEmpty());
    }

    @Test
    void getTrucksByCompany_NoTrucks_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        List<TruckViewDTO> result = service.getTrucksByCompany(company.getId(), 0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllByTruckType_NoTrucks_ShouldReturnEmptyList() {
        List<TruckViewDTO> result = service.getAllByTruckType("BOX", 0, 10, "registrationPlate", true);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransportServicesForTruck_InvalidPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = createDriver(company);

        TruckViewDTO truck = service.create(new TruckCreateDTO("TX1234TX", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX));
        TransportCargoService serviceEntity = new TransportCargoService();
        serviceEntity.setTransportCompany(company);
        serviceEntity.setClient(client);
        serviceEntity.setDestination(destination);
        serviceEntity.setPrice(new BigDecimal("2000.00"));
        serviceEntity.setStartingDate(LocalDate.now());
        serviceEntity.setEndingDate(LocalDate.now().plusDays(1));
        serviceEntity.setVehicle(truckRepo.getById(truck.getId()).get());
        serviceEntity.setDriver(driver);
        serviceEntity.setWeightInKilograms(new BigDecimal("5000.00"));
        serviceEntity.setLengthInCentimeters(200);
        serviceEntity.setWidthInCentimeters(150);
        serviceEntity.setHeightInCentimeters(100);
        transportServiceRepo.create(serviceEntity);

        List<? extends TransportServiceViewDTO> services = service.getTransportServicesForTruck(truck.getId(), -1, 10);
        assertTrue(services.isEmpty());
    }

    @Test
    void getTransportServicesForTruck_InvalidPageSize_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        TruckViewDTO truck = service.create(new TruckCreateDTO("TX1234TX", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX));
        List<? extends TransportServiceViewDTO> services = service.getTransportServicesForTruck(truck.getId(), 0, 0);
        assertTrue(services.isEmpty());
    }

    @Test
    void getActiveTransportServices_EmptyPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = createDriver(company);

        TruckViewDTO truck = service.create(new TruckCreateDTO("TX1234TX", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX));
        TransportCargoService activeService = new TransportCargoService();
        activeService.setTransportCompany(company);
        activeService.setClient(client);
        activeService.setDestination(destination);
        activeService.setPrice(new BigDecimal("2000.00"));
        activeService.setStartingDate(LocalDate.now());
        activeService.setEndingDate(LocalDate.now().plusDays(1));
        activeService.setVehicle(truckRepo.getById(truck.getId()).get());
        activeService.setDriver(driver);
        activeService.setDelivered(false);
        activeService.setWeightInKilograms(new BigDecimal("5000.00"));
        activeService.setLengthInCentimeters(200);
        activeService.setWidthInCentimeters(150);
        activeService.setHeightInCentimeters(100);
        transportServiceRepo.create(activeService);

        List<? extends TransportServiceViewDTO> services = service.getActiveTransportServices(truck.getId(), 1, 1);
        assertTrue(services.isEmpty());
    }

    @Test
    void getAllByTruckType_InvalidPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        service.create(new TruckCreateDTO("BOX1", company.getId(), 10000.0, 0.0, CargoType.REGULAR, TruckType.BOX));
        List<TruckViewDTO> result = service.getAllByTruckType("BOX", -1, 10, "registrationPlate", true);
        assertTrue(result.isEmpty());
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