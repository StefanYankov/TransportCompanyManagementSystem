package UI.engines;

import java.util.Scanner;

import UI.controllers.DriverController;
import UI.controllers.TransportCompanyController;
import jakarta.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.services.contracts.IDriverService;
import services.services.contracts.IQualificationService;
import services.services.contracts.ITransportCompanyService;

public class ConsoleEngine implements IEngine {
    private final Validator validator;
    private final Scanner scanner;
    private final ITransportCompanyService transportCompanyService;
    private final IDriverService driverService;
    private final IQualificationService qualificationService;
    private static final Logger logger = LoggerFactory.getLogger(ConsoleEngine.class);

    public ConsoleEngine(Validator validator, ITransportCompanyService transportCompanyService,
                         IDriverService driverService,
                         IQualificationService qualificationService) {
        this.validator = validator;
        this.driverService = driverService;
        this.transportCompanyService = transportCompanyService;
        this.qualificationService = qualificationService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the console UI loop, displaying the menu and processing user commands.
     */
    @Override
    public void start() {

        while (true) {
            displayMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
//                    case "1":
//                        createClient();
//                        break;
//                    case "2":
//                        updateClient();
//                        break;
//                    case "3":
//                        deleteClient();
//                        break;
//                    case "4":
//                        viewClientById();
//                        break;
//                    case "5":
//                        listAllClients();
//                        break;
                    case "0":
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 0 and 5.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println(System.lineSeparator() + "Press Enter to continue...");
            scanner.nextLine();
        }


    }

    private void displayMenu() {
        System.out.println("=== Main menu ===");
        System.out.println("1. Transport Company Controller");
        System.out.println("2. Client Controller");
        System.out.println("3. Vehicle Controller");
        System.out.println("4. Employee Controller");
        System.out.println("5. Transport Service Controller");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }
}

