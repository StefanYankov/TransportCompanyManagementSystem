package services;

import data.models.employee.Employee;
import data.repositories.IGenericRepository;
import org.modelmapper.ModelMapper;

public class EmployeeService implements IEmployeeService {

    private final IGenericRepository<Employee, Long> employeeRepository;
    private final ModelMapper modelMapper;

    public EmployeeService(
            IGenericRepository<Employee, Long> employeeRepository,
            ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }


}
