package UI.engines;

import java.util.Scanner;

import UI.controllers.DriverController;
import UI.controllers.TransportCompanyController;
import jakarta.validation.Validator;

import services.services.contracts.IDriverService;
import services.services.contracts.ITransportCompanyService;

public class ConsoleEngine implements IEngine {
    private final Validator validator;
    private final Scanner scanner;
    private final ITransportCompanyService transportCompanyService;
    private final IDriverService driverService;

    public ConsoleEngine(Validator validator, ITransportCompanyService transportCompanyService, IDriverService driverService) {
        this.validator = validator;
        this.driverService = driverService;
        this.transportCompanyService = transportCompanyService;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void start() {

        System.out.println("-".repeat(12) + System.lineSeparator() + "Company controller starts" + System.lineSeparator() + "-".repeat(12));
        TransportCompanyController companyController = new TransportCompanyController(validator, transportCompanyService);
        companyController.run();
        System.out.println("-".repeat(12) + System.lineSeparator() + "Company controller ends" + System.lineSeparator() + "-".repeat(12));

//        DriverController driverController = new DriverController(validator, driverService);
//        driverController.run();

    }
}

