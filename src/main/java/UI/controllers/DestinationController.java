package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.dto.transportservices.DestinationCreateDTO;
import services.data.dto.transportservices.DestinationUpdateDTO;
import services.data.dto.transportservices.DestinationViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.services.contracts.IDestinationService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class DestinationController {
    private static final Logger logger = LoggerFactory.getLogger(DestinationController.class);

    private final IDestinationService service;
    private final Validator validator;
    private final Scanner scanner;

    public DestinationController(IDestinationService service, Validator validator, Scanner scanner) {

        this.service = service;
        this.validator = validator;
        this.scanner = scanner;
        logger.info("DestinationController initialized with dependencies");
    }

    public void handleMenu() {
        logger.info("Entering DestinationController menu");
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            logger.debug("User selected menu option: {}", choice);
            if (choice == 0) {
                logger.info("Exiting DestinationController menu");
                break;
            }
            processChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println(System.lineSeparator() + "--- Destination Management ---" + System.lineSeparator());
        System.out.println("1. List all destinations");
        System.out.println("2. Add a new destination");
        System.out.println("3. Update a destination");
        System.out.println("4. Delete a destination");
        System.out.println("5. Get transport services by destination");
        System.out.println("0. Back to main menu");
        System.out.print("Enter your choice: ");
        logger.debug("Displayed Destination Management menu");
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
                    logger.info("Processing list all destinations request");
                    listAllDestinations();
                    break;
                case 2:
                    logger.info("Processing add new destination request");
                    addNewDestination();
                    break;
                case 3:
                    logger.info("Processing update destination request");
                    updateDestination();
                    break;
                case 4:
                    logger.info("Processing delete destination request");
                    deleteDestination();
                    break;
                case 5:
                    logger.info("Processing get transport services by destination request");
                    getTransportServicesByDestination();
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

    private void listAllDestinations() {
        logger.debug("Fetching all destinations");
        List<DestinationViewDTO> destinations = service.getAll(0, Integer.MAX_VALUE, "startingLocation", true, "");
        if (destinations.isEmpty()) {
            logger.info("No destinations found");
            System.out.println("No destinations found.");
        } else {
            logger.info("Found {} destinations", destinations.size());
            destinations.forEach(System.out::println);
        }
    }

    private void addNewDestination() {

        System.out.print("Enter starting location: ");
        String startingLocation = scanner.nextLine().trim();
        logger.debug("Received starting location: {}", startingLocation);

        System.out.print("Enter ending location: ");
        String endingLocation = scanner.nextLine().trim();
        logger.debug("Received ending location: {}", endingLocation);

        DestinationCreateDTO createDTO = new DestinationCreateDTO();
        createDTO.setStartingLocation(startingLocation);
        createDTO.setEndingLocation(endingLocation);

        Set<ConstraintViolation<DestinationCreateDTO>> violations =
                validator.validate(createDTO);

        if (!violations.isEmpty()) {
            logger.warn("Validation failed for new destination: {}", createDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<DestinationCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Creating new destination with DTO: {}", createDTO);
        DestinationViewDTO created = service.create(createDTO);
        System.out.println("Destination created with ID: " + created.getId());
        logger.info("Destination created successfully with ID: {}", created.getId());
    }

    private void updateDestination() {

        Long id = getLongInput("Enter destination ID to update: ");
        logger.debug("Fetching destination with ID: {}", id);
        DestinationViewDTO destination = service.getById(id);

        if (destination == null) {
            logger.info("No destination found with ID: {}", id);
            System.out.println("Destination not found.");
            return;
        }

        System.out.print("Enter new starting location (leave blank to keep '" + destination.getStartingLocation() + "'): ");
        String startingLocation = scanner.nextLine().trim();
        logger.debug("Received new starting location: {}", startingLocation.isEmpty() ? "unchanged" : startingLocation);

        System.out.print("Enter new ending location (leave blank to keep '" + destination.getEndingLocation() + "'): ");
        String endingLocation = scanner.nextLine().trim();
        logger.debug("Received new ending location: {}", endingLocation.isEmpty() ? "unchanged" : endingLocation);

        DestinationUpdateDTO updateDTO = new DestinationUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setStartingLocation(destination.getStartingLocation());
        updateDTO.setEndingLocation(destination.getEndingLocation());

        if (!startingLocation.isEmpty()) {
            updateDTO.setStartingLocation(startingLocation);
        }

        if (!endingLocation.isEmpty()) {
            updateDTO.setEndingLocation(endingLocation);
        }

        Set<ConstraintViolation<DestinationUpdateDTO>> violations =
                validator.validate(updateDTO);

        if (!violations.isEmpty()) {
            logger.warn("Validation failed for updated destination: {}", updateDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<DestinationUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Updating destination with ID {} using DTO: {}", id, updateDTO);
        service.update(updateDTO);
        System.out.println("Destination updated successfully.");
        logger.info("Destination with ID {} updated successfully", id);
    }

    private void deleteDestination() {
        Long id = getLongInput("Enter destination ID to delete: ");
        logger.info("Deleting destination with ID: {}", id);
        service.delete(id);
        System.out.println("Destination deleted.");
        logger.info("Destination with ID {} deleted successfully", id);
    }

    private void getTransportServicesByDestination() {
        logger.debug("Fetching transport services by destination");
        Map<Long, List<TransportServiceViewDTO>> servicesByDest = service.getTransportServicesByDestination();
        if (servicesByDest.isEmpty()) {
            logger.info("No transport services found by destination");
            System.out.println("No services found.");
        } else {
            logger.info("Found transport services for {} destinations", servicesByDest.size());
            servicesByDest.forEach((destId, services) -> {
                System.out.println("Destination ID " + destId + ":");
                if (services.isEmpty()) {
                    logger.debug("No services for destination ID: {}", destId);
                    System.out.println("  No services.");
                } else {
                    logger.debug("Found {} services for destination ID: {}", services.size(), destId);
                    services.forEach(System.out::println);
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