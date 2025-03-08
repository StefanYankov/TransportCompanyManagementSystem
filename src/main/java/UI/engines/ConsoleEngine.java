package UI.engines;

import UI.controllers.*;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.IO.IServiceSerializer;
import services.IO.ServiceSerializer;
import services.data.dto.transportservices.*;
import services.services.contracts.*;

import java.util.Scanner;

public class ConsoleEngine implements IEngine {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleEngine.class);
    private static final Scanner scanner = new Scanner(System.in);

    private final Validator validator;
    private final ITransportCompanyService companyService;
    private final IClientService clientService;
    private final IDispatcherService dispatcherService;
    private final IDriverService driverService;
    private final IQualificationService qualificationService;
    private final ITransportCargoServiceService cargoServiceService;
    private final ITransportPassengersServiceService passengerServiceService;
    private final IDestinationService destinationService;
    private final ITruckService truckService;
    private final IBusService busService;
    private final IVanService vanService;


    public ConsoleEngine(Validator validator,
                         ITransportCompanyService companyService,
                         IClientService clientService,
                         IDispatcherService dispatcherService,
                         IDriverService driverService,
                         IQualificationService qualificationService,
                         ITransportCargoServiceService cargoServiceService,
                         ITransportPassengersServiceService passengerServiceService,
                         IDestinationService destinationService,
                         ITruckService truckService,
                         IBusService busService,
                         IVanService vanService) {
        this.validator = validator;
        this.companyService = companyService;
        this.clientService = clientService;
        this.dispatcherService = dispatcherService;
        this.driverService = driverService;
        this.qualificationService = qualificationService;
        this.cargoServiceService = cargoServiceService;
        this.passengerServiceService = passengerServiceService;
        this.destinationService = destinationService;
        this.truckService = truckService;
        this.busService = busService;
        this.vanService = vanService;
        logger.info("ConsoleEngine initialized with dependencies");
    }


    @Override
    public void start() {
        // ## Initiate UI
        logger.info("Starting ConsoleEngine");

        // Initialize controllers
        TransportCompanyController companyController = new TransportCompanyController(companyService, validator, scanner);
        ClientController clientController = new ClientController(clientService, validator, scanner);
        DispatcherController dispatcherController = new DispatcherController(dispatcherService, validator, scanner);
        DriverController driverController = new DriverController(driverService, validator, scanner);
        QualificationController qualificationController = new QualificationController(qualificationService, validator, scanner);
        TransportCargoServiceController cargoServiceController = new TransportCargoServiceController(cargoServiceService, validator, scanner);
        TransportPassengersServiceController passengerServiceController = new TransportPassengersServiceController(passengerServiceService, validator, scanner);
        DestinationController destinationController = new DestinationController(destinationService, validator, scanner);
        TruckController truckController = new TruckController(truckService, validator, scanner);
        BusController busController = new BusController(busService, validator, scanner);
        VanController vanController = new VanController(vanService, validator, scanner);

        IServiceSerializer<ITransportCompanyService, TransportServiceViewDTO, TransportServiceCreateDTO> transportCompanySerializer
                = new ServiceSerializer<>(companyService, TransportServiceViewDTO.class, TransportServiceCreateDTO.class);
        IServiceSerializer<ITransportPassengersServiceService, TransportPassengersServiceViewDTO, TransportPassengersServiceCreateDTO> passengerSerializer
                = new ServiceSerializer<>(passengerServiceService, TransportPassengersServiceViewDTO.class, TransportPassengersServiceCreateDTO.class);
        IServiceSerializer<ITransportCargoServiceService, TransportCargoServiceViewDTO, TransportCargoServiceCreateDTO> cargoSerializer
                = new ServiceSerializer<>(cargoServiceService, TransportCargoServiceViewDTO.class, TransportCargoServiceCreateDTO.class);

        SerializationController serializationController =
                new SerializationController(
                        companyService,
                        passengerServiceService,
                        cargoServiceService,
                        transportCompanySerializer,
                        passengerSerializer,cargoSerializer,
                        validator,
                        scanner);

        logger.info("All controllers and serializers initialized");

        while (true) {
            displayMenu();
            int choice = getUserChoice();
            logger.debug("User selected menu option: {}", choice);
            if (choice == 0) {
                logger.info("Exiting ConsoleEngine");
                break;
            }
            processChoice(choice, companyController, clientController, dispatcherController, driverController,
                    qualificationController, cargoServiceController, passengerServiceController,
                    destinationController, truckController, busController, vanController, serializationController);
        }
        System.out.println("Exiting application...");
        logger.info("ConsoleEngine shut down");
    }

    private static void displayMenu() {
        System.out.println(System.lineSeparator() + "=== Transport Management System ===" + System.lineSeparator());
        System.out.println("1. Manage Transport Companies");
        System.out.println("2. Manage Clients");
        System.out.println("3. Manage Dispatchers");
        System.out.println("4. Manage Drivers");
        System.out.println("5. Manage Qualifications");
        System.out.println("6. Manage Cargo Services");
        System.out.println("7. Manage Passenger Services");
        System.out.println("8. Manage Destinations");
        System.out.println("9. Manage Trucks");
        System.out.println("10. Manage Buses");
        System.out.println("11. Manage Vans");
        System.out.println("12. Manage Serialization");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
        logger.debug("Displayed main menu");
    }

    private static int getUserChoice() {
        try {
            String input = scanner.nextLine().trim();
            int choice = Integer.parseInt(input);
            logger.debug("Received valid menu choice: {}", choice);
            return choice;
        } catch (NumberFormatException e) {
            logger.warn("Invalid menu input received: {}", e.getMessage());
            return -1;
        }
    }

    private static void processChoice(int choice, TransportCompanyController companyController,
                                      ClientController clientController, DispatcherController dispatcherController,
                                      DriverController driverController, QualificationController qualificationController,
                                      TransportCargoServiceController cargoServiceController,
                                      TransportPassengersServiceController passengerServiceController,
                                      DestinationController destinationController, TruckController truckController,
                                      BusController busController, VanController vanController,
                                      SerializationController serializationController) {
        try {
            switch (choice) {
                case 1:
                    logger.info("Processing transport companies management request");
                    companyController.handleMenu();
                    break;
                case 2:
                    logger.info("Processing clients management request");
                    clientController.handleMenu();
                    break;
                case 3:
                    logger.info("Processing dispatchers management request");
                    dispatcherController.handleMenu();
                    break;
                case 4:
                    logger.info("Processing drivers management request");
                    driverController.handleMenu();
                    break;
                case 5:
                    logger.info("Processing qualifications management request");
                    qualificationController.handleMenu();
                    break;
                case 6:
                    logger.info("Processing cargo services management request");
                    cargoServiceController.handleMenu();
                    break;
                case 7:
                    logger.info("Processing passenger services management request");
                    passengerServiceController.handleMenu();
                    break;
                case 8:
                    logger.info("Processing destinations management request");
                    destinationController.handleMenu();
                    break;
                case 9:
                    logger.info("Processing trucks management request");
                    truckController.handleMenu();
                    break;
                case 10:
                    logger.info("Processing buses management request");
                    busController.handleMenu();
                    break;
                case 11:
                    logger.info("Processing vans management request");
                    vanController.handleMenu();
                    break;
                case 12:
                    logger.info("Processing serialization management request");
                    serializationController.handleMenu();
                    break;
                default:
                    logger.warn("Invalid choice received: {}", choice);
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            logger.error("Error processing choice {}: {}", choice, e.getMessage(), e);
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}