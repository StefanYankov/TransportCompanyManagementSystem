package UI.controllers;

import data.repositories.exceptions.RepositoryException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import services.data.dto.companies.TransportCompanyViewDTO;
import services.data.dto.employees.DriverCreateDTO;
import services.data.dto.employees.DriverUpdateDTO;
import services.data.dto.employees.DriverViewDTO;
import services.data.dto.employees.QualificationViewDTO;
import services.services.contracts.IDriverService;
import services.services.contracts.IQualificationService;
import services.services.contracts.ITransportCompanyService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class DriverController {

    private final Validator validator;
    private final IDriverService driverService;
    private final IQualificationService qualificationService;
    private final ITransportCompanyService transportCompanyService;

    public DriverController(Validator validator, IDriverService driverService, IQualificationService qualificationService,
                            ITransportCompanyService transportCompanyService) {
        this.validator = validator;
        this.driverService = driverService;
        this.qualificationService = qualificationService;
        this.transportCompanyService = transportCompanyService;
    }

    public void run() {

//        // ## Create Driver asynchronously
//        DriverCreateDTO createDto = new DriverCreateDTO();
//        createDto.setFirstName("John");
//        createDto.setFamilyName("Doe");
//        createDto.setSalary(new BigDecimal("50000"));
//        TransportCompanyViewDTO company = transportCompanyService.getById(1L); // Use TransportCompanyService
//        createDto.setTransportCompany(company);
//        Set<QualificationViewDTO> qualifications = new HashSet<>();
//        qualifications.add(qualificationService.getById(1L)); // Use QualificationService
//        qualifications.add(qualificationService.getById(2L));
//        createDto.setQualifications(qualifications);
//
//        Set<ConstraintViolation<DriverCreateDTO>> createViolations = validator.validate(createDto);
//        if (!createViolations.isEmpty()) {
//            System.out.println("Validation errors occurred during create:");
//            for (ConstraintViolation<DriverCreateDTO> violation : createViolations) {
//                System.out.println("Field: " + violation.getPropertyPath() + " - " + violation.getMessage());
//            }
//        } else {
//            try {
//                DriverViewDTO driver = driverService.createAsync(createDto).get();
//                Long driverId = driver.getId();
//                System.out.println("Created driver: " + driver.getFirstName() + " " + driver.getFamilyName() + " (ID: " + driverId + ")");
//
//                // 2. Update the driver asynchronously
//                DriverUpdateDTO updateDto = new DriverUpdateDTO();
//                updateDto.setId(driverId);
//                updateDto.setFirstName("Johnny");
//                updateDto.setFamilyName("Doe");
//                updateDto.setSalary(new BigDecimal("55000"));
//                updateDto.setTransportCompanyId(company.getId());
//                updateDto.setQualificationIds(Set.of(1L, 3L));
//
//                Set<ConstraintViolation<DriverUpdateDTO>> updateViolations = validator.validate(updateDto);
//                if (!updateViolations.isEmpty()) {
//                    System.out.println("Validation errors occurred during update:");
//                    for (ConstraintViolation<DriverUpdateDTO> violation : updateViolations) {
//                        System.out.println("Field: " + violation.getPropertyPath() + " - " + violation.getMessage());
//                    }
//                } else {
//                    DriverViewDTO updatedDriver = driverService.updateAsync(updateDto).get();
//                    System.out.println("Updated driver: " + updatedDriver.getFirstName() + " " + updatedDriver.getFamilyName() + " (ID: " + updatedDriver.getId() + ")");
//                }
//
//                // 3. Get by ID (valid)
//                DriverViewDTO fetchedDriver = driverService.getById(driverId);
//                if (fetchedDriver != null) {
//                    System.out.println("Fetched driver: " + fetchedDriver.getFirstName() + " " + fetchedDriver.getFamilyName() + " (ID: " + fetchedDriver.getId() + ")");
//                } else {
//                    System.out.println("Driver with ID " + driverId + " not found.");
//                }
//
//                // 4. Get by ID (invalid) with try-catch
//                try {
//                    DriverViewDTO invalidDriver = driverService.getById(999L);
//                    System.out.println("Unexpectedly fetched driver with invalid ID: " + invalidDriver.getId());
//                } catch (RepositoryException e) {
//                    System.out.println("Expected error for invalid ID 999: " + e.getMessage());
//                }
//
//                // 5. Get all drivers
//                    // creating a second driver for the
//                DriverCreateDTO driverDto2 = new DriverCreateDTO();
//                driverDto2.setFirstName("Dala");
//                driverDto2.setFamilyName("Vera");
//                driverDto2.setSalary(new BigDecimal("2500"));
//                driverDto2.setTransportCompany(company); // use same company as in #1
//                driverDto2.setQualifications(qualifications);// Use same qualifications as in #1
//
//                Set<ConstraintViolation<DriverCreateDTO>> createViolations2 = validator.validate(driverDto2);
//                if (!createViolations.isEmpty()) {
//                    System.out.println("Validation errors occurred during create:");
//                    for (ConstraintViolation<DriverCreateDTO> violation : createViolations2) {
//                        System.out.println("Field: " + violation.getPropertyPath() + " - " + violation.getMessage());
//                    }
//                } else {
//                    DriverViewDTO driver2 = driverService.createAsync(createDto).get();
//                    System.out.println("Created driver: " + driver.getFirstName() + " " + driver.getFamilyName() + " (ID: " + driver2.getId() + ")");
//                }
//
//                List<DriverViewDTO> getAllDrivers = driverService.getAll(0, 10, "familyName", true);
//                System.out.println("All drivers:");
//                getAllDrivers.forEach(d -> System.out.println(d.getFirstName() + " " + d.getFamilyName() + " (ID: " + d.getId() + ")"));
//
//                // 6. Delete the driver and verify
//                driverService.delete(driverId);
//                System.out.println("Deleted driver with ID: " + driverId);
//
//                try {
//                    DriverViewDTO deletedDriver = driverService.getById(driverId);
//                    System.out.println("Unexpectedly fetched deleted driver: " + deletedDriver.getId());
//                } catch (RepositoryException e) {
//                    System.out.println("Confirmed deletion - driver with ID " + driverId + " not found: " + e.getMessage());
//                }

        // 7. Display total count cargo services by driver
//                    TransportCargoService cargo1 = new TransportCargoService();
//                    cargo1.setTransportCompany(company);
//                    cargo1.setPrice(new BigDecimal("1000"));
//                    cargo1.setStartingDate(LocalDate.now());
//                    cargo1.setWeightInKilograms(BigDecimal.valueOf(25));
//                    cargo1.setLengthInCentimeters(50);
//                    cargo1.setWidthInCentimeters(25);
//                    cargo1.setHeightInCentimeters(25);
//                    cargo1.setDriver(driver1);
//                    cargoRepo.create(cargo1);
//
//                    TransportCargoService cargo2 = new TransportCargoService();
//                    cargo2.setTransportCompany(company);
//                    cargo2.setPrice(new BigDecimal("2000"));
//                    cargo2.setStartingDate(LocalDate.now());
//                    cargo2.setWeightInKilograms(BigDecimal.valueOf(25));
//                    cargo2.setLengthInCentimeters(50);
//                    cargo2.setWidthInCentimeters(25);
//                    cargo2.setHeightInCentimeters(25);
//                    cargo2.setDriver(driver1);
//                    cargoRepo.create(cargo2);
//
//                    TransportCargoService cargo3 = new TransportCargoService();
//                    cargo3.setTransportCompany(company);
//                    cargo3.setPrice(new BigDecimal("1500"));
//                    cargo3.setStartingDate(LocalDate.now());
//                    cargo3.setWeightInKilograms(BigDecimal.valueOf(25));
//                    cargo3.setLengthInCentimeters(50);
//                    cargo3.setWidthInCentimeters(25);
//                    cargo3.setHeightInCentimeters(25);
//                    cargo3.setDriver(driver2);
//                    cargoRepo.create(cargo3);

//                    // Get trip counts with pagination (page 0, size 2, sorted by count descending)
//                    Map<Long, Integer> tripCounts = driverService.getDriverTripCounts(true, false, 0, 2);
//                    // Display results
//                    System.out.println("Driver Trip Counts (most trips first, page 0, size 2):");
//                    // TODO: for loop to display
//
//            } catch (InterruptedException | ExecutionException e) {
//                System.err.println("Error during async operation: " + e.getMessage());
//            } catch (RepositoryException e) {
//                System.err.println("Repository error: " + e.getMessage());
//            }
//    }
    }
}
