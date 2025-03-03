package data.common.seeding;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GenericSeederTest {
    private SessionFactory sessionFactory;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<Qualification, Long> qualificationRepo;
    private Gson gson;

    @BeforeEach
    void setUp() {
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

            companyRepo = new GenericRepository<>(sessionFactory, TransportCompany.class);
            qualificationRepo = new GenericRepository<>(sessionFactory, Qualification.class);
            gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
        } catch (Exception e) {
            fail("Failed to initialize SessionFactory: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    void seed_EmptyDatabase_SeedsData() {
        // Arrange
        GenericSeeder<TransportCompany, Long> seeder = new GenericSeeder<>(
                companyRepo, "data/companies.json", TransportCompany.class, gson, null);

        // Act
        seeder.seed();

        // Assert
        List<TransportCompany> companies = companyRepo.getAll(0, 10, "name", true);
        assertEquals(4, companies.size());
        assertTrue(companies.stream().anyMatch(c -> c.getName().equals("Starter")));
        assertTrue(companies.stream().anyMatch(c -> c.getName().equals("IBM")));
    }

    @Test
    void seed_NonEmptyDatabase_SkipsSeeding() {
        // Arrange: Pre-populate database
        TransportCompany existing = new TransportCompany("Existing Co", "456 Oak Ave");
        companyRepo.create(existing);

        GenericSeeder<TransportCompany, Long> seeder = new GenericSeeder<>(
                companyRepo, "data/companies.json", TransportCompany.class, gson, null);

        // Act
        seeder.seed();

        // Assert: Only the existing company should be present
        List<TransportCompany> companies = companyRepo.getAll(0, 10, "name", true);
        assertEquals(1, companies.size());
        assertEquals("Existing Co", companies.getFirst().getName());
    }

    @Test
    void seed_EmptyJsonFile_SkipsSeeding() {
        // Arrange
        GenericSeeder<Qualification, Long> seeder = new GenericSeeder<>(
                qualificationRepo, "data/empty.json", Qualification.class, gson, null);

        // Act
        seeder.seed();

        // Assert
        List<Qualification> qualifications = qualificationRepo.getAll(0, 10, "name", true);
        assertTrue(qualifications.isEmpty());
    }

    @Test
    void seed_JsonFileNotFound_ThrowsRuntimeException() {
        // Arrange
        GenericSeeder<TransportCompany, Long> seeder = new GenericSeeder<>(
                companyRepo, "data/missing.json", TransportCompany.class, gson, null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> seeder.seed());
        List<TransportCompany> companies = companyRepo.getAll(0, 10, "name", true);
        assertTrue(companies.isEmpty());
    }

    @Test
    void constructor_NullParameters_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> new GenericSeeder<>(null, "path", TransportCompany.class, gson, null));
        assertThrows(NullPointerException.class, () -> new GenericSeeder<>(companyRepo, null, TransportCompany.class, gson, null));
        assertThrows(NullPointerException.class, () -> new GenericSeeder<>(companyRepo, "path", null, gson, null));
        assertThrows(NullPointerException.class, () -> new GenericSeeder<>(companyRepo, "path", TransportCompany.class, null, null));
    }
}