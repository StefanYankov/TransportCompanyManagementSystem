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
import services.data.dto.employees.DriverCreateDTO;
import services.data.dto.employees.DriverUpdateDTO;
import services.data.dto.employees.DriverViewDTO;
import services.data.dto.transportservices.TransportCargoServiceViewDTO;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;
import services.data.mapping.mappers.DriverMapper;
import services.data.mapping.mappers.TransportCargoServiceMapper;
import services.data.mapping.mappers.TransportPassengersServiceMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DriverServiceTests {
    private SessionFactory sessionFactory;
    private IGenericRepository<Driver, Long> driverRepo;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<Dispatcher, Long> dispatcherRepo;
    private IGenericRepository<TransportCargoService, Long> cargoRepo;
    private IGenericRepository<TransportPassengersService, Long> passengersRepo;
    private IGenericRepository<Qualification, Long> qualificationRepo;
    private IGenericRepository<Client, Long> clientRepo;
    private IGenericRepository<Destination, Long> destinationRepo;
    private IGenericRepository<Vehicle, Long> vehicleRepo;
    private IGenericRepository<Truck, Long> truckRepo;
    private IGenericRepository<Van, Long> vanRepo;
    private DriverService driverService;
    private DriverMapper driverMapper;
    private TransportCargoServiceMapper cargoServiceMapper;
    private TransportPassengersServiceMapper passengersServiceMapper;

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

            driverRepo = new GenericRepository<>(sessionFactory, Driver.class);
            companyRepo = new GenericRepository<>(sessionFactory, TransportCompany.class);
            dispatcherRepo = new GenericRepository<>(sessionFactory, Dispatcher.class);
            cargoRepo = new GenericRepository<>(sessionFactory, TransportCargoService.class);
            passengersRepo = new GenericRepository<>(sessionFactory, TransportPassengersService.class);
            qualificationRepo = new GenericRepository<>(sessionFactory, Qualification.class);
            clientRepo = new GenericRepository<>(sessionFactory, Client.class);
            destinationRepo = new GenericRepository<>(sessionFactory, Destination.class);
            vehicleRepo = new GenericRepository<>(sessionFactory, Vehicle.class);
            truckRepo = new GenericRepository<>(sessionFactory, Truck.class);
            vanRepo = new GenericRepository<>(sessionFactory, Van.class);

            driverMapper = new DriverMapper(companyRepo, dispatcherRepo, qualificationRepo);
            cargoServiceMapper = new TransportCargoServiceMapper(companyRepo,clientRepo,driverRepo,destinationRepo,vehicleRepo);
            passengersServiceMapper = new TransportPassengersServiceMapper();
            driverService = new DriverService(driverRepo, companyRepo, dispatcherRepo, cargoRepo, passengersRepo, qualificationRepo, driverMapper, cargoServiceMapper, passengersServiceMapper);

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

            Client client = new Client();
            client.setName("Test Client");
            client.setTelephone("1234567890");
            client.setEmail("test@client.com");
            clientRepo.create(client);

            Destination destination = new Destination();
            destination.setStartingLocation("City A");
            destination.setEndingLocation("City B");
            destinationRepo.create(destination);
        } catch (Exception e) {
            fail("Failed to initialize SessionFactory: " + e.getMessage());
        }
    }


    @AfterEach
    void TearDown() {
        if (sessionFactory != null) sessionFactory.close();
    }

    @Test
    void getDriversByQualification_ValidQualification_ShouldReturnDrivers() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Qualification existingQualification = qualificationRepo.getAll(0, 1, null, true).getFirst();
        DriverCreateDTO dto = new DriverCreateDTO("Alice", "Smith", new BigDecimal("50000"), company.getId(), null, Set.of(existingQualification.getId()));
        driverService.create(dto);

        List<DriverViewDTO> result = driverService.getDriversByQualification("Heavy Duty License");
        assertEquals(1, result.size());
        assertEquals("Smith", result.getFirst().getFamilyName());
        assertTrue(result.getFirst().getQualificationIds().contains(existingQualification.getId()));
    }

    @Test
    void getDriversByQualification_MultipleDrivers_ShouldReturnAllMatching() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Qualification qualification = qualificationRepo.getAll(0, 1, null, true).getFirst();
        driverService.create(new DriverCreateDTO("Jane", "Smith", new BigDecimal("60000"), company.getId(), null, Set.of(qualification.getId())));
        driverService.create(new DriverCreateDTO("John", "Doe", new BigDecimal("65000"), company.getId(), null, Set.of(qualification.getId())));

        List<DriverViewDTO> result = driverService.getDriversByQualification("Heavy Duty License");
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(d -> "Smith".equals(d.getFamilyName())));
        assertTrue(result.stream().anyMatch(d -> "Doe".equals(d.getFamilyName())));
    }

    @Test
    void getDriversByQualification_InvalidQualification_ShouldReturnEmptyList() {
        List<DriverViewDTO> result = driverService.getDriversByQualification("Invalid License");
        assertTrue(result.isEmpty());
    }

    @Test
    void getDriversSortedBySalary_Ascending_ShouldReturnSortedList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        driverService.create(new DriverCreateDTO("Hank", "Red", new BigDecimal("70000"), company.getId(), null, Set.of()));
        driverService.create(new DriverCreateDTO("Ivy", "Yellow", new BigDecimal("60000"), company.getId(), null, Set.of()));

        List<DriverViewDTO> result = driverService.getDriversSortedBySalary(true, "qualifications");
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("60000.00"), result.get(0).getSalary()); // Precision adjusted
        assertEquals(new BigDecimal("70000.00"), result.get(1).getSalary()); // Precision adjusted
    }

    @Test
    void getDriversSortedBySalary_NoDrivers_ShouldReturnEmptyList() {
        List<Driver> existing = driverRepo.getAll(0, Integer.MAX_VALUE, null, true);
        existing.forEach(driverRepo::delete);

        List<DriverViewDTO> result = driverService.getDriversSortedBySalary(true, "qualifications");
        assertTrue(result.isEmpty());
    }

    @Test
    void getRevenueByDriver_DriverWithServices_ShouldReturnTotalRevenue() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        DriverCreateDTO dto = new DriverCreateDTO("Test", "Driver", new BigDecimal("50000"), company.getId(), null, Set.of());
        DriverViewDTO created = driverService.create(dto);

        Driver driver = driverRepo.getById(created.getId()).get();
        TransportCargoService cargoService = new TransportCargoService();
        cargoService.setTransportCompany(company);
        cargoService.setDriver(driver);
        cargoService.setPrice(new BigDecimal("2000"));
        cargoService.setStartingDate(LocalDate.now());
        cargoService.setWeightInKilograms(BigDecimal.valueOf(25));
        cargoService.setLengthInCentimeters(50);
        cargoService.setWidthInCentimeters(25);
        cargoService.setHeightInCentimeters(25);
        cargoRepo.create(cargoService);

        TransportPassengersService passengerService = new TransportPassengersService();
        passengerService.setTransportCompany(company);
        passengerService.setDriver(driver);
        passengerService.setPrice(new BigDecimal("1500"));
        passengerService.setStartingDate(LocalDate.now());
        passengerService.setNumberOfPassengers(20);
        passengersRepo.create(passengerService);

        BigDecimal revenue = driverService.getRevenueByDriver(created.getId());
        assertEquals(new BigDecimal("3500.00"), revenue);
    }

    @Test
    void getRevenueByDriver_NonExistentId_ShouldReturnZero() {
        BigDecimal revenue = driverService.getRevenueByDriver(999L);
        assertEquals(BigDecimal.ZERO, revenue);
    }

    @Test
    void getAll_MultipleDrivers_ShouldReturnPaginatedList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        driverService.create(new DriverCreateDTO("Alice", "Smith", new BigDecimal("50000"), company.getId(), null, Set.of()));
        driverService.create(new DriverCreateDTO("Bob", "Jones", new BigDecimal("60000"), company.getId(), null, Set.of()));
        driverService.create(new DriverCreateDTO("Charlie", "Brown", new BigDecimal("55000"), company.getId(), null, Set.of()));

        List<DriverViewDTO> result = driverService.getAll(0, 2, "familyName", true, "qualifications");
        assertEquals(2, result.size());
        assertEquals("Brown", result.get(0).getFamilyName());
        assertEquals("Jones", result.get(1).getFamilyName());
    }

    @Test
    void getDriversByDispatcher_ValidDispatcherId_ShouldReturnDrivers() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Dispatcher dispatcher = dispatcherRepo.getAll(0, 1, null, true).getFirst();
        DriverCreateDTO dto = new DriverCreateDTO("Jane", "Doe", new BigDecimal("50000"), company.getId(), dispatcher.getId(), Set.of());
        DriverViewDTO created = driverService.create(dto);

        List<DriverViewDTO> result = driverService.getDriversByDispatcher(dispatcher.getId());
        assertEquals(1, result.size());
        assertEquals("Doe", result.getFirst().getFamilyName());
    }

    @Test
    void getDriversByCompany_ValidCompanyId_ShouldReturnDrivers() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        driverService.create(new DriverCreateDTO("Alice", "Smith", new BigDecimal("50000"), company.getId(), null, Set.of()));
        driverService.create(new DriverCreateDTO("Bob", "Jones", new BigDecimal("60000"), company.getId(), null, Set.of()));

        List<DriverViewDTO> result = driverService.getDriversByCompany(company.getId(), 0, 10, "familyName", true, "qualifications");
        assertEquals(2, result.size());
        assertEquals("Jones", result.get(0).getFamilyName());
        assertEquals("Smith", result.get(1).getFamilyName());
    }

    @Test
    void create_NullDTO_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> driverService.create(null));
    }

    @Test
    void update_FullUpdate_ShouldUpdateAllFields() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Dispatcher dispatcher = dispatcherRepo.getAll(0, 1, null, true).getFirst();
        Qualification qualification = qualificationRepo.getAll(0, 1, null, true).getFirst();
        DriverCreateDTO createDto = new DriverCreateDTO("Initial", "Driver", new BigDecimal("50000"), company.getId(), null, Set.of(qualification.getId()));
        DriverViewDTO created = driverService.create(createDto);

        DriverUpdateDTO updateDto = new DriverUpdateDTO(created.getId(), "Updated", "Name", new BigDecimal("55000"), company.getId(), dispatcher.getId(), Set.of(qualification.getId()));
        DriverViewDTO result = driverService.update(updateDto);

        assertEquals("Updated", result.getFirstName());
        assertEquals("Name", result.getFamilyName());
        assertEquals(new BigDecimal("55000"), result.getSalary());
        assertEquals(company.getId(), result.getTransportCompanyId());
        assertEquals(dispatcher.getId(), result.getDispatcherId());
        assertEquals(1, result.getQualificationIds().size());
    }

    @Test
    void update_PartialUpdate_ShouldPreserveUnchangedFields() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Qualification qualification = qualificationRepo.getAll(0, 1, null, true).getFirst();
        DriverCreateDTO createDto = new DriverCreateDTO("Initial", "Driver", new BigDecimal("50000"), company.getId(), null, Set.of(qualification.getId()));
        DriverViewDTO created = driverService.create(createDto);

        DriverUpdateDTO updateDto = new DriverUpdateDTO(created.getId(), "Updated", null, null, null, null, null);
        DriverViewDTO result = driverService.update(updateDto);

        assertEquals("Updated", result.getFirstName());
        assertEquals("Driver", result.getFamilyName());
        assertEquals(new BigDecimal("50000.00"), result.getSalary());
        assertEquals(company.getId(), result.getTransportCompanyId());
        assertNull(result.getDispatcherId());
        assertEquals(1, result.getQualificationIds().size());
    }

    @Test
    void update_NullDTO_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> driverService.update(null));
    }

    @Test
    void delete_ExistingId_ShouldDeleteDriver() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        DriverCreateDTO dto = new DriverCreateDTO("Delete", "Me", new BigDecimal("50000"), company.getId(), null, Set.of());
        DriverViewDTO created = driverService.create(dto);

        driverService.delete(created.getId());
        DriverViewDTO result = driverService.getById(created.getId(), "qualifications");
        assertNull(result);
    }

    @Test
    void delete_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> driverService.delete(null));
    }

    @Test
    void delete_NonExistentId_ShouldThrowRepositoryException() {
        assertThrows(RepositoryException.class, () -> driverService.delete(999L));
    }

    @Test
    void getById_ExistingId_ShouldReturnDriver() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Qualification qualification = qualificationRepo.getAll(0, 1, null, true).getFirst();
        DriverCreateDTO dto = new DriverCreateDTO("Alice", "Smith", new BigDecimal("50000"), company.getId(), null, Set.of(qualification.getId()));
        DriverViewDTO created = driverService.create(dto);

        DriverViewDTO result = driverService.getById(created.getId(), "qualifications");
        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        assertEquals("Smith", result.getFamilyName());
        assertEquals(1, result.getQualificationIds().size());
    }

    @Test
    void getById_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> driverService.getById(null, "qualifications"));
    }

    @Test
    void getById_NonExistentId_ShouldReturnNull() {
        DriverViewDTO result = driverService.getById(999L, "qualifications");
        assertNull(result);
    }

    @Test
    void getAll_ValidPagination_ShouldReturnDrivers() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        driverService.create(new DriverCreateDTO("Alice", "Smith", new BigDecimal("50000"), company.getId(), null, Set.of()));
        driverService.create(new DriverCreateDTO("Bob", "Jones", new BigDecimal("60000"), company.getId(), null, Set.of()));

        List<DriverViewDTO> result = driverService.getAll(0, 2, "familyName", true, "qualifications");
        assertEquals(2, result.size());
        assertEquals("Jones", result.get(0).getFamilyName());
        assertEquals("Smith", result.get(1).getFamilyName());
    }

    @Test
    void getAll_MaxIntSize_ShouldReturnAllDrivers() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        driverService.create(new DriverCreateDTO("Alice", "Smith", new BigDecimal("50000"), company.getId(), null, Set.of()));
        driverService.create(new DriverCreateDTO("Bob", "Jones", new BigDecimal("60000"), company.getId(), null, Set.of()));

        List<DriverViewDTO> result = driverService.getAll(0, Integer.MAX_VALUE, "familyName", true, "qualifications");
        assertEquals(2, result.size());
        assertEquals("Jones", result.get(0).getFamilyName());
        assertEquals("Smith", result.get(1).getFamilyName());
    }

    @Test
    void getAll_InvalidPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        driverService.create(new DriverCreateDTO("Alice", "Smith", new BigDecimal("50000"), company.getId(), null, Set.of()));

        List<DriverViewDTO> result = driverService.getAll(1, 1, "familyName", true, "qualifications");
        assertTrue(result.isEmpty());
    }

    @Test
    void getDriverTransportCounts_DriverWithServices_ShouldReturnCounts() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        DriverCreateDTO dto = new DriverCreateDTO("Bob", "Jones", new BigDecimal("60000"), company.getId(), null, Set.of());
        DriverViewDTO created = driverService.create(dto);

        Driver driver = driverRepo.getById(created.getId()).get();
        TransportCargoService cargoService = new TransportCargoService();
        cargoService.setTransportCompany(company);
        cargoService.setDriver(driver);
        cargoService.setPrice(new BigDecimal("2000"));
        cargoService.setStartingDate(LocalDate.now());
        cargoService.setWeightInKilograms(BigDecimal.valueOf(25));
        cargoService.setLengthInCentimeters(50);
        cargoService.setWidthInCentimeters(25);
        cargoService.setHeightInCentimeters(25);
        cargoRepo.create(cargoService);

        Map<Long, Integer> counts = driverService.getDriverTransportCounts();
        assertTrue(counts.containsKey(created.getId()));
        assertEquals(1, counts.get(created.getId()));
    }

    @Test
    void getDriverTransportCounts_NoServices_ShouldReturnZeroCount() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        DriverCreateDTO dto = new DriverCreateDTO("Bob", "Jones", new BigDecimal("60000"), company.getId(), null, Set.of());
        DriverViewDTO created = driverService.create(dto);

        Map<Long, Integer> counts = driverService.getDriverTransportCounts();
        assertTrue(counts.containsKey(created.getId()));
        assertEquals(0, counts.get(created.getId()));
    }

    @Test
    void getDriverTripCounts_DriverWithServices_ShouldReturnCorrectCount() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        DriverCreateDTO dto = new DriverCreateDTO("Test", "Driver", new BigDecimal("50000"), company.getId(), null, Set.of());
        DriverViewDTO created = driverService.create(dto);

        TransportCargoService service = new TransportCargoService();
        service.setTransportCompany(company);
        service.setDriver(driverRepo.getById(created.getId()).get());
        service.setPrice(new BigDecimal("2000"));
        service.setStartingDate(LocalDate.now());
        service.setWeightInKilograms(BigDecimal.valueOf(25));
        service.setLengthInCentimeters(50);
        service.setWidthInCentimeters(25);
        service.setHeightInCentimeters(25);
        cargoRepo.create(service);

        Map<Long, Integer> counts = driverService.getDriverTripCounts(true, true, 0, 10);
        assertTrue(counts.containsKey(created.getId()));
        assertEquals(1, counts.get(created.getId()));
    }

    @Test
    void getDriverTripCounts_NoDrivers_ShouldReturnEmptyMap() {
        List<Driver> existing = driverRepo.getAll(0, Integer.MAX_VALUE, null, true);
        existing.forEach(driverRepo::delete);

        Map<Long, Integer> counts = driverService.getDriverTripCounts(true, true, 0, 10);
        assertTrue(counts.isEmpty());
    }

    @Test
    void getTransportServicesForDriver_ValidDriverId_ShouldReturnMixedServices() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        Client client = clientRepo.getAll(0, 1, null, true).getFirst();
        Destination destination = destinationRepo.getAll(0, 1, null, true).getFirst();
        DriverCreateDTO dto = new DriverCreateDTO("Test", "Driver", new BigDecimal("50000"), company.getId(), null, Set.of());
        DriverViewDTO created = driverService.create(dto);

        Driver driver = driverRepo.getById(created.getId()).get();

        Truck truck = new Truck();
        truck.setRegistrationPlate("TRK123");
        truck.setTransportCompany(company);
        truck.setMaxCargoCapacityKg(10000.0);
        truck.setCurrentCargoCapacityKg(0.0);
        truck.setCargoType(CargoType.REGULAR);
        truck.setTruckType(TruckType.BOX);
        truckRepo.create(truck);

        TransportCargoService cargoService = new TransportCargoService();
        cargoService.setTransportCompany(company);
        cargoService.setDriver(driver);
        cargoService.setVehicle(truck);
        cargoService.setClient(client);
        cargoService.setDestination(destination);
        cargoService.setPrice(new BigDecimal("2000"));
        cargoService.setStartingDate(LocalDate.now());
        cargoService.setEndingDate(LocalDate.now().plusDays(1));
        cargoService.setWeightInKilograms(BigDecimal.valueOf(5000));
        cargoService.setLengthInCentimeters(200);
        cargoService.setWidthInCentimeters(150);
        cargoService.setHeightInCentimeters(100);
        cargoRepo.create(cargoService);

        Van van = new Van();
        van.setRegistrationPlate("VAN123");
        van.setTransportCompany(company);
        van.setMaxPassengerCapacity(8);
        van.setHasPassengerOverheadStorage(true);
        vanRepo.create(van);

        TransportPassengersService passengerService = new TransportPassengersService();
        passengerService.setTransportCompany(company);
        passengerService.setDriver(driver);
        passengerService.setVehicle(van);
        passengerService.setClient(client);
        passengerService.setDestination(destination);
        passengerService.setPrice(new BigDecimal("1500"));
        passengerService.setStartingDate(LocalDate.now().plusDays(2));
        passengerService.setEndingDate(LocalDate.now().plusDays(3));
        passengerService.setNumberOfPassengers(6);
        passengersRepo.create(passengerService);

        List<Object> services = driverService.getTransportServicesForDriver(created.getId(), 0, 10);
        assertEquals(2, services.size());

        Object firstService = services.getFirst();
        assertInstanceOf(TransportCargoServiceViewDTO.class, firstService);
        TransportCargoServiceViewDTO cargoDto = (TransportCargoServiceViewDTO) firstService;
        assertEquals(new BigDecimal("2000.00"), cargoDto.getPrice());
        assertEquals(truck.getId(), cargoDto.getVehicleId());
        assertEquals(5000.0, cargoDto.getWeightInKilograms().doubleValue(), 0.01);

        Object secondService = services.get(1);
        assertInstanceOf(TransportPassengersServiceViewDTO.class, secondService);
        TransportPassengersServiceViewDTO passengerDto = (TransportPassengersServiceViewDTO) secondService;
        assertEquals(new BigDecimal("1500.00"), passengerDto.getPrice());
        assertEquals(van.getId(), passengerDto.getVehicleId());
        assertEquals(6, passengerDto.getNumberOfPassengers());
    }

    @Test
    void getTransportServicesForDriver_NoServices_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        DriverCreateDTO dto = new DriverCreateDTO("Test", "Driver", new BigDecimal("50000"), company.getId(), null, Set.of());
        DriverViewDTO created = driverService.create(dto);

        List<Object> services = driverService.getTransportServicesForDriver(created.getId(), 0, 10);
        assertTrue(services.isEmpty());
    }

    @Test
    void getTransportServicesForDriver_InvalidPage_ShouldReturnEmptyList() {
        TransportCompany company = companyRepo.getAll(0, 1, null, true).getFirst();
        DriverCreateDTO dto = new DriverCreateDTO("Test", "Driver", new BigDecimal("50000"), company.getId(), null, Set.of());
        DriverViewDTO created = driverService.create(dto);

        List<Object> services = driverService.getTransportServicesForDriver(created.getId(), -1, 10);
        assertTrue(services.isEmpty());
    }

    @Test
    void getTransportServicesForDriver_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> driverService.getTransportServicesForDriver(null, 0, 10));
    }
}