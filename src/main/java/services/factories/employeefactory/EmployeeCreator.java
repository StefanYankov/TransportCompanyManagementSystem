package services.factories.employeefactory;

import data.models.TransportCompany;
import data.models.employee.Employee;

import java.math.BigDecimal;

public interface EmployeeCreator {
    public Employee createEmployee(String firstName, String familyName, BigDecimal salary, TransportCompany company);

}
