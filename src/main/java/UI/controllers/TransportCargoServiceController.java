package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.dto.transportservices.TransportCargoServiceCreateDTO;
import services.data.dto.transportservices.TransportCargoServiceUpdateDTO;
import services.data.dto.transportservices.TransportCargoServiceViewDTO;
import services.services.contracts.ITransportCargoServiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class TransportCargoServiceController {
    private static final Logger logger = LoggerFactory.getLogger(TransportCargoServiceController.class);

    private final ITransportCargoServiceService service;
    private final Validator validator;
    private final Scanner scanner;

    public TransportCargoServiceController(ITransportCargoServiceService service, Validator validator, Scanner scanner) {
        this.service = service;
        this.validator = validator;
        this.scanner = scanner;
        logger.info("TransportCargoServiceController initialized with dependencies");

    }

    public void handleMenu() {
        logger.info("Entering TransportCargoServiceController menu");
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            logger.debug("User selected menu option: {}", choice);
            if (choice == 0) {
                logger.info("Exiting TransportCargoServiceController menu");
                break;
            }
            processChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println(System.lineSeparator() + "--- Cargo Service Management ---" + System.lineSeparator());
        System.out.println("1. List all cargo services");
        System.out.println("2. Add a new cargo service");
        System.out.println("3. Update a cargo service");
        System.out.println("4. Delete a cargo service");
        System.out.println("5. Get cargo services by company");
        System.out.println("6. Get cargo services by client");
        System.out.println("7. Get cargo services by driver");
        System.out.println("8. Get active cargo services");
        System.out.println("0. Back to main menu");
        System.out.print("Enter your choice: ");
        logger.debug("Displayed Cargo Service Management menu");
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

    private void processChoice(int choice) {
        try {
            switch (choice) {
                case 1:
                    logger.info("Processing list all cargo services request");
                    listAllCargoServices();
                    break;
                case 2:
                    logger.info("Processing add new cargo service request");
                    addNewCargoService();
                    break;
                case 3:
                    logger.info("Processing update cargo service request");
                    updateCargoService();
                    break;
                case 4:
                    logger.info("Processing delete cargo service request");
                    deleteCargoService();
                    break;
                case 5:
                    logger.info("Processing get cargo services by company request");
                    getCargoServicesByCompany();
                    break;
                case 6:
                    logger.info("Processing get cargo services by client request");
                    getCargoServicesByClient();
                    break;
                case 7:
                    logger.info("Processing get cargo services by driver request");
                    getCargoServicesByDriver();
                    break;
                case 8:
                    logger.info("Processing get active cargo services request");
                    getActiveCargoServices();
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

    private void listAllCargoServices() {
        logger.debug("Fetching all cargo services");
        List<TransportCargoServiceViewDTO> services = service.getAll(0, Integer.MAX_VALUE, "id", true);
        if (services.isEmpty()) {
            logger.info("No cargo services found");
            System.out.println("No cargo services found.");
        } else {
            logger.info("Found {} cargo services", services.size());
            services.forEach(System.out::println);
        }
    }

    private void addNewCargoService() {
        System.out.print("Enter transport company ID: ");
        Long companyId;
        try {
            companyId = Long.parseLong(scanner.nextLine().trim());
            logger.debug("Received transport company ID: {}", companyId);
        } catch (NumberFormatException e) {
            logger.warn("Invalid transport company ID input: {}", e.getMessage());
            System.out.println("Invalid company ID. Aborting.");
            return;
        }

        System.out.print("Enter starting date (YYYY-MM-DD): ");
        LocalDate startingDate;
        try {
            startingDate = LocalDate.parse(scanner.nextLine().trim());
            logger.debug("Received starting date: {}", startingDate);
        } catch (Exception e) {
            logger.warn("Invalid starting date input: {}", e.getMessage());
            System.out.println("Invalid starting date. Aborting.");
            return;
        }

        System.out.print("Enter ending date (YYYY-MM-DD): ");
        LocalDate endingDate;
        try {
            endingDate = LocalDate.parse(scanner.nextLine().trim());
            logger.debug("Received ending date: {}", endingDate);
        } catch (Exception e) {
            logger.warn("Invalid ending date input: {}", e.getMessage());
            System.out.println("Invalid ending date. Aborting.");
            return;
        }

        System.out.print("Enter destination ID: ");
        Long destinationId;
        try {
            destinationId = Long.parseLong(scanner.nextLine().trim());
            logger.debug("Received destination ID: {}", destinationId);
        } catch (NumberFormatException e) {
            logger.warn("Invalid destination ID input: {}", e.getMessage());
            System.out.println("Invalid destination ID. Aborting.");
            return;
        }

        System.out.print("Enter client ID: ");
        Long clientId;
        try {
            clientId = Long.parseLong(scanner.nextLine().trim());
            logger.debug("Received client ID: {}", clientId);
        } catch (NumberFormatException e) {
            logger.warn("Invalid client ID input: {}", e.getMessage());
            System.out.println("Invalid client ID. Aborting.");
            return;
        }

        System.out.print("Enter price: ");
        BigDecimal price;
        try {
            price = new BigDecimal(scanner.nextLine().trim());
            logger.debug("Received price: {}", price);
        } catch (NumberFormatException e) {
            logger.warn("Invalid price input: {}", e.getMessage());
            System.out.println("Invalid price. Aborting.");
            return;
        }

        System.out.print("Enter vehicle ID: ");
        Long vehicleId;
        try {
            vehicleId = Long.parseLong(scanner.nextLine().trim());
            logger.debug("Received vehicle ID: {}", vehicleId);
        } catch (NumberFormatException e) {
            logger.warn("Invalid vehicle ID input: {}", e.getMessage());
            System.out.println("Invalid vehicle ID. Aborting.");
            return;
        }

        System.out.print("Enter driver ID: ");
        Long driverId;
        try {
            driverId = Long.parseLong(scanner.nextLine().trim());
            logger.debug("Received driver ID: {}", driverId);
        } catch (NumberFormatException e) {
            logger.warn("Invalid driver ID input: {}", e.getMessage());
            System.out.println("Invalid driver ID. Aborting.");
            return;
        }

        System.out.print("Enter weight in kilograms: ");
        BigDecimal weight;
        try {
            weight = new BigDecimal(scanner.nextLine().trim());
            logger.debug("Received weight: {}", weight);
        } catch (NumberFormatException e) {
            logger.warn("Invalid weight input: {}", e.getMessage());
            System.out.println("Invalid weight. Aborting.");
            return;
        }

        System.out.print("Enter length in centimeters: ");
        int length;
        try {
            length = Integer.parseInt(scanner.nextLine().trim());
            logger.debug("Received length: {}", length);
        } catch (NumberFormatException e) {
            logger.warn("Invalid length input: {}", e.getMessage());
            System.out.println("Invalid length. Aborting.");
            return;
        }

        System.out.print("Enter width in centimeters: ");
        int width;
        try {
            width = Integer.parseInt(scanner.nextLine().trim());
            logger.debug("Received width: {}", width);
        } catch (NumberFormatException e) {
            logger.warn("Invalid width input: {}", e.getMessage());
            System.out.println("Invalid width. Aborting.");
            return;
        }

        System.out.print("Enter height in centimeters: ");
        int height;
        try {
            height = Integer.parseInt(scanner.nextLine().trim());
            logger.debug("Received height: {}", height);
        } catch (NumberFormatException e) {
            logger.warn("Invalid height input: {}", e.getMessage());
            System.out.println("Invalid height. Aborting.");
            return;
        }

        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();
        logger.debug("Received description: {}", description);

        TransportCargoServiceCreateDTO createDTO = new TransportCargoServiceCreateDTO();
        createDTO.setTransportCompanyId(companyId);
        createDTO.setStartingDate(startingDate);
        createDTO.setEndingDate(endingDate);
        createDTO.setDestinationId(destinationId);
        createDTO.setClientId(clientId);
        createDTO.setPrice(price);
        createDTO.setVehicleId(vehicleId);
        createDTO.setDriverId(driverId);
        createDTO.setWeightInKilograms(weight);
        createDTO.setLengthInCentimeters(length);
        createDTO.setWidthInCentimeters(width);
        createDTO.setHeightInCentimeters(height);
        createDTO.setDescription(description);

        Set<ConstraintViolation<TransportCargoServiceCreateDTO>> violations = validator.validate(createDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for new cargo service: {}", createDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<TransportCargoServiceCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Creating new cargo service with DTO: {}", createDTO);
        TransportCargoServiceViewDTO created = service.create(createDTO);
        System.out.println("Cargo service created with ID: " + created.getId());
        logger.info("Cargo service created successfully with ID: {}", created.getId());
    }

    private void updateCargoService() {
        Long id = getLongInput("Enter cargo service ID to update: ");
        logger.debug("Fetching cargo service with ID: {}", id);
        TransportCargoServiceViewDTO serviceDTO = service.getById(id);

        if (serviceDTO == null) {
            logger.info("No cargo service found with ID: {}", id);
            System.out.println("Cargo service not found.");
            return;
        }

        Long companyId = getLongInputWithDefault("Enter new transport company ID (leave blank to keep " + serviceDTO.getTransportCompanyId() + "): ", serviceDTO.getTransportCompanyId());
        logger.debug("Received new transport company ID: {}", companyId);

        System.out.print("Enter new starting date (YYYY-MM-DD, leave blank to keep " + serviceDTO.getStartingDate() + "): ");
        String startingDateInput = scanner.nextLine().trim();
        LocalDate startingDate = serviceDTO.getStartingDate();
        if (!startingDateInput.isEmpty()) {
            try {
                startingDate = LocalDate.parse(startingDateInput);
                logger.debug("Received new starting date: {}", startingDate);
            } catch (Exception e) {
                logger.warn("Invalid new starting date input: {}", startingDateInput);
                System.out.println("Invalid starting date. Aborting.");
                return;
            }
        }

        System.out.print("Enter new ending date (YYYY-MM-DD, leave blank to keep " + serviceDTO.getEndingDate() + "): ");
        String endingDateInput = scanner.nextLine().trim();
        LocalDate endingDate = serviceDTO.getEndingDate();
        if (!endingDateInput.isEmpty()) {
            try {
                endingDate = LocalDate.parse(endingDateInput);
                logger.debug("Received new ending date: {}", endingDate);
            } catch (Exception e) {
                logger.warn("Invalid new ending date input: {}", endingDateInput);
                System.out.println("Invalid ending date. Aborting.");
                return;
            }
        }

        Long destinationId = getLongInputWithDefault("Enter new destination ID (leave blank to keep " + serviceDTO.getDestinationId() + "): ", serviceDTO.getDestinationId());
        logger.debug("Received new destination ID: {}", destinationId);

        Long clientId = getLongInputWithDefault("Enter new client ID (leave blank to keep " + serviceDTO.getClientId() + "): ", serviceDTO.getClientId());
        logger.debug("Received new client ID: {}", clientId);

        System.out.print("Enter new price (leave blank to keep " + serviceDTO.getPrice() + "): ");
        String priceInput = scanner.nextLine().trim();
        BigDecimal price = serviceDTO.getPrice();
        if (!priceInput.isEmpty()) {
            try {
                price = new BigDecimal(priceInput);
                logger.debug("Received new price: {}", price);
            } catch (NumberFormatException e) {
                logger.warn("Invalid new price input: {}", priceInput);
                System.out.println("Invalid price. Aborting.");
                return;
            }
        }

        Long vehicleId = getLongInputWithDefault("Enter new vehicle ID (leave blank to keep " + serviceDTO.getVehicleId() + "): ", serviceDTO.getVehicleId());
        logger.debug("Received new vehicle ID: {}", vehicleId);

        Long driverId = getLongInputWithDefault("Enter new driver ID (leave blank to keep " + serviceDTO.getDriverId() + "): ", serviceDTO.getDriverId());
        logger.debug("Received new driver ID: {}", driverId);

        System.out.print("Enter new weight in kilograms (leave blank to keep " + serviceDTO.getWeightInKilograms() + "): ");
        String weightInput = scanner.nextLine().trim();
        BigDecimal weight = serviceDTO.getWeightInKilograms();
        if (!weightInput.isEmpty()) {
            try {
                weight = new BigDecimal(weightInput);
                logger.debug("Received new weight: {}", weight);
            } catch (NumberFormatException e) {
                logger.warn("Invalid new weight input: {}", weightInput);
                System.out.println("Invalid weight. Aborting.");
                return;
            }
        }

        System.out.print("Enter new length in centimeters (leave blank to keep " + serviceDTO.getLengthInCentimeters() + "): ");
        String lengthInput = scanner.nextLine().trim();
        int length = serviceDTO.getLengthInCentimeters();
        if (!lengthInput.isEmpty()) {
            try {
                length = Integer.parseInt(lengthInput);
                logger.debug("Received new length: {}", length);
            } catch (NumberFormatException e) {
                logger.warn("Invalid new length input: {}", lengthInput);
                System.out.println("Invalid length. Aborting.");
                return;
            }
        }

        System.out.print("Enter new width in centimeters (leave blank to keep " + serviceDTO.getWidthInCentimeters() + "): ");
        String widthInput = scanner.nextLine().trim();
        int width = serviceDTO.getWidthInCentimeters();
        if (!widthInput.isEmpty()) {
            try {
                width = Integer.parseInt(widthInput);
                logger.debug("Received new width: {}", width);
            } catch (NumberFormatException e) {
                logger.warn("Invalid new width input: {}", widthInput);
                System.out.println("Invalid width. Aborting.");
                return;
            }
        }

        System.out.print("Enter new height in centimeters (leave blank to keep " + serviceDTO.getHeightInCentimeters() + "): ");
        String heightInput = scanner.nextLine().trim();
        int height = serviceDTO.getHeightInCentimeters();
        if (!heightInput.isEmpty()) {
            try {
                height = Integer.parseInt(heightInput);
                logger.debug("Received new height: {}", height);
            } catch (NumberFormatException e) {
                logger.warn("Invalid new height input: {}", heightInput);
                System.out.println("Invalid height. Aborting.");
                return;
            }
        }

        System.out.print("Enter new description (leave blank to keep '" + serviceDTO.getDescription() + "'): ");
        String description = scanner.nextLine().trim();
        logger.debug("Received new description: {}", description.isEmpty() ? "unchanged" : description);

        TransportCargoServiceUpdateDTO updateDTO = new TransportCargoServiceUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setTransportCompanyId(companyId);
        updateDTO.setStartingDate(startingDate);
        updateDTO.setEndingDate(endingDate);
        updateDTO.setDestinationId(destinationId);
        updateDTO.setClientId(clientId);
        updateDTO.setPrice(price);
        updateDTO.setVehicleId(vehicleId);
        updateDTO.setDriverId(driverId);
        updateDTO.setWeightInKilograms(weight);
        updateDTO.setLengthInCentimeters(length);
        updateDTO.setWidthInCentimeters(width);
        updateDTO.setHeightInCentimeters(height);
        updateDTO.setDescription(description.isEmpty() ? serviceDTO.getDescription() : description);

        Set<ConstraintViolation<TransportCargoServiceUpdateDTO>> violations = validator.validate(updateDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for updated cargo service: {}", updateDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<TransportCargoServiceUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Updating cargo service with ID {} using DTO: {}", id, updateDTO);
        service.update(updateDTO);
        System.out.println("Cargo service updated successfully.");
        logger.info("Cargo service with ID {} updated successfully", id);
    }

    private void deleteCargoService() {
        Long id = getLongInput("Enter cargo service ID to delete: ");
        logger.info("Deleting cargo service with ID: {}", id);
        service.delete(id);
        System.out.println("Cargo service deleted.");
        logger.info("Cargo service with ID {} deleted successfully", id);
    }

    private void getCargoServicesByCompany() {
        Long id = getLongInput("Enter company ID: ");
        logger.debug("Fetching cargo services for company ID: {}", id);
        List<TransportCargoServiceViewDTO> services = service.getByCompany(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            logger.info("No cargo services found for company ID: {}", id);
            System.out.println("No cargo services found.");
        } else {
            logger.info("Found {} cargo services for company ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private void getCargoServicesByClient() {
        Long id = getLongInput("Enter client ID: ");
        logger.debug("Fetching cargo services for client ID: {}", id);
        List<TransportCargoServiceViewDTO> services = service.getByClient(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            logger.info("No cargo services found for client ID: {}", id);
            System.out.println("No cargo services found.");
        } else {
            logger.info("Found {} cargo services for client ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private void getCargoServicesByDriver() {
        Long id = getLongInput("Enter driver ID: ");
        logger.debug("Fetching cargo services for driver ID: {}", id);
        List<TransportCargoServiceViewDTO> services = service.getByDriver(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            logger.info("No cargo services found for driver ID: {}", id);
            System.out.println("No cargo services found.");
        } else {
            logger.info("Found {} cargo services for driver ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private void getActiveCargoServices() {
        logger.debug("Fetching active cargo services");
        List<TransportCargoServiceViewDTO> services = service.getActiveServices(0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            logger.info("No active cargo services found");
            System.out.println("No active cargo services found.");
        } else {
            logger.info("Found {} active cargo services", services.size());
            services.forEach(System.out::println);
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

    private Long getLongInputWithDefault(String prompt, Long defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            logger.debug("Keeping default Long value: {}", defaultValue);
            return defaultValue;
        }
        try {
            Long value = Long.parseLong(input);
            logger.debug("Received new Long input: {}", value);
            return value;
        } catch (NumberFormatException e) {
            logger.warn("Invalid number input, reverting to default: {}", defaultValue);
            System.out.println("Invalid input. Keeping default value.");
            return defaultValue;
        }
    }
}