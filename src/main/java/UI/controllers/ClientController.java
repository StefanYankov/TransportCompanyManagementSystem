package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final IClientService service;
    private final Validator validator;
    private final Scanner scanner;

    public ClientController(IClientService service, Validator validator, Scanner scanner) {
        this.service = service;
        this.validator = validator;
        this.scanner = scanner;
        logger.info("ClientController initialized with dependencies");
    }

    public void handleMenu() {
        logger.info("Entering ClientController menu");
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            logger.debug("User selected menu option: {}", choice);
            if (choice == 0) {
                logger.info("Exiting ClientController menu");
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
                    logger.info("Processing list all clients request");
                    listAllClients();
                    break;
                case 2:
                    logger.info("Processing add new client request");
                    addNewClient();
                    break;
                case 3:
                    logger.info("Processing update client request");
                    updateClient();
                    break;
                case 4:
                    logger.info("Processing delete client request");
                    deleteClient();
                    break;
                case 5:
                    logger.info("Processing get transport services by client request");
                    getTransportServicesByClient();
                    break;
                case 6:
                    logger.info("Processing get transport service counts per client request");
                    getTransportServiceCountsPerClient();
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

    private void listAllClients() {
        logger.debug("Fetching all clients");
        List<ClientViewDTO> clients = service.getAll(0, Integer.MAX_VALUE, "name", true, "");
        if (clients.isEmpty()) {
            logger.info("No clients found");
            System.out.println("No clients found.");
        } else {
            logger.info("Found {} clients", clients.size());
            clients.forEach(System.out::println);
        }
    }

    private void addNewClient() {
        System.out.print("Enter client name: ");
        String name = scanner.nextLine().trim();
        logger.debug("Received client name: {}", name);

        System.out.print("Enter client telephone: ");
        String telephone = scanner.nextLine().trim();
        logger.debug("Received client telephone: {}", telephone);

        System.out.print("Enter client email: ");
        String email = scanner.nextLine().trim();
        logger.debug("Received client email: {}", email);

        ClientCreateDTO createDTO = new ClientCreateDTO();
        createDTO.setName(name);
        createDTO.setTelephone(telephone);
        createDTO.setEmail(email);

        Set<ConstraintViolation<ClientCreateDTO>> violations = validator.validate(createDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for new client: {}", createDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<ClientCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Creating new client with DTO: {}", createDTO);
        ClientViewDTO created = service.create(createDTO);
        System.out.println("Client created with ID: " + created.getId());
        logger.info("Client created successfully with ID: {}", created.getId());
    }

    private void updateClient() {
        Long id = getLongInput("Enter client ID to update: ");
        logger.debug("Fetching client with ID: {}", id);
        ClientViewDTO client = service.getById(id);
        if (client == null) {
            logger.info("No client found with ID: {}", id);
            System.out.println("Client not found.");
            return;
        }

        System.out.print("Enter new name (leave blank to keep '" + client.getName() + "'): ");
        String name = scanner.nextLine().trim();
        logger.debug("Received new name: {}", name.isEmpty() ? "unchanged" : name);

        System.out.print("Enter new telephone (leave blank to keep '" + client.getTelephone() + "'): ");
        String telephone = scanner.nextLine().trim();
        logger.debug("Received new telephone: {}", telephone.isEmpty() ? "unchanged" : telephone);

        System.out.print("Enter new email (leave blank to keep '" + client.getEmail() + "'): ");
        String email = scanner.nextLine().trim();
        logger.debug("Received new email: {}", email.isEmpty() ? "unchanged" : email);

        ClientUpdateDTO updateDTO = new ClientUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setName(client.getName());
        updateDTO.setTelephone(client.getTelephone());
        updateDTO.setEmail(client.getEmail());

        if (!name.isEmpty()) {
            updateDTO.setName(name);
        }

        if (!telephone.isEmpty()) {
            updateDTO.setTelephone(telephone);
        }

        if (!email.isEmpty()) {
            updateDTO.setEmail(email);
        }

        Set<ConstraintViolation<ClientUpdateDTO>> violations = validator.validate(updateDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for updated client: {}", updateDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<ClientUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Updating client with ID {} using DTO: {}", id, updateDTO);
        service.update(updateDTO);
        System.out.println("Client updated successfully.");
        logger.info("Client with ID {} updated successfully", id);
    }

    private void deleteClient() {
        Long id = getLongInput("Enter client ID to delete: ");
        logger.info("Deleting client with ID: {}", id);
        service.delete(id);
        System.out.println("Client deleted.");
        logger.info("Client with ID {} deleted successfully", id);
    }

    private void getTransportServicesByClient() {
        Long id = getLongInput("Enter client ID: ");
        logger.debug("Fetching transport services for client ID: {}", id);
        List<TransportServiceViewDTO> services = service.getTransportServicesByClient(id);
        if (services.isEmpty()) {
            logger.info("No transport services found for client ID: {}", id);
            System.out.println("No services found.");
        } else {
            logger.info("Found {} transport services for client ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private void getTransportServiceCountsPerClient() {
        logger.debug("Fetching transport service counts per client");
        Map<Long, Integer> counts = service.getTransportServiceCountsPerClient();
        if (counts.isEmpty()) {
            logger.info("No transport service counts found");
            System.out.println("No service counts found.");
        } else {
            logger.info("Found transport service counts for {} clients", counts.size());
            counts.forEach((id, count) -> System.out.println("Client ID " + id + ": " + count + " services"));
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