import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.common.seeding.GenericSeeder;
import data.common.seeding.ISeeder;
import data.common.seeding.LocalDateAdapter;
import data.common.seeding.LocalDateTimeAdapter;
import data.models.TransportCompany;
import data.models.employee.Qualification;
import data.models.employee.Employee;
import data.models.transportservices.TransportService;
import data.models.vehicles.Colour;

import data.models.vehicles.Vehicle;
import data.repositories.GenericRepository;
import data.repositories.IGenericRepository;
import data.repositories.SessionFactoryUtil;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validator;
import org.hibernate.SessionFactory;
import services.TransportCompanyService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

        // Initialize SessionFactory and ExecutorService
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();

        // Initialize repositories
        IGenericRepository<Employee, Long> employeeRepository =
                new GenericRepository<>(sessionFactory, executorService, Employee.class);
        IGenericRepository<TransportCompany, Long> companyRepository =
                new GenericRepository<>(sessionFactory, executorService, TransportCompany.class);
        IGenericRepository<Qualification, Long> qualificationRepository =
                new GenericRepository<>(sessionFactory, executorService, Qualification.class);
        IGenericRepository<Colour, Long> colourRepository =
                new GenericRepository<>(sessionFactory, executorService, Colour.class);
        IGenericRepository<Vehicle, Long> vehicleRepository =
                new GenericRepository<>(sessionFactory, executorService, Vehicle.class);
        IGenericRepository<TransportService, Long> transportServiceRepository
                = new GenericRepository<>(sessionFactory, executorService, TransportService.class);

        // DATA SEEDING

        // Gson instance
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()) // Handles LocalDateTime
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())         // Handles LocalDate
                .create();


        // Path to your JSON file containing seeding data
        String jsonFilePath = Objects.requireNonNull(
                Main.class.getClassLoader().getResource("data/data_qualifications.json")
        ).getPath();

        String jsonFilePathColours = Objects.requireNonNull(
                Main.class.getClassLoader().getResource("data/colours.json")
        ).getPath();


        // Initialize the seeders
        ISeeder<Qualification, Long> qualificationSeeder =
                new GenericSeeder<>(qualificationRepository, jsonFilePath, Qualification.class, gson);

        ISeeder<Colour, Long> colourSeeder =
                new GenericSeeder<>(colourRepository, jsonFilePathColours, Colour.class, gson);

        CompletableFuture<Void> seedingFuture = qualificationSeeder.seedAsync();
        CompletableFuture<Void> colourSeedingFuture = colourSeeder.seedAsync();

        CompletableFuture.allOf(seedingFuture, colourSeedingFuture).join();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Scanner scanner = new Scanner(System.in);
//        System.out.print("Enter Company Name: ");
//        String companyName = scanner.nextLine();
//
//        System.out.print("Enter Address: ");
//        String address = scanner.nextLine();
//
//        // Create a DTO with user input
//        CreateTransportCompanyInputModel inputModel = new CreateTransportCompanyInputModel(companyName, address);
//
//        // Perform validation
//        Set<ConstraintViolation<CreateTransportCompanyInputModel>> violations = validator.validate(inputModel);
        TransportCompanyService service = new TransportCompanyService(companyRepository, employeeRepository);
//
//        // Check if validation passed
//        if (!violations.isEmpty()) {
//            // If there are validation errors, print them
//            for (ConstraintViolation<CreateTransportCompanyInputModel> violation : violations) {
//                System.out.println("Error: " + violation.getMessage());
//            }
//        } else {
//            // If validation passes, pass the input to the service layer
//            //service.CreateTransportCompany(inputModel); // TODO: FIX ASYNC
//        }

        try {
            //var company1 = service.getCompanyByIdAsync(1L).get();
            var company1 = service.getCompaniesByNameAsynch("starter", "name", true).get().getFirst();
            System.out.println("-".repeat(12) + System.lineSeparator() + "Company 1: " + company1.getName() + System.lineSeparator() +
                    company1.getAddress() + System.lineSeparator() + "-".repeat(12));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


        scanner.close();


        // Clean up resources (Hibernate session factory)
        SessionFactoryUtil.shutdown();

        // Shutdown ExecutorService
        executorService.shutdown();
    }
}