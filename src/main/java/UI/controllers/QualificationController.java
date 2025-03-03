package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import services.data.dto.employees.DriverViewDTO;
import services.data.dto.employees.QualificationCreateDTO;
import services.data.dto.employees.QualificationUpdateDTO;
import services.data.dto.employees.QualificationViewDTO;
import services.services.contracts.IQualificationService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class QualificationController {
    private final IQualificationService service;
    private final Validator validator;
    private final Scanner scanner;

    public QualificationController(IQualificationService service, Validator validator, Scanner scanner) {
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
        System.out.println(System.lineSeparator() + "--- Qualification Management ---" + System.lineSeparator());
        System.out.println("1. List all qualifications");
        System.out.println("2. Add a new qualification");
        System.out.println("3. Update a qualification");
        System.out.println("4. Delete a qualification");
        System.out.println("5. Get drivers by qualification");
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
                case 1: listAllQualifications(); break;
                case 2: addNewQualification(); break;
                case 3: updateQualification(); break;
                case 4: deleteQualification(); break;
                case 5: getDriversByQualification(); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void listAllQualifications() {
        List<QualificationViewDTO> qualifications = service.getAll(0, Integer.MAX_VALUE, "name", true, "");
        if (qualifications.isEmpty()) {
            System.out.println("No qualifications found.");
        } else {
            qualifications.forEach(System.out::println);
        }
    }

    private void addNewQualification() {

        System.out.print("Enter qualification name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();

        QualificationCreateDTO createDTO = new QualificationCreateDTO();
        createDTO.setName(name);
        createDTO.setDescription(description);

        Set<ConstraintViolation<QualificationCreateDTO>> violations =
                validator.validate(createDTO);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<QualificationCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        QualificationViewDTO created = service.create(createDTO);
        System.out.println("Qualification created with ID: " + created.getId());
    }

    private void updateQualification() {

        Long id = getLongInput("Enter qualification ID to update: ");
        QualificationViewDTO qualification = service.getById(id);

        if (qualification == null) {
            System.out.println("Qualification not found.");
            return;
        }

        System.out.print("Enter new name (leave blank to keep '" + qualification.getName() + "'): ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter new description (leave blank to keep '" + qualification.getDescription() + "'): ");
        String description = scanner.nextLine().trim();

        QualificationUpdateDTO updateDTO = new QualificationUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setName(qualification.getName());
        updateDTO.setDescription(qualification.getDescription());

        if (!name.isEmpty()) {
            updateDTO.setName(name);
        }

        if (!description.isEmpty()) {
            updateDTO.setDescription(description);
        }

        Set<ConstraintViolation<QualificationUpdateDTO>> violations =
                validator.validate(updateDTO);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<QualificationUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        service.update(updateDTO);
        System.out.println("Qualification updated successfully.");
    }

    private void deleteQualification() {
        Long id = getLongInput("Enter qualification ID to delete: ");
        service.delete(id);
        System.out.println("Qualification deleted.");
    }

    private void getDriversByQualification() {
        Map<Long, List<DriverViewDTO>> driversByQual = service.getDriversByQualification();
        driversByQual.forEach((qualId, drivers) -> {

            System.out.println("Qualification ID " + qualId + ":");

            if (drivers.isEmpty()) {
                System.out.println("  No drivers.");
            } else {
                drivers.forEach(System.out::println);
            }
        });
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