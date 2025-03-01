package data.common.seeding;

import com.google.gson.JsonSyntaxException;
import data.models.TransportCompany;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.repositories.GenericRepository;
import data.repositories.IGenericRepository;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class GenericSeederTest {
    private SessionFactory sessionFactory;
    private ExecutorService executorService;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private Gson gson;

    @BeforeEach
    public void setUp() {
        // H2 in-memory setup: Fresh DB per test with "create-drop"
        sessionFactory = new Configuration().configure("META-INF/persistence.xml").buildSessionFactory();
        executorService = Executors.newFixedThreadPool(2);
        companyRepo = new GenericRepository<>(sessionFactory, TransportCompany.class);

        // Gson with adapters for your entities
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
    }

    @AfterEach
    public void tearDown() {
        executorService.shutdown();
        sessionFactory.close();
    }

    @Test
    public void Seed_WhenDatabaseIsEmpty_ShouldInsertEntitiesFromJson() throws IOException {
        // Arrange: Create a temp JSON file
        String jsonContent = "[{\"id\": 1, \"name\": \"Fast Transport\", \"address\": \"123 Main St\", \"createdOn\": \"2025-02-21T10:00:00\"}]";
        File tempFile = File.createTempFile("test_seed", ".json");
        Files.writeString(tempFile.toPath(), jsonContent);
        GenericSeeder<TransportCompany, Long> seeder = new GenericSeeder<>(companyRepo, tempFile.getAbsolutePath(), TransportCompany.class, gson);

        // Act: Seed the data
        seeder.seed();

        // Assert: Verify the data was inserted
        List<TransportCompany> companies = companyRepo.getAll(0, 10, "id", true);
        assertEquals(1, companies.size(), "H2 should have one seeded company");
        assertEquals("Fast Transport", companies.getFirst().getName());
    }

    @Test
    public void Seed_WhenDatabaseContainsData_ShouldSkipSeeding() throws IOException {
        // Arrange: Seed initial data and create a temp JSON file
        TransportCompany existing = new TransportCompany();
        existing.setName("Existing Co");
        existing.setAddress("456 Oak Ave");
        companyRepo.create(existing);
        String jsonContent = "[{\"id\": 2, \"name\": \"New Co\", \"address\": \"789 Pine Rd\", \"createdOn\": \"2025-02-21T10:00:00\"}]";
        File tempFile = File.createTempFile("test_seed_skip", ".json");
        Files.writeString(tempFile.toPath(), jsonContent);
        GenericSeeder<TransportCompany, Long> seeder = new GenericSeeder<>(companyRepo, tempFile.getAbsolutePath(), TransportCompany.class, gson);

        // Act: Attempt to seed
        seeder.seed();

        // Assert: Verify only the original data exists
        List<TransportCompany> companies = companyRepo.getAll(0, 10, "id", true);
        assertEquals(1, companies.size(), "H2 should still have only the original company");
        assertEquals("Existing Co", companies.getFirst().getName());
    }

    @Test
    public void Seed_WhenJsonFileIsEmpty_ShouldLogWarningAndSkipSeeding() throws IOException {
        // Arrange: Create an empty JSON file
        File tempFile = File.createTempFile("test_seed_empty", ".json");
        Files.writeString(tempFile.toPath(), "[]");
        GenericSeeder<TransportCompany, Long> seeder = new GenericSeeder<>(companyRepo, tempFile.getAbsolutePath(), TransportCompany.class, gson);

        // Act: Seed with empty JSON
        seeder.seed();

        // Assert: Verify no data was seeded
        List<TransportCompany> companies = companyRepo.getAll(0, 10, "id", true);
        assertTrue(companies.isEmpty(), "H2 should remain empty with empty JSON");
    }

    @Test
    public void Seed_WhenJsonFileDoesNotExist_ShouldThrowException() {

        // Arrange: Set up seeder with a non-existent file path
        GenericSeeder<TransportCompany, Long> seeder = new GenericSeeder<>(companyRepo, "/nonexistent.json", TransportCompany.class, gson);

        // Act & Assert: Expect an exception
        RuntimeException exception = assertThrows(RuntimeException.class,
                seeder::seed,
                "Expected exception for missing JSON file");
        assertTrue(exception.getMessage().contains("Failed to seed data from JSON"));
    }

    @Test
    public void Seed_WhenJsonIsMalformed_ShouldThrowException() throws IOException {
        // Arrange: Malformed JSON (missing closing bracket)
        String jsonContent = "[{\"id\": 1, \"name\": \"Fast Transport\", \"address\": \"123 Main St\"";
        File tempFile = File.createTempFile("test_seed_malformed", ".json");
        Files.writeString(tempFile.toPath(), jsonContent);

        GenericSeeder<TransportCompany, Long> seeder = new GenericSeeder<>(companyRepo, tempFile.getAbsolutePath(), TransportCompany.class, gson);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                seeder::seed,
                "Expected exception for malformed JSON");
        assertInstanceOf(JsonSyntaxException.class, exception.getCause(), "Should be a Gson parsing error");
    }

    @Test
    public void Seed_WhenJsonViolatesConstraint_ShouldThrowException() throws IOException {
        // Arrange: JSON with duplicate ID (assuming ID is primary key)
        String jsonContent = "[{\"id\": 1, \"name\": \"Co A\", \"address\": \"123 St\", \"createdOn\": \"2025-02-21T10:00:00\"}," +
                "{\"id\": 1, \"name\": \"Co B\", \"address\": \"456 St\", \"createdOn\": \"2025-02-21T10:00:00\"}]";
        File tempFile = File.createTempFile("test_seed_constraint", ".json");
        Files.writeString(tempFile.toPath(), jsonContent);

        GenericSeeder<TransportCompany, Long> seeder = new GenericSeeder<>(companyRepo, tempFile.getAbsolutePath(), TransportCompany.class, gson);

        // Act & Assert: H2 throws a constraint violation
        RuntimeException exception = assertThrows(RuntimeException.class,
                seeder::seed,
                "Expected exception for duplicate ID in H2");
        assertTrue(exception.getMessage().contains("Failed to seed"), "Should propagate DB error");
    }

    // New Tests for Edge Cases
    @Test
    public void Seed_WhenJsonContainsNullEntity_ShouldSkipNullAndSeedRest() throws IOException {
        // Arrange: JSON with a null entry
        String jsonContent = "[null, {\"id\": 2, \"name\": \"Valid Co\", \"address\": \"789 Pine Rd\", \"createdOn\": \"2025-02-21T10:00:00\"}]";
        File tempFile = File.createTempFile("test_seed_null", ".json");
        Files.writeString(tempFile.toPath(), jsonContent);

        GenericSeeder<TransportCompany, Long> seeder = new GenericSeeder<>(companyRepo, tempFile.getAbsolutePath(), TransportCompany.class, gson);
        seeder.seed();

        // Assert: Only valid entity seeded
        List<TransportCompany> companies = companyRepo.getAll(0, 10, "id", true);
        assertEquals(1, companies.size(), "H2 should have one valid company");
        assertEquals("Valid Co", companies.getFirst().getName());
    }

}