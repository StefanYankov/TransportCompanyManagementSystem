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
import data.models.transportservices.TransportService;
import data.models.vehicles.Bus;
import data.models.vehicles.Truck;
import data.models.vehicles.Van;
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

import services.data.mapping.mappers.*;
import services.services.*;
import services.services.QualificationService;
import services.services.TransportCompanyService;
import services.services.contracts.*;


import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        // ## Initialize SessionFactory
        SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();

        // ## Initiate utility classes

        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        // ## Initialize repositories
        IGenericRepository<Employee, Long> employeeRepository =
                new GenericRepository<>(sessionFactory, Employee.class);
        IGenericRepository<TransportCompany, Long> companyRepository =
                new GenericRepository<>(sessionFactory, TransportCompany.class);
        IGenericRepository<Qualification, Long> qualificationRepository =
                new GenericRepository<>(sessionFactory, Qualification.class);
        IGenericRepository<Vehicle, Long> vehicleRepository =
                new GenericRepository<>(sessionFactory, Vehicle.class);
        IGenericRepository<TransportService, Long> transportServiceRepository =
                new GenericRepository<>(sessionFactory, TransportService.class);
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
        IGenericRepository<Truck, Long> truckRepository = new GenericRepository<>(sessionFactory, Truck.class);
        IGenericRepository<Bus, Long> busRepository = new GenericRepository<>(sessionFactory, Bus.class);
        IGenericRepository<Van, Long> vanRepository = new GenericRepository<>(sessionFactory, Van.class);

        // Initialize repositories ends ##

        // ## Data seeding
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()) // Handles LocalDateTime
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())         // Handles LocalDate
                .setPrettyPrinting()
                .create();

        // Load JSON from resources;
        String qualificationJsonFilePath = "data/data_qualifications.json";

        ISeeder<Qualification, Long> qualificationSeeder =
                new GenericSeeder<>(qualificationRepository, qualificationJsonFilePath, Qualification.class, gson, validator);

        try {
            qualificationSeeder.seed();
            logger.info("Initial seeding for {} completed successfully", Qualification.class.getName());
        } catch (Exception e) {
            logger.error("Seeding failed due to {}", e.getMessage());
        }

        String companiesJsonFilePath = "data/companies.json";
        ISeeder<TransportCompany, Long> companiesSeeder =
                new GenericSeeder<>(companyRepository, companiesJsonFilePath, TransportCompany.class, gson, validator);
        try {
            companiesSeeder.seed();
            logger.info("Initial seeding for {} completed successfully", TransportCompany.class.getName());
        } catch (Exception e) {
            logger.error("Seeding failed due to {}", e.getMessage());
        }


        // ## Initialize mappers

        BusMapper busMapper = new BusMapper(companyRepository);
        ClientMapper clientMapper = new ClientMapper();
        DestinationMapper destinationMapper = new DestinationMapper();
        DispatcherMapper dispatcherMapper = new DispatcherMapper(companyRepository, driverRepository);
        DriverMapper driverMapper = new DriverMapper(companyRepository, dispatcherRepository, qualificationRepository);
        EmployeeMapper employeeMapper = new EmployeeMapper();
        QualificationMapper qualificationMapper = new QualificationMapper();
        TransportCargoServiceMapper cargoServiceMapper = new TransportCargoServiceMapper(companyRepository, clientRepository, driverRepository, destinationRepository, vehicleRepository);
        TransportCompanyMapper companyMapper = new TransportCompanyMapper();
        TransportPassengersServiceMapper passengersServiceMapper = new TransportPassengersServiceMapper(companyRepository, clientRepository, driverRepository, vehicleRepository, destinationRepository);
        TransportServiceMapper transportServiceMapper = new TransportServiceMapper(companyRepository, clientRepository, driverRepository, destinationRepository, vehicleRepository);
        TruckMapper truckMapper = new TruckMapper(companyRepository);
        VanMapper vanMapper = new VanMapper(companyRepository);
        VehicleMapper vehicleMapper = new VehicleMapper(companyRepository);

        // Initialize mappers ends ##

        // ## Initiate serviceLayer

        ITransportCompanyService companyService =
                new TransportCompanyService(companyRepository, employeeRepository, vehicleRepository, transportServiceRepository, companyMapper, employeeMapper, vehicleMapper, transportServiceMapper, clientMapper);
        IClientService clientService =
                new ClientService(clientRepository, transportServiceRepository, clientMapper, transportServiceMapper);
        IDispatcherService dispatcherService =
                new DispatcherService(dispatcherRepository, driverRepository, dispatcherMapper, driverMapper);
        IDriverService driverService =
                new DriverService(driverRepository, companyRepository, dispatcherRepository, cargoServiceRepository, passengerServiceRepository, qualificationRepository, driverMapper, cargoServiceMapper, passengersServiceMapper);
        IQualificationService qualificationService =
                new QualificationService(qualificationRepository, driverRepository, qualificationMapper, driverMapper);
        ITransportCargoServiceService cargoServiceService =
                new TransportCargoServiceService(cargoServiceRepository, companyRepository, clientRepository, driverRepository, destinationRepository, vehicleRepository);
        ITransportPassengersServiceService passengerServiceService =
                new TransportPassengersServiceService(passengerServiceRepository, passengersServiceMapper);
        IDestinationService destinationService =
                new DestinationService(destinationRepository, transportServiceRepository, destinationMapper, transportServiceMapper);
        ITruckService truckService =
                new TruckService(truckRepository, companyRepository, cargoServiceRepository, truckMapper, cargoServiceMapper);
        IBusService busService =
                new BusService(busRepository, companyRepository, passengerServiceRepository, busMapper, passengersServiceMapper);
        IVanService vanService =
                new VanService(vanRepository, companyRepository, passengerServiceRepository, vanMapper, passengersServiceMapper);

        // ## Initiate engine
        IEngine engine = new ConsoleEngine(validator,
                companyService,
                clientService,
                dispatcherService,
                driverService,
                qualificationService,
                cargoServiceService,
                passengerServiceService,
                destinationService,
                truckService,
                busService,
                vanService);

        // ## Start Engine
        engine.start();

        // ## Clean up resources (Hibernate session factory)
        SessionFactoryUtil.shutdown();
    }
}