import UI.engines.ConsoleEngine;
import UI.engines.IEngine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.common.seeding.GenericSeeder;
import data.common.seeding.ISeeder;
import data.common.seeding.LocalDateAdapter;
import data.common.seeding.LocalDateTimeAdapter;
import data.models.Client;
import data.models.TransportCompany;
import data.models.employee.Dispatcher;
import data.models.employee.Driver;
import data.models.employee.Qualification;
import data.models.employee.Employee;
import data.models.transportservices.Destination;
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
import services.data.dto.transportservices.TransportPassengersServiceCreateDTO;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;
import services.data.mapping.mappers.DriverMapper;
import services.data.mapping.mappers.QualificationMapper;
import services.data.mapping.mappers.TransportCompanyMapper;
//import services.services.DriverService;
//import services.services.QualificationService;
//import services.services.TransportCompanyService;
import services.data.mapping.mappers.TransportPassengersServiceMapper;
import services.services.contracts.IDriverService;
import services.services.contracts.IQualificationService;
import services.services.contracts.ITransportCompanyService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        // Initialize SessionFactory and ExecutorService
        SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();

        // ## Initialize repositories
        IGenericRepository<Employee, Long> employeeRepository =
                new GenericRepository<>(sessionFactory,  Employee.class);
        IGenericRepository<TransportCompany, Long> companyRepository =
                new GenericRepository<>(sessionFactory, TransportCompany.class);
        IGenericRepository<Qualification, Long> qualificationRepository =
                new GenericRepository<>(sessionFactory, Qualification.class);
        IGenericRepository<Vehicle, Long> vehicleRepository =
                new GenericRepository<>(sessionFactory, Vehicle.class);
        IGenericRepository<TransportCargoService, Long> cargoServiceRepository =
                new GenericRepository<>(sessionFactory, TransportCargoService.class);
        IGenericRepository<TransportPassengersService, Long> passengerServiceRepository =
                new GenericRepository<>(sessionFactory, TransportPassengersService.class);
        IGenericRepository<Driver, Long> driverRepository =
                new GenericRepository<>(sessionFactory, Driver.class);
        IGenericRepository<Dispatcher, Long> dispatcherRepository =
                new GenericRepository<>(sessionFactory, Dispatcher.class);
        IGenericRepository<Client, Long> clientRepository
                = new GenericRepository<>(sessionFactory, Client.class);
        IGenericRepository<Destination, Long> destinationRepository =
                new GenericRepository<>(sessionFactory, Destination.class);

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
        DriverMapper driveMapper = new DriverMapper(companyRepository,dispatcherRepository,qualificationRepository);
        TransportCompanyMapper companyMapper = new TransportCompanyMapper();
        QualificationMapper qualificationMapper = new QualificationMapper();
        TransportPassengersServiceMapper mapper = new TransportPassengersServiceMapper(companyRepository, clientRepository, driverRepository, vehicleRepository, destinationRepository);

        // ## Initiate serviceLayer
        // Test mapping a CreateDTO to entity
        TransportPassengersServiceCreateDTO createDto = new TransportPassengersServiceCreateDTO();
        createDto.setTransportCompanyId(1L);
        createDto.setVehicleId(2L);
        createDto.setDestinationId(3L);
        createDto.setDriverId(4L);
        createDto.setClientId(5L);
        createDto.setPrice(new java.math.BigDecimal("1000.00"));
        createDto.setStartingDate(java.time.LocalDate.now());
        createDto.setEndingDate(java.time.LocalDate.now().plusDays(1));
        createDto.setNumberOfPassengers(6);

        TransportPassengersService entity = mapper.toEntity(createDto);
        System.out.println("Entity created: " + entity.getNumberOfPassengers());

        // Test mapping entity to ViewDTO
        TransportPassengersServiceViewDTO viewDto = mapper.toViewDTO(entity);
        System.out.println("View DTO created: " + viewDto.getNumberOfPassengers());

        // If we get here without exceptions, ModelMapper is working standalone
        System.out.println("ModelMapper test completed successfully!");

        // ## Initiate engine
       // IEngine engine = new ConsoleEngine(validator, transportCompanyService, driverService, qualificationService);
        //engine.start();


        // Clean up resources (Hibernate session factory)
        SessionFactoryUtil.shutdown();

    }
}