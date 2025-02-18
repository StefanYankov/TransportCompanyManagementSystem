package services.factories.employeefactory;

import data.models.TransportCompany;
import data.models.employee.Driver;
import data.models.employee.Employee;

import java.math.BigDecimal;

public class DriverCreator implements EmployeeCreator {
    @Override
    public Employee createEmployee(String firstName, String familyName, BigDecimal salary, TransportCompany company) {
        Driver driver = new Driver();
        driver.setFirstName(firstName);
        driver.setFamilyName(familyName);
        driver.setSalary(salary);
        driver.setTransportCompany(company);
        return driver;
    }
}