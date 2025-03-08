package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(QualificationController.class);

    private final IQualificationService service;
    private final Validator validator;
    private final Scanner scanner;

    public QualificationController(IQualificationService service, Validator validator, Scanner scanner) {
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
        System.out.println(System.lineSeparator() + "--- Qualification Management ---" + System.lineSeparator());
        System.out.println("1. List all qualifications");
        System.out.println("2. Add a new qualification");
        System.out.println("3. Update a qualification");
        System.out.println("4. Delete a qualification");
        System.out.println("5. Get drivers by qualification");
        System.out.println("0. Back to main menu");
        System.out.print("Enter your choice: ");
        logger.debug("Displayed Qualification Management menu");
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
                    logger.info("Processing list all qualifications request");
                    listAllQualifications();
                    break;
                case 2:
                    logger.info("Processing add new qualification request");
                    addNewQualification();
                    break;
                case 3:
                    logger.info("Processing update qualification request");
                    updateQualification();
                    break;
                case 4:
                    logger.info("Processing delete qualification request");
                    deleteQualification();
                    break;
                case 5:
                    logger.info("Processing get drivers by qualification request");
                    getDriversByQualification();
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

    private void listAllQualifications() {
        logger.debug("Fetching all qualifications");
        List<QualificationViewDTO> qualifications = service.getAll(0, Integer.MAX_VALUE, "name", true, "");
        if (qualifications.isEmpty()) {
            logger.info("No qualifications found");
            System.out.println("No qualifications found.");
        } else {
            logger.info("Found {} qualifications", qualifications.size());
            qualifications.forEach(System.out::println);
        }
    }

    private void addNewQualification() {
        System.out.print("Enter qualification name: ");
        String name = scanner.nextLine().trim();
        logger.debug("Received qualification name: {}", name);

        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();
        logger.debug("Received description: {}", description);

        QualificationCreateDTO createDTO = new QualificationCreateDTO();
        createDTO.setName(name);
        createDTO.setDescription(description);

        Set<ConstraintViolation<QualificationCreateDTO>> violations = validator.validate(createDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for new qualification: {}", createDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<QualificationCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Creating new qualification with DTO: {}", createDTO);
        QualificationViewDTO created = service.create(createDTO);
        System.out.println("Qualification created with ID: " + created.getId());
        logger.info("Qualification created successfully with ID: {}", created.getId());
    }

    private void updateQualification() {
        Long id = getLongInput("Enter qualification ID to update: ");
        logger.debug("Fetching qualification with ID: {}", id);
        QualificationViewDTO qualification = service.getById(id);

        if (qualification == null) {
            logger.info("No qualification found with ID: {}", id);
            System.out.println("Qualification not found.");
            return;
        }

        System.out.print("Enter new name (leave blank to keep '" + qualification.getName() + "'): ");
        String name = scanner.nextLine().trim();
        logger.debug("Received new name: {}", name.isEmpty() ? "unchanged" : name);

        System.out.print("Enter new description (leave blank to keep '" + qualification.getDescription() + "'): ");
        String description = scanner.nextLine().trim();
        logger.debug("Received new description: {}", description.isEmpty() ? "unchanged" : description);

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

        Set<ConstraintViolation<QualificationUpdateDTO>> violations = validator.validate(updateDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for updated qualification: {}", updateDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<QualificationUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Updating qualification with ID {} using DTO: {}", id, updateDTO);
        service.update(updateDTO);
        System.out.println("Qualification updated successfully.");
        logger.info("Qualification with ID {} updated successfully", id);
    }

    private void deleteQualification() {
        Long id = getLongInput("Enter qualification ID to delete: ");
        logger.info("Deleting qualification with ID: {}", id);
        service.delete(id);
        System.out.println("Qualification deleted.");
        logger.info("Qualification with ID {} deleted successfully", id);
    }

    private void getDriversByQualification() {
        logger.debug("Fetching drivers by qualification");
        Map<Long, List<DriverViewDTO>> driversByQual = service.getDriversByQualification();
        if (driversByQual.isEmpty()) {
            logger.info("No drivers found by qualification");
            System.out.println("No drivers found.");
        } else {
            logger.info("Found drivers for {} qualifications", driversByQual.size());
            driversByQual.forEach((qualId, drivers) -> {
                System.out.println("Qualification ID " + qualId + ":");
                if (drivers.isEmpty()) {
                    logger.debug("No drivers for qualification ID: {}", qualId);
                    System.out.println("  No drivers.");
                } else {
                    logger.debug("Found {} drivers for qualification ID: {}", drivers.size(), qualId);
                    drivers.forEach(System.out::println);
                }
            });
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
}