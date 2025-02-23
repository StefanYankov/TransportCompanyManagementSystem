import UI.engines.ConsoleEngine;
import UI.engines.IEngine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.common.seeding.GenericSeeder;
import data.common.seeding.ISeeder;
import data.common.seeding.LocalDateAdapter;
import data.common.seeding.LocalDateTimeAdapter;
import data.models.TransportCompany;
import data.models.employee.Dispatcher;
import data.models.employee.Driver;
import data.models.employee.Qualification;
import data.models.employee.Employee;
import data.models.transportservices.TransportCargoService;

import data.models.transportservices.TransportPassengersService;
import data.models.vehicles.Vehicle;
import data.repositories.GenericRepository;
import data.repositories.IGenericRepository;
import data.repositories.SessionFactoryUtil;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validator;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.mapping.mappers.DriverMapper;
import services.data.mapping.mappers.TransportCompanyMapper;
import services.services.DriverService;
import services.services.TransportCompanyService;
import services.services.contracts.IDriverService;
import services.services.contracts.ITransportCompanyService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        // Initialize SessionFactory and ExecutorService
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();

        // ## Initialize repositories
        IGenericRepository<Employee, Long> employeeRepository =
                new GenericRepository<>(sessionFactory, executorService, Employee.class);
        IGenericRepository<TransportCompany, Long> companyRepository =
                new GenericRepository<>(sessionFactory, executorService, TransportCompany.class);
        IGenericRepository<Qualification, Long> qualificationRepository =
                new GenericRepository<>(sessionFactory, executorService, Qualification.class);
        IGenericRepository<Vehicle, Long> vehicleRepository =
                new GenericRepository<>(sessionFactory, executorService, Vehicle.class);
        IGenericRepository<TransportCargoService, Long> cargoServiceRepository =
                new GenericRepository<>(sessionFactory, executorService, TransportCargoService.class);
        IGenericRepository<TransportPassengersService, Long> passengerServiceRepository =
                new GenericRepository<>(sessionFactory, executorService, TransportPassengersService.class);
        IGenericRepository<Driver, Long> driverRepository =
                new GenericRepository<>(sessionFactory, executorService, Driver.class);
        IGenericRepository<Dispatcher, Long> dispatcherRepository =
                new GenericRepository<>(sessionFactory, executorService, Dispatcher.class);

        // ## Data seeding
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()) // Handles LocalDateTime
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())         // Handles LocalDate
                .setPrettyPrinting()
                .create();

        // Load JSON from resources;
        String qualificationJsonFilePath;
        String companiesJsonFilePath;
        try {
            qualificationJsonFilePath = Objects.requireNonNull(
                    Main.class.getClassLoader().getResource("data/data_qualifications.json"),
                    "Qualifications JSON file not found in resources"
            ).getPath();
            companiesJsonFilePath = Objects.requireNonNull(
                    Main.class.getClassLoader().getResource("data/companies.json"),
                    "Companies JSON file not found in resources"
            ).getPath();
        } catch (NullPointerException e) {
            logger.error("Failed to load JSON resources: {}", e.getMessage());
            return;
        }

        ISeeder<Qualification, Long> qualificationSeeder =
                new GenericSeeder<>(qualificationRepository, qualificationJsonFilePath, Qualification.class, gson);
        ISeeder<TransportCompany, Long> companiesSeeder =
                new GenericSeeder<>(companyRepository, companiesJsonFilePath, TransportCompany.class, gson);

        try {
            qualificationSeeder.seed();
            logger.info("Initial seeding for {} completed successfully", Qualification.class.getName());
        } catch (Exception e) {
            logger.error("Seeding failed due to {}", e.getMessage() );
        }

        try {
            companiesSeeder.seed();
            logger.info("Initial seeding for {} completed successfully", TransportCompany.class.getName());
        } catch (Exception e) {
            logger.error("Seeding failed due to {}", e.getMessage() );
        }


        // seeding ends ##


        // ## Initiate utility classes
        // 1. DTO validations
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // 2. Mappers
        DriverMapper driveMapper = new DriverMapper();
        TransportCompanyMapper companyMapper = new TransportCompanyMapper();

        // ## Initiate serviceLayer

        IDriverService driverService =
                new DriverService(driverRepository,
                        driveMapper, companyRepository, dispatcherRepository, cargoServiceRepository,
                        passengerServiceRepository, qualificationRepository);

        ITransportCompanyService transportCompanyService =
                new TransportCompanyService(companyRepository,
                        cargoServiceRepository,
                        passengerServiceRepository,
                        companyMapper);


        // ## Initiate engine
        IEngine engine = new ConsoleEngine(validator, transportCompanyService, driverService);
        engine.start();


        // Clean up resources (Hibernate session factory)
        SessionFactoryUtil.shutdown();

        // Shutdown ExecutorService
        executorService.shutdown();
    }
}