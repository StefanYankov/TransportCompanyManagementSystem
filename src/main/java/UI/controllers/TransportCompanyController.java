package UI.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import services.data.dto.companies.TransportCompanyCreateDTO;
import services.data.dto.companies.TransportCompanyUpdateDTO;
import services.data.dto.companies.TransportCompanyViewDTO;
import services.services.contracts.ITransportCompanyService;

import java.text.MessageFormat;
import java.util.List;

public class TransportCompanyController {
    private final Validator validator;
    private final ITransportCompanyService transportCompanyService;

    public TransportCompanyController(Validator validator, ITransportCompanyService transportCompanyService) {
        this.validator = validator;
        this.transportCompanyService = transportCompanyService;
    }

    public void run() {

        // CREATE starter LTD. company (just Starter seeded by the seeder)
        String name = "Starter LTD.";
        String address = "1 Vasil Levski blvd., Troyan, Lovech";

        TransportCompanyCreateDTO company = new TransportCompanyCreateDTO(name, address);
        var violations = validator.validate(company);

        // If there are violations, handle them
        if (!violations.isEmpty()) {
            // Handle validation errors (you could throw an exception, return an error response, etc.)
            System.out.println("Validation errors occurred:");
            for (ConstraintViolation<TransportCompanyCreateDTO> violation : violations) {
                System.out.println("Field: " + violation.getPropertyPath() + " - " + violation.getMessage());
            }
            return;
        }

        try {
            var entity = transportCompanyService.create(company);
            System.out.println("Company created successfully.");
            System.out.println(entity.toString());

        } catch (Exception e) {
            System.out.println("Error while creating company.");
            e.printStackTrace();
        }

        // Get by id
        TransportCompanyViewDTO viewDTO;
        try {
            viewDTO = transportCompanyService.getById(5L);
            System.out.println("Get by ID successfully.");
            System.out.println(viewDTO.toString());
        } catch (Exception e) {
            System.out.println("Error while getting by ID.");
            e.printStackTrace();
        }

        // Get all
        try {
            List<TransportCompanyViewDTO> entities =
                    transportCompanyService.getAll(0, 10, "name", true);
            System.out.println("Printing all entities successfully." + System.lineSeparator());
            for (var entity : entities) {
                System.out.println(entity.toString());
            }
            System.out.println();

        } catch (Exception e) {
            System.out.println("Error while printing all entities." + System.lineSeparator());
        }

        // Delete entity
        try {
            transportCompanyService.delete(5L);
            System.out.println("Company deleted successfully.");
        } catch (Exception e) {
            System.out.println("Error while deleting company.");
        }

        // trying to get deleted entity
        Long idToDelete = 5L;
        try {
            transportCompanyService.getById(idToDelete);
        } catch (Exception e) {
            System.out.println(MessageFormat.format("Error while getting by {0}.", idToDelete));
        }

        // update a company

        try {
            Long idEntityToUpdate = 1L; // from the company seeder
            TransportCompanyViewDTO entityToUpdate = transportCompanyService.getById(idEntityToUpdate);
            System.out.println("Name before update" + entityToUpdate.getCompanyName());

            TransportCompanyUpdateDTO updateDTO = new TransportCompanyUpdateDTO();
            updateDTO.setId(idEntityToUpdate); // Set the ID explicitly
            updateDTO.setCompanyName("DXC Technology");
            updateDTO.setAddress(entityToUpdate.getAddress());
            transportCompanyService.update(updateDTO);
            // getting company after updated name
            TransportCompanyViewDTO updatedEntity = transportCompanyService.getById(idEntityToUpdate);
            System.out.println("Name after update" + updatedEntity.getCompanyName());
        } catch (Exception e) {
            System.out.println("Error while updating company.");
        }



    }


}
