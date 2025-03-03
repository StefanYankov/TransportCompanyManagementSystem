package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import services.data.dto.transportservices.TransportPassengersServiceCreateDTO;
import services.data.dto.transportservices.TransportPassengersServiceUpdateDTO;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;
import services.services.contracts.ITransportPassengersServiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class TransportPassengersServiceController {
    private final ITransportPassengersServiceService service;
    private final Validator validator;
    private final Scanner scanner;

    public TransportPassengersServiceController(ITransportPassengersServiceService service, Validator validator, Scanner scanner) {
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
        System.out.println(System.lineSeparator() + "--- Passenger Service Management ---" + System.lineSeparator());
        System.out.println("1. List all passenger services");
        System.out.println("2. Add a new passenger service");
        System.out.println("3. Update a passenger service");
        System.out.println("4. Delete a passenger service");
        System.out.println("5. Get transports sorted by destination");
        System.out.println("6. Get total transport count");
        System.out.println("7. Get active services");
        System.out.println("8. Get services by driver");
        System.out.println("9. Get services by client");
        System.out.println("10. Get services by company");
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
                case 1: listAllPassengerServices(); break;
                case 2: addNewPassengerService(); break;
                case 3: updatePassengerService(); break;
                case 4: deletePassengerService(); break;
                case 5: getTransportsSortedByDestination(); break;
                case 6: getTotalTransportCount(); break;
                case 7: getActiveServices(); break;
                case 8: getServicesByDriver(); break;
                case 9: getServicesByClient(); break;
                case 10: getServicesByCompany(); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void listAllPassengerServices() {

        List<TransportPassengersServiceViewDTO> services = service.getAll(0, Integer.MAX_VALUE, "id", true, "");

        if (services.isEmpty()) {
            System.out.println("No passenger services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void addNewPassengerService() {

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

        System.out.print("Enter number of passengers: ");
        int passengers = Integer.parseInt(scanner.nextLine().trim());

        TransportPassengersServiceCreateDTO createDTO = new TransportPassengersServiceCreateDTO();
        createDTO.setTransportCompanyId(companyId);
        createDTO.setStartingDate(startingDate);
        createDTO.setEndingDate(endingDate);
        createDTO.setDestinationId(destinationId);
        createDTO.setClientId(clientId);
        createDTO.setPrice(price);
        createDTO.setVehicleId(vehicleId);
        createDTO.setDriverId(driverId);
        createDTO.setNumberOfPassengers(passengers);

        Set<ConstraintViolation<TransportPassengersServiceCreateDTO>> violations =
                validator.validate(createDTO);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<TransportPassengersServiceCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        TransportPassengersServiceViewDTO created = service.create(createDTO);
        System.out.println("Passenger service created with ID: " + created.getId());
    }

    private void updatePassengerService() {

        Long id = getLongInput("Enter passenger service ID to update: ");
        TransportPassengersServiceViewDTO serviceDTO = service.getById(id);

        if (serviceDTO == null) {
            System.out.println("Passenger service not found.");
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

        System.out.print("Enter new number of passengers (leave blank to keep " + serviceDTO.getNumberOfPassengers() + "): ");
        String passengersInput = scanner.nextLine().trim();

        TransportPassengersServiceUpdateDTO updateDTO = new TransportPassengersServiceUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setTransportCompanyId(serviceDTO.getTransportCompanyId());
        updateDTO.setStartingDate(serviceDTO.getStartingDate());
        updateDTO.setEndingDate(serviceDTO.getEndingDate());
        updateDTO.setDestinationId(serviceDTO.getDestinationId());
        updateDTO.setClientId(serviceDTO.getClientId());
        updateDTO.setPrice(serviceDTO.getPrice());
        updateDTO.setVehicleId(serviceDTO.getVehicleId());
        updateDTO.setDriverId(serviceDTO.getDriverId());
        updateDTO.setNumberOfPassengers(serviceDTO.getNumberOfPassengers());

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

        if (!passengersInput.isEmpty()) {
            updateDTO.setNumberOfPassengers(Integer.parseInt(passengersInput));
        }

        Set<ConstraintViolation<TransportPassengersServiceUpdateDTO>> violations =
                validator.validate(updateDTO);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<TransportPassengersServiceUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        service.update(updateDTO);
        System.out.println("Passenger service updated successfully.");
    }

    private void deletePassengerService() {
        Long id = getLongInput("Enter passenger service ID to delete: ");
        service.delete(id);
        System.out.println("Passenger service deleted.");
    }

    private void getTransportsSortedByDestination() {
        System.out.print("Sort ascending? (yes/no): ");
        boolean ascending = scanner.nextLine().trim().equalsIgnoreCase("yes");
        List<TransportPassengersServiceViewDTO> services = service.getTransportsSortedByDestination(0, Integer.MAX_VALUE, ascending);
        if (services.isEmpty()) {
            System.out.println("No passenger services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getTotalTransportCount() {
        int count = service.getTotalTransportCount();
        System.out.println("Total transport count: " + count);
    }

    private void getActiveServices() {
        List<TransportPassengersServiceViewDTO> services = service.getActiveServices(0, Integer.MAX_VALUE, "id", true);
        if (services.isEmpty()) {
            System.out.println("No active passenger services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getServicesByDriver() {
        Long id = getLongInput("Enter driver ID: ");
        List<TransportPassengersServiceViewDTO> services = service.getByDriver(id, 0, Integer.MAX_VALUE, "id", true);
        if (services.isEmpty()) {
            System.out.println("No passenger services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getServicesByClient() {
        Long id = getLongInput("Enter client ID: ");
        List<TransportPassengersServiceViewDTO> services = service.getByClient(id, 0, Integer.MAX_VALUE, "id", true);
        if (services.isEmpty()) {
            System.out.println("No passenger services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getServicesByCompany() {
        Long id = getLongInput("Enter company ID: ");
        List<TransportPassengersServiceViewDTO> services = service.getByCompany(id, 0, Integer.MAX_VALUE, "id", true);
        if (services.isEmpty()) {
            System.out.println("No passenger services found.");
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