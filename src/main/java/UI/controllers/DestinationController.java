package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
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
    private final IDestinationService service;
    private final Validator validator;
    private final Scanner scanner;

    public DestinationController(IDestinationService service, Validator validator, Scanner scanner) {
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
        System.out.println(System.lineSeparator() + "--- Destination Management ---" + System.lineSeparator());
        System.out.println("1. List all destinations");
        System.out.println("2. Add a new destination");
        System.out.println("3. Update a destination");
        System.out.println("4. Delete a destination");
        System.out.println("5. Get transport services by destination");
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
                    listAllDestinations();
                    break;
                case 2:
                    addNewDestination();
                    break;
                case 3:
                    updateDestination();
                    break;
                case 4:
                    deleteDestination();
                    break;
                case 5:
                    getTransportServicesByDestination();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void listAllDestinations() {
        List<DestinationViewDTO> destinations = service.getAll(0, Integer.MAX_VALUE, "startingLocation", true, "");
        if (destinations.isEmpty()) {
            System.out.println("No destinations found.");
        } else {
            destinations.forEach(System.out::println);
        }
    }

    private void addNewDestination() {

        System.out.print("Enter starting location: ");
        String startingLocation = scanner.nextLine().trim();

        System.out.print("Enter ending location: ");
        String endingLocation = scanner.nextLine().trim();

        DestinationCreateDTO createDTO = new DestinationCreateDTO();
        createDTO.setStartingLocation(startingLocation);
        createDTO.setEndingLocation(endingLocation);

        Set<ConstraintViolation<DestinationCreateDTO>> violations =
                validator.validate(createDTO);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<DestinationCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        DestinationViewDTO created = service.create(createDTO);
        System.out.println("Destination created with ID: " + created.getId());
    }

    private void updateDestination() {

        Long id = getLongInput("Enter destination ID to update: ");
        DestinationViewDTO destination = service.getById(id);

        if (destination == null) {
            System.out.println("Destination not found.");
            return;
        }

        System.out.print("Enter new starting location (leave blank to keep '" + destination.getStartingLocation() + "'): ");
        String startingLocation = scanner.nextLine().trim();

        System.out.print("Enter new ending location (leave blank to keep '" + destination.getEndingLocation() + "'): ");
        String endingLocation = scanner.nextLine().trim();

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
            System.out.println("Validation errors:");
            for (ConstraintViolation<DestinationUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        service.update(updateDTO);
        System.out.println("Destination updated successfully.");
    }

    private void deleteDestination() {
        Long id = getLongInput("Enter destination ID to delete: ");
        service.delete(id);
        System.out.println("Destination deleted.");
    }

    private void getTransportServicesByDestination() {
        Map<Long, List<TransportServiceViewDTO>> servicesByDest = service.getTransportServicesByDestination();
        servicesByDest.forEach((destId, services) -> {
            System.out.println("Destination ID " + destId + ":");
            if (services.isEmpty()) {
                System.out.println("  No services.");
            } else {
                services.forEach(System.out::println);
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