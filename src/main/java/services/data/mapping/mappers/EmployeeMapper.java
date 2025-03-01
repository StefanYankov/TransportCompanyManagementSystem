package services.data.mapping.mappers;

import data.models.employee.Employee;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import services.data.dto.employees.EmployeeViewDTO;

public class EmployeeMapper {
    private final ModelMapper modelMapper;

    public EmployeeMapper() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE)
                .setSkipNullEnabled(true);
        configureMappings();
    }

    private void configureMappings() {
        modelMapper.createTypeMap(Employee.class, EmployeeViewDTO.class)
                .addMapping(Employee::getId, EmployeeViewDTO::setId)
                .addMapping(Employee::getFirstName, EmployeeViewDTO::setFirstName)
                .addMapping(Employee::getFamilyName, EmployeeViewDTO::setFamilyName)
                .addMapping(Employee::getSalary, EmployeeViewDTO::setSalary)
                .addMapping(src -> src.getTransportCompany().getId(), EmployeeViewDTO::setTransportCompanyId);
    }

    public EmployeeViewDTO toViewDTO(Employee employee) {
        if (employee == null) return null;
        return modelMapper.map(employee, EmployeeViewDTO.class);
    }
}