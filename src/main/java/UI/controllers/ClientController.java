package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import services.data.dto.clients.ClientCreateDTO;
import services.data.dto.clients.ClientUpdateDTO;
import services.data.dto.clients.ClientViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.services.contracts.IClientService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ClientController {
    private final IClientService service;
    private final Validator validator;
    private final Scanner scanner;

    public ClientController(IClientService service, Validator validator, Scanner scanner) {
        this.service = service;
        this.validator = validator;
        this.scanner = scanner;
    }

    public void handleMenu() {
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            if (choice == 0) {
                break;
            }
            processChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println(System.lineSeparator() + "--- Client Management ---" + System.lineSeparator());
        System.out.println("1. List all clients");
        System.out.println("2. Add a new client");
        System.out.println("3. Update a client");
        System.out.println("4. Delete a client");
        System.out.println("5. Get transport services by client");
        System.out.println("6. Get transport service counts per client");
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
                    listAllClients();
                    break;
                case 2:
                    addNewClient();
                    break;
                case 3:
                    updateClient();
                    break;
                case 4:
                    deleteClient();
                    break;
                case 5:
                    getTransportServicesByClient();
                    break;
                case 6:
                    getTransportServiceCountsPerClient();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void listAllClients() {
        List<ClientViewDTO> clients = service.getAll(0, Integer.MAX_VALUE, "name", true, "");
        if (clients.isEmpty()) {
            System.out.println("No clients found.");
        } else {
            clients.forEach(System.out::println);
        }
    }

    private void addNewClient() {
        System.out.print("Enter client name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter client telephone: ");
        String telephone = scanner.nextLine().trim();
        System.out.print("Enter client email: ");
        String email = scanner.nextLine().trim();

        ClientCreateDTO createDTO = new ClientCreateDTO();
        createDTO.setName(name);
        createDTO.setTelephone(telephone);
        createDTO.setEmail(email);

        Set<ConstraintViolation<ClientCreateDTO>> violations =
                validator.validate(createDTO);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<ClientCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }
        ClientViewDTO created = service.create(createDTO);
        System.out.println("Client created with ID: " + created.getId());
    }

    private void updateClient() {
        Long id = getLongInput("Enter client ID to update: ");
        ClientViewDTO client = service.getById(id);
        if (client == null) {
            System.out.println("Client not found.");
            return;
        }

        System.out.print("Enter new name (leave blank to keep '" + client.getName() + "'): ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter new telephone (leave blank to keep '" + client.getTelephone() + "'): ");
        String telephone = scanner.nextLine().trim();
        System.out.print("Enter new email (leave blank to keep '" + client.getEmail() + "'): ");
        String email = scanner.nextLine().trim();

        ClientUpdateDTO updateDTO = new ClientUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setName(client.getName());
        updateDTO.setTelephone(client.getTelephone());
        updateDTO.setEmail(client.getEmail());

        if (!name.isEmpty()){
            updateDTO.setName(name);
        }
        if (!telephone.isEmpty()){
            updateDTO.setTelephone(telephone);
        }
        if (!email.isEmpty()){
            updateDTO.setEmail(email);
        }

        Set<ConstraintViolation<ClientUpdateDTO>> violations =
                validator.validate(updateDTO);

        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<ClientUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        service.update(updateDTO);
        System.out.println("Client updated successfully.");
    }

    private void deleteClient() {
        Long id = getLongInput("Enter client ID to delete: ");
        service.delete(id);
        System.out.println("Client deleted.");
    }

    private void getTransportServicesByClient() {
        Long id = getLongInput("Enter client ID: ");
        List<TransportServiceViewDTO> services = service.getTransportServicesByClient(id);
        if (services.isEmpty()) {
            System.out.println("No services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getTransportServiceCountsPerClient() {
        Map<Long, Integer> counts = service.getTransportServiceCountsPerClient();
        counts.forEach((id, count) -> System.out.println("Client ID " + id + ": " + count + " services"));
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