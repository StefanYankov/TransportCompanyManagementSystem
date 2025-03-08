package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.dto.clients.ClientViewDTO;
import services.data.dto.companies.TransportCompanyCreateDTO;
import services.data.dto.companies.TransportCompanyUpdateDTO;
import services.data.dto.companies.TransportCompanyViewDTO;
import services.data.dto.employees.EmployeeViewDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;
import services.data.dto.vehicles.VehicleViewDTO;
import services.services.contracts.ITransportCompanyService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class TransportCompanyController {
    private static final Logger logger = LoggerFactory.getLogger(TransportCompanyController.class);

    private final ITransportCompanyService service;
    private final Validator validator;
    private final Scanner scanner;

    public TransportCompanyController(ITransportCompanyService service, Validator validator, Scanner scanner) {
        this.service = service;
        this.validator = validator;
        this.scanner = scanner;
        logger.info("{} initialized with dependencies", this.getClass().getSimpleName());

    }

    public void handleMenu() {
        logger.info("Entering {} menu", this.getClass().getSimpleName());
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            logger.debug("User selected menu option: {}", choice);
            if (choice == 0) {
                logger.info("Exiting {} menu", this.getClass().getSimpleName());
                break;
            }
            processChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println(System.lineSeparator() + "--- Transport Company Management ---" + System.lineSeparator());
        System.out.println("1. List all companies");
        System.out.println("2. Add a new company");
        System.out.println("3. Update a company");
        System.out.println("4. Delete a company");
        System.out.println("5. Get companies by revenue range");
        System.out.println("6. Get employees by company");
        System.out.println("7. Get vehicles by company");
        System.out.println("8. Get transport services by company");
        System.out.println("9. Get employee counts per company");
        System.out.println("10. Get total revenue");
        System.out.println("11. Get total transport count");
        System.out.println("12. Get all clients for company");
        System.out.println("0. Back to main menu");
        System.out.print("Enter your choice: ");
        logger.debug("Displayed Transport Company Management menu");
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
                    logger.info("Processing list all companies request");
                    listAllCompanies();
                    break;
                case 2:
                    logger.info("Processing add new company request");
                    addNewCompany();
                    break;
                case 3:
                    logger.info("Processing update company request");
                    updateCompany();
                    break;
                case 4:
                    logger.info("Processing delete company request");
                    deleteCompany();
                    break;
                case 5:
                    logger.info("Processing get companies by revenue range request");
                    getCompaniesByRevenueRange();
                    break;
                case 6:
                    logger.info("Processing get employees by company request");
                    getEmployeesByCompany();
                    break;
                case 7:
                    logger.info("Processing get vehicles by company request");
                    getVehiclesByCompany();
                    break;
                case 8:
                    logger.info("Processing get transport services by company request");
                    getTransportServicesByCompany();
                    break;
                case 9:
                    logger.info("Processing get employee counts per company request");
                    getEmployeeCountsPerCompany();
                    break;
                case 10:
                    logger.info("Processing get total revenue request");
                    getTotalRevenue();
                    break;
                case 11:
                    logger.info("Processing get total transport count request");
                    getTotalTransportCount();
                    break;
                case 12:
                    logger.info("Processing get all clients for company request");
                    getAllClientsForCompany();
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

    private void listAllCompanies() {
        logger.debug("Fetching all companies");
        List<TransportCompanyViewDTO> companies = service.getAll(0, Integer.MAX_VALUE, "name", true, "");
        if (companies.isEmpty()) {
            logger.info("No companies found");
            System.out.println("No companies found.");
        } else {
            logger.info("Found {} companies", companies.size());
            companies.forEach(System.out::println);
        }
    }

    private void addNewCompany() {
        System.out.print("Enter company name: ");
        String name = scanner.nextLine().trim();
        logger.debug("Received company name: {}", name);

        System.out.print("Enter address: ");
        String address = scanner.nextLine().trim();
        logger.debug("Received address: {}", address);

        TransportCompanyCreateDTO createDTO = new TransportCompanyCreateDTO();
        createDTO.setName(name);
        createDTO.setAddress(address);

        Set<ConstraintViolation<TransportCompanyCreateDTO>> violations = validator.validate(createDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for new company: {}", createDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<TransportCompanyCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Creating new company with DTO: {}", createDTO);
        TransportCompanyViewDTO created = service.create(createDTO);
        System.out.println("Transport company created with ID: " + created.getId());
        logger.info("Transport company created successfully with ID: {}", created.getId());
    }

    private void updateCompany() {
        Long id = getLongInput("Enter company ID to update: ");
        logger.debug("Fetching company with ID: {}", id);
        TransportCompanyViewDTO company = service.getById(id);
        if (company == null) {
            logger.info("No company found with ID: {}", id);
            System.out.println("Company not found.");
            return;
        }

        System.out.print("Enter new name (leave blank to keep '" + company.getName() + "'): ");
        String name = scanner.nextLine().trim();
        logger.debug("Received new name: {}", name.isEmpty() ? "unchanged" : name);

        System.out.print("Enter new address (leave blank to keep '" + company.getAddress() + "'): ");
        String address = scanner.nextLine().trim();
        logger.debug("Received new address: {}", address.isEmpty() ? "unchanged" : address);

        TransportCompanyUpdateDTO updateDTO = new TransportCompanyUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setName(company.getName());
        updateDTO.setAddress(company.getAddress());

        if (!name.isEmpty()) {
            updateDTO.setName(name);
        }

        if (!address.isEmpty()) {
            updateDTO.setAddress(address);
        }

        Set<ConstraintViolation<TransportCompanyUpdateDTO>> violations = validator.validate(updateDTO);
        if (!violations.isEmpty()) {
            logger.warn("Validation failed for updated company: {}", updateDTO);
            System.out.println("Validation errors:");
            for (ConstraintViolation<TransportCompanyUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        logger.info("Updating company with ID {} using DTO: {}", id, updateDTO);
        service.update(updateDTO);
        System.out.println("Company updated successfully.");
        logger.info("Company with ID {} updated successfully", id);
    }

    private void deleteCompany() {
        Long id = getLongInput("Enter company ID to delete: ");
        logger.info("Deleting company with ID: {}", id);
        service.delete(id);
        System.out.println("Company deleted.");
        logger.info("Company with ID {} deleted successfully", id);
    }

    private void getCompaniesByRevenueRange() {
        System.out.print("Enter minimum revenue: ");
        BigDecimal min;
        try {
            min = new BigDecimal(scanner.nextLine().trim());
            logger.debug("Received minimum revenue: {}", min);
        } catch (NumberFormatException e) {
            logger.warn("Invalid minimum revenue input: {}", e.getMessage());
            System.out.println("Invalid minimum revenue. Aborting.");
            return;
        }

        System.out.print("Enter maximum revenue: ");
        BigDecimal max;
        try {
            max = new BigDecimal(scanner.nextLine().trim());
            logger.debug("Received maximum revenue: {}", max);
        } catch (NumberFormatException e) {
            logger.warn("Invalid maximum revenue input: {}", e.getMessage());
            System.out.println("Invalid maximum revenue. Aborting.");
            return;
        }

        logger.debug("Fetching companies by revenue range: {} to {}", min, max);
        List<TransportCompanyViewDTO> companies = service.getCompaniesBetweenRevenue(min, max);
        if (companies.isEmpty()) {
            logger.info("No companies found in revenue range {} to {}", min, max);
            System.out.println("No companies found in this revenue range.");
        } else {
            logger.info("Found {} companies in revenue range {} to {}", companies.size(), min, max);
            companies.forEach(System.out::println);
        }
    }

    private void getEmployeesByCompany() {
        Long id = getLongInput("Enter company ID: ");
        logger.debug("Fetching employees for company ID: {}", id);
        List<EmployeeViewDTO> employees = service.getEmployeesByCompany(id);
        if (employees.isEmpty()) {
            logger.info("No employees found for company ID: {}", id);
            System.out.println("No employees found.");
        } else {
            logger.info("Found {} employees for company ID: {}", employees.size(), id);
            employees.forEach(System.out::println);
        }
    }

    private void getVehiclesByCompany() {
        Long id = getLongInput("Enter company ID: ");
        logger.debug("Fetching vehicles for company ID: {}", id);
        List<VehicleViewDTO> vehicles = service.getVehiclesByCompany(id);
        if (vehicles.isEmpty()) {
            logger.info("No vehicles found for company ID: {}", id);
            System.out.println("No vehicles found.");
        } else {
            logger.info("Found {} vehicles for company ID: {}", vehicles.size(), id);
            vehicles.forEach(System.out::println);
        }
    }

    private void getTransportServicesByCompany() {
        Long id = getLongInput("Enter company ID: ");
        logger.debug("Fetching transport services for company ID: {}", id);
        List<TransportServiceViewDTO> services = service.getTransportServicesByCompany(id);
        if (services.isEmpty()) {
            logger.info("No services found for company ID: {}", id);
            System.out.println("No services found.");
        } else {
            logger.info("Found {} services for company ID: {}", services.size(), id);
            services.forEach(System.out::println);
        }
    }

    private void getEmployeeCountsPerCompany() {
        logger.debug("Fetching employee counts per company");
        Map<Long, Integer> counts = service.getEmployeeCountsPerCompany();
        if (counts.isEmpty()) {
            logger.info("No employee counts found");
            System.out.println("No employee counts found.");
        } else {
            logger.info("Found employee counts for {} companies", counts.size());
            counts.forEach((id, count) -> System.out.println("Company ID " + id + ": " + count + " employees"));
        }
    }

    private void getTotalRevenue() {
        Long id = getLongInput("Enter company ID: ");
        logger.debug("Fetching total revenue for company ID: {}", id);
        BigDecimal revenue = service.getTotalRevenue(id);
        System.out.println("Total revenue: $" + revenue);
        logger.info("Retrieved total revenue ${} for company ID: {}", revenue, id);
    }

    private void getTotalTransportCount() {
        Long id = getLongInput("Enter company ID: ");
        logger.debug("Fetching total transport count for company ID: {}", id);
        int count = service.getTotalTransportCount(id);
        System.out.println("Total transport count: " + count);
        logger.info("Retrieved total transport count {} for company ID: {}", count, id);
    }

    private void getAllClientsForCompany() {
        Long id = getLongInput("Enter company ID: ");
        logger.debug("Received company ID: {}", id);

        System.out.print("Show only paid clients? (yes/no): ");
        String paidInput = scanner.nextLine().trim();
        boolean paid = paidInput.equalsIgnoreCase("yes");
        logger.debug("Received paid clients filter: {}", paid);

        logger.debug("Fetching clients for company ID: {}", id);
        List<ClientViewDTO> clients = service.getAllClientsForCompany(id, paid);
        if (clients.isEmpty()) {
            logger.info("No clients found for company ID: {} (paid filter: {})", id, paid);
            System.out.println("No clients found.");
        } else {
            logger.info("Found {} clients for company ID: {} (paid filter: {})", clients.size(), id, paid);
            clients.forEach(System.out::println);
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