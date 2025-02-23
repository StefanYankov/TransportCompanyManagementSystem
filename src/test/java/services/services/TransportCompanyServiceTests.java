package services.services;

import data.models.Client;
import data.models.TransportCompany;
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
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;
import services.common.exceptions.DuplicateEntityException;
import services.data.dto.companies.TransportCompanyCreateDTO;
import services.data.dto.companies.TransportCompanyViewDTO;
import services.data.mapping.mappers.TransportCompanyMapper;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;


import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransportCompanyServiceTests {
    private static SessionFactory sessionFactory;
    private static ExecutorService executorService;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<TransportCargoService, Long> cargoRepo;
    private IGenericRepository<TransportPassengersService, Long> passengersRepo;
    private TransportCompanyMapper mapper;
    private TransportCompanyService companyService;

    @BeforeEach
    public void SetUp() {
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
            executorService = Executors.newFixedThreadPool(2);

            // Initialize repositories for TransportCompany and Driver
            companyRepo = new GenericRepository<>(sessionFactory, executorService, TransportCompany.class);
            cargoRepo = new GenericRepository<>(sessionFactory, executorService, TransportCargoService.class);
            passengersRepo = new GenericRepository<>(sessionFactory, executorService, TransportPassengersService.class);

            mapper = new TransportCompanyMapper();
            companyService = new TransportCompanyService(companyRepo, cargoRepo, passengersRepo, mapper);
        } catch (Exception e) {
            fail("Failed to initialize SessionFactory: " + e.getMessage());
        }
    }

    @AfterEach
    public void TearDown() {
        // H2 drops tables automatically with create-drop, but we close resources manually
        if (executorService != null) {
            executorService.shutdown();
        }
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    public void Create_TransportCompany_ShouldReturnDTO() {
        // Arrange
        TransportCompanyCreateDTO dto = new TransportCompanyCreateDTO();
        dto.setCompanyName("Fast Transport");
        dto.setAddress("123 Main St");

        // Act
        TransportCompanyViewDTO result = companyService.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Fast Transport", result.getCompanyName());
        assertEquals("123 Main St", result.getAddress());
    }

    @Test
    public void CreateAsync_TransportCompany_ShouldCompleteSuccessfully() {
        // Arrange
        TransportCompanyCreateDTO dto = new TransportCompanyCreateDTO();
        dto.setCompanyName("Async Transport");
        dto.setAddress("456 Elm St");

        // Act
        CompletableFuture<TransportCompanyViewDTO> future = companyService.createAsync(dto);
        TransportCompanyViewDTO result = future.join(); // Wait for async completion

        // Assert
        assertNotNull(result);
        assertEquals("Async Transport", result.getCompanyName());
        assertEquals("456 Elm St", result.getAddress());
    }

    @Test
    public void Create_DuplicateCompanyName_ShouldThrowRepositoryException() {
        // Arrange: First, create a TransportCompanyCreateDTO with a unique company name
        TransportCompanyCreateDTO dto = new TransportCompanyCreateDTO();
        dto.setCompanyName("Existing Company");
        dto.setAddress("123 Main St");

        // First insert with a unique name
        companyService.create(dto); // Insert the first company

        // Now, try to insert the same company name again
        TransportCompanyCreateDTO duplicateDto = new TransportCompanyCreateDTO();
        duplicateDto.setCompanyName("Existing Company");
        duplicateDto.setAddress("456 Elm St");

        // Act & Assert: Expect RepositoryException to be thrown
        assertThrows(RepositoryException.class, () -> companyService.create(duplicateDto));
    }

    @Test
    public void CreateAsync_DuplicateCompanyName_ShouldThrowRepositoryException() {
        // Arrange: First, create a TransportCompanyCreateDTO with a unique company name
        TransportCompanyCreateDTO dto = new TransportCompanyCreateDTO();
        dto.setCompanyName("Existing Company");
        dto.setAddress("456 Elm St");

        // First insert with a unique name
        companyService.createAsync(dto).join(); // Insert the first company and wait for completion

        // Now, try to insert the same company name again asynchronously
        TransportCompanyCreateDTO duplicateDto = new TransportCompanyCreateDTO();
        duplicateDto.setCompanyName("Existing Company");
        duplicateDto.setAddress("789 Oak St");

        // Act: Execute createAsync and capture the exception using CompletableFuture
        CompletableFuture<TransportCompanyViewDTO> future = companyService.createAsync(duplicateDto);

        // Assert: Ensure that the exception is thrown and captured in the future
        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertInstanceOf(RepositoryException.class, exception.getCause());
        // assertTrue(exception.getCause().getMessage().contains("duplicate")); // TODO: test
    }

    @Test
    public void GetById_ValidId_ShouldReturnTransportCompanyViewDTO() {
        // Arrange
        TransportCompany company = new TransportCompany("Test Company", "Test Address");
        companyRepo.create(company); // Insert the company into the DB

        // Act
        TransportCompanyViewDTO result = companyService.getById(company.getId());

        // Assert
        assertNotNull(result);
        assertEquals(company.getId(), result.getId());
        assertEquals("Test Company", result.getCompanyName());
    }

    @Test
    public void GetById_NonExistentId_ShouldThrowRepositoryExceptionWithMessage() {
        // Arrange
        Long invalidId = 999L; // ID that does not exist in the DB

        // Act & Assert: Expect RepositoryException to be thrown
        RepositoryException exception = assertThrows(RepositoryException.class, () -> companyService.getById(invalidId));

        // Assert: Verify that the exception message contains the expected text
        assertTrue(exception.getMessage().contains("Failed to retrieve entity of type TransportCompany with ID 999"));
    }

    @Test
    public void GetAll_ValidPagination_ShouldReturnListOfDTOs() {
        // Arrange
        int page = 0;
        int size = 10;
        String orderBy = "name";
        boolean ascending = true;

        TransportCompany company1 = new TransportCompany("Company 1", "Address 1");
        TransportCompany company2 = new TransportCompany("Company 2", "Address 2");
        companyRepo.create(company1);
        companyRepo.create(company2);

        // Act
        List<TransportCompanyViewDTO> result = companyService.getAll(page, size, orderBy, ascending);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Company 1", result.getFirst().getCompanyName());
        assertEquals("Company 2", result.get(1).getCompanyName());
    }

    @Test
    public void GetAll_NoCompanies_ShouldThrowRepositoryExceptionWithMessage() {
        // Arrange:

        // Act: Expect RepositoryException to be thrown
        RepositoryException exception = assertThrows(RepositoryException.class, () -> companyService.getAll(0, 10, "companyName", true));

        // Assert: Verify that the exception message contains the expected text
        assertTrue(exception.getMessage().contains("Failed to retrieve all entities of type TransportCompany"));
    }

    @Test
    public void GetAll_OutOfBoundsPage_ShouldReturnEmptyList() {
        // Arrange: Page number that is too high for the available data
        int invalidPage = 999;

        // Act
        List<TransportCompanyViewDTO> result = companyService.getAll(invalidPage, 10, "name", true);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void GetAll_NonExistingOrderBy_ShouldThrowRepositoryExceptionWithMessage() {
        // Arrange: Order by that doens't exist
        String orderBy = "some random column name";

        // Act
        RepositoryException exception = assertThrows(RepositoryException.class, () -> companyService.getAll(0, 10, orderBy, true));

        // Assert: Verify that the exception message contains the expected text
        assertTrue(exception.getMessage().contains("Failed to retrieve all entities of type TransportCompany"));
    }

    @Test
    public void GetByIdAsync_ValidId_ShouldReturnTransportCompanyViewDTO() {
        // Arrange
        TransportCompany company = new TransportCompany("Async Test Company", "Async Test Address");
        companyRepo.create(company);

        // Act
        CompletableFuture<TransportCompanyViewDTO> future = companyService.getByIdAsync(company.getId());
        TransportCompanyViewDTO result = future.join();

        // Assert
        assertNotNull(result);
        assertEquals(company.getId(), result.getId());
        assertEquals("Async Test Company", result.getCompanyName());
    }

    @Test
    public void GetByIdAsync_NonExistentId_ShouldThrowRepositoryException() {
        // Arrange
        Long invalidId = 999L;

        // Act
        CompletableFuture<TransportCompanyViewDTO> future = companyService.getByIdAsync(invalidId);

        // Assert
        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertInstanceOf(RepositoryException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Failed to retrieve entity of type TransportCompany with ID 999"));
    }

    @Test
    public void GetByIdAsync_NullId_ShouldThrowRepositoryException() {
        // Act
        CompletableFuture<TransportCompanyViewDTO> future = companyService.getByIdAsync(null);

        // Assert
        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertInstanceOf(RepositoryException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Failed to retrieve entity of type TransportCompany with ID null"));
    }

    @Test
    public void GetAllAsync_ValidPagination_ShouldReturnListOfDTOs() {
        // Arrange
        int page = 0;
        int size = 10;
        String orderBy = "name";
        boolean ascending = true;

        TransportCompany company1 = new TransportCompany("Async Company 1", "Address 1");
        TransportCompany company2 = new TransportCompany("Async Company 2", "Address 2");
        companyRepo.create(company1);
        companyRepo.create(company2);

        // Act
        CompletableFuture<List<TransportCompanyViewDTO>> future = companyService.getAllAsync(page, size, orderBy, ascending);
        List<TransportCompanyViewDTO> result = future.join();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Async Company 1", result.getFirst().getCompanyName());
        assertEquals("Async Company 2", result.get(1).getCompanyName());
    }

    @Test
    public void GetAllAsync_OutOfBoundsPage_ShouldReturnEmptyList() {
        // Arrange
        int invalidPage = 999;

        // Act
        CompletableFuture<List<TransportCompanyViewDTO>> future = companyService.getAllAsync(invalidPage, 10, "name", true);
        List<TransportCompanyViewDTO> result = future.join();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void GetAllAsync_NonExistingOrderBy_ShouldThrowRepositoryException() {
        // Arrange
        String invalidOrderBy = "nonexistentColumn";

        // Act
        CompletableFuture<List<TransportCompanyViewDTO>> future = companyService.getAllAsync(0, 10, invalidOrderBy, true);

        // Assert
        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertInstanceOf(RepositoryException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Failed to retrieve all entities of type TransportCompany"));
    }

    @Test
    public void DeleteAsync_ValidId_ShouldDeleteCompany() {
        // Arrange
        TransportCompany company = new TransportCompany("To Delete", "Delete Address");
        companyRepo.create(company);

        // Act
        CompletableFuture<Void> future = companyService.deleteAsync(company.getId());
        future.join(); // Wait for completion

        // Assert
        assertThrows(RepositoryException.class, () -> companyRepo.getById(company.getId())); // Should be gone
    }

    @Test
    public void DeleteAsync_NonExistentId_ShouldThrowRepositoryException() {
        // Arrange
        Long invalidId = 999L;

        // Act
        CompletableFuture<Void> future = companyService.deleteAsync(invalidId);

        // Assert
        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertTrue(exception.getCause() instanceof RepositoryException);
        assertTrue(exception.getCause().getMessage().contains("Failed to delete entity of type TransportCompany with ID 999"));
    }

    @Test
    public void DeleteAsync_NullId_ShouldThrowRepositoryException() {
        // Act
        CompletableFuture<Void> future = companyService.deleteAsync(null);

        // Assert
        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertTrue(exception.getCause() instanceof RepositoryException);
        assertTrue(exception.getCause().getMessage().contains("Failed to delete entity of type TransportCompany with ID null"));
    }

    @Test
    public void GetCompaniesSortedByRevenue_Ascending_ShouldReturnSortedList() {
        // Arrange: Create two companies with transport services
        TransportCompany company1 = new TransportCompany("Company 1", "Address 1");
        TransportCompany company2 = new TransportCompany("Company 2", "Address 2");
        companyRepo.create(company1);
        companyRepo.create(company2);

        // Create a cargo service for company1 with all required fields
        TransportCargoService cargoService1 = new TransportCargoService();
        cargoService1.setTransportCompany(company1);
        cargoService1.setPrice(new BigDecimal("100.00"));
        cargoService1.setStartingDate(LocalDate.now()); // Required field
        cargoService1.setWeightInKilograms(new BigDecimal("10.00")); // Required field
        cargoService1.setLengthInCentimeters(100); // Required field
        cargoService1.setWidthInCentimeters(50); // Required field
        cargoService1.setHeightInCentimeters(30); // Required field
        cargoRepo.create(cargoService1);

        // Create a cargo service for company2 with all required fields
        TransportCargoService cargoService2 = new TransportCargoService();
        cargoService2.setTransportCompany(company2);
        cargoService2.setPrice(new BigDecimal("200.00"));
        cargoService2.setStartingDate(LocalDate.now()); // Required field
        cargoService2.setWeightInKilograms(new BigDecimal("20.00")); // Required field
        cargoService2.setLengthInCentimeters(150); // Required field
        cargoService2.setWidthInCentimeters(75); // Required field
        cargoService2.setHeightInCentimeters(40); // Required field
        cargoRepo.create(cargoService2);

        // Act
        List<TransportCompany> result = companyService.getCompaniesSortedByRevenue(true); // Ascending

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Company 1", result.get(0).getName()); // 100.00 first
        assertEquals("Company 2", result.get(1).getName()); // 200.00 second
    }

    @Test
    public void GetCompaniesSortedByRevenue_Descending_ShouldReturnSortedList() {
        // Arrange
        TransportCompany company1 = new TransportCompany("Company 1", "Address 1");
        TransportCompany company2 = new TransportCompany("Company 2", "Address 2");
        companyRepo.create(company1);
        companyRepo.create(company2);

        TransportCargoService cargoService1 = new TransportCargoService();
        cargoService1.setTransportCompany(company1);
        cargoService1.setPrice(new BigDecimal("100.00"));
        cargoService1.setStartingDate(LocalDate.now());
        cargoService1.setWeightInKilograms(new BigDecimal("10.00"));
        cargoService1.setLengthInCentimeters(100);
        cargoService1.setWidthInCentimeters(50);
        cargoService1.setHeightInCentimeters(30);
        cargoRepo.create(cargoService1);

        TransportCargoService cargoService2 = new TransportCargoService();
        cargoService2.setTransportCompany(company2);
        cargoService2.setPrice(new BigDecimal("200.00"));
        cargoService2.setStartingDate(LocalDate.now());
        cargoService2.setWeightInKilograms(new BigDecimal("20.00"));
        cargoService2.setLengthInCentimeters(150);
        cargoService2.setWidthInCentimeters(75);
        cargoService2.setHeightInCentimeters(40);
        cargoRepo.create(cargoService2);

        // Act
        List<TransportCompany> result = companyService.getCompaniesSortedByRevenue(false); // Descending

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Company 2", result.get(0).getName()); // 200.00 first
        assertEquals("Company 1", result.get(1).getName()); // 100.00 second
    }

    @Test
    public void GetCompaniesSortedByRevenue_NoServices_ShouldReturnCompaniesWithZeroRevenue() {
        // Arrange
        TransportCompany company1 = new TransportCompany("Company 1", "Address 1");
        TransportCompany company2 = new TransportCompany("Company 2", "Address 2");
        companyRepo.create(company1);
        companyRepo.create(company2);

        // No services created, so revenue should be 0


        // Act
        List<TransportCompany> result = companyService.getCompaniesSortedByRevenue(true);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getName().equals("Company 1")));
        assertTrue(result.stream().anyMatch(c -> c.getName().equals("Company 2")));
    }

    @Test
    public void GetCompaniesSortedByRevenue_InvalidField_ShouldThrowRepositoryException() {
        // Arrange:
        String aggregationField = "invalidField";
        TransportCompany company = new TransportCompany("Company", "Address");
        companyRepo.create(company);

        // Act
        RepositoryException exception = assertThrows(RepositoryException.class, () -> {
            companyRepo.findWithAggregation("transportServices", aggregationField, "id", true);
        });

        // Assert
        assertTrue(exception.getMessage().contains("Failed to find entities of type TransportCompany with aggregation"));
    }

    @Test
    public void GetRevenueForPeriod_ValidPeriod_ShouldReturnTotalRevenue() {
        // Arrange
        TransportCompany company = new TransportCompany("Test Company", "Address");
        companyRepo.create(company);

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        TransportCargoService cargoService = new TransportCargoService();
        cargoService.setTransportCompany(company);
        cargoService.setPrice(new BigDecimal("100.00"));
        cargoService.setStartingDate(LocalDate.of(2023, 6, 1));
        cargoService.setWeightInKilograms(new BigDecimal("10.00"));
        cargoService.setLengthInCentimeters(100);
        cargoService.setWidthInCentimeters(50);
        cargoService.setHeightInCentimeters(30);
        cargoRepo.create(cargoService);

        TransportPassengersService passengerService = new TransportPassengersService();
        passengerService.setTransportCompany(company);
        passengerService.setPrice(new BigDecimal("200.00"));
        passengerService.setStartingDate(LocalDate.of(2023, 6, 2));
        passengerService.setNumberOfPassengers(5);
        passengersRepo.create(passengerService);

        // Act
        BigDecimal revenue = companyService.getRevenueForPeriod(company.getId(), startDate, endDate);

        // Assert
        assertNotNull(revenue);
        assertEquals(new BigDecimal("300.00"), revenue);
    }

    @Test
    public void GetRevenueForPeriod_NoServicesInPeriod_ShouldReturnZero() {
        // Arrange
        TransportCompany company = new TransportCompany("Test Company", "Address");
        companyRepo.create(company);

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        TransportCargoService cargoService = new TransportCargoService();
        cargoService.setTransportCompany(company);
        cargoService.setPrice(new BigDecimal("100.00"));
        cargoService.setStartingDate(LocalDate.of(2022, 6, 1)); // Outside period
        cargoService.setWeightInKilograms(new BigDecimal("10.00"));
        cargoService.setLengthInCentimeters(100);
        cargoService.setWidthInCentimeters(50);
        cargoService.setHeightInCentimeters(30);
        cargoRepo.create(cargoService);

        // Act
        BigDecimal revenue = companyService.getRevenueForPeriod(company.getId(), startDate, endDate);

        // Assert
        assertNotNull(revenue);
        assertEquals(BigDecimal.ZERO, revenue);
    }

    @Test
    public void GetRevenueForPeriod_NullPrices_ShouldIgnoreAndReturnPartialRevenue() {
        // Arrange
        TransportCompany company = new TransportCompany("Test Company", "Address");
        companyRepo.create(company);

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        TransportCargoService cargoService = new TransportCargoService();
        cargoService.setTransportCompany(company);
        cargoService.setPrice(null); // Null price
        cargoService.setStartingDate(LocalDate.of(2023, 6, 1));
        cargoService.setWeightInKilograms(new BigDecimal("10.00"));
        cargoService.setLengthInCentimeters(100);
        cargoService.setWidthInCentimeters(50);
        cargoService.setHeightInCentimeters(30);
        cargoRepo.create(cargoService);

        TransportPassengersService passengerService = new TransportPassengersService();
        passengerService.setTransportCompany(company);
        passengerService.setPrice(new BigDecimal("200.00"));
        passengerService.setStartingDate(LocalDate.of(2023, 6, 2));
        passengerService.setNumberOfPassengers(5);
        passengersRepo.create(passengerService);

        // Act
        BigDecimal revenue = companyService.getRevenueForPeriod(company.getId(), startDate, endDate);

        // Assert
        assertNotNull(revenue);
        assertEquals(new BigDecimal("200.00"), revenue);
    }

    @Test
    public void GetRevenueForPeriod_NonExistentCompany_ShouldReturnZero() {
        // Arrange
        Long invalidId = 999L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        // Act
        BigDecimal revenue = companyService.getRevenueForPeriod(invalidId, startDate, endDate);

        // Assert
        assertNotNull(revenue);
        assertEquals(BigDecimal.ZERO, revenue);
    }

}

