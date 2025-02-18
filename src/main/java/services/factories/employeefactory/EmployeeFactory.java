package services.factories.employeefactory;

import data.models.TransportCompany;
import data.models.employee.Employee;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class EmployeeFactory {
    private final Map<String, EmployeeCreator> creatorMap = new HashMap<>();

    public EmployeeFactory() {
        creatorMap.put("Driver", new DriverCreator());
        creatorMap.put("Dispatcher", new DispatcherCreator());
    }

    public Employee createEmployee(String role, String firstName, String familyName, BigDecimal salary, TransportCompany company) {
        EmployeeCreator creator = creatorMap.get(role);
        if (creator != null) {
            return creator.createEmployee(firstName, familyName, salary, company);
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}
