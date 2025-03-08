package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.dto.vehicles.BusCreateDTO;
import services.data.dto.vehicles.BusUpdateDTO;
import services.data.dto.vehicles.BusViewDTO;
import services.services.contracts.IBusService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class BusController {
    private static final Logger logger = LoggerFactory.getLogger(BusController.class);

    private final IBusService service;
    private final Validator validator;
    private final Scanner scanner;

    public BusController(IBusService service, Validator validator, Scanner scanner) {
        this.service = service;
        this.validator = validator;
        this.scanner = scanner;
        logger.info("{} initialized with dependencies", this.getClass().getSimpleName());
    }

    public void handleMenu() {
        logger.info("Entering {} menu", this.getClass().getSimpleName());

        while (true) {
            displayMenu();
            int choice = getUserChoice();
            logger.debug("User selected menu option: {}", choice);

            if (choice == 0) {
                logger.info("Exiting {} menu", this.getClass().getSimpleName());
                break;
            }
            processChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println(System.lineSeparator() + "--- Bus Management ---" + System.lineSeparator());
        System.out.println("1. List all buses");
        System.out.println("2. Add a new bus");
        System.out.println("3. Update a bus");
        System.out.println("4. Delete a bus");
        System.out.println("5. Get transport services for bus");
        System.out.println("6. Get active transport services");
        System.out.println("7. Get buses by company");
        System.out.println("0. Back to main menu");
        System.out.print("Enter your choice: ");
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
                    logger.info("Processing list all buses request");
                    listAllBuses();
                    break;
                case 2:
                    logger.info("Processing add new bus request");
                    addNewBus();
                    break;
                case 3:
                    logger.info("Processing update bus request");
                    updateBus();
                    break;
                case 4:
                    logger.info("Processing delete bus request");
                    deleteBus();
                    break;
                case 5:
                    logger.info("Processing get transport services for bus request");
                    getTransportServicesForBus();
                    break;
                case 6:
                    logger.info("Processing get active transport services request");
                    getActiveTransportServices();
                    break;
                case 7:
                    logger.info("Processing get buses by company request");
                    getBusesByCompany();
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

    private void listAllBuses() {
        logger.debug("Fetching all buses");
        List<BusViewDTO> buses = service.getAll(0, Integer.MAX_VALUE, "registrationPlate", true);
        if (buses.isEmpty()) {
            logger.info("No buses found");
            System.out.println("No buses found.");
        } else {
            logger.info("Found {} buses", buses.size());
            buses.forEach(System.out::println);
        }
    }

    private void addNewBus() {
        System.out.print("Enter registration plate: ");
        String regPlate = scanner.nextLine().trim();
        logger.debug("Received registration plate: {}", regPlate);

        System.out.print("Enter transport company ID: ");
        Long companyId = Long.parseLong(scanner.nextLine().trim());
        logger.debug("Received transport company ID: {}", companyId);

        System.out.print("Enter max passenger capacity: ");
        String maxCapacityInput = scanner.nextLine().trim();
        Integer maxCapacity;
        try {
            maxCapacity = Integer.parseInt(maxCapacityInput);
            logger.debug("Received max passenger capacity: {}", maxCapacity);
        } catch (NumberFormatException e) {
            logger.warn("Invalid max passenger capacity input: {}", maxCapacityInput);
            System.out.println("Invalid max passenger capacity. Aborting.");
            return;
        }

        System.out.print("Has restroom (true/false): ");
        String hasRestroomInput = scanner.nextLine().trim();
        boolean hasRestroom = Boolean.parseBoolean(hasRestroomInput);
        logger.debug("Received hasRestroom: {}", hasRestroom);

        System.out.print("Enter luggage capacity: ");
        String luggageCapacityInput = scanner.nextLine().trim();
        BigDecimal luggageCapacity;
        try {
            luggageCapacity = new BigDecimal(luggageCapacityInput);
            logger.debug("Received luggage capacity: {}", luggageCapacity);
        } catch (NumberFormatException e) {
            logger.warn("Invalid luggage capacity input: {}", luggageCapacityInput);
            System.out.println("Invalid luggage capacity. Aborting.");
            return;
        }

        BusCreateDTO createDTO = new BusCreateDTO();
        createDTO.setRegistrationPlate(regPlate);
        createDTO.setTransportCompanyId(companyId);
        createDTO.setMaxPassengerCapacity(maxCapacity);
        createDTO.setHasRestroom(hasRestroom);
        createDTO.setLuggageCapacity(luggageCapacity);

        Set<ConstraintViolation<BusCreateDTO>> violations =
                validator.validate(createDTO);

        if (!violations.isEmpty()) {
            logger.warn("Validation failed for new bus: {}", createDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<BusCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Creating new bus with DTO: {}", createDTO);
        BusViewDTO created = service.create(createDTO);
        System.out.println("Bus created with ID: " + created.getId());
        logger.info("Bus created successfully with ID: {}", created.getId());

    }

    private void updateBus() {
        Long id = getLongInput("Enter bus ID to update: ");
        logger.debug("Fetching bus with ID: {}", id);
        BusViewDTO bus = service.getById(id);
        if (bus == null) {
            logger.info("No bus found with ID: {}", id);
            System.out.println("Bus not found.");
            return;
        }

        System.out.print("Enter new registration plate (leave blank to keep '" + bus.getRegistrationPlate() + "'): ");
        String regPlate = scanner.nextLine().trim();
        logger.debug("Received new registration plate: {}", regPlate.isEmpty() ? "unchanged" : regPlate);

        Long companyId = getLongInputWithDefault("Enter new transport company ID (leave blank to keep " + bus.getTransportCompanyId() + "): ", bus.getTransportCompanyId());
        logger.debug("Received new transport company ID: {}", companyId);

        System.out.print("Enter new max passenger capacity (leave blank to keep " + bus.getMaxPassengerCapacity() + "): ");
        String maxCapacityInput = scanner.nextLine().trim();
        Integer maxCapacity = bus.getMaxPassengerCapacity();
        if (!maxCapacityInput.isEmpty()) {
            try {
                maxCapacity = Integer.parseInt(maxCapacityInput);
                logger.debug("Received new max passenger capacity: {}", maxCapacity);
            } catch (NumberFormatException e) {
                logger.warn("Invalid new max passenger capacity input: {}", maxCapacityInput);
                System.out.println("Invalid max passenger capacity. Aborting.");
                return;
            }
        }

        System.out.print("Has restroom (true/false, leave blank to keep " + bus.getHasRestroom() + "): ");
        String hasRestroomInput = scanner.nextLine().trim();
        boolean hasRestroom = bus.getHasRestroom();
        if (!hasRestroomInput.isEmpty()) {
            hasRestroom = Boolean.parseBoolean(hasRestroomInput);
            logger.debug("Received new hasRestroom: {}", hasRestroom);
        }

        System.out.print("Enter new luggage capacity (leave blank to keep " + bus.getLuggageCapacity() + "): ");
        String luggageCapacityInput = scanner.nextLine().trim();
        BigDecimal luggageCapacity = bus.getLuggageCapacity();
        if (!luggageCapacityInput.isEmpty()) {
            try {
                luggageCapacity = new BigDecimal(luggageCapacityInput);
                logger.debug("Received new luggage capacity: {}", luggageCapacity);
            } catch (NumberFormatException e) {
                logger.warn("Invalid new luggage capacity input: {}", luggageCapacityInput);
                System.out.println("Invalid luggage capacity. Aborting.");
                return;
            }
        }

        BusUpdateDTO updateDTO = new BusUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setRegistrationPlate(bus.getRegistrationPlate());
        updateDTO.setTransportCompanyId(bus.getTransportCompanyId());
        updateDTO.setMaxPassengerCapacity(bus.getMaxPassengerCapacity());
        updateDTO.setHasRestroom(bus.getHasRestroom());
        updateDTO.setLuggageCapacity(bus.getLuggageCapacity());

        if (!regPlate.isEmpty()) updateDTO.setRegistrationPlate(regPlate);
        if (companyId != null && !companyId.equals(bus.getTransportCompanyId())) updateDTO.setTransportCompanyId(companyId);
        updateDTO.setMaxPassengerCapacity(maxCapacity);
        updateDTO.setHasRestroom(hasRestroom);
        updateDTO.setLuggageCapacity(luggageCapacity);

        Set<ConstraintViolation<BusUpdateDTO>> violations = validator.validate(updateDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for updated bus: {}", updateDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<BusUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Updating bus with ID {} using DTO: {}", id, updateDTO);
        service.update(updateDTO);
        System.out.println("Bus updated successfully.");
        logger.info("Bus with ID {} updated successfully", id);
    }

    private void deleteBus() {
        Long id = getLongInput("Enter bus ID to delete: ");
        logger.info("Deleting bus with ID: {}", id);
        service.delete(id);
        System.out.println("Bus deleted.");
        logger.info("Bus with ID {} deleted successfully", id);
    }

    private void getTransportServicesForBus() {
        Long id = getLongInput("Enter bus ID: ");
        logger.debug("Fetching transport services for bus ID: {}", id);
        List<? extends TransportServiceViewDTO> services = service.getTransportServicesForBus(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            logger.info("No transport services found for bus ID: {}", id);
            System.out.println("No services found.");
        } else {
            logger.info("Found {} transport services for bus ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private void getActiveTransportServices() {
        Long id = getLongInput("Enter bus ID: ");
        logger.debug("Fetching active transport services for bus ID: {}", id);
        List<? extends TransportServiceViewDTO> services = service.getActiveTransportServices(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            logger.info("No active transport services found for bus ID: {}", id);
            System.out.println("No active services found.");
        } else {
            logger.info("Found {} active transport services for bus ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private void getBusesByCompany() {
        Long id = getLongInput("Enter company ID: ");
        logger.debug("Fetching buses for company ID: {}", id);
        List<BusViewDTO> buses = service.getBusesByCompany(id, 0, Integer.MAX_VALUE);
        if (buses.isEmpty()) {
            logger.info("No buses found for company ID: {}", id);
            System.out.println("No buses found.");
        } else {
            logger.info("Found {} buses for company ID: {}", buses.size(), id);
            buses.forEach(System.out::println);
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