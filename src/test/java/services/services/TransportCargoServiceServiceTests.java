package services.services;

import data.models.*;
import data.models.employee.Dispatcher;
import data.models.employee.Driver;
import data.models.employee.Employee;
import data.models.employee.Qualification;
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
import services.data.dto.transportservices.TransportCargoServiceCreateDTO;
import services.data.dto.transportservices.TransportCargoServiceUpdateDTO;
import services.data.dto.transportservices.TransportCargoServiceViewDTO;
import services.data.mapping.mappers.TransportCargoServiceMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TransportCargoServiceServiceTests {
    private SessionFactory sessionFactory;
    private IGenericRepository<TransportCargoService, Long> cargoServiceRepo;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<Client, Long> clientRepo;
    private IGenericRepository<Driver, Long> driverRepo;
    private IGenericRepository<Destination, Long> destinationRepo;
    private IGenericRepository<Vehicle, Long> vehicleRepo;
    private TransportCargoServiceService service;
    private TransportCargoServiceMapper mapper;

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

            cargoServiceRepo = new GenericRepository<>(sessionFactory, TransportCargoService.class);
            companyRepo = new GenericRepository<>(sessionFactory, TransportCompany.class);
            clientRepo = new GenericRepository<>(sessionFactory, Client.class);
            driverRepo = new GenericRepository<>(sessionFactory, Driver.class);
            destinationRepo = new GenericRepository<>(sessionFactory, Destination.class);
            vehicleRepo = new GenericRepository<>(sessionFactory, Vehicle.class); // Changed from Truck to Vehicle

            mapper = new TransportCargoServiceMapper(companyRepo, clientRepo, driverRepo, destinationRepo, vehicleRepo);
            service = new TransportCargoServiceService(cargoServiceRepo, companyRepo, clientRepo, driverRepo, destinationRepo, vehicleRepo);

            // Seed data
            TransportCompany company = new TransportCompany("Fast Transport", "123 Main St");
            companyRepo.create(company);

            Client client = new Client();
            client.setName("Test Client");
            client.setTelephone("1234567890");
            client.setEmail("test@client.com");
            clientRepo.create(client);

            Driver driver = new Driver();
            driver.setFirstName("John");
            driver.setFamilyName("Doe");
            driver.setSalary(new BigDecimal("50000"));
            driver.setTransportCompany(company);
            driverRepo.create(driver);

            Destination destination = new Destination();
            destination.setStartingLocation("City A");
            destination.setEndingLocation("City B");
            destinationRepo.create(destination);

            Truck truck = new Truck();
            truck.setRegistrationPlate("TRK123");
            truck.setTransportCompany(company);
            truck.setMaxCargoCapacityKg(10000.0);
            truck.setCurrentCargoCapacityKg(0.0);
            truck.setCargoType(CargoType.REGULAR);
            truck.setTruckType(TruckType.BOX);
            vehicleRepo.create(truck); // Still create a Truck, but persist via vehicleRepo if needed
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
    void create_ValidDTO_ShouldCreateCargoService() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        List<Vehicle> vehicles = vehicleRepo.getAll(0, 1, null, true); // Fetch Vehicle instead of Truck
        Vehicle truck = vehicles.getFirst();

        TransportCargoServiceCreateDTO dto = new TransportCargoServiceCreateDTO();
        dto.setClientId(client.getId());
        dto.setDestinationId(destination.getId());
        dto.setDriverId(driver.getId());
        dto.setEndingDate(LocalDate.now().plusDays(2));
        dto.setPrice(new BigDecimal("2000"));
        dto.setStartingDate(LocalDate.now());
        dto.setTransportCompanyId(company.getId());
        dto.setVehicleId(truck.getId());
        dto.setWeightInKilograms(new BigDecimal("5000"));
        dto.setLengthInCentimeters(200);
        dto.setWidthInCentimeters(150);
        dto.setHeightInCentimeters(100);
        dto.setDescription("Test cargo");

        TransportCargoServiceViewDTO result = service.create(dto);

        assertNotNull(result.getId());
        assertEquals(company.getId(), result.getTransportCompanyId());
        assertEquals(client.getId(), result.getClientId());
        assertEquals(driver.getId(), result.getDriverId());
        assertEquals(destination.getId(), result.getDestinationId());
        assertEquals(truck.getId(), result.getVehicleId());
        assertEquals(new BigDecimal("2000"), result.getPrice());
        assertEquals(new BigDecimal("5000"), result.getWeightInKilograms());
        assertEquals(200, result.getLengthInCentimeters());
        assertEquals("Test cargo", result.getDescription());
    }

    @Test
    void update_FullUpdate_ShouldUpdateCargoService() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        List<Vehicle> vehicles = vehicleRepo.getAll(0, 1, null, true);
        Vehicle truck = vehicles.getFirst();

        TransportCargoServiceCreateDTO createDto = new TransportCargoServiceCreateDTO();
        createDto.setClientId(client.getId());
        createDto.setDestinationId(destination.getId());
        createDto.setDriverId(driver.getId());
        createDto.setEndingDate(LocalDate.now().plusDays(2));
        createDto.setPrice(new BigDecimal("2000"));
        createDto.setStartingDate(LocalDate.now());
        createDto.setTransportCompanyId(company.getId());
        createDto.setVehicleId(truck.getId());
        createDto.setWeightInKilograms(new BigDecimal("5000"));
        createDto.setLengthInCentimeters(200);
        createDto.setWidthInCentimeters(150);
        createDto.setHeightInCentimeters(100);
        createDto.setDescription("Initial cargo");
        TransportCargoServiceViewDTO created = service.create(createDto);

        TransportCargoServiceUpdateDTO updateDto = new TransportCargoServiceUpdateDTO();
        updateDto.setId(created.getId());
        updateDto.setTransportCompanyId(company.getId());
        updateDto.setStartingDate(LocalDate.now().plusDays(1));
        updateDto.setEndingDate(LocalDate.now().plusDays(3));
        updateDto.setDestinationId(destination.getId());
        updateDto.setClientId(client.getId());
        updateDto.setPrice(new BigDecimal("2500"));
        updateDto.setVehicleId(truck.getId());
        updateDto.setDriverId(driver.getId());
        updateDto.setWeightInKilograms(new BigDecimal("6000"));
        updateDto.setLengthInCentimeters(220);
        updateDto.setWidthInCentimeters(160);
        updateDto.setHeightInCentimeters(110);
        updateDto.setDescription("Updated cargo");

        TransportCargoServiceViewDTO result = service.update(updateDto);

        assertEquals(new BigDecimal("2500"), result.getPrice());
        assertEquals(new BigDecimal("6000"), result.getWeightInKilograms());
        assertEquals(220, result.getLengthInCentimeters());
        assertEquals("Updated cargo", result.getDescription());
        assertFalse(result.isDelivered());
    }

    @Test
    void delete_ExistingId_ShouldDeleteCargoService() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        List<Vehicle> vehicles = vehicleRepo.getAll(0, 1, null, true);
        Vehicle truck = vehicles.getFirst();

        TransportCargoServiceCreateDTO dto = new TransportCargoServiceCreateDTO();
        dto.setClientId(client.getId());
        dto.setDestinationId(destination.getId());
        dto.setDriverId(driver.getId());
        dto.setEndingDate(LocalDate.now().plusDays(2));
        dto.setPrice(new BigDecimal("2000"));
        dto.setStartingDate(LocalDate.now());
        dto.setTransportCompanyId(company.getId());
        dto.setVehicleId(truck.getId());
        dto.setWeightInKilograms(new BigDecimal("5000"));
        dto.setLengthInCentimeters(200);
        dto.setWidthInCentimeters(150);
        dto.setHeightInCentimeters(100);
        dto.setDescription("Temp cargo");
        TransportCargoServiceViewDTO created = service.create(dto);

        service.delete(created.getId());
        TransportCargoServiceViewDTO result = service.getById(created.getId());
        assertNull(result);
    }

    @Test
    void getById_ExistingId_ShouldReturnCargoService() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        List<Vehicle> vehicles = vehicleRepo.getAll(0, 1, null, true);
        Vehicle truck = vehicles.getFirst();

        TransportCargoServiceCreateDTO dto = new TransportCargoServiceCreateDTO();
        dto.setClientId(client.getId());
        dto.setDestinationId(destination.getId());
        dto.setDriverId(driver.getId());
        dto.setEndingDate(LocalDate.now().plusDays(2));
        dto.setPrice(new BigDecimal("2000"));
        dto.setStartingDate(LocalDate.now());
        dto.setTransportCompanyId(company.getId());
        dto.setVehicleId(truck.getId());
        dto.setWeightInKilograms(new BigDecimal("5000"));
        dto.setLengthInCentimeters(200);
        dto.setWidthInCentimeters(150);
        dto.setHeightInCentimeters(100);
        dto.setDescription("Test cargo");
        TransportCargoServiceViewDTO created = service.create(dto);

        TransportCargoServiceViewDTO result = service.getById(created.getId());
        assertNotNull(result);
        assertEquals("Test cargo", result.getDescription());
        assertEquals(new BigDecimal("5000.00"), result.getWeightInKilograms());
    }

    @Test
    void getAll_MultipleServices_ShouldReturnPaginatedList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        List<Vehicle> vehicles = vehicleRepo.getAll(0, 1, null, true);
        Vehicle truck = vehicles.getFirst();

        TransportCargoServiceCreateDTO dto1 = new TransportCargoServiceCreateDTO();
        dto1.setClientId(client.getId());
        dto1.setDestinationId(destination.getId());
        dto1.setDriverId(driver.getId());
        dto1.setEndingDate(LocalDate.now().plusDays(2));
        dto1.setPrice(new BigDecimal("2000"));
        dto1.setStartingDate(LocalDate.now());
        dto1.setTransportCompanyId(company.getId());
        dto1.setVehicleId(truck.getId());
        dto1.setWeightInKilograms(new BigDecimal("5000"));
        dto1.setLengthInCentimeters(200);
        dto1.setWidthInCentimeters(150);
        dto1.setHeightInCentimeters(100);
        dto1.setDescription("Cargo 1");
        service.create(dto1);

        TransportCargoServiceCreateDTO dto2 = new TransportCargoServiceCreateDTO();
        dto2.setClientId(client.getId());
        dto2.setDestinationId(destination.getId());
        dto2.setDriverId(driver.getId());
        dto2.setEndingDate(LocalDate.now().plusDays(3));
        dto2.setPrice(new BigDecimal("2500"));
        dto2.setStartingDate(LocalDate.now().plusDays(1));
        dto2.setTransportCompanyId(company.getId());
        dto2.setVehicleId(truck.getId());
        dto2.setWeightInKilograms(new BigDecimal("6000"));
        dto2.setLengthInCentimeters(220);
        dto2.setWidthInCentimeters(160);
        dto2.setHeightInCentimeters(110);
        dto2.setDescription("Cargo 2");
        service.create(dto2);

        List<TransportCargoServiceViewDTO> result = service.getAll(0, 2, "startingDate", true);
        assertEquals(2, result.size());
        assertEquals("Cargo 1", result.get(0).getDescription());
        assertEquals("Cargo 2", result.get(1).getDescription());
    }

    @Test
    void getByCompany_ValidCompanyId_ShouldReturnServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        List<Vehicle> vehicles = vehicleRepo.getAll(0, 1, null, true);
        Vehicle truck = vehicles.getFirst();

        TransportCargoServiceCreateDTO dto = new TransportCargoServiceCreateDTO();
        dto.setClientId(client.getId());
        dto.setDestinationId(destination.getId());
        dto.setDriverId(driver.getId());
        dto.setEndingDate(LocalDate.now().plusDays(2));
        dto.setPrice(new BigDecimal("2000"));
        dto.setStartingDate(LocalDate.now());
        dto.setTransportCompanyId(company.getId());
        dto.setVehicleId(truck.getId());
        dto.setWeightInKilograms(new BigDecimal("5000"));
        dto.setLengthInCentimeters(200);
        dto.setWidthInCentimeters(150);
        dto.setHeightInCentimeters(100);
        dto.setDescription("Company cargo");
        service.create(dto);

        List<TransportCargoServiceViewDTO> result = service.getByCompany(company.getId(), 0, 10);
        assertEquals(1, result.size());
        assertEquals(company.getId(), result.getFirst().getTransportCompanyId());
    }

    @Test
    void getByClient_ValidClientId_ShouldReturnServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        List<Vehicle> vehicles = vehicleRepo.getAll(0, 1, null, true);
        Vehicle truck = vehicles.getFirst();

        TransportCargoServiceCreateDTO dto = new TransportCargoServiceCreateDTO();
        dto.setClientId(client.getId());
        dto.setDestinationId(destination.getId());
        dto.setDriverId(driver.getId());
        dto.setEndingDate(LocalDate.now().plusDays(2));
        dto.setPrice(new BigDecimal("2000"));
        dto.setStartingDate(LocalDate.now());
        dto.setTransportCompanyId(company.getId());
        dto.setVehicleId(truck.getId());
        dto.setWeightInKilograms(new BigDecimal("5000"));
        dto.setLengthInCentimeters(200);
        dto.setWidthInCentimeters(150);
        dto.setHeightInCentimeters(100);
        dto.setDescription("Client cargo");
        service.create(dto);

        List<TransportCargoServiceViewDTO> result = service.getByClient(client.getId(), 0, 10);
        assertEquals(1, result.size());
        assertEquals(client.getId(), result.getFirst().getClientId());
    }

    @Test
    void getByDriver_ValidDriverId_ShouldReturnServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        List<Vehicle> vehicles = vehicleRepo.getAll(0, 1, null, true);
        Vehicle truck = vehicles.getFirst();

        TransportCargoServiceCreateDTO dto = new TransportCargoServiceCreateDTO();
        dto.setClientId(client.getId());
        dto.setDestinationId(destination.getId());
        dto.setDriverId(driver.getId());
        dto.setEndingDate(LocalDate.now().plusDays(2));
        dto.setPrice(new BigDecimal("2000"));
        dto.setStartingDate(LocalDate.now());
        dto.setTransportCompanyId(company.getId());
        dto.setVehicleId(truck.getId());
        dto.setWeightInKilograms(new BigDecimal("5000"));
        dto.setLengthInCentimeters(200);
        dto.setWidthInCentimeters(150);
        dto.setHeightInCentimeters(100);
        dto.setDescription("Driver cargo");
        service.create(dto);

        List<TransportCargoServiceViewDTO> result = service.getByDriver(driver.getId(), 0, 10);
        assertEquals(1, result.size());
        assertEquals(driver.getId(), result.getFirst().getDriverId());
    }

    @Test
    void getActiveServices_WithActiveService_ShouldReturnActiveServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        List<Vehicle> vehicles = vehicleRepo.getAll(0, 1, null, true);
        Vehicle truck = vehicles.getFirst();

        TransportCargoServiceCreateDTO dto = new TransportCargoServiceCreateDTO();
        dto.setClientId(client.getId());
        dto.setDestinationId(destination.getId());
        dto.setDriverId(driver.getId());
        dto.setEndingDate(LocalDate.now().plusDays(2));
        dto.setPrice(new BigDecimal("2000"));
        dto.setStartingDate(LocalDate.now());
        dto.setTransportCompanyId(company.getId());
        dto.setVehicleId(truck.getId());
        dto.setWeightInKilograms(new BigDecimal("5000"));
        dto.setLengthInCentimeters(200);
        dto.setWidthInCentimeters(150);
        dto.setHeightInCentimeters(100);
        dto.setDescription("Active cargo");
        service.create(dto);

        TransportCargoService deliveredService = new TransportCargoService();
        deliveredService.setTransportCompany(company);
        deliveredService.setClient(client);
        deliveredService.setDriver(driver);
        deliveredService.setDestination(destination);
        deliveredService.setVehicle(truck);
        deliveredService.setPrice(new BigDecimal("2500"));
        deliveredService.setStartingDate(LocalDate.now());
        deliveredService.setEndingDate(LocalDate.now().plusDays(1));
        deliveredService.setDelivered(true);
        deliveredService.setWeightInKilograms(new BigDecimal("6000"));
        deliveredService.setLengthInCentimeters(220);
        deliveredService.setWidthInCentimeters(160);
        deliveredService.setHeightInCentimeters(110);
        cargoServiceRepo.create(deliveredService);

        List<TransportCargoServiceViewDTO> result = service.getActiveServices(0, 10);
        assertEquals(1, result.size());
        assertEquals("Active cargo", result.getFirst().getDescription());
        assertFalse(result.getFirst().isDelivered());
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
        TransportCargoServiceUpdateDTO dto = new TransportCargoServiceUpdateDTO();
        assertThrows(IllegalArgumentException.class, () -> service.update(dto));
    }

    @Test
    void update_NonExistentId_ShouldThrowRepositoryException() {
        TransportCargoServiceUpdateDTO dto = new TransportCargoServiceUpdateDTO();
        dto.setId(999L);
        dto.setTransportCompanyId(1L);
        dto.setStartingDate(LocalDate.now());
        dto.setEndingDate(LocalDate.now().plusDays(1));
        dto.setDestinationId(1L);
        dto.setClientId(1L);
        dto.setPrice(new BigDecimal("2000"));
        dto.setVehicleId(1L);
        dto.setDriverId(1L);
        dto.setWeightInKilograms(new BigDecimal("5000"));
        dto.setLengthInCentimeters(200);
        dto.setWidthInCentimeters(150);
        dto.setHeightInCentimeters(100);
        dto.setDescription("Non-existent");
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
    void getByCompany_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getByCompany(null, 0, 10));
    }

    @Test
    void getByClient_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getByClient(null, 0, 10));
    }

    @Test
    void getByDriver_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getByDriver(null, 0, 10));
    }

    @Test
    void getAll_InvalidOrderBy_ShouldThrowRepositoryException() {
        assertThrows(RepositoryException.class, () -> service.getAll(0, 10, "invalidField", true));
    }

    // Edge Cases
    @Test
    void getById_NonExistentId_ShouldReturnNull() {
        TransportCargoServiceViewDTO result = service.getById(999L);
        assertNull(result);
    }

    @Test
    void getAll_NoServices_ShouldReturnEmptyList() {
        List<TransportCargoServiceViewDTO> result = service.getAll(0, 10, "startingDate", true);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_InvalidPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        List<Vehicle> vehicles = vehicleRepo.getAll(0, 1, null, true);
        Vehicle truck = vehicles.getFirst();

        TransportCargoServiceCreateDTO dto = new TransportCargoServiceCreateDTO();
        dto.setClientId(client.getId());
        dto.setDestinationId(destination.getId());
        dto.setDriverId(driver.getId());
        dto.setEndingDate(LocalDate.now().plusDays(2));
        dto.setPrice(new BigDecimal("2000"));
        dto.setStartingDate(LocalDate.now());
        dto.setTransportCompanyId(company.getId());
        dto.setVehicleId(truck.getId());
        dto.setWeightInKilograms(new BigDecimal("5000"));
        dto.setLengthInCentimeters(200);
        dto.setWidthInCentimeters(150);
        dto.setHeightInCentimeters(100);
        dto.setDescription("Cargo");
        service.create(dto);

        List<TransportCargoServiceViewDTO> result = service.getAll(1, 1, "startingDate", true);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByCompany_NoServices_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        List<TransportCargoServiceViewDTO> result = service.getByCompany(company.getId(), 0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByClient_NoServices_ShouldReturnEmptyList() {
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        List<TransportCargoServiceViewDTO> result = service.getByClient(client.getId(), 0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByDriver_NoServices_ShouldReturnEmptyList() {
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        List<TransportCargoServiceViewDTO> result = service.getByDriver(driver.getId(), 0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void getActiveServices_NoActiveServices_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Driver driver = driverRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        List<Vehicle> vehicles = vehicleRepo.getAll(0, 1, null, true);
        Vehicle truck = vehicles.getFirst();

        TransportCargoService deliveredService = new TransportCargoService();
        deliveredService.setTransportCompany(company);
        deliveredService.setClient(client);
        deliveredService.setDriver(driver);
        deliveredService.setDestination(destination);
        deliveredService.setVehicle(truck);
        deliveredService.setPrice(new BigDecimal("2000"));
        deliveredService.setStartingDate(LocalDate.now());
        deliveredService.setEndingDate(LocalDate.now().plusDays(1));
        deliveredService.setDelivered(true);
        deliveredService.setWeightInKilograms(new BigDecimal("5000"));
        deliveredService.setLengthInCentimeters(200);
        deliveredService.setWidthInCentimeters(150);
        deliveredService.setHeightInCentimeters(100);
        cargoServiceRepo.create(deliveredService);

        List<TransportCargoServiceViewDTO> result = service.getActiveServices(0, 10);
        assertTrue(result.isEmpty());
    }


    @Test
    void getByCompany_InvalidPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        List<TransportCargoServiceViewDTO> result = service.getByCompany(company.getId(), -1, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByClient_InvalidSize_ShouldReturnEmptyList() {
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        List<TransportCargoServiceViewDTO> result = service.getByClient(client.getId(), 0, 0);
        assertTrue(result.isEmpty());
    }
}