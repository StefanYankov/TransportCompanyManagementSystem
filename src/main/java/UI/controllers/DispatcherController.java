package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
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
    private final IDispatcherService service;
    private final Validator validator;
    private final Scanner scanner;

    public DispatcherController(IDispatcherService service, Validator validator, Scanner scanner) {
        this.service = service;
        this.validator = validator;
        this.scanner = scanner;
    }

    public void handleMenu() {
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            if (choice == 0) {
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
                    listAllDispatchers();
                    break;
                case 2:
                    addNewDispatcher();
                    break;
                case 3:
                    updateDispatcher();
                    break;
                case 4:
                    deleteDispatcher();
                    break;
                case 5:
                    getDriversByDispatcher();
                    break;
                case 6:
                    getDispatchersSortedBySalary();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void listAllDispatchers() {
        List<DispatcherViewDTO> dispatchers = service.getAll(0, Integer.MAX_VALUE, "firstName", true);
        if (dispatchers.isEmpty()) {
            System.out.println("No dispatchers found.");
        } else {
            dispatchers.forEach(System.out::println);
        }
    }

    private void addNewDispatcher() {
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter family name: ");
        String familyName = scanner.nextLine().trim();

        System.out.print("Enter salary: ");
        String salaryInput = scanner.nextLine().trim();
        BigDecimal salary = new BigDecimal(salaryInput);

        System.out.print("Enter transport company ID: ");
        Long companyId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter supervised driver IDs (comma-separated, optional): ");
        String driverIdsInput = scanner.nextLine().trim();
        Set<Long> supervisedDriverIds;
        if (driverIdsInput.isEmpty()) {
            supervisedDriverIds = null;
        } else {
            supervisedDriverIds = Arrays
                    .stream(driverIdsInput.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
        }

        DispatcherCreateDTO createDTO = new DispatcherCreateDTO();
        createDTO.setFirstName(firstName);
        createDTO.setFamilyName(familyName);
        createDTO.setSalary(salary);
        createDTO.setTransportCompanyId(companyId);
        createDTO.setSupervisedDriverIds(supervisedDriverIds);

        Set<ConstraintViolation<DispatcherCreateDTO>> violations =
                validator.validate(createDTO);
        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<DispatcherCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        DispatcherViewDTO created = service.create(createDTO);
        System.out.println("Dispatcher created with ID: " + created.getId());
    }

    private void updateDispatcher() {
        Long id = getLongInput("Enter dispatcher ID to update: ");
        DispatcherViewDTO dispatcher = service.getById(id);
        if (dispatcher == null) {
            System.out.println("Dispatcher not found.");
            return;
        }

        System.out.print("Enter new first name (leave blank to keep '" + dispatcher.getFirstName() + "'): ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter new family name (leave blank to keep '" + dispatcher.getFamilyName() + "'): ");
        String familyName = scanner.nextLine().trim();

        System.out.print("Enter new salary (leave blank to keep " + dispatcher.getSalary() + "): ");
        String salaryInput = scanner.nextLine().trim();

        System.out.print("Enter new transport company ID (leave blank to keep " + dispatcher.getTransportCompanyId() + "): ");
        Long companyId = getLongInput("Enter new transport company ID (leave blank to keep " + dispatcher.getTransportCompanyId() + "): ");

        System.out.print("Enter new supervised driver IDs (comma-separated, leave blank to keep current): ");
        String driverIdsInput = scanner.nextLine().trim();

        DispatcherUpdateDTO updateDTO = new DispatcherUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setFirstName(dispatcher.getFirstName());
        updateDTO.setFamilyName(dispatcher.getFamilyName());
        updateDTO.setSalary(dispatcher.getSalary());
        updateDTO.setTransportCompanyId(dispatcher.getTransportCompanyId());
        updateDTO.setSupervisedDriverIds(dispatcher.getSupervisedDriverIds());

        if (!firstName.isEmpty()){
            updateDTO.setFirstName(firstName);
        }
        if (!familyName.isEmpty()){
            updateDTO.setFamilyName(familyName);
        }
        if (!salaryInput.isEmpty()){
            updateDTO.setSalary(new BigDecimal(salaryInput));
        }
        if (companyId != null){
            updateDTO.setTransportCompanyId(companyId);
        }

        if (!driverIdsInput.isEmpty()) {
            Set<Long> supervisedDriverIds = Arrays
                    .stream(driverIdsInput.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
            updateDTO.setSupervisedDriverIds(supervisedDriverIds);
        }

        Set<ConstraintViolation<DispatcherUpdateDTO>> violations =
                validator.validate(updateDTO);
        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<DispatcherUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        service.update(updateDTO);
        System.out.println("Dispatcher updated successfully.");
    }

    private void deleteDispatcher() {
        Long id = getLongInput("Enter dispatcher ID to delete: ");
        service.delete(id);
        System.out.println("Dispatcher deleted.");
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

    private void getDispatchersSortedBySalary() {
        System.out.print("Sort ascending? (yes/no): ");
        boolean ascending = scanner.nextLine().trim().equalsIgnoreCase("yes");
        List<DispatcherViewDTO> dispatchers = service.getDispatchersSortedBySalary(0, Integer.MAX_VALUE, ascending);
        if (dispatchers.isEmpty()) {
            System.out.println("No dispatchers found.");
        } else {
            dispatchers.forEach(System.out::println);
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