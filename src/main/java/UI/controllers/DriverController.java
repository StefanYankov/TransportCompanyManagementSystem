package UI.controllers;

import jakarta.validation.Validator;
import services.data.dto.companies.TransportCompanyViewDTO;
import services.data.dto.employees.DispatcherViewDTO;
import services.data.dto.employees.DriverCreateDTO;
import services.data.dto.employees.QualificationViewDTO;
import services.services.contracts.IDriverService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class DriverController {

    private final Validator validator;
    private final IDriverService driverService;

    public DriverController(Validator validator, IDriverService driverService) {
        this.validator = validator;
        this.driverService = driverService;
    }

    public void run() {
        // ## Create Driver

        // Driver basic input
        String driverName = "Jane";
        String driverFamilyName = "Doe";
        BigDecimal salary = BigDecimal.valueOf(5000);

        // Driver input where we have a relation

        // TODO: fetch the data with companyService
        TransportCompanyViewDTO company = new TransportCompanyViewDTO();
        company.setId(1L);
        company.setCompanyName("Starter");
        company.setAddress("1 Vasil Levski blvd., Troyan, Lovech");

        // TODO: fetch the data with dispatcherService
        DispatcherViewDTO dispatcher = new DispatcherViewDTO();
        dispatcher.setId(1L);
        dispatcher.setFirstName("John");
        dispatcher.setFamilyName("Smith");

        // TODO: fetch the data with qualificationService
        Set<QualificationViewDTO> qualifications = new HashSet<>();
        QualificationViewDTO qualification1 = new QualificationViewDTO();
        qualification1.setId(1L);
        qualification1.setName("A1");
        qualification1.setDescription("Light motorcycle up to 125cc and power up to 11 kW");
        qualifications.add(qualification1);


        DriverCreateDTO driver = new DriverCreateDTO(driverName, driverFamilyName, salary, company, dispatcher, qualifications);

        var test = driverService.create(driver);
        System.out.println(test);
    }


}
