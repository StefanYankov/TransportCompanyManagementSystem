package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.dto.employees.DispatcherCreateDTO;
import services.data.dto.employees.DispatcherUpdateDTO;
import services.data.dto.employees.DispatcherViewDTO;
import services.data.dto.employees.DriverViewDTO;
import services.services.contracts.IDispatcherService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class DispatcherController {
    private static final Logger logger = LoggerFactory.getLogger(DispatcherController.class);

    private final IDispatcherService service;
    private final Validator validator;
    private final Scanner scanner;

    public DispatcherController(IDispatcherService service, Validator validator, Scanner scanner) {
        this.service = service;
        this.validator = validator;
        this.scanner = scanner;
        logger.info("DispatcherController initialized with dependencies");
    }

    public void handleMenu() {
        logger.info("Entering DispatcherController menu");
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            logger.debug("User selected menu option: {}", choice);
            if (choice == 0) {
                logger.info("Exiting DispatcherController menu");
                break;
            }
            processChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println(System.lineSeparator() + "--- Dispatcher Management ---" + System.lineSeparator());
        System.out.println("1. List all dispatchers");
        System.out.println("2. Add a new dispatcher");
        System.out.println("3. Update a dispatcher");
        System.out.println("4. Delete a dispatcher");
        System.out.println("5. Get drivers by dispatcher");
        System.out.println("6. Get dispatchers sorted by salary");
        System.out.println("0. Back to main menu");
        System.out.print("Enter your choice: ");
        logger.debug("Displayed Dispatcher Management menu");
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
                    logger.info("Processing list all dispatchers request");
                    listAllDispatchers();
                    break;
                case 2:
                    logger.info("Processing add new dispatcher request");
                    addNewDispatcher();
                    break;
                case 3:
                    logger.info("Processing update dispatcher request");
                    updateDispatcher();
                    break;
                case 4:
                    logger.info("Processing delete dispatcher request");
                    deleteDispatcher();
                    break;
                case 5:
                    logger.info("Processing get drivers by dispatcher request");
                    getDriversByDispatcher();
                    break;
                case 6:
                    logger.info("Processing get dispatchers sorted by salary request");
                    getDispatchersSortedBySalary();
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

    private void listAllDispatchers() {
        logger.debug("Fetching all dispatchers");
        List<DispatcherViewDTO> dispatchers = service.getAll(0, Integer.MAX_VALUE, "firstName", true);
        if (dispatchers.isEmpty()) {
            logger.info("No dispatchers found");
            System.out.println("No dispatchers found.");
        } else {
            logger.info("Found {} dispatchers", dispatchers.size());
            dispatchers.forEach(System.out::println);
        }
    }

    private void addNewDispatcher() {
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine().trim();
        logger.debug("Received first name: {}", firstName);

        System.out.print("Enter family name: ");
        String familyName = scanner.nextLine().trim();
        logger.debug("Received family name: {}", familyName);

        System.out.print("Enter salary: ");
        String salaryInput = scanner.nextLine().trim();
        BigDecimal salary;
        try {
            salary = new BigDecimal(salaryInput);
            logger.debug("Received salary: {}", salary);
        } catch (NumberFormatException e) {
            logger.warn("Invalid salary input: {}", salaryInput);
            System.out.println("Invalid salary. Aborting.");
            return;
        }

        Long companyId = getLongInput("Enter transport company ID: ");
        logger.debug("Received transport company ID: {}", companyId);

        System.out.print("Enter supervised driver IDs (comma-separated, optional): ");
        String driverIdsInput = scanner.nextLine().trim();
        Set<Long> supervisedDriverIds;
        if (driverIdsInput.isEmpty()) {
            supervisedDriverIds = null;
            logger.debug("No supervised driver IDs provided");
        } else {
            try {
                supervisedDriverIds = Arrays
                        .stream(driverIdsInput.split(","))
                        .map(String::trim)
                        .map(Long::parseLong)
                        .collect(Collectors.toSet());
                logger.debug("Received supervised driver IDs: {}", supervisedDriverIds);
            } catch (NumberFormatException e) {
                logger.warn("Invalid supervised driver IDs input: {}", driverIdsInput);
                System.out.println("Invalid driver IDs format. Aborting.");
                return;
            }
        }

        DispatcherCreateDTO createDTO = new DispatcherCreateDTO();
        createDTO.setFirstName(firstName);
        createDTO.setFamilyName(familyName);
        createDTO.setSalary(salary);
        createDTO.setTransportCompanyId(companyId);
        createDTO.setSupervisedDriverIds(supervisedDriverIds);

        Set<ConstraintViolation<DispatcherCreateDTO>> violations = validator.validate(createDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for new dispatcher: {}", createDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<DispatcherCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Creating new dispatcher with DTO: {}", createDTO);
        DispatcherViewDTO created = service.create(createDTO);
        System.out.println("Dispatcher created with ID: " + created.getId());
        logger.info("Dispatcher created successfully with ID: {}", created.getId());
    }

    private void updateDispatcher() {
        Long id = getLongInput("Enter dispatcher ID to update: ");
        logger.debug("Fetching dispatcher with ID: {}", id);
        DispatcherViewDTO dispatcher = service.getById(id);
        if (dispatcher == null) {
            logger.info("No dispatcher found with ID: {}", id);
            System.out.println("Dispatcher not found.");
            return;
        }

        System.out.print("Enter new first name (leave blank to keep '" + dispatcher.getFirstName() + "'): ");
        String firstName = scanner.nextLine().trim();
        logger.debug("Received new first name: {}", firstName.isEmpty() ? "unchanged" : firstName);

        System.out.print("Enter new family name (leave blank to keep '" + dispatcher.getFamilyName() + "'): ");
        String familyName = scanner.nextLine().trim();
        logger.debug("Received new family name: {}", familyName.isEmpty() ? "unchanged" : familyName);

        System.out.print("Enter new salary (leave blank to keep " + dispatcher.getSalary() + "): ");
        String salaryInput = scanner.nextLine().trim();
        BigDecimal salary = dispatcher.getSalary();
        if (!salaryInput.isEmpty()) {
            try {
                salary = new BigDecimal(salaryInput);
                logger.debug("Received new salary: {}", salary);
            } catch (NumberFormatException e) {
                logger.warn("Invalid new salary input: {}", salaryInput);
                System.out.println("Invalid salary. Aborting.");
                return;
            }
        }

        Long companyId = getLongInputWithDefault("Enter new transport company ID (leave blank to keep " + dispatcher.getTransportCompanyId() + "): ", dispatcher.getTransportCompanyId());
        logger.debug("Received new transport company ID: {}", companyId);

        System.out.print("Enter new supervised driver IDs (comma-separated, leave blank to keep current): ");
        String driverIdsInput = scanner.nextLine().trim();
        Set<Long> supervisedDriverIds = dispatcher.getSupervisedDriverIds();
        if (!driverIdsInput.isEmpty()) {
            try {
                supervisedDriverIds = Arrays
                        .stream(driverIdsInput.split(","))
                        .map(String::trim)
                        .map(Long::parseLong)
                        .collect(Collectors.toSet());
                logger.debug("Received new supervised driver IDs: {}", supervisedDriverIds);
            } catch (NumberFormatException e) {
                logger.warn("Invalid new supervised driver IDs input: {}", driverIdsInput);
                System.out.println("Invalid driver IDs format. Aborting.");
                return;
            }
        }

        DispatcherUpdateDTO updateDTO = new DispatcherUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setFirstName(dispatcher.getFirstName());
        updateDTO.setFamilyName(dispatcher.getFamilyName());
        updateDTO.setSalary(dispatcher.getSalary());
        updateDTO.setTransportCompanyId(dispatcher.getTransportCompanyId());
        updateDTO.setSupervisedDriverIds(dispatcher.getSupervisedDriverIds());

        if (!firstName.isEmpty()) {
            updateDTO.setFirstName(firstName);
        }

        if (!familyName.isEmpty()) {
            updateDTO.setFamilyName(familyName);
        }

        if (!salaryInput.isEmpty()) {
            updateDTO.setSalary(salary);
        }

        if (companyId != null && !companyId.equals(dispatcher.getTransportCompanyId())) {
            updateDTO.setTransportCompanyId(companyId);
        }

        if (!driverIdsInput.isEmpty()) {
            updateDTO.setSupervisedDriverIds(supervisedDriverIds);
        }

        Set<ConstraintViolation<DispatcherUpdateDTO>> violations = validator.validate(updateDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for updated dispatcher: {}", updateDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<DispatcherUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Updating dispatcher with ID {} using DTO: {}", id, updateDTO);
        service.update(updateDTO);
        System.out.println("Dispatcher updated successfully.");
        logger.info("Dispatcher with ID {} updated successfully", id);
    }

    private void deleteDispatcher() {
        Long id = getLongInput("Enter dispatcher ID to delete: ");
        logger.info("Deleting dispatcher with ID: {}", id);
        service.delete(id);
        System.out.println("Dispatcher deleted.");
        logger.info("Dispatcher with ID {} deleted successfully", id);
    }

    private void getDriversByDispatcher() {
        Long id = getLongInput("Enter dispatcher ID: ");
        logger.debug("Fetching drivers for dispatcher ID: {}", id);
        List<DriverViewDTO> drivers = service.getDriversByDispatcher(id);
        if (drivers.isEmpty()) {
            logger.info("No drivers found for dispatcher ID: {}", id);
            System.out.println("No drivers found.");
        } else {
            logger.info("Found {} drivers for dispatcher ID: {}", drivers.size(), id);
            drivers.forEach(System.out::println);
        }
    }

    private void getDispatchersSortedBySalary() {
        System.out.print("Sort ascending? (yes/no): ");
        String sortInput = scanner.nextLine().trim();
        boolean ascending = sortInput.equalsIgnoreCase("yes");
        logger.debug("Received sort order: {}", ascending ? "ascending" : "descending");

        logger.debug("Fetching dispatchers sorted by salary");
        List<DispatcherViewDTO> dispatchers = service.getDispatchersSortedBySalary(0, Integer.MAX_VALUE, ascending);
        if (dispatchers.isEmpty()) {
            logger.info("No dispatchers found when sorted by salary");
            System.out.println("No dispatchers found.");
        } else {
            logger.info("Found {} dispatchers sorted by salary", dispatchers.size());
            dispatchers.forEach(System.out::println);
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