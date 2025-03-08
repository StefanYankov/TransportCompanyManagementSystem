package UI.controllers;

import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.IO.IServiceSerializer;
import services.data.dto.transportservices.*;
import services.services.contracts.ITransportCargoServiceService;
import services.services.contracts.ITransportCompanyService;
import services.services.contracts.ITransportPassengersServiceService;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class SerializationController {
    private static final Logger logger = LoggerFactory.getLogger(SerializationController.class);

    private final ITransportCompanyService transportCompanyService;
    private final ITransportPassengersServiceService transportPassengersServiceService;
    private final ITransportCargoServiceService transportCargoServiceService;
    private final IServiceSerializer<ITransportCompanyService, TransportServiceViewDTO, TransportServiceCreateDTO> transportCompanySerializer;
    private final IServiceSerializer<ITransportPassengersServiceService, TransportPassengersServiceViewDTO, TransportPassengersServiceCreateDTO> passengerSerializer;
    private final IServiceSerializer<ITransportCargoServiceService, TransportCargoServiceViewDTO, TransportCargoServiceCreateDTO> cargoSerializer;
    private final Validator validator;
    private final Scanner scanner;

    public SerializationController(ITransportCompanyService transportServiceService,
                                   ITransportPassengersServiceService transportPassengersServiceService,
                                   ITransportCargoServiceService transportCargoServiceService,
                                   IServiceSerializer<ITransportCompanyService, TransportServiceViewDTO, TransportServiceCreateDTO> transportCompanySerializer,
                                   IServiceSerializer<ITransportPassengersServiceService, TransportPassengersServiceViewDTO, TransportPassengersServiceCreateDTO> passengerSerializer,
                                   IServiceSerializer<ITransportCargoServiceService, TransportCargoServiceViewDTO, TransportCargoServiceCreateDTO> cargoSerializer,
                                   Validator validator,
                                   Scanner scanner) {
        this.transportCompanyService = transportServiceService;
        this.transportPassengersServiceService = transportPassengersServiceService;
        this.transportCargoServiceService = transportCargoServiceService;
        this.transportCompanySerializer = transportCompanySerializer;
        this.passengerSerializer = passengerSerializer;
        this.cargoSerializer = cargoSerializer;
        this.validator = validator;
        this.scanner = scanner;
        logger.info("{} initialized with dependencies", this.getClass().getSimpleName());
    }

    private int getUserChoice() {
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

    public void handleMenu() {
        logger.info("Entering SerializationController menu");
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            logger.debug("User selected menu option: {}", choice);
            if (choice == 0) {
                logger.info("Exiting SerializationController menu");
                break;
            }
            processChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println(System.lineSeparator() + "--- Serialization ---" + System.lineSeparator());
        System.out.println("1. Export Transport Services");
        System.out.println("2. Import Transport Passenger Services");
        System.out.println("3. Import Transport Cargo Services");
        System.out.println("0. Back to Main Menu");
        logger.debug("Displayed Serialization menu");
    }

    private void processChoice(int choice) {
        try {
            switch (choice) {
                case 1:
                    logger.info("Processing export transport services request");
                    exportTransportServices();
                    break;
                case 2:
                    logger.info("Processing import transport passenger services request");
                    importTransportPassengersServices();
                    break;
                case 3:
                    logger.info("Processing import transport cargo services request");
                    importTransportCargoServices();
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

    private void exportTransportServices() {
        Long companyId = getLongInput("Enter company ID to export transport services for: ");
        logger.debug("Received company ID for export: {}", companyId);

        System.out.print("Enter file name to export to (e.g., transport_services_company1.json): ");
        String fileName = scanner.nextLine().trim();
        String filePath = fileName;
        logger.debug("Received export file name: {}", fileName);

        try {
            logger.info("Fetching transport services for company ID: {}", companyId);
            List<TransportServiceViewDTO> services = transportCompanyService.getTransportServicesByCompany(companyId);
            if (services.isEmpty()) {
                logger.info("No transport services found for company ID: {}", companyId);
                System.out.println("No transport services available to export.");
                return;
            }

            logger.info("Serializing {} transport services to JSON", services.size());
            String json = transportCompanySerializer.serializeToJson(services);

            logger.debug("Writing JSON to file: {}", filePath);
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(json);
            }

            System.out.println("Transport services exported successfully to " + filePath);
            System.out.println("Exported Data:" + System.lineSeparator() + json);
            logger.info("Export completed successfully to {}", filePath);
        } catch (Exception e) {
            logger.error("Error exporting transport services: {}", e.getMessage(), e);
            System.out.println("Error exporting transport services: " + e.getMessage());
        }
    }

    private void importTransportPassengersServices() {
        System.out.print("Enter file name to import from (e.g., transport_passenger_services1.json): ");
        String fileName = scanner.nextLine().trim();
        String filePath = fileName;
        logger.debug("Received import file name for passenger services: {}", fileName);

        try {
            logger.info("Reading file: {}", filePath);
            String json = new String(Files.readAllBytes(Paths.get(filePath)));

            logger.info("Deserializing transport passenger services from JSON");
            List<TransportPassengersServiceCreateDTO> dtos = passengerSerializer.deserializeListFromJson(json);
            if (dtos.isEmpty()) {
                logger.info("No transport passenger services found in file: {}", filePath);
                System.out.println("No transport passenger services found in the file.");
                return;
            }

            logger.info("Importing {} transport passenger services", dtos.size());
            for (TransportPassengersServiceCreateDTO dto : dtos) {
                transportPassengersServiceService.create(dto);
                logger.debug("Imported passenger service: {}", dto);
            }

            System.out.println("Transport services imported successfully. " + dtos.size() + " service(s) added.");
            System.out.println("Imported Data:" + System.lineSeparator() + json);
            logger.info("Passenger services import completed successfully from {}", filePath);
        } catch (Exception e) {
            logger.error("Error importing transport passenger services: {}", e.getMessage(), e);
            System.out.println("Error importing transport passenger services: " + e.getMessage());
        }
    }

    private void importTransportCargoServices() {
        System.out.print("Enter file name to import from (e.g., transport_cargo_services1.json): ");
        String fileName = scanner.nextLine().trim();
        String filePath = fileName;
        logger.debug("Received import file name for cargo services: {}", fileName);

        try {
            logger.info("Reading file: {}", filePath);
            String json = new String(Files.readAllBytes(Paths.get(filePath)));

            logger.info("Deserializing transport cargo services from JSON");
            List<TransportCargoServiceCreateDTO> dtos = cargoSerializer.deserializeListFromJson(json);
            if (dtos.isEmpty()) {
                logger.info("No transport cargo services found in file: {}", filePath);
                System.out.println("No transport cargo services found in the file.");
                return;
            }

            logger.info("Importing {} transport cargo services", dtos.size());
            for (TransportCargoServiceCreateDTO dto : dtos) {
                transportCargoServiceService.create(dto);
                logger.debug("Imported cargo service: {}", dto);
            }

            System.out.println("Transport cargo services imported successfully. " + dtos.size() + " service(s) added.");
            System.out.println("Imported Data:" + System.lineSeparator() + json);
            logger.info("Cargo services import completed successfully from {}", filePath);
        } catch (Exception e) {
            logger.error("Error importing transport cargo services: {}", e.getMessage(), e);
            System.out.println("Error importing transport cargo services: " + e.getMessage());
        }
    }

    private Long getLongInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                Long value = Long.parseLong(input);
                logger.debug("Received valid Long input: {}", value);
                return value;
            } catch (NumberFormatException e) {
                logger.warn("Invalid number input: {}", input);
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}