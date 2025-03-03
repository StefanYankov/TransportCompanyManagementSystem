package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
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
    private final ITransportCompanyService service;
    private final Validator validator;
    private final Scanner scanner;

    public TransportCompanyController(ITransportCompanyService service, Validator validator, Scanner scanner) {
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
                    listAllCompanies();
                    break;
                case 2:
                    addNewCompany();
                    break;
                case 3:
                    updateCompany();
                    break;
                case 4:
                    deleteCompany();
                    break;
                case 5:
                    getCompaniesByRevenueRange();
                    break;
                case 6:
                    getEmployeesByCompany();
                    break;
                case 7:
                    getVehiclesByCompany();
                    break;
                case 8:
                    getTransportServicesByCompany();
                    break;
                case 9:
                    getEmployeeCountsPerCompany();
                    break;
                case 10:
                    getTotalRevenue();
                    break;
                case 11:
                    getTotalTransportCount();
                    break;
                case 12:
                    getAllClientsForCompany();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void listAllCompanies() {
        List<TransportCompanyViewDTO> companies = service.getAll(0, Integer.MAX_VALUE, "name", true, "");
        if (companies.isEmpty()) {
            System.out.println("No companies found.");
        } else {
            companies.forEach(System.out::println);
        }
    }

    private void addNewCompany() {
        System.out.print("Enter company name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter address: ");
        String address = scanner.nextLine().trim();

        TransportCompanyCreateDTO createDTO = new TransportCompanyCreateDTO();
        createDTO.setName(name);
        createDTO.setAddress(address);

        Set<ConstraintViolation<TransportCompanyCreateDTO>> violations =
                validator.validate(createDTO);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<TransportCompanyCreateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }
        TransportCompanyViewDTO created = service.create(createDTO);
        System.out.println("Transport company created with ID: " + created.getId());
    }

    private void updateCompany() {
        Long id = getLongInput("Enter company ID to update: ");
        TransportCompanyViewDTO company = service.getById(id);
        if (company == null) {
            System.out.println("Company not found.");
            return;
        }
        System.out.print("Enter new name (leave blank to keep '" + company.getName() + "'): ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter new address (leave blank to keep '" + company.getAddress() + "'): ");
        String address = scanner.nextLine().trim();

        TransportCompanyUpdateDTO updateDTO = new TransportCompanyUpdateDTO();
        updateDTO.setId(id);
        updateDTO.setName(company.getName());
        updateDTO.setAddress(company.getAddress());

        if (!name.isEmpty()){
            updateDTO.setName(name);
        }
        if (!address.isEmpty()){
            updateDTO.setAddress(address);
        }

        Set<ConstraintViolation<TransportCompanyUpdateDTO>> violations =
                validator.validate(updateDTO);
        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            for (ConstraintViolation<TransportCompanyUpdateDTO> violation : violations) {
                System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return;
        }

        service.update(updateDTO);
        System.out.println("Company updated successfully.");
    }

    private void deleteCompany() {
        Long id = getLongInput("Enter company ID to delete: ");
        service.delete(id);
        System.out.println("Company deleted.");
    }

    private void getCompaniesByRevenueRange() {
        System.out.print("Enter minimum revenue: ");
        BigDecimal min = new BigDecimal(scanner.nextLine().trim());
        System.out.print("Enter maximum revenue: ");
        BigDecimal max = new BigDecimal(scanner.nextLine().trim());
        List<TransportCompanyViewDTO> companies = service.getCompaniesBetweenRevenue(min, max);
        if (companies.isEmpty()) {
            System.out.println("No companies found in this revenue range.");
        } else {
            companies.forEach(System.out::println);
        }
    }

    private void getEmployeesByCompany() {
        Long id = getLongInput("Enter company ID: ");
        List<EmployeeViewDTO> employees = service.getEmployeesByCompany(id);
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
        } else {
            employees.forEach(System.out::println);
        }
    }

    private void getVehiclesByCompany() {
        Long id = getLongInput("Enter company ID: ");
        List<VehicleViewDTO> vehicles = service.getVehiclesByCompany(id);
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
        } else {
            vehicles.forEach(System.out::println);
        }
    }

    private void getTransportServicesByCompany() {
        Long id = getLongInput("Enter company ID: ");
        List<TransportServiceViewDTO> services = service.getTransportServicesByCompany(id);
        if (services.isEmpty()) {
            System.out.println("No services found.");
        } else {
            services.forEach(System.out::println);
        }
    }

    private void getEmployeeCountsPerCompany() {
        Map<Long, Integer> counts = service.getEmployeeCountsPerCompany();
        counts.forEach((id, count) -> System.out.println("Company ID " + id + ": " + count + " employees"));
    }

    private void getTotalRevenue() {
        Long id = getLongInput("Enter company ID: ");
        BigDecimal revenue = service.getTotalRevenue(id);
        System.out.println("Total revenue: $" + revenue);
    }

    private void getTotalTransportCount() {
        Long id = getLongInput("Enter company ID: ");
        int count = service.getTotalTransportCount(id);
        System.out.println("Total transport count: " + count);
    }

    private void getAllClientsForCompany() {
        Long id = getLongInput("Enter company ID: ");
        System.out.print("Show only paid clients? (yes/no): ");
        boolean paid = scanner.nextLine().trim().equalsIgnoreCase("yes");
        List<ClientViewDTO> clients = service.getAllClientsForCompany(id, paid);
        if (clients.isEmpty()) {
            System.out.println("No clients found.");
        } else {
            clients.forEach(System.out::println);
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