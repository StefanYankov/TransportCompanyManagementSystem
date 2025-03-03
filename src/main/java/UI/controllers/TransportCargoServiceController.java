package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import services.data.dto.transportservices.TransportCargoServiceCreateDTO;
import services.data.dto.transportservices.TransportCargoServiceUpdateDTO;
import services.data.dto.transportservices.TransportCargoServiceViewDTO;
import services.services.contracts.ITransportCargoServiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class TransportCargoServiceController {
    private final ITransportCargoServiceService service;
    private final Validator validator;
    private final Scanner scanner;

    public TransportCargoServiceController(ITransportCargoServiceService service, Validator validator, Scanner scanner) {
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
        System.out.println(System.lineSeparator() + "--- Cargo Service Management ---" + System.lineSeparator());
        System.out.println("1. List all cargo services");
        System.out.println("2. Add a new cargo service");
        System.out.println("3. Update a cargo service");
        System.out.println("4. Delete a cargo service");
        System.out.println("5. Get cargo services by company");
        System.out.println("6. Get cargo services by client");
        System.out.println("7. Get cargo services by driver");
        System.out.println("8. Get active cargo services");
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
                case 1: listAllCargoServices(); break;
                case 2: addNewCargoService(); break;
                case 3: updateCargoService(); break;
                case 4: deleteCargoService(); break;
                case 5: getCargoServicesByCompany(); break;
                case 6: getCargoServicesByClient(); break;
                case 7: getCargoServicesByDriver(); break;
                case 8: getActiveCargoServices(); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void listAllCargoServices() {

        List<TransportCargoServiceViewDTO> services = service.getAll(0, Integer.MAX_VALUE, "id", true);

        if (services.isEmpty()) {
            System.out.println("No cargo services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void addNewCargoService() {
        System.out.print("Enter transport company ID: ");
        Long companyId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter starting date (YYYY-MM-DD): ");
        LocalDate startingDate = LocalDate.parse(scanner.nextLine().trim());

        System.out.print("Enter ending date (YYYY-MM-DD): ");
        LocalDate endingDate = LocalDate.parse(scanner.nextLine().trim());

        System.out.print("Enter destination ID: ");
        Long destinationId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter client ID: ");
        Long clientId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter price: ");
        BigDecimal price = new BigDecimal(scanner.nextLine().trim());

        System.out.print("Enter vehicle ID: ");
        Long vehicleId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter driver ID: ");
        Long driverId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter weight in kilograms: ");
        BigDecimal weight = new BigDecimal(scanner.nextLine().trim());

        System.out.print("Enter length in centimeters: ");
        int length = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter width in centimeters: ");
        int width = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter height in centimeters: ");
        int height = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();

        TransportCargoServiceCreateDTO createDTO = new TransportCargoServiceCreateDTO();
        createDTO.setTransportCompanyId(companyId);
        createDTO.setStartingDate(startingDate);
        createDTO.setEndingDate(endingDate);
        createDTO.setDestinationId(destinationId);
        createDTO.setClientId(clientId);
        createDTO.setPrice(price);
        createDTO.setVehicleId(vehicleId);
        createDTO.setDriverId(driverId);
        createDTO.setWeightInKilograms(weight);
        createDTO.setLengthInCentimeters(length);
        createDTO.setWidthInCentimeters(width);
        createDTO.setHeightInCentimeters(height);
        createDTO.setDescription(description);

        Set<ConstraintViolation<TransportCargoServiceCreateDTO>> violations =
                validator.validate(createDTO);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<TransportCargoServiceCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        TransportCargoServiceViewDTO created = service.create(createDTO);
        System.out.println("Cargo service created with ID: " + created.getId());
    }

    private void updateCargoService() {

        Long id = getLongInput("Enter cargo service ID to update: ");

        TransportCargoServiceViewDTO serviceDTO = service.getById(id);

        if (serviceDTO == null) {
            System.out.println("Cargo service not found.");
            return;
        }

        System.out.print("Enter new transport company ID (leave blank to keep " + serviceDTO.getTransportCompanyId() + "): ");
        Long companyId = getLongInput("Enter new transport company ID (leave blank to keep " + serviceDTO.getTransportCompanyId() + "): ");

        System.out.print("Enter new starting date (YYYY-MM-DD, leave blank to keep " + serviceDTO.getStartingDate() + "): ");
        String startingDateInput = scanner.nextLine().trim();

        System.out.print("Enter new ending date (YYYY-MM-DD, leave blank to keep " + serviceDTO.getEndingDate() + "): ");
        String endingDateInput = scanner.nextLine().trim();

        System.out.print("Enter new destination ID (leave blank to keep " + serviceDTO.getDestinationId() + "): ");
        Long destinationId = getLongInput("Enter new destination ID (leave blank to keep " + serviceDTO.getDestinationId() + "): ");

        System.out.print("Enter new client ID (leave blank to keep " + serviceDTO.getClientId() + "): ");
        Long clientId = getLongInput("Enter new client ID (leave blank to keep " + serviceDTO.getClientId() + "): ");

        System.out.print("Enter new price (leave blank to keep " + serviceDTO.getPrice() + "): ");
        String priceInput = scanner.nextLine().trim();

        System.out.print("Enter new vehicle ID (leave blank to keep " + serviceDTO.getVehicleId() + "): ");
        Long vehicleId = getLongInput("Enter new vehicle ID (leave blank to keep " + serviceDTO.getVehicleId() + "): ");

        System.out.print("Enter new driver ID (leave blank to keep " + serviceDTO.getDriverId() + "): ");
        Long driverId = getLongInput("Enter new driver ID (leave blank to keep " + serviceDTO.getDriverId() + "): ");

        System.out.print("Enter new weight in kilograms (leave blank to keep " + serviceDTO.getWeightInKilograms() + "): ");
        String weightInput = scanner.nextLine().trim();

        System.out.print("Enter new length in centimeters (leave blank to keep " + serviceDTO.getLengthInCentimeters() + "): ");
        String lengthInput = scanner.nextLine().trim();

        System.out.print("Enter new width in centimeters (leave blank to keep " + serviceDTO.getWidthInCentimeters() + "): ");
        String widthInput = scanner.nextLine().trim();

        System.out.print("Enter new height in centimeters (leave blank to keep " + serviceDTO.getHeightInCentimeters() + "): ");
        String heightInput = scanner.nextLine().trim();

        System.out.print("Enter new description (leave blank to keep '" + serviceDTO.getDescription() + "'): ");
        String description = scanner.nextLine().trim();

        TransportCargoServiceUpdateDTO updateDTO = new TransportCargoServiceUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setTransportCompanyId(serviceDTO.getTransportCompanyId());
        updateDTO.setStartingDate(serviceDTO.getStartingDate());
        updateDTO.setEndingDate(serviceDTO.getEndingDate());
        updateDTO.setDestinationId(serviceDTO.getDestinationId());
        updateDTO.setClientId(serviceDTO.getClientId());
        updateDTO.setPrice(serviceDTO.getPrice());
        updateDTO.setVehicleId(serviceDTO.getVehicleId());
        updateDTO.setDriverId(serviceDTO.getDriverId());
        updateDTO.setWeightInKilograms(serviceDTO.getWeightInKilograms());
        updateDTO.setLengthInCentimeters(serviceDTO.getLengthInCentimeters());
        updateDTO.setWidthInCentimeters(serviceDTO.getWidthInCentimeters());
        updateDTO.setHeightInCentimeters(serviceDTO.getHeightInCentimeters());
        updateDTO.setDescription(serviceDTO.getDescription());

        if (companyId != null) {
            updateDTO.setTransportCompanyId(companyId);
        }

        if (!startingDateInput.isEmpty()) {
            updateDTO.setStartingDate(LocalDate.parse(startingDateInput));
        }

        if (!endingDateInput.isEmpty()) {
            updateDTO.setEndingDate(LocalDate.parse(endingDateInput));
        }

        if (destinationId != null) {
            updateDTO.setDestinationId(destinationId);
        }

        if (clientId != null) {
            updateDTO.setClientId(clientId);
        }

        if (!priceInput.isEmpty()) {
            updateDTO.setPrice(new BigDecimal(priceInput));
        }

        if (vehicleId != null) {
            updateDTO.setVehicleId(vehicleId);
        }

        if (driverId != null) {
            updateDTO.setDriverId(driverId);
        }

        if (!weightInput.isEmpty()) {
            updateDTO.setWeightInKilograms(new BigDecimal(weightInput));
        }

        if (!lengthInput.isEmpty()) {
            updateDTO.setLengthInCentimeters(Integer.parseInt(lengthInput));
        }

        if (!widthInput.isEmpty()) {
            updateDTO.setWidthInCentimeters(Integer.parseInt(widthInput));
        }

        if (!heightInput.isEmpty()) {
            updateDTO.setHeightInCentimeters(Integer.parseInt(heightInput));
        }

        if (!description.isEmpty()) {
            updateDTO.setDescription(description);
        }

        Set<ConstraintViolation<TransportCargoServiceUpdateDTO>> violations =
                validator.validate(updateDTO);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<TransportCargoServiceUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        service.update(updateDTO);
        System.out.println("Cargo service updated successfully.");
    }

    private void deleteCargoService() {
        Long id = getLongInput("Enter cargo service ID to delete: ");
        service.delete(id);
        System.out.println("Cargo service deleted.");
    }

    private void getCargoServicesByCompany() {
        Long id = getLongInput("Enter company ID: ");
        List<TransportCargoServiceViewDTO> services = service.getByCompany(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            System.out.println("No cargo services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getCargoServicesByClient() {
        Long id = getLongInput("Enter client ID: ");
        List<TransportCargoServiceViewDTO> services = service.getByClient(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            System.out.println("No cargo services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getCargoServicesByDriver() {
        Long id = getLongInput("Enter driver ID: ");
        List<TransportCargoServiceViewDTO> services = service.getByDriver(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            System.out.println("No cargo services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getActiveCargoServices() {
        List<TransportCargoServiceViewDTO> services = service.getActiveServices(0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            System.out.println("No active cargo services found.");
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