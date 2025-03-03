package UI.controllers;

import data.models.transportservices.CargoType;
import data.models.vehicles.TruckType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.dto.vehicles.TruckCreateDTO;
import services.data.dto.vehicles.TruckUpdateDTO;
import services.data.dto.vehicles.TruckViewDTO;
import services.services.contracts.ITruckService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class TruckController {
    private final ITruckService service;
    private final Validator validator;
    private final Scanner scanner;

    public TruckController(ITruckService service, Validator validator, Scanner scanner) {
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
        System.out.println(System.lineSeparator() + "--- Truck Management ---" + System.lineSeparator());
        System.out.println("1. List all trucks");
        System.out.println("2. Add a new truck");
        System.out.println("3. Update a truck");
        System.out.println("4. Delete a truck");
        System.out.println("5. Get transport services for truck");
        System.out.println("6. Get active transport services");
        System.out.println("7. Get trucks by company");
        System.out.println("8. Get all by truck type");
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
                case 1: listAllTrucks(); break;
                case 2: addNewTruck(); break;
                case 3: updateTruck(); break;
                case 4: deleteTruck(); break;
                case 5: getTransportServicesForTruck(); break;
                case 6: getActiveTransportServices(); break;
                case 7: getTrucksByCompany(); break;
                case 8: getAllByTruckType(); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void listAllTrucks() {
        List<TruckViewDTO> trucks = service.getAll(0, Integer.MAX_VALUE, "registrationPlate", true);
        if (trucks.isEmpty()) {
            System.out.println("No trucks found.");
        } else {
            trucks.forEach(System.out::println);
        }
    }

    private void addNewTruck() {

        System.out.print("Enter registration plate: ");
        String regPlate = scanner.nextLine().trim();

        System.out.print("Enter transport company ID: ");
        Long companyId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter max cargo capacity (kg): ");
        Double maxCargo = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Enter current cargo capacity (kg): ");
        Double currentCargo = Double.parseDouble(scanner.nextLine().trim());

        CargoType cargoType = selectEnum(CargoType.class, "Select cargo type:", null).get();
        TruckType truckType = selectEnum(TruckType.class, "Select truck type:", null).get();

        TruckCreateDTO dto = new TruckCreateDTO();
        dto.setRegistrationPlate(regPlate);
        dto.setTransportCompanyId(companyId);
        dto.setMaxCargoCapacityKg(maxCargo);
        dto.setCurrentCargoCapacityKg(currentCargo);
        dto.setCargoType(cargoType);
        dto.setTruckType(truckType);

        Set<ConstraintViolation<TruckCreateDTO>> violations =
                validator.validate(dto);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<TruckCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        TruckViewDTO created = service.create(dto);
        System.out.println("Truck created with ID: " + created.getId());
    }

    private void updateTruck() {

        Long id = getLongInput("Enter truck ID to update: ");
        TruckViewDTO truck = service.getById(id);

        if (truck == null) {
            System.out.println("Truck not found.");
            return;
        }

        System.out.print("Enter new registration plate (leave blank to keep '" + truck.getRegistrationPlate() + "'): ");
        String regPlate = scanner.nextLine().trim();

        System.out.print("Enter new transport company ID (leave blank to keep " + truck.getTransportCompanyId() + "): ");
        Long companyId = getLongInput("Enter new transport company ID (leave blank to keep " + truck.getTransportCompanyId() + "): ");

        System.out.print("Enter new max cargo capacity (kg, leave blank to keep " + truck.getMaxCargoCapacityKg() + "): ");
        String maxCargoInput = scanner.nextLine().trim();

        System.out.print("Enter new current cargo capacity (kg, leave blank to keep " + truck.getCurrentCargoCapacityKg() + "): ");
        String currentCargoInput = scanner.nextLine().trim();

        Optional<CargoType> newCargoType = selectEnum(CargoType.class, "Select new cargo type:", truck.getCargoType());
        Optional<TruckType> newTruckType = selectEnum(TruckType.class, "Select new truck type:", truck.getTruckType());


        TruckUpdateDTO dto = new TruckUpdateDTO();
        dto.setId(id);
        dto.setRegistrationPlate(truck.getRegistrationPlate());
        dto.setTransportCompanyId(truck.getTransportCompanyId());
        dto.setMaxCargoCapacityKg(truck.getMaxCargoCapacityKg());
        dto.setCurrentCargoCapacityKg(truck.getCurrentCargoCapacityKg());
        dto.setCargoType(truck.getCargoType());
        dto.setTruckType(truck.getTruckType());

        if (!regPlate.isEmpty()) {
            dto.setRegistrationPlate(regPlate);
        }

        if (companyId != null) {
            dto.setTransportCompanyId(companyId);
        }

        if (!maxCargoInput.isEmpty()) {
            dto.setMaxCargoCapacityKg(Double.parseDouble(maxCargoInput));
        }

        if (!currentCargoInput.isEmpty()) {
            dto.setCurrentCargoCapacityKg(Double.parseDouble(currentCargoInput));
        }

        newCargoType.ifPresent(cargoType -> dto.setCargoType(cargoType));
        newTruckType.ifPresent(truckType -> dto.setTruckType(truckType));

        Set<ConstraintViolation<TruckUpdateDTO>> violations =
                validator.validate(dto);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<TruckUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        service.update(dto);
        System.out.println("Truck updated successfully.");
    }

    private void deleteTruck() {
        Long id = getLongInput("Enter truck ID to delete: ");
        service.delete(id);
        System.out.println("Truck deleted.");
    }

    private void getTransportServicesForTruck() {
        Long id = getLongInput("Enter truck ID: ");
        List<? extends TransportServiceViewDTO> services = service.getTransportServicesForTruck(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            System.out.println("No services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getActiveTransportServices() {
        Long id = getLongInput("Enter truck ID: ");
        List<? extends TransportServiceViewDTO>services = service.getActiveTransportServices(id, 0, Integer.MAX_VALUE);

        if (services.isEmpty()) {
            System.out.println("No active services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getTrucksByCompany() {
        Long id = getLongInput("Enter company ID: ");
        List<TruckViewDTO> trucks = service.getTrucksByCompany(id, 0, Integer.MAX_VALUE);

        if (trucks.isEmpty()) {
            System.out.println("No trucks found.");
        } else {
            trucks.forEach(System.out::println);
        }
    }

    private void getAllByTruckType() {
        // Use selection menu to choose truck type
        TruckType selectedTruckType = selectEnum(TruckType.class, "Select truck type to filter by:").get();

        List<TruckViewDTO> trucks = service.getAllByTruckType(selectedTruckType.name(), 0, Integer.MAX_VALUE, "registrationPlate", true);
        if (trucks.isEmpty()) {
            System.out.println("No trucks found.");
        } else {
            trucks.forEach(System.out::println);
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

    private <T extends Enum<T>> Optional<T> selectEnum(Class<T> enumClass, String prompt, T currentValue) {
        T[] values = enumClass.getEnumConstants();
        System.out.println(prompt);
        if (currentValue != null) {
            System.out.println("0. Keep current value: " + currentValue.name());
        }

        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + ". " + values[i].name());
        }

        while (true) {
            System.out.print("Enter your choice (" + (currentValue != null ? "0 to keep current, " : "") + "1-" + values.length + "): ");
            String input = scanner.nextLine().trim();
            if (currentValue != null && (input.isEmpty() || input.equals("0"))) {
                return Optional.empty();
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= values.length) {
                    return Optional.of(values[choice - 1]);
                } else {
                    System.out.println("Invalid choice. Please enter a number between " + (currentValue != null ? "0" : "1") + " and " + values.length + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private <T extends Enum<T>> Optional<T> selectEnum(Class<T> enumClass, String prompt) {
        T[] values = enumClass.getEnumConstants();
        System.out.println(prompt);
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + ". " + values[i].name());
        }
        while (true) {
            System.out.print("Enter your choice (1-" + values.length + "): ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= values.length) {
                    return Optional.of(values[choice - 1]);
                } else {
                    System.out.println("Invalid choice. Please enter a number between 1 and " + values.length + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}