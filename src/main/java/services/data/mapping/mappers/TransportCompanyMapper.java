package services.data.mapping.mappers;

import data.models.employee.Employee;
import data.models.TransportCompany;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import services.data.dto.companies.TransportCompanyCreateDTO;
import services.data.dto.companies.TransportCompanyUpdateDTO;
import services.data.dto.companies.TransportCompanyViewDTO;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class TransportCompanyMapper {
    private final ModelMapper modelMapper;

    public TransportCompanyMapper() {
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
        modelMapper.createTypeMap(TransportCompanyCreateDTO.class, TransportCompany.class)
                .addMappings(mapper -> mapper.skip(TransportCompany::setId));

        modelMapper.createTypeMap(TransportCompany.class, TransportCompanyViewDTO.class)
                .addMapping(TransportCompany::getId, TransportCompanyViewDTO::setId)
                .addMapping(TransportCompany::getName, TransportCompanyViewDTO::setName)
                .addMapping(TransportCompany::getAddress, TransportCompanyViewDTO::setAddress)
                .addMapping(src -> {
                    Set<Employee> employees = src.getEmployees();
                    return employees != null ? employees.stream()
                            .map(Employee::getId)
                            .collect(Collectors.toSet()) : Collections.emptySet();
                }, TransportCompanyViewDTO::setEmployeeIds);

        modelMapper.createTypeMap(TransportCompanyUpdateDTO.class, TransportCompany.class)
                .addMapping(TransportCompanyUpdateDTO::getId, TransportCompany::setId)
                .addMapping(TransportCompanyUpdateDTO::getName, TransportCompany::setName)
                .addMapping(TransportCompanyUpdateDTO::getAddress, TransportCompany::setAddress);
    }

    public TransportCompany toEntity(TransportCompanyCreateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("TransportCompanyCreateDTO must not be null");
        return modelMapper.map(dto, TransportCompany.class);
    }

    public TransportCompany toEntity(TransportCompanyUpdateDTO dto, TransportCompany existing) {
        if (dto == null) throw new IllegalArgumentException("TransportCompanyUpdateDTO must not be null");
        modelMapper.map(dto, existing);
        return existing;
    }

    public TransportCompanyViewDTO toViewDTO(TransportCompany company) {
        if (company == null) return null;
        return modelMapper.map(company, TransportCompanyViewDTO.class);
    }
}