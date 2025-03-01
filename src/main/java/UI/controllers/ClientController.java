//package UI.controllers;
//
//import jakarta.validation.Validator;
//import services.data.dto.clients.ClientCreateDTO;
//import services.data.dto.clients.ClientUpdateDTO;
//import services.data.dto.clients.ClientViewDTO;
//import services.data.dto.transportservices.TransportServiceViewDTO;
//import services.services.contracts.IClientService;
//
//import java.util.List;
//import java.util.Scanner;
//
//public class ClientController {
//
//    private final Validator validator;
//    private final IClientService clientService;
//    private final Scanner scanner;
//
//    public ClientController(Validator validator, IClientService clientService) {
//        this.clientService = clientService;
//        this.validator = validator;
//        this.scanner = new Scanner(System.in);
//    }
//
//    public void start() {
//        while (true) {
//            displayMenu();
//            String choice = scanner.nextLine().trim();
//            try {
//                switch (choice) {
//                    case "1":
//                        createClient();
//                        break;
//                    case "2":
//                        updateClient();
//                        break;
//                    case "3":
//                        deleteClient();
//                        break;
//                    case "4":
//                        viewClientById();
//                        break;
//                    case "5":
//                        listAllClients();
//                        break;
//                    case "6":
//                        listAllTransportServices();
//                        break;
//                    case "7":
//                        listUnpaidTransportServices();
//                        break;
//                    case "8":
//                        listDeliveredTransportServices();
//                        break;
//                    case "9":
//                        listPendingTransportServices();
//                        break;
//                    case "0":
//                        System.out.println("Exiting...");
//                        return;
//                    default:
//                        System.out.println("Invalid choice. Please enter a number between 0 and 9.");
//                }
//            } catch (Exception e) {
//                System.out.println("Error: " + e.getMessage());
//            }
//            System.out.println("\nPress Enter to continue...");
//            scanner.nextLine();
//        }
//    }
//
//    private void displayMenu() {
//        System.out.println("=== Client Management Console ===");
//        System.out.println("1. Create Client");
//        System.out.println("2. Update Client");
//        System.out.println("3. Delete Client");
//        System.out.println("4. View Client by ID");
//        System.out.println("5. List All Clients");
//        System.out.println("6. List All Transport Services for a Client");
//        System.out.println("7. List Unpaid Transport Services for a Client");
//        System.out.println("8. List Delivered Transport Services for a Client");
//        System.out.println("9. List Pending Transport Services for a Client");
//        System.out.println("0. Exit");
//        System.out.print("Enter your choice: ");
//    }
//
//    private void createClient() {
//        System.out.print("Enter client name: ");
//        String name = scanner.nextLine();
//        System.out.print("Enter client telephone: ");
//        String telephone = scanner.nextLine();
//        System.out.print("Enter client email: ");
//        String email = scanner.nextLine();
//
//        ClientCreateDTO dto = new ClientCreateDTO(name, telephone, email);
//        ClientViewDTO created = clientService.create(dto);
//        System.out.println("Client created with ID: " + created.getId());
//    }
//
//    private void updateClient() {
//        System.out.print("Enter client ID to update: ");
//        Long id = Long.parseLong(scanner.nextLine());
//        System.out.print("Enter new client name: ");
//        String name = scanner.nextLine();
//        System.out.print("Enter new client telephone: ");
//        String telephone = scanner.nextLine();
//        System.out.print("Enter new client email: ");
//        String email = scanner.nextLine();
//
//        ClientUpdateDTO dto = new ClientUpdateDTO(name, telephone, email);
//        dto.setId(id);
//        ClientViewDTO updated = clientService.update(dto);
//        System.out.println("Client updated: " + updated.getName() + " (ID: " + updated.getId() + ")");
//    }
//
//    private void deleteClient() {
//        System.out.print("Enter client ID to delete: ");
//        Long id = Long.parseLong(scanner.nextLine());
//        clientService.delete(id);
//        System.out.println("Client with ID " + id + " deleted (if it existed).");
//    }
//
//    private void viewClientById() {
//        System.out.print("Enter client ID to view: ");
//        Long id = Long.parseLong(scanner.nextLine());
//        ClientViewDTO client = clientService.getById(id);
//        if (client != null) {
//            displayClient(client);
//        } else {
//            System.out.println("No client found with ID: " + id);
//        }
//    }
//
//    private void listAllClients() {
//        System.out.print("Enter page number (0-based): ");
//        int page = Integer.parseInt(scanner.nextLine());
//        System.out.print("Enter page size: ");
//        int size = Integer.parseInt(scanner.nextLine());
//        List<ClientViewDTO> clients = clientService.getAll(page, size, "name", true);
//        if (clients.isEmpty()) {
//            System.out.println("No clients found.");
//        } else {
//            System.out.println("Clients (Page " + page + ", Size " + size + "):");
//            clients.forEach(this::displayClient);
//        }
//    }
//
//    private void listAllTransportServices() {
//        System.out.print("Enter client ID: ");
//        Long clientId = Long.parseLong(scanner.nextLine());
//        System.out.print("Enter page number (0-based): ");
//        int page = Integer.parseInt(scanner.nextLine());
//        System.out.print("Enter page size: ");
//        int size = Integer.parseInt(scanner.nextLine());
//        List<? extends TransportServiceViewDTO> services = clientService.getAllTransportServices(clientId, page, size);
//        displayTransportServices(services, "All Transport Services");
//    }
//
//    private void listUnpaidTransportServices() {
//        System.out.print("Enter client ID: ");
//        Long clientId = Long.parseLong(scanner.nextLine());
//        System.out.print("Enter page number (0-based): ");
//        int page = Integer.parseInt(scanner.nextLine());
//        System.out.print("Enter page size: ");
//        int size = Integer.parseInt(scanner.nextLine());
//        List<? extends TransportServiceViewDTO> services = clientService.getUnpaidTransportServices(clientId, page, size);
//        displayTransportServices(services, "Unpaid Transport Services");
//    }
//
//    private void listDeliveredTransportServices() {
//        System.out.print("Enter client ID: ");
//        Long clientId = Long.parseLong(scanner.nextLine());
//        System.out.print("Enter page number (0-based): ");
//        int page = Integer.parseInt(scanner.nextLine());
//        System.out.print("Enter page size: ");
//        int size = Integer.parseInt(scanner.nextLine());
//        List<? extends TransportServiceViewDTO> services = clientService.getDeliveredTransportServices(clientId, page, size);
//        displayTransportServices(services, "Delivered Transport Services");
//    }
//
//    private void listPendingTransportServices() {
//        System.out.print("Enter client ID: ");
//        Long clientId = Long.parseLong(scanner.nextLine());
//        System.out.print("Enter page number (0-based): ");
//        int page = Integer.parseInt(scanner.nextLine());
//        System.out.print("Enter page size: ");
//        int size = Integer.parseInt(scanner.nextLine());
//        List<? extends TransportServiceViewDTO> services = clientService.getPendingTransportServices(clientId, page, size);
//        displayTransportServices(services, "Pending Transport Services");
//    }
//
//    private void displayClient(ClientViewDTO client) {
//        System.out.printf("ID: %d, Name: %s, Telephone: %s, Email: %s%n",
//                client.getId(), client.getName(), client.getTelephone(), client.getEmail());
//    }
//
//    private void displayTransportServices(List<? extends TransportServiceViewDTO> services, String title) {
//        if (services.isEmpty()) {
//            System.out.println("No " + title.toLowerCase() + " found.");
//        } else {
//            System.out.println(title + ":");
//            for (TransportServiceViewDTO service : services) {
//                System.out.printf("ID: %d, Price: %s, Paid: %b, Delivered: %b, Client ID: %d%n",
//                        service.getId(), service.getPrice(), service.isPaid(), service.isDelivered(), service.getClientId());
//            }
//        }
//    }
//}
