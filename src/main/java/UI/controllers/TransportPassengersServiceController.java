package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.dto.transportservices.TransportPassengersServiceCreateDTO;
import services.data.dto.transportservices.TransportPassengersServiceUpdateDTO;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;
import services.services.contracts.ITransportPassengersServiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class TransportPassengersServiceController {
    private static final Logger logger = LoggerFactory.getLogger(TransportPassengersServiceController.class);

    private final ITransportPassengersServiceService service;
    private final Validator validator;
    private final Scanner scanner;

    public TransportPassengersServiceController(ITransportPassengersServiceService service, Validator validator, Scanner scanner) {
        this.service = service;
        this.validator = validator;
        this.scanner = scanner;
        logger.info("{} initialized with dependencies", this.getClass().getSimpleName());
    }

    public void handleMenu() {
        logger.info("Entering TransportPassengersServiceController menu");
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            logger.debug("User selected menu option: {}", choice);
            if (choice == 0) {
                logger.info("Exiting TransportPassengersServiceController menu");
                break;
            }
            processChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println(System.lineSeparator() + "--- Passenger Service Management ---" + System.lineSeparator());
        System.out.println("1. List all passenger services");
        System.out.println("2. Add a new passenger service");
        System.out.println("3. Update a passenger service");
        System.out.println("4. Delete a passenger service");
        System.out.println("5. Get transports sorted by destination");
        System.out.println("6. Get total transport count");
        System.out.println("7. Get active services");
        System.out.println("8. Get services by driver");
        System.out.println("9. Get services by client");
        System.out.println("10. Get services by company");
        System.out.println("0. Back to main menu");
        System.out.print("Enter your choice: ");
        logger.debug("Displayed Passenger Service Management menu");
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
                    logger.info("Processing list all passenger services request");
                    listAllPassengerServices();
                    break;
                case 2:
                    logger.info("Processing add new passenger service request");
                    addNewPassengerService();
                    break;
                case 3:
                    logger.info("Processing update passenger service request");
                    updatePassengerService();
                    break;
                case 4:
                    logger.info("Processing delete passenger service request");
                    deletePassengerService();
                    break;
                case 5:
                    logger.info("Processing get transports sorted by destination request");
                    getTransportsSortedByDestination();
                    break;
                case 6:
                    logger.info("Processing get total transport count request");
                    getTotalTransportCount();
                    break;
                case 7:
                    logger.info("Processing get active services request");
                    getActiveServices();
                    break;
                case 8:
                    logger.info("Processing get services by driver request");
                    getServicesByDriver();
                    break;
                case 9:
                    logger.info("Processing get services by client request");
                    getServicesByClient();
                    break;
                case 10:
                    logger.info("Processing get services by company request");
                    getServicesByCompany();
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

    private void listAllPassengerServices() {
        logger.debug("Fetching all passenger services");
        List<TransportPassengersServiceViewDTO> services = service.getAll(0, Integer.MAX_VALUE, "id", true, "");
        if (services.isEmpty()) {
            logger.info("No passenger services found");
            System.out.println("No passenger services found.");
        } else {
            logger.info("Found {} passenger services", services.size());
            services.forEach(System.out::println);
        }
    }

    private void addNewPassengerService() {
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

        System.out.print("Enter number of passengers: ");
        int passengers;
        try {
            passengers = Integer.parseInt(scanner.nextLine().trim());
            logger.debug("Received number of passengers: {}", passengers);
        } catch (NumberFormatException e) {
            logger.warn("Invalid number of passengers input: {}", e.getMessage());
            System.out.println("Invalid number of passengers. Aborting.");
            return;
        }

        TransportPassengersServiceCreateDTO createDTO = new TransportPassengersServiceCreateDTO();
        createDTO.setTransportCompanyId(companyId);
        createDTO.setStartingDate(startingDate);
        createDTO.setEndingDate(endingDate);
        createDTO.setDestinationId(destinationId);
        createDTO.setClientId(clientId);
        createDTO.setPrice(price);
        createDTO.setVehicleId(vehicleId);
        createDTO.setDriverId(driverId);
        createDTO.setNumberOfPassengers(passengers);

        Set<ConstraintViolation<TransportPassengersServiceCreateDTO>> violations = validator.validate(createDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for new passenger service: {}", createDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<TransportPassengersServiceCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Creating new passenger service with DTO: {}", createDTO);
        TransportPassengersServiceViewDTO created = service.create(createDTO);
        System.out.println("Passenger service created with ID: " + created.getId());
        logger.info("Passenger service created successfully with ID: {}", created.getId());
    }

    private void updatePassengerService() {
        Long id = getLongInput("Enter passenger service ID to update: ");
        logger.debug("Fetching passenger service with ID: {}", id);
        TransportPassengersServiceViewDTO serviceDTO = service.getById(id);

        if (serviceDTO == null) {
            logger.info("No passenger service found with ID: {}", id);
            System.out.println("Passenger service not found.");
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

        System.out.print("Enter new number of passengers (leave blank to keep " + serviceDTO.getNumberOfPassengers() + "): ");
        String passengersInput = scanner.nextLine().trim();
        int passengers = serviceDTO.getNumberOfPassengers();
        if (!passengersInput.isEmpty()) {
            try {
                passengers = Integer.parseInt(passengersInput);
                logger.debug("Received new number of passengers: {}", passengers);
            } catch (NumberFormatException e) {
                logger.warn("Invalid new number of passengers input: {}", passengersInput);
                System.out.println("Invalid number of passengers. Aborting.");
                return;
            }
        }

        TransportPassengersServiceUpdateDTO updateDTO = new TransportPassengersServiceUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setTransportCompanyId(companyId);
        updateDTO.setStartingDate(startingDate);
        updateDTO.setEndingDate(endingDate);
        updateDTO.setDestinationId(destinationId);
        updateDTO.setClientId(clientId);
        updateDTO.setPrice(price);
        updateDTO.setVehicleId(vehicleId);
        updateDTO.setDriverId(driverId);
        updateDTO.setNumberOfPassengers(passengers);

        Set<ConstraintViolation<TransportPassengersServiceUpdateDTO>> violations = validator.validate(updateDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for updated passenger service: {}", updateDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<TransportPassengersServiceUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Updating passenger service with ID {} using DTO: {}", id, updateDTO);
        service.update(updateDTO);
        System.out.println("Passenger service updated successfully.");
        logger.info("Passenger service with ID {} updated successfully", id);
    }

    private void deletePassengerService() {
        Long id = getLongInput("Enter passenger service ID to delete: ");
        logger.info("Deleting passenger service with ID: {}", id);
        service.delete(id);
        System.out.println("Passenger service deleted.");
        logger.info("Passenger service with ID {} deleted successfully", id);
    }

    private void getTransportsSortedByDestination() {
        System.out.print("Sort ascending? (yes/no): ");
        String sortInput = scanner.nextLine().trim();
        boolean ascending = sortInput.equalsIgnoreCase("yes");
        logger.debug("Received sort order: {}", ascending ? "ascending" : "descending");

        logger.debug("Fetching transports sorted by destination");
        List<TransportPassengersServiceViewDTO> services = service.getTransportsSortedByDestination(0, Integer.MAX_VALUE, ascending);
        if (services.isEmpty()) {
            logger.info("No passenger services found when sorted by destination");
            System.out.println("No passenger services found.");
        } else {
            logger.info("Found {} passenger services sorted by destination", services.size());
            services.forEach(System.out::println);
        }
    }

    private void getTotalTransportCount() {
        logger.debug("Fetching total transport count");
        int count = service.getTotalTransportCount();
        System.out.println("Total transport count: " + count);
        logger.info("Retrieved total transport count: {}", count);
    }

    private void getActiveServices() {
        logger.debug("Fetching active passenger services");
        List<TransportPassengersServiceViewDTO> services = service.getActiveServices(0, Integer.MAX_VALUE, "id", true);
        if (services.isEmpty()) {
            logger.info("No active passenger services found");
            System.out.println("No active passenger services found.");
        } else {
            logger.info("Found {} active passenger services", services.size());
            services.forEach(System.out::println);
        }
    }

    private void getServicesByDriver() {
        Long id = getLongInput("Enter driver ID: ");
        logger.debug("Fetching passenger services for driver ID: {}", id);
        List<TransportPassengersServiceViewDTO> services = service.getByDriver(id, 0, Integer.MAX_VALUE, "id", true);
        if (services.isEmpty()) {
            logger.info("No passenger services found for driver ID: {}", id);
            System.out.println("No passenger services found.");
        } else {
            logger.info("Found {} passenger services for driver ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private void getServicesByClient() {
        Long id = getLongInput("Enter client ID: ");
        logger.debug("Fetching passenger services for client ID: {}", id);
        List<TransportPassengersServiceViewDTO> services = service.getByClient(id, 0, Integer.MAX_VALUE, "id", true);
        if (services.isEmpty()) {
            logger.info("No passenger services found for client ID: {}", id);
            System.out.println("No passenger services found.");
        } else {
            logger.info("Found {} passenger services for client ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private void getServicesByCompany() {
        Long id = getLongInput("Enter company ID: ");
        logger.debug("Fetching passenger services for company ID: {}", id);
        List<TransportPassengersServiceViewDTO> services = service.getByCompany(id, 0, Integer.MAX_VALUE, "id", true);
        if (services.isEmpty()) {
            logger.info("No passenger services found for company ID: {}", id);
            System.out.println("No passenger services found.");
        } else {
            logger.info("Found {} passenger services for company ID: {}", services.size(), id);
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