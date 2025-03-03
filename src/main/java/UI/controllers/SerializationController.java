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
import java.util.List;
import java.util.Scanner;

public class SerializationController {
    private static final Logger logger = LoggerFactory.getLogger(SerializationController.class);

    private final ITransportCompanyService transportCompanyService;
    private final IServiceSerializer<ITransportCompanyService, TransportServiceViewDTO, TransportServiceCreateDTO> transportCompanySerializer;
    private final IServiceSerializer<ITransportPassengersServiceService, TransportPassengersServiceViewDTO, TransportPassengersServiceCreateDTO> passengerSerializer;
    private final IServiceSerializer<ITransportCargoServiceService, TransportCargoServiceViewDTO, TransportCargoServiceCreateDTO> cargoSerializer;
    private final Validator validator;
    private final Scanner scanner;

    public SerializationController(ITransportCompanyService transportServiceService,
                                   IServiceSerializer<ITransportCompanyService, TransportServiceViewDTO, TransportServiceCreateDTO> transportCompanySerializer,
                                   IServiceSerializer<ITransportPassengersServiceService, TransportPassengersServiceViewDTO, TransportPassengersServiceCreateDTO> passengerSerializer,
                                   IServiceSerializer<ITransportCargoServiceService, TransportCargoServiceViewDTO, TransportCargoServiceCreateDTO> cargoSerializer,
                                   Validator validator,
                                   Scanner scanner) {
        this.transportCompanyService = transportServiceService;
        this.transportCompanySerializer = transportCompanySerializer;
        this.passengerSerializer = passengerSerializer;
        this.cargoSerializer = cargoSerializer;
        this.validator = validator;
        this.scanner = scanner;
    }

    public void handleMenu() {
        while (true) {
            displayMenu();
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice == 0) {
                    logger.info("Exiting Serialization menu");
                    break;
                }
                processChoice(choice);
            } catch (NumberFormatException e) {
                logger.warn("Invalid input received: {}", input);
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void displayMenu() {
        System.out.println(System.lineSeparator() + "--- Serialization ---" + System.lineSeparator());
        System.out.println("1. Export Transport Services");
        System.out.println("2. Import Transport Passenger Services");
        System.out.println("3. Import Transport Cargo Services");
        System.out.println("0. Back to Main Menu");
    }

    private void processChoice(int choice) {
        switch (choice) {
            case 1:
                exportTransportServices();
                break;
            case 2:
                importTransportPassengersServices();
                break;
            case 3:
                importTransportCargoServices();
                break;
            default:
                logger.warn("Invalid menu choice: {}", choice);
                System.out.println("Invalid choice. Please try again.");
        }
    }



    private void exportTransportServices() {
        Long companyId = getLongInput("Enter company ID to export transport services for: ");

        System.out.print("Enter file name to export to (e.g., transport_services_company1.json): ");
        String fileName = scanner.nextLine().trim();
        String filePath = fileName;

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
//        System.out.print("Enter file name to import from (e.g., transport_services_company1.json): ");
//        String fileName = scanner.nextLine().trim();
//        String filePath = fileName;
//
//        try {
//            logger.info("Reading file: {}", filePath);
//            String json = new String(Files.readAllBytes(Paths.get(filePath)));
//
//            logger.info("Deserializing transport services from JSON");
//            List<TransportPassengersServiceCreateDTO> dtos = passengerSerializer.deserializeListFromJson(json);
//            if (dtos.isEmpty()) {
//                logger.info("No transport services found in file: {}", filePath);
//                System.out.println("No transport services found in the file.");
//                return;
//            }
//
//            logger.info("Importing {} transport services", dtos.size());
//            for (TransportPassengersServiceCreateDTO dto : dtos) {
//                transportCompanyService.create(dto); // Assuming create method exists
//                logger.debug("Imported service: {}", dto);
//            }
//
//            System.out.println("Transport services imported successfully. " + dtos.size() + " service(s) added.");
//            System.out.println("Imported Data:" + System.lineSeparator() + json);
//            logger.info("Import completed successfully from {}", filePath);
//        } catch (Exception e) {
//            logger.error("Error importing transport services: {}", e.getMessage(), e);
//            System.out.println("Error importing transport services: " + e.getMessage());
//        }
    }
    private void importTransportCargoServices() {
    }

    private Long getLongInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                logger.warn("Invalid number input: {}", input);
                System.out.println("Please enter a valid number.");
            }
        }
    }
}