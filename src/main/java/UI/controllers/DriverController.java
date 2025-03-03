package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import services.data.dto.employees.DriverCreateDTO;
import services.data.dto.employees.DriverUpdateDTO;
import services.data.dto.employees.DriverViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.services.contracts.IDriverService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class DriverController {
    private final IDriverService service;
    private final Validator validator;
    private final Scanner scanner;

    public DriverController(IDriverService service, Validator validator, Scanner scanner) {
        this.service = service;
        this.validator = validator;
        this.scanner = scanner;
    }

    public void handleMenu() {
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            if (choice == 0) break;
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
    }

    private int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void processChoice(int choice) {
        try {
            switch (choice) {
                case 1:
                    listAllDrivers();
                    break;
                case 2:
                    addNewDriver();
                    break;
                case 3:
                    updateDriver();
                    break;
                case 4:
                    deleteDriver();
                    break;
                case 5:
                    getDriversByQualification();
                    break;
                case 6:
                    getDriversSortedBySalary();
                    break;
                case 7:
                    getDriverTransportCounts();
                    break;
                case 8:
                    getRevenueByDriver();
                    break;
                case 9:
                    getDriverTripCounts();
                    break;
                case 10:
                    getDriversByDispatcher();
                    break;
                case 11:
                    getDriversByCompany();
                    break;
                case 12:
                    getTransportServicesForDriver();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void listAllDrivers() {
        List<DriverViewDTO> drivers = service.getAll(0, Integer.MAX_VALUE, "firstName", true);
        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
        } else {
            drivers.forEach(System.out::println);
        }
    }

    private void addNewDriver() {
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter family name: ");
        String familyName = scanner.nextLine().trim();

        System.out.print("Enter salary: ");
        String salaryInput = scanner.nextLine().trim();
        BigDecimal salary = new BigDecimal(salaryInput);

        System.out.print("Enter transport company ID: ");
        Long companyId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter dispatcher ID (optional, leave blank): ");
        Long dispatcherId = getLongInput("Enter dispatcher ID (optional, leave blank): ");

        System.out.print("Enter qualification IDs (comma-separated, optional): ");
        String qualsInput = scanner.nextLine().trim();

        Set<Long> qualificationIds;
        if (qualsInput.isEmpty()) {
            qualificationIds = null;
        } else {
            qualificationIds = Arrays
                    .stream(qualsInput.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
        }

        DriverCreateDTO createDTO = new DriverCreateDTO();
        createDTO.setFirstName(firstName);
        createDTO.setFamilyName(familyName);
        createDTO.setSalary(salary);
        createDTO.setTransportCompanyId(companyId);
        createDTO.setDispatcherId(dispatcherId);
        createDTO.setQualificationIds(qualificationIds);

        Set<ConstraintViolation<DriverCreateDTO>> violations =
                validator.validate(createDTO);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<DriverCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        DriverViewDTO created = service.create(createDTO);
        System.out.println("Driver created with ID: " + created.getId());
    }

    private void updateDriver() {
        Long id = getLongInput("Enter driver ID to update: ");
        DriverViewDTO driver = service.getById(id);

        if (driver == null) {
            System.out.println("Driver not found.");
            return;
        }

        System.out.print("Enter new first name (leave blank to keep '" + driver.getFirstName() + "'): ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter new family name (leave blank to keep '" + driver.getFamilyName() + "'): ");
        String familyName = scanner.nextLine().trim();

        System.out.print("Enter new salary (leave blank to keep " + driver.getSalary() + "): ");
        String salaryInput = scanner.nextLine().trim();

        System.out.print("Enter new transport company ID (leave blank to keep " + driver.getTransportCompanyId() + "): ");
        Long companyId = getLongInput("Enter new transport company ID (leave blank to keep " + driver.getTransportCompanyId() + "): ");

        System.out.print("Enter new dispatcher ID (leave blank to keep " + (driver.getDispatcherId() != null ? driver.getDispatcherId() : "none") + "): ");
        Long dispatcherId = getLongInput("Enter new dispatcher ID (leave blank to keep " + (driver.getDispatcherId() != null ? driver.getDispatcherId() : "none") + "): ");

        System.out.print("Enter new qualification IDs (comma-separated, leave blank to keep current): ");
        String qualsInput = scanner.nextLine().trim();

        DriverUpdateDTO updateDTO = new DriverUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setFirstName(driver.getFirstName());
        updateDTO.setFamilyName(driver.getFamilyName());
        updateDTO.setSalary(driver.getSalary());
        updateDTO.setTransportCompanyId(driver.getTransportCompanyId());
        updateDTO.setDispatcherId(driver.getDispatcherId());
        updateDTO.setQualificationIds(driver.getQualificationIds());

        if (!firstName.isEmpty()){
            updateDTO.setFirstName(firstName);
        }

        if (!familyName.isEmpty()){
            updateDTO.setFamilyName(familyName);
        }

        if (!salaryInput.isEmpty()){
            updateDTO.setSalary(new BigDecimal(salaryInput));
        }

        if (companyId != null) {
            updateDTO.setTransportCompanyId(companyId);
        }

        if (dispatcherId != null){
            updateDTO.setDispatcherId(dispatcherId);
        }
        if (!qualsInput.isEmpty()) {
            Set<Long> qualificationIds = Arrays
                    .stream(qualsInput.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
            updateDTO.setQualificationIds(qualificationIds);
        }

        Set<ConstraintViolation<DriverUpdateDTO>> violations = validator.validate(updateDTO);
        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<DriverUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        service.update(updateDTO);
        System.out.println("Driver updated successfully.");
    }

    private void deleteDriver() {
        Long id = getLongInput("Enter driver ID to delete: ");
        service.delete(id);
        System.out.println("Driver deleted.");
    }

    private void getDriversByQualification() {
        System.out.print("Enter qualification name: ");
        String qualName = scanner.nextLine().trim();
        List<DriverViewDTO> drivers = service.getDriversByQualification(qualName);
        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
        } else {
            drivers.forEach(System.out::println);
        }
    }

    private void getDriversSortedBySalary() {
        System.out.print("Sort ascending? (yes/no): ");
        boolean ascending = scanner.nextLine().trim().equalsIgnoreCase("yes");
        List<DriverViewDTO> drivers = service.getDriversSortedBySalary(ascending);
        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
        } else {
            drivers.forEach(System.out::println);
        }
    }

    private void getDriverTransportCounts() {
        Map<Long, Integer> counts = service.getDriverTransportCounts();
        counts.forEach((id, count) -> System.out.println("Driver ID " + id + ": " + count + " transports"));
    }

    private void getRevenueByDriver() {
        Long id = getLongInput("Enter driver ID: ");
        BigDecimal revenue = service.getRevenueByDriver(id);
        System.out.println("Revenue: $" + revenue);
    }

    private void getDriverTripCounts() {
        System.out.print("Sort by count? (yes/no): ");
        boolean byCount = scanner.nextLine().trim().equalsIgnoreCase("yes");
        System.out.print("Sort ascending? (yes/no): ");
        boolean ascending = scanner.nextLine().trim().equalsIgnoreCase("yes");
        Map<Long, Integer> counts = service.getDriverTripCounts(byCount, ascending, 0, Integer.MAX_VALUE);
        counts.forEach((id, count) -> System.out.println("Driver ID " + id + ": " + count + " trips"));
    }

    private void getDriversByDispatcher() {
        Long id = getLongInput("Enter dispatcher ID: ");
        List<DriverViewDTO> drivers = service.getDriversByDispatcher(id);
        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
        } else {
            drivers.forEach(System.out::println);
        }
    }

    private void getDriversByCompany() {
        Long id = getLongInput("Enter company ID: ");
        List<DriverViewDTO> drivers = service.getDriversByCompany(id, 0, Integer.MAX_VALUE, "firstName", true);
        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
        } else {
            drivers.forEach(System.out::println);
        }
    }

    private void getTransportServicesForDriver() {
        Long id = getLongInput("Enter driver ID: ");
        List<TransportServiceViewDTO> services = service.getTransportServicesForDriver(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            System.out.println("No services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private Long getLongInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}