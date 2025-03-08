package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.dto.employees.DriverCreateDTO;
import services.data.dto.employees.DriverUpdateDTO;
import services.data.dto.employees.DriverViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.services.contracts.IDriverService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class DriverController {
    private static final Logger logger = LoggerFactory.getLogger(DriverController.class);

    private final IDriverService service;
    private final Validator validator;
    private final Scanner scanner;

    public DriverController(IDriverService service, Validator validator, Scanner scanner) {
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
        System.out.println(System.lineSeparator() + "--- Driver Management ---" + System.lineSeparator());
        System.out.println("1. List all drivers");
        System.out.println("2. Add a new driver");
        System.out.println("3. Update a driver");
        System.out.println("4. Delete a driver");
        System.out.println("5. Get drivers by qualification");
        System.out.println("6. Get drivers sorted by salary");
        System.out.println("7. Get driver transport counts");
        System.out.println("8. Get revenue by driver");
        System.out.println("9. Get driver trip counts");
        System.out.println("10. Get drivers by dispatcher");
        System.out.println("11. Get drivers by company");
        System.out.println("12. Get transport services for driver");
        System.out.println("0. Back to main menu");
        System.out.print("Enter your choice: ");
        logger.debug("Displayed Driver Management menu");
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
                    logger.info("Processing list all drivers request");
                    listAllDrivers();
                    break;
                case 2:
                    logger.info("Processing add new driver request");
                    addNewDriver();
                    break;
                case 3:
                    logger.info("Processing update driver request");
                    updateDriver();
                    break;
                case 4:
                    logger.info("Processing delete driver request");
                    deleteDriver();
                    break;
                case 5:
                    logger.info("Processing get drivers by qualification request");
                    getDriversByQualification();
                    break;
                case 6:
                    logger.info("Processing get drivers sorted by salary request");
                    getDriversSortedBySalary();
                    break;
                case 7:
                    logger.info("Processing get driver transport counts request");
                    getDriverTransportCounts();
                    break;
                case 8:
                    logger.info("Processing get revenue by driver request");
                    getRevenueByDriver();
                    break;
                case 9:
                    logger.info("Processing get driver trip counts request");
                    getDriverTripCounts();
                    break;
                case 10:
                    logger.info("Processing get drivers by dispatcher request");
                    getDriversByDispatcher();
                    break;
                case 11:
                    logger.info("Processing get drivers by company request");
                    getDriversByCompany();
                    break;
                case 12:
                    logger.info("Processing get transport services for driver request");
                    getTransportServicesForDriver();
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

    private void listAllDrivers() {
        logger.debug("Fetching all drivers");
        List<DriverViewDTO> drivers = service.getAll(0, Integer.MAX_VALUE, "firstName", true);
        if (drivers.isEmpty()) {
            logger.info("No drivers found");
            System.out.println("No drivers found.");
        } else {
            logger.info("Found {} drivers", drivers.size());
            drivers.forEach(System.out::println);
        }
    }

    private void addNewDriver() {
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

        Long dispatcherId = getLongInput("Enter dispatcher ID (optional, leave blank): ");
        logger.debug("Received dispatcher ID: {}", dispatcherId != null ? dispatcherId : "none");

        System.out.print("Enter qualification IDs (comma-separated, optional): ");
        String qualsInput = scanner.nextLine().trim();
        Set<Long> qualificationIds;
        if (qualsInput.isEmpty()) {
            qualificationIds = null;
            logger.debug("No qualification IDs provided");
        } else {
            try {
                qualificationIds = Arrays
                        .stream(qualsInput.split(","))
                        .map(String::trim)
                        .map(Long::parseLong)
                        .collect(Collectors.toSet());
                logger.debug("Received qualification IDs: {}", qualificationIds);
            } catch (NumberFormatException e) {
                logger.warn("Invalid qualification IDs input: {}", qualsInput);
                System.out.println("Invalid qualification IDs format. Aborting.");
                return;
            }
        }

        DriverCreateDTO createDTO = new DriverCreateDTO();
        createDTO.setFirstName(firstName);
        createDTO.setFamilyName(familyName);
        createDTO.setSalary(salary);
        createDTO.setTransportCompanyId(companyId);
        createDTO.setDispatcherId(dispatcherId);
        createDTO.setQualificationIds(qualificationIds);

        Set<ConstraintViolation<DriverCreateDTO>> violations = validator.validate(createDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for new driver: {}", createDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<DriverCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Creating new driver with DTO: {}", createDTO);
        DriverViewDTO created = service.create(createDTO);
        System.out.println("Driver created with ID: " + created.getId());
        logger.info("Driver created successfully with ID: {}", created.getId());
    }

    private void updateDriver() {
        Long id = getLongInput("Enter driver ID to update: ");
        logger.debug("Fetching driver with ID: {}", id);
        DriverViewDTO driver = service.getById(id);

        if (driver == null) {
            logger.info("No driver found with ID: {}", id);
            System.out.println("Driver not found.");
            return;
        }

        System.out.print("Enter new first name (leave blank to keep '" + driver.getFirstName() + "'): ");
        String firstName = scanner.nextLine().trim();
        logger.debug("Received new first name: {}", firstName.isEmpty() ? "unchanged" : firstName);

        System.out.print("Enter new family name (leave blank to keep '" + driver.getFamilyName() + "'): ");
        String familyName = scanner.nextLine().trim();
        logger.debug("Received new family name: {}", familyName.isEmpty() ? "unchanged" : familyName);

        System.out.print("Enter new salary (leave blank to keep " + driver.getSalary() + "): ");
        String salaryInput = scanner.nextLine().trim();
        BigDecimal salary = driver.getSalary();
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

        Long companyId = getLongInput("Enter new transport company ID (leave blank to keep " + driver.getTransportCompanyId() + "): ");
        logger.debug("Received new transport company ID: {}", companyId != null ? companyId : "unchanged");

        Long dispatcherId = getLongInput("Enter new dispatcher ID (leave blank to keep " + (driver.getDispatcherId() != null ? driver.getDispatcherId() : "none") + "): ");
        logger.debug("Received new dispatcher ID: {}", dispatcherId != null ? dispatcherId : "unchanged");

        System.out.print("Enter new qualification IDs (comma-separated, leave blank to keep current): ");
        String qualsInput = scanner.nextLine().trim();
        Set<Long> qualificationIds = driver.getQualificationIds();
        if (!qualsInput.isEmpty()) {
            try {
                qualificationIds = Arrays
                        .stream(qualsInput.split(","))
                        .map(String::trim)
                        .map(Long::parseLong)
                        .collect(Collectors.toSet());
                logger.debug("Received new qualification IDs: {}", qualificationIds);
            } catch (NumberFormatException e) {
                logger.warn("Invalid new qualification IDs input: {}", qualsInput);
                System.out.println("Invalid qualification IDs format. Aborting.");
                return;
            }
        }

        DriverUpdateDTO updateDTO = new DriverUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setFirstName(driver.getFirstName());
        updateDTO.setFamilyName(driver.getFamilyName());
        updateDTO.setSalary(driver.getSalary());
        updateDTO.setTransportCompanyId(driver.getTransportCompanyId());
        updateDTO.setDispatcherId(driver.getDispatcherId());
        updateDTO.setQualificationIds(driver.getQualificationIds());

        if (!firstName.isEmpty()) {
            updateDTO.setFirstName(firstName);
        }

        if (!familyName.isEmpty()) {
            updateDTO.setFamilyName(familyName);
        }

        if (!salaryInput.isEmpty()) {
            updateDTO.setSalary(salary);
        }

        if (companyId != null) {
            updateDTO.setTransportCompanyId(companyId);
        }

        if (dispatcherId != null) {
            updateDTO.setDispatcherId(dispatcherId);
        }

        if (!qualsInput.isEmpty()) {
            updateDTO.setQualificationIds(qualificationIds);
        }

        Set<ConstraintViolation<DriverUpdateDTO>> violations = validator.validate(updateDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for updated driver: {}", updateDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<DriverUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Updating driver with ID {} using DTO: {}", id, updateDTO);
        service.update(updateDTO);
        System.out.println("Driver updated successfully.");
        logger.info("Driver with ID {} updated successfully", id);
    }

    private void deleteDriver() {
        Long id = getLongInput("Enter driver ID to delete: ");
        logger.info("Deleting driver with ID: {}", id);
        service.delete(id);
        System.out.println("Driver deleted.");
        logger.info("Driver with ID {} deleted successfully", id);
    }

    private void getDriversByQualification() {
        System.out.print("Enter qualification name: ");
        String qualName = scanner.nextLine().trim();
        logger.debug("Received qualification name: {}", qualName);
        List<DriverViewDTO> drivers = service.getDriversByQualification(qualName);
        if (drivers.isEmpty()) {
            logger.info("No drivers found for qualification: {}", qualName);
            System.out.println("No drivers found.");
        } else {
            logger.info("Found {} drivers for qualification: {}", drivers.size(), qualName);
            drivers.forEach(System.out::println);
        }
    }

    private void getDriversSortedBySalary() {
        System.out.print("Sort ascending? (yes/no): ");
        String sortInput = scanner.nextLine().trim();
        boolean ascending = sortInput.equalsIgnoreCase("yes");
        logger.debug("Received sort order: {}", ascending ? "ascending" : "descending");
        List<DriverViewDTO> drivers = service.getDriversSortedBySalary(ascending);
        if (drivers.isEmpty()) {
            logger.info("No drivers found when sorted by salary");
            System.out.println("No drivers found.");
        } else {
            logger.info("Found {} drivers sorted by salary", drivers.size());
            drivers.forEach(System.out::println);
        }
    }

    private void getDriverTransportCounts() {
        logger.debug("Fetching driver transport counts");
        Map<Long, Integer> counts = service.getDriverTransportCounts();
        if (counts.isEmpty()) {
            logger.info("No transport counts found");
            System.out.println("No transport counts found.");
        } else {
            logger.info("Found transport counts for {} drivers", counts.size());
            counts.forEach((id, count) -> System.out.println("Driver ID " + id + ": " + count + " transports"));
        }
    }

    private void getRevenueByDriver() {
        Long id = getLongInput("Enter driver ID: ");
        logger.debug("Fetching revenue for driver ID: {}", id);
        BigDecimal revenue = service.getRevenueByDriver(id);
        System.out.println("Revenue: $" + revenue);
        logger.info("Retrieved revenue ${} for driver ID: {}", revenue, id);
    }

    private void getDriverTripCounts() {
        System.out.print("Sort by count? (yes/no): ");
        String byCountInput = scanner.nextLine().trim();
        boolean byCount = byCountInput.equalsIgnoreCase("yes");
        logger.debug("Received sort by count: {}", byCount);

        System.out.print("Sort ascending? (yes/no): ");
        String sortInput = scanner.nextLine().trim();
        boolean ascending = sortInput.equalsIgnoreCase("yes");
        logger.debug("Received sort order: {}", ascending ? "ascending" : "descending");

        logger.debug("Fetching driver trip counts");
        Map<Long, Integer> counts = service.getDriverTripCounts(byCount, ascending, 0, Integer.MAX_VALUE);
        if (counts.isEmpty()) {
            logger.info("No trip counts found");
            System.out.println("No trip counts found.");
        } else {
            logger.info("Found trip counts for {} drivers", counts.size());
            counts.forEach((id, count) -> System.out.println("Driver ID " + id + ": " + count + " trips"));
        }
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

    private void getDriversByCompany() {
        Long id = getLongInput("Enter company ID: ");
        logger.debug("Fetching drivers for company ID: {}", id);
        List<DriverViewDTO> drivers = service.getDriversByCompany(id, 0, Integer.MAX_VALUE, "firstName", true);
        if (drivers.isEmpty()) {
            logger.info("No drivers found for company ID: {}", id);
            System.out.println("No drivers found.");
        } else {
            logger.info("Found {} drivers for company ID: {}", drivers.size(), id);
            drivers.forEach(System.out::println);
        }
    }

    private void getTransportServicesForDriver() {
        Long id = getLongInput("Enter driver ID: ");
        logger.debug("Fetching transport services for driver ID: {}", id);
        List<TransportServiceViewDTO> services = service.getTransportServicesForDriver(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            logger.info("No transport services found for driver ID: {}", id);
            System.out.println("No services found.");
        } else {
            logger.info("Found {} transport services for driver ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private Long getLongInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                logger.debug("Received empty input, returning null");
                return null;
            }
            try {
                Long value = Long.parseLong(input);
                logger.debug("Received valid Long input: {}", value);
                return value;
            } catch (NumberFormatException e) {
                logger.warn("Invalid number input: {}", input);
                System.out.println("Invalid input. Please enter a valid number or leave blank.");
            }
        }
    }
}