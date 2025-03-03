package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;
import services.data.dto.vehicles.VanCreateDTO;
import services.data.dto.vehicles.VanUpdateDTO;
import services.data.dto.vehicles.VanViewDTO;
import services.services.contracts.IVanService;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class VanController {
    private final IVanService service;
    private final Validator validator;
    private final Scanner scanner;

    public VanController(IVanService service, Validator validator, Scanner scanner) {
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
        System.out.println(System.lineSeparator() + "--- Van Management ---" + System.lineSeparator());
        System.out.println("1. List all vans");
        System.out.println("2. Add a new van");
        System.out.println("3. Update a van");
        System.out.println("4. Delete a van");
        System.out.println("5. Get transport services for van");
        System.out.println("6. Get active transport services");
        System.out.println("7. Get vans by company");
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
                case 1: listAllVans(); break;
                case 2: addNewVan(); break;
                case 3: updateVan(); break;
                case 4: deleteVan(); break;
                case 5: getTransportServicesForVan(); break;
                case 6: getActiveTransportServices(); break;
                case 7: getVansByCompany(); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void listAllVans() {
        List<VanViewDTO> vans = service.getAll(0, Integer.MAX_VALUE, "registrationPlate", true);
        if (vans.isEmpty()) {
            System.out.println("No vans found.");
        } else {
            vans.forEach(System.out::println);
        }
    }

    private void addNewVan() {
        System.out.print("Enter registration plate: ");
        String regPlate = scanner.nextLine().trim();

        System.out.print("Enter transport company ID: ");
        Long companyId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter max passenger capacity: ");
        Integer maxCapacity = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Has passenger overhead storage (true/false): ");
        boolean hasOverhead = Boolean.parseBoolean(scanner.nextLine().trim());

        VanCreateDTO createDTO = new VanCreateDTO();
        createDTO.setRegistrationPlate(regPlate);
        createDTO.setTransportCompanyId(companyId);
        createDTO.setMaxPassengerCapacity(maxCapacity);
        createDTO.setHasPassengerOverheadStorage(hasOverhead);

        Set<ConstraintViolation<VanCreateDTO>> violations =
                validator.validate(createDTO);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<VanCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        VanViewDTO created = service.create(createDTO);
        System.out.println("Van created with ID: " + created.getId());
    }

    private void updateVan() {
        Long id = getLongInput("Enter van ID to update: ");
        VanViewDTO van = service.getById(id);
        if (van == null) {
            System.out.println("Van not found.");
            return;
        }

        System.out.print("Enter new registration plate (leave blank to keep '" + van.getRegistrationPlate() + "'): ");
        String regPlate = scanner.nextLine().trim();

        System.out.print("Enter new transport company ID (leave blank to keep " + van.getTransportCompanyId() + "): ");
        Long companyId = getLongInput("Enter new transport company ID (leave blank to keep " + van.getTransportCompanyId() + "): ");

        System.out.print("Enter new max passenger capacity (leave blank to keep " + van.getMaxPassengerCapacity() + "): ");
        String maxCapacityInput = scanner.nextLine().trim();

        System.out.print("Has passenger overhead storage (true/false, leave blank to keep " + van.getHasPassengerOverheadStorage() + "): ");
        String hasOverheadInput = scanner.nextLine().trim();

        VanUpdateDTO updateDTO = new VanUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setRegistrationPlate(van.getRegistrationPlate());
        updateDTO.setTransportCompanyId(van.getTransportCompanyId());
        updateDTO.setMaxPassengerCapacity(van.getMaxPassengerCapacity());
        updateDTO.setHasPassengerOverheadStorage(van.getHasPassengerOverheadStorage());

        if (!regPlate.isEmpty()) {
            updateDTO.setRegistrationPlate(regPlate);
        }

        if (companyId != null) {
            updateDTO.setTransportCompanyId(companyId);
        }

        if (!maxCapacityInput.isEmpty()) {
            updateDTO.setMaxPassengerCapacity(Integer.parseInt(maxCapacityInput));
        }

        if (!hasOverheadInput.isEmpty()) {
            updateDTO.setHasPassengerOverheadStorage(Boolean.parseBoolean(hasOverheadInput));
        }

        Set<ConstraintViolation<VanUpdateDTO>> violations =
                validator.validate(updateDTO);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<VanUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        service.update(updateDTO);
        System.out.println("Van updated successfully.");
    }

    private void deleteVan() {
        Long id = getLongInput("Enter van ID to delete: ");
        service.delete(id);
        System.out.println("Van deleted.");
    }

    private void getTransportServicesForVan() {
        Long id = getLongInput("Enter van ID: ");
        List<TransportPassengersServiceViewDTO> services = service.getTransportServicesForVan(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            System.out.println("No services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getActiveTransportServices() {
        Long id = getLongInput("Enter van ID: ");
        List<TransportPassengersServiceViewDTO> services = service.getActiveTransportServices(id, 0, Integer.MAX_VALUE);
        if (services.isEmpty()) {
            System.out.println("No active services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getVansByCompany() {
        Long id = getLongInput("Enter company ID: ");
        List<VanViewDTO> vans = service.getVansByCompany(id, 0, Integer.MAX_VALUE);
        if (vans.isEmpty()) {
            System.out.println("No vans found.");
        } else {
            vans.forEach(System.out::println);
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