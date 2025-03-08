package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;
import services.data.dto.vehicles.VanCreateDTO;
import services.data.dto.vehicles.VanUpdateDTO;
import services.data.dto.vehicles.VanViewDTO;
import services.services.contracts.IVanService;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class VanController {
    private static final Logger logger = LoggerFactory.getLogger(VanController.class);

    private final IVanService service;
    private final Validator validator;
    private final Scanner scanner;

    public VanController(IVanService service, Validator validator, Scanner scanner) {
        this.service = service;
        this.validator = validator;
        this.scanner = scanner;
        logger.info("{} initialized with dependencies", service.getClass().getSimpleName());
    }

    public void handleMenu() {
        logger.info("Entering {} menu", service.getClass().getSimpleName());
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            logger.debug("User selected menu option: {}", choice);
            if (choice == 0) {
                logger.info("Exiting {} menu", service.getClass().getSimpleName());
                break;
            }
            processChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println(System.lineSeparator() + "--- Van Management ---" + System.lineSeparator());
        System.out.println("1. List all vans");
        System.out.println("2. Add a new van");
        System.out.println("3. Update a van");
        System.out.println("4. Delete a van");
        System.out.println("5. Get transport services for van");
        System.out.println("6. Get active transport services");
        System.out.println("7. Get vans by company");
        System.out.println("0. Back to main menu");
        System.out.print("Enter your choice: ");
        logger.debug("Displayed Van Management menu");
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
                    logger.info("Processing list all vans request");
                    listAllVans();
                    break;
                case 2:
                    logger.info("Processing add new van request");
                    addNewVan();
                    break;
                case 3:
                    logger.info("Processing update van request");
                    updateVan();
                    break;
                case 4:
                    logger.info("Processing delete van request");
                    deleteVan();
                    break;
                case 5:
                    logger.info("Processing get transport services for van request");
                    getTransportServicesForVan();
                    break;
                case 6:
                    logger.info("Processing get active transport services request");
                    getActiveTransportServices();
                    break;
                case 7:
                    logger.info("Processing get vans by company request");
                    getVansByCompany();
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

    private void listAllVans() {
        logger.debug("Fetching all vans");
        List<VanViewDTO> vans = service.getAll(0, Integer.MAX_VALUE, "registrationPlate", true);
        if (vans.isEmpty()) {
            logger.info("No vans found");
            System.out.println("No vans found.");
        } else {
            logger.info("Found {} vans", vans.size());
            vans.forEach(System.out::println);
        }
    }

    private void addNewVan() {
        System.out.print("Enter registration plate: ");
        String regPlate = scanner.nextLine().trim();
        logger.debug("Received registration plate: {}", regPlate);

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

        System.out.print("Enter max passenger capacity: ");
        Integer maxCapacity;
        try {
            maxCapacity = Integer.parseInt(scanner.nextLine().trim());
            logger.debug("Received max passenger capacity: {}", maxCapacity);
        } catch (NumberFormatException e) {
            logger.warn("Invalid max passenger capacity input: {}", e.getMessage());
            System.out.println("Invalid max passenger capacity. Aborting.");
            return;
        }

        System.out.print("Has passenger overhead storage (true/false): ");
        String hasOverheadInput = scanner.nextLine().trim();
        boolean hasOverhead = Boolean.parseBoolean(hasOverheadInput);
        logger.debug("Received has passenger overhead storage: {}", hasOverhead);

        VanCreateDTO createDTO = new VanCreateDTO();
        createDTO.setRegistrationPlate(regPlate);
        createDTO.setTransportCompanyId(companyId);
        createDTO.setMaxPassengerCapacity(maxCapacity);
        createDTO.setHasPassengerOverheadStorage(hasOverhead);

        Set<ConstraintViolation<VanCreateDTO>> violations = validator.validate(createDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for new van: {}", createDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<VanCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Creating new van with DTO: {}", createDTO);
        VanViewDTO created = service.create(createDTO);
        System.out.println("Van created with ID: " + created.getId());
        logger.info("Van created successfully with ID: {}", created.getId());
    }

    private void updateVan() {
        Long id = getLongInput("Enter van ID to update: ");
        logger.debug("Fetching van with ID: {}", id);
        VanViewDTO van = service.getById(id);
        if (van == null) {
            logger.info("No van found with ID: {}", id);
            System.out.println("Van not found.");
            return;
        }

        System.out.print("Enter new registration plate (leave blank to keep '" + van.getRegistrationPlate() + "'): ");
        String regPlate = scanner.nextLine().trim();
        logger.debug("Received new registration plate: {}", regPlate.isEmpty() ? "unchanged" : regPlate);

        Long companyId = getLongInputWithDefault("Enter new transport company ID (leave blank to keep " + van.getTransportCompanyId() + "): ", van.getTransportCompanyId());
        logger.debug("Received new transport company ID: {}", companyId);

        System.out.print("Enter new max passenger capacity (leave blank to keep " + van.getMaxPassengerCapacity() + "): ");
        String maxCapacityInput = scanner.nextLine().trim();
        Integer maxCapacity = van.getMaxPassengerCapacity();
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

        System.out.print("Has passenger overhead storage (true/false, leave blank to keep " + van.getHasPassengerOverheadStorage() + "): ");
        String hasOverheadInput = scanner.nextLine().trim();
        boolean hasOverhead = van.getHasPassengerOverheadStorage();
        if (!hasOverheadInput.isEmpty()) {
            hasOverhead = Boolean.parseBoolean(hasOverheadInput);
            logger.debug("Received new has passenger overhead storage: {}", hasOverhead);
        }

        VanUpdateDTO updateDTO = new VanUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setRegistrationPlate(regPlate.isEmpty() ? van.getRegistrationPlate() : regPlate);
        updateDTO.setTransportCompanyId(companyId);
        updateDTO.setMaxPassengerCapacity(maxCapacity);
        updateDTO.setHasPassengerOverheadStorage(hasOverhead);

        Set<ConstraintViolation<VanUpdateDTO>> violations = validator.validate(updateDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for updated van: {}", updateDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<VanUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Updating van with ID {} using DTO: {}", id, updateDTO);
        service.update(updateDTO);
        System.out.println("Van updated successfully.");
        logger.info("Van with ID {} updated successfully", id);
    }

    private void deleteVan() {
        Long id = getLongInput("Enter van ID to delete: ");
        logger.info("Deleting van with ID: {}", id);
        service.delete(id);
        System.out.println("Van deleted.");
        logger.info("Van with ID {} deleted successfully", id);
    }

    private void getTransportServicesForVan() {
        Long id = getLongInput("Enter van ID: ");
        logger.debug("Fetching transport services for van ID: {}", id);
        List<TransportPassengersServiceViewDTO> services = service.getTransportServicesForVan(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            logger.info("No transport services found for van ID: {}", id);
            System.out.println("No services found.");
        } else {
            logger.info("Found {} transport services for van ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private void getActiveTransportServices() {
        Long id = getLongInput("Enter van ID: ");
        logger.debug("Fetching active transport services for van ID: {}", id);
        List<TransportPassengersServiceViewDTO> services = service.getActiveTransportServices(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            logger.info("No active transport services found for van ID: {}", id);
            System.out.println("No active services found.");
        } else {
            logger.info("Found {} active transport services for van ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private void getVansByCompany() {
        Long id = getLongInput("Enter company ID: ");
        logger.debug("Fetching vans for company ID: {}", id);
        List<VanViewDTO> vans = service.getVansByCompany(id, 0, Integer.MAX_VALUE);
        if (vans.isEmpty()) {
            logger.info("No vans found for company ID: {}", id);
            System.out.println("No vans found.");
        } else {
            logger.info("Found {} vans for company ID: {}", vans.size(), id);
            vans.forEach(System.out::println);
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