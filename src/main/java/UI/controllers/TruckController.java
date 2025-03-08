package UI.controllers;

import data.models.transportservices.CargoType;
import data.models.vehicles.TruckType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(TruckController.class);

    private final ITruckService service;
    private final Validator validator;
    private final Scanner scanner;

    public TruckController(ITruckService service, Validator validator, Scanner scanner) {
        this.service = service;
        this.validator = validator;
        this.scanner = scanner;
        logger.info("TruckController initialized with dependencies");
    }

    public void handleMenu() {
        logger.info("Entering TruckController menu");
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            logger.debug("User selected menu option: {}", choice);
            if (choice == 0) {
                logger.info("Exiting TruckController menu");
                break;
            }
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
        logger.debug("Displayed Truck Management menu");
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
                    logger.info("Processing list all trucks request");
                    listAllTrucks();
                    break;
                case 2:
                    logger.info("Processing add new truck request");
                    addNewTruck();
                    break;
                case 3:
                    logger.info("Processing update truck request");
                    updateTruck();
                    break;
                case 4:
                    logger.info("Processing delete truck request");
                    deleteTruck();
                    break;
                case 5:
                    logger.info("Processing get transport services for truck request");
                    getTransportServicesForTruck();
                    break;
                case 6:
                    logger.info("Processing get active transport services request");
                    getActiveTransportServices();
                    break;
                case 7:
                    logger.info("Processing get trucks by company request");
                    getTrucksByCompany();
                    break;
                case 8:
                    logger.info("Processing get all by truck type request");
                    getAllByTruckType();
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

    private void listAllTrucks() {
        logger.debug("Fetching all trucks");
        List<TruckViewDTO> trucks = service.getAll(0, Integer.MAX_VALUE, "registrationPlate", true);
        if (trucks.isEmpty()) {
            logger.info("No trucks found");
            System.out.println("No trucks found.");
        } else {
            logger.info("Found {} trucks", trucks.size());
            trucks.forEach(System.out::println);
        }
    }

    private void addNewTruck() {
        System.out.print("Enter registration plate: ");
        String regPlate = scanner.nextLine().trim();
        logger.debug("Received registration plate: {}", regPlate);

        System.out.print("Enter transport company ID: ");
        Long companyId;
        try {
            companyId = Long.parseLong(scanner.nextLine().trim());
            logger.debug("Received transport company ID: {}", companyId);
        } catch (NumberFormatException e) {
            logger.warn("Invalid transport company ID input: {}", e.getMessage());
            System.out.println("Invalid company ID. Aborting.");
            return;
        }

        System.out.print("Enter max cargo capacity (kg): ");
        Double maxCargo;
        try {
            maxCargo = Double.parseDouble(scanner.nextLine().trim());
            logger.debug("Received max cargo capacity: {}", maxCargo);
        } catch (NumberFormatException e) {
            logger.warn("Invalid max cargo capacity input: {}", e.getMessage());
            System.out.println("Invalid max cargo capacity. Aborting.");
            return;
        }

        System.out.print("Enter current cargo capacity (kg): ");
        Double currentCargo;
        try {
            currentCargo = Double.parseDouble(scanner.nextLine().trim());
            logger.debug("Received current cargo capacity: {}", currentCargo);
        } catch (NumberFormatException e) {
            logger.warn("Invalid current cargo capacity input: {}", e.getMessage());
            System.out.println("Invalid current cargo capacity. Aborting.");
            return;
        }

        Optional<CargoType> cargoTypeOpt = selectEnum(CargoType.class, "Select cargo type:", null);
        if (cargoTypeOpt.isEmpty()) {
            logger.warn("No cargo type selected");
            System.out.println("Cargo type selection failed. Aborting.");
            return;
        }
        CargoType cargoType = cargoTypeOpt.get();
        logger.debug("Received cargo type: {}", cargoType);

        Optional<TruckType> truckTypeOpt = selectEnum(TruckType.class, "Select truck type:", null);
        if (truckTypeOpt.isEmpty()) {
            logger.warn("No truck type selected");
            System.out.println("Truck type selection failed. Aborting.");
            return;
        }
        TruckType truckType = truckTypeOpt.get();
        logger.debug("Received truck type: {}", truckType);

        TruckCreateDTO dto = new TruckCreateDTO();
        dto.setRegistrationPlate(regPlate);
        dto.setTransportCompanyId(companyId);
        dto.setMaxCargoCapacityKg(maxCargo);
        dto.setCurrentCargoCapacityKg(currentCargo);
        dto.setCargoType(cargoType);
        dto.setTruckType(truckType);

        Set<ConstraintViolation<TruckCreateDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for new truck: {}", dto);
            System.out.println("Validation errors:");
            for (ConstraintViolation<TruckCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Creating new truck with DTO: {}", dto);
        TruckViewDTO created = service.create(dto);
        System.out.println("Truck created with ID: " + created.getId());
        logger.info("Truck created successfully with ID: {}", created.getId());
    }

    private void updateTruck() {
        Long id = getLongInput("Enter truck ID to update: ");
        logger.debug("Fetching truck with ID: {}", id);
        TruckViewDTO truck = service.getById(id);

        if (truck == null) {
            logger.info("No truck found with ID: {}", id);
            System.out.println("Truck not found.");
            return;
        }

        System.out.print("Enter new registration plate (leave blank to keep '" + truck.getRegistrationPlate() + "'): ");
        String regPlate = scanner.nextLine().trim();
        logger.debug("Received new registration plate: {}", regPlate.isEmpty() ? "unchanged" : regPlate);

        Long companyId = getLongInputWithDefault("Enter new transport company ID (leave blank to keep " + truck.getTransportCompanyId() + "): ", truck.getTransportCompanyId());
        logger.debug("Received new transport company ID: {}", companyId);

        System.out.print("Enter new max cargo capacity (kg, leave blank to keep " + truck.getMaxCargoCapacityKg() + "): ");
        String maxCargoInput = scanner.nextLine().trim();
        Double maxCargo = truck.getMaxCargoCapacityKg();
        if (!maxCargoInput.isEmpty()) {
            try {
                maxCargo = Double.parseDouble(maxCargoInput);
                logger.debug("Received new max cargo capacity: {}", maxCargo);
            } catch (NumberFormatException e) {
                logger.warn("Invalid new max cargo capacity input: {}", maxCargoInput);
                System.out.println("Invalid max cargo capacity. Aborting.");
                return;
            }
        }

        System.out.print("Enter new current cargo capacity (kg, leave blank to keep " + truck.getCurrentCargoCapacityKg() + "): ");
        String currentCargoInput = scanner.nextLine().trim();
        Double currentCargo = truck.getCurrentCargoCapacityKg();
        if (!currentCargoInput.isEmpty()) {
            try {
                currentCargo = Double.parseDouble(currentCargoInput);
                logger.debug("Received new current cargo capacity: {}", currentCargo);
            } catch (NumberFormatException e) {
                logger.warn("Invalid new current cargo capacity input: {}", currentCargoInput);
                System.out.println("Invalid current cargo capacity. Aborting.");
                return;
            }
        }

        Optional<CargoType> newCargoType = selectEnum(CargoType.class, "Select new cargo type:", truck.getCargoType());
        logger.debug("Received new cargo type: {}", newCargoType.isPresent() ? newCargoType.get() : "unchanged");

        Optional<TruckType> newTruckType = selectEnum(TruckType.class, "Select new truck type:", truck.getTruckType());
        logger.debug("Received new truck type: {}", newTruckType.isPresent() ? newTruckType.get() : "unchanged");

        TruckUpdateDTO dto = new TruckUpdateDTO();
        dto.setId(id);
        dto.setRegistrationPlate(regPlate.isEmpty() ? truck.getRegistrationPlate() : regPlate);
        dto.setTransportCompanyId(companyId);
        dto.setMaxCargoCapacityKg(maxCargo);
        dto.setCurrentCargoCapacityKg(currentCargo);
        dto.setCargoType(newCargoType.orElse(truck.getCargoType()));
        dto.setTruckType(newTruckType.orElse(truck.getTruckType()));

        Set<ConstraintViolation<TruckUpdateDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for updated truck: {}", dto);
            System.out.println("Validation errors:");
            for (ConstraintViolation<TruckUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Updating truck with ID {} using DTO: {}", id, dto);
        service.update(dto);
        System.out.println("Truck updated successfully.");
        logger.info("Truck with ID {} updated successfully", id);
    }

    private void deleteTruck() {
        Long id = getLongInput("Enter truck ID to delete: ");
        logger.info("Deleting truck with ID: {}", id);
        service.delete(id);
        System.out.println("Truck deleted.");
        logger.info("Truck with ID {} deleted successfully", id);
    }

    private void getTransportServicesForTruck() {
        Long id = getLongInput("Enter truck ID: ");
        logger.debug("Fetching transport services for truck ID: {}", id);
        List<? extends TransportServiceViewDTO> services = service.getTransportServicesForTruck(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            logger.info("No transport services found for truck ID: {}", id);
            System.out.println("No services found.");
        } else {
            logger.info("Found {} transport services for truck ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private void getActiveTransportServices() {
        Long id = getLongInput("Enter truck ID: ");
        logger.debug("Fetching active transport services for truck ID: {}", id);
        List<? extends TransportServiceViewDTO> services = service.getActiveTransportServices(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            logger.info("No active transport services found for truck ID: {}", id);
            System.out.println("No active services found.");
        } else {
            logger.info("Found {} active transport services for truck ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private void getTrucksByCompany() {
        Long id = getLongInput("Enter company ID: ");
        logger.debug("Fetching trucks for company ID: {}", id);
        List<TruckViewDTO> trucks = service.getTrucksByCompany(id, 0, Integer.MAX_VALUE);
        if (trucks.isEmpty()) {
            logger.info("No trucks found for company ID: {}", id);
            System.out.println("No trucks found.");
        } else {
            logger.info("Found {} trucks for company ID: {}", trucks.size(), id);
            trucks.forEach(System.out::println);
        }
    }

    private void getAllByTruckType() {
        Optional<TruckType> selectedTruckTypeOpt = selectEnum(TruckType.class, "Select truck type to filter by:");
        if (selectedTruckTypeOpt.isEmpty()) {
            logger.warn("No truck type selected for filtering");
            System.out.println("Truck type selection failed. Aborting.");
            return;
        }
        TruckType selectedTruckType = selectedTruckTypeOpt.get();
        logger.debug("Received truck type for filtering: {}", selectedTruckType);

        logger.debug("Fetching trucks by truck type: {}", selectedTruckType);
        List<TruckViewDTO> trucks = service.getAllByTruckType(selectedTruckType.name(), 0, Integer.MAX_VALUE, "registrationPlate", true);
        if (trucks.isEmpty()) {
            logger.info("No trucks found for truck type: {}", selectedTruckType);
            System.out.println("No trucks found.");
        } else {
            logger.info("Found {} trucks for truck type: {}", trucks.size(), selectedTruckType);
            trucks.forEach(System.out::println);
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

    private Long getLongInputWithDefault(String prompt, Long defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            logger.debug("Keeping default Long value: {}", defaultValue);
            return defaultValue;
        }
        try {
            Long value = Long.parseLong(input);
            logger.debug("Received new Long input: {}", value);
            return value;
        } catch (NumberFormatException e) {
            logger.warn("Invalid number input, reverting to default: {}", defaultValue);
            System.out.println("Invalid input. Keeping default value.");
            return defaultValue;
        }
    }
    private <T extends Enum<T>> Optional<T> selectEnum(Class<T> enumClass, String prompt, T currentValue) {
        T[] values = enumClass.getEnumConstants();
        System.out.println(prompt);
        logger.debug("Displaying enum selection for {} with current value: {}", enumClass.getSimpleName(), currentValue);
        if (currentValue != null) {
            System.out.println("0. Keep current value: " + currentValue.name());
        }

        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + ". " + values[i].name());
        }

        while (true) {
            System.out.print("Enter your choice (" + (currentValue != null ? "0 to keep current, " : "") + "1-" + values.length + "): ");
            String input = scanner.nextLine().trim();
            logger.debug("Received enum selection input: {}", input);
            if (currentValue != null && (input.isEmpty() || input.equals("0"))) {
                logger.debug("Keeping current enum value: {}", currentValue);
                return Optional.empty();
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice >= (currentValue != null ? 0 : 1) && choice <= values.length) {
                    if (choice == 0) {
                        logger.debug("Keeping current enum value: {}", currentValue);
                        return Optional.empty();
                    }
                    T selectedValue = values[choice - 1];
                    logger.debug("Selected new enum value: {}", selectedValue);
                    return Optional.of(selectedValue);
                } else {
                    logger.warn("Invalid enum choice: {}, expected range {} to {}", choice, (currentValue != null ? 0 : 1), values.length);
                    System.out.println("Invalid choice. Please enter a number between " + (currentValue != null ? "0" : "1") + " and " + values.length + ".");
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid enum selection input: {}", input);
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private <T extends Enum<T>> Optional<T> selectEnum(Class<T> enumClass, String prompt) {
        return selectEnum(enumClass, prompt, null);
    }
}