package UI.engines;

import UI.controllers.*;
import jakarta.validation.Validator;
import services.IO.IServiceSerializer;
import services.IO.ServiceSerializer;
import services.data.dto.transportservices.*;
import services.services.contracts.*;

import java.util.Scanner;

public class ConsoleEngine implements IEngine {
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
    }


    @Override
    public void start() {
        // ## Initiate UI

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
                        transportCompanySerializer,
                        passengerSerializer,cargoSerializer,validator, scanner);

        while (true) {
            displayMenu();
            int choice = getUserChoice();
            if (choice == 0) {
                break;
            }
            processChoice(choice, companyController, clientController, dispatcherController, driverController,
                    qualificationController, cargoServiceController, passengerServiceController,
                    destinationController, truckController, busController, vanController, serializationController);
        }
        System.out.println("Exiting application...");
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
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
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
        switch (choice) {
            case 1:
                companyController.handleMenu();
                break;
            case 2:
                clientController.handleMenu();
                break;
            case 3:
                dispatcherController.handleMenu();
                break;
            case 4:
                driverController.handleMenu();
                break;
            case 5:
                qualificationController.handleMenu();
                break;
            case 6:
                cargoServiceController.handleMenu();
                break;
            case 7:
                passengerServiceController.handleMenu();
                break;
            case 8:
                destinationController.handleMenu();
                break;
            case 9:
                truckController.handleMenu();
                break;
            case 10:
                busController.handleMenu();
                break;
            case 11:
                vanController.handleMenu();
                break;
            case 12:
                serializationController.handleMenu();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
}