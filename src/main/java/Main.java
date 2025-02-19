import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.common.seeding.GenericSeeder;
import data.common.seeding.ISeeder;
import data.common.seeding.LocalDateAdapter;
import data.common.seeding.LocalDateTimeAdapter;
import data.models.TransportCompany;
import data.models.employee.Qualification;
import data.models.employee.Employee;
import data.models.transportservices.TransportCargoService;
import data.models.vehicles.Colour;

import data.models.vehicles.Vehicle;
import data.repositories.GenericRepository;
import data.repositories.IGenericRepository;
import data.repositories.SessionFactoryUtil;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validator;
import org.hibernate.SessionFactory;
import services.TransportCompanyService;
import services.data.dto.transportcompany.TransportCompanyCreateDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
        IGenericRepository<TransportCargoService, Long> cargoServiceRepository =
                new GenericRepository<>(sessionFactory, executorService, TransportCargoService.class);

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
        System.out.print("Enter Company Name: ");
        String companyName = scanner.nextLine();

        System.out.print("Enter Address: ");
        String address = scanner.nextLine();

//        // Create a DTO with user input
        TransportCompanyCreateDto inputModel = new TransportCompanyCreateDto(companyName, address);

        // Perform validation
        Set<ConstraintViolation<TransportCompanyCreateDto>> violations = validator.validate(inputModel);
        TransportCompanyService service = new TransportCompanyService(companyRepository, cargoServiceRepository);

//        // Check if validation passed
//        if (!violations.isEmpty()) {
//            // If there are validation errors, print them
//            for (ConstraintViolation<TransportCompanyCreateDto> violation : violations) {
//                System.out.println("Error: " + violation.getMessage());
//            }
//        } else {
//            // If validation passes, pass the input to the service layer
//            service.createTransportCompanyAsync(inputModel);
//        }
//
//        Map<String, String> companies = new HashMap<>();
//        companies.put("TSB", "Troyan");
//        companies.put("SAP", "Sofia");
//        companies.put("DXC", "Sofia");
//        companies.put("IBM", "Lovech");
//
//        List<CompletableFuture<Void>> createCompanyFutures = new ArrayList<>();
//
//
//        for (var company : companies.entrySet()) {
//            inputModel.setCompanyName(company.getKey());
//            inputModel.setAddress(company.getValue());
//
//            // Check if validation passed
//            Set<ConstraintViolation<TransportCompanyCreateDto>> violations2 = validator.validate(inputModel);
//            if (!violations.isEmpty()) {
//                // If there are validation errors, print them
//                for (ConstraintViolation<TransportCompanyCreateDto> violation : violations2) {
//                    System.out.println("Error: " + violation.getMessage());
//                }
//            } else {
//                // If validation passes, create the company asynchronously and collect the futures
//                CompletableFuture<Void> createCompanyFuture = service.createTransportCompanyAsync(inputModel);
//                createCompanyFutures.add(createCompanyFuture);
//            }
//        }


        scanner.close();


        // Clean up resources (Hibernate session factory)
        SessionFactoryUtil.shutdown();

        // Shutdown ExecutorService
        executorService.shutdown();
    }
}