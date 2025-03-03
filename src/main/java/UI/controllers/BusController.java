package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.dto.vehicles.BusCreateDTO;
import services.data.dto.vehicles.BusUpdateDTO;
import services.data.dto.vehicles.BusViewDTO;
import services.services.contracts.IBusService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class BusController {
    private final IBusService service;
    private final Validator validator;
    private final Scanner scanner;

    public BusController(IBusService service, Validator validator, Scanner scanner) {
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
        System.out.println(System.lineSeparator() + "--- Bus Management ---" + System.lineSeparator());
        System.out.println("1. List all buses");
        System.out.println("2. Add a new bus");
        System.out.println("3. Update a bus");
        System.out.println("4. Delete a bus");
        System.out.println("5. Get transport services for bus");
        System.out.println("6. Get active transport services");
        System.out.println("7. Get buses by company");
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
                case 1: listAllBuses(); break;
                case 2: addNewBus(); break;
                case 3: updateBus(); break;
                case 4: deleteBus(); break;
                case 5: getTransportServicesForBus(); break;
                case 6: getActiveTransportServices(); break;
                case 7: getBusesByCompany(); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void listAllBuses() {
        List<BusViewDTO> buses = service.getAll(0, Integer.MAX_VALUE, "registrationPlate", true);
        if (buses.isEmpty()) {
            System.out.println("No buses found.");
        } else {
            buses.forEach(System.out::println);
        }
    }

    private void addNewBus() {
        System.out.print("Enter registration plate: ");
        String regPlate = scanner.nextLine().trim();

        System.out.print("Enter transport company ID: ");
        Long companyId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter max passenger capacity: ");
        Integer maxCapacity = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Has restroom (true/false): ");
        boolean hasRestroom = Boolean.parseBoolean(scanner.nextLine().trim());

        System.out.print("Enter luggage capacity: ");
        BigDecimal luggageCapacity = new BigDecimal(scanner.nextLine().trim());

        BusCreateDTO createDTO = new BusCreateDTO();
        createDTO.setRegistrationPlate(regPlate);
        createDTO.setTransportCompanyId(companyId);
        createDTO.setMaxPassengerCapacity(maxCapacity);
        createDTO.setHasRestroom(hasRestroom);
        createDTO.setLuggageCapacity(luggageCapacity);

        Set<ConstraintViolation<BusCreateDTO>> violations =
                validator.validate(createDTO);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<BusCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        BusViewDTO created = service.create(createDTO);
        System.out.println("Bus created with ID: " + created.getId());
    }

    private void updateBus() {
        Long id = getLongInput("Enter bus ID to update: ");
        BusViewDTO bus = service.getById(id);
        if (bus == null) {
            System.out.println("Bus not found.");
            return;
        }

        System.out.print("Enter new registration plate (leave blank to keep '" + bus.getRegistrationPlate() + "'): ");
        String regPlate = scanner.nextLine().trim();

        System.out.print("Enter new transport company ID (leave blank to keep " + bus.getTransportCompanyId() + "): ");
        Long companyId = getLongInput("Enter new transport company ID (leave blank to keep " + bus.getTransportCompanyId() + "): ");

        System.out.print("Enter new max passenger capacity (leave blank to keep " + bus.getMaxPassengerCapacity() + "): ");
        String maxCapacityInput = scanner.nextLine().trim();

        System.out.print("Has restroom (true/false, leave blank to keep " + bus.getHasRestroom() + "): ");
        String hasRestroomInput = scanner.nextLine().trim();

        System.out.print("Enter new luggage capacity (leave blank to keep " + bus.getLuggageCapacity() + "): ");
        String luggageCapacityInput = scanner.nextLine().trim();

        BusUpdateDTO updateDTO = new BusUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setRegistrationPlate(bus.getRegistrationPlate());
        updateDTO.setTransportCompanyId(bus.getTransportCompanyId());
        updateDTO.setMaxPassengerCapacity(bus.getMaxPassengerCapacity());
        updateDTO.setHasRestroom(bus.getHasRestroom());
        updateDTO.setLuggageCapacity(bus.getLuggageCapacity());

        if (!regPlate.isEmpty()) {
            updateDTO.setRegistrationPlate(regPlate);
        }

        if (companyId != null) {
            updateDTO.setTransportCompanyId(companyId);
        }

        if (!maxCapacityInput.isEmpty()) {
            updateDTO.setMaxPassengerCapacity(Integer.parseInt(maxCapacityInput));
        }

        if (!hasRestroomInput.isEmpty()) {
            updateDTO.setHasRestroom(Boolean.parseBoolean(hasRestroomInput));
        }

        if (!luggageCapacityInput.isEmpty()) {
            updateDTO.setLuggageCapacity(new BigDecimal(luggageCapacityInput));
        }

        Set<ConstraintViolation<BusUpdateDTO>> violations =
                validator.validate(updateDTO);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<BusUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        service.update(updateDTO);
        System.out.println("Bus updated successfully.");
    }

    private void deleteBus() {
        Long id = getLongInput("Enter bus ID to delete: ");
        service.delete(id);
        System.out.println("Bus deleted.");
    }

    private void getTransportServicesForBus() {
        Long id = getLongInput("Enter bus ID: ");
        List<? extends TransportServiceViewDTO> services = service.getTransportServicesForBus(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            System.out.println("No services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getActiveTransportServices() {
        Long id = getLongInput("Enter bus ID: ");
        List<? extends TransportServiceViewDTO> services = service.getActiveTransportServices(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            System.out.println("No active services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getBusesByCompany() {
        Long id = getLongInput("Enter company ID: ");
        List<BusViewDTO> buses = service.getBusesByCompany(id, 0, Integer.MAX_VALUE);
        if (buses.isEmpty()) {
            System.out.println("No buses found.");
        } else {
            buses.forEach(System.out::println);
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