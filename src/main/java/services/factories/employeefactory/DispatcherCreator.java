package services.factories.employeefactory;

import data.models.TransportCompany;
import data.models.employee.Dispatcher;
import data.models.employee.Employee;

import java.math.BigDecimal;

public class DispatcherCreator implements EmployeeCreator {
    @Override
    public Employee createEmployee(String firstName, String familyName, BigDecimal salary, TransportCompany company) {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setFirstName(firstName);
        dispatcher.setFamilyName(familyName);
        dispatcher.setSalary(salary);
        dispatcher.setTransportCompany(company);
        return dispatcher;
    }
}
