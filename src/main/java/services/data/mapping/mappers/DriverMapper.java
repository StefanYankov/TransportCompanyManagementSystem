package services.data.mapping.mappers;

import data.models.employee.Driver;
import data.models.employee.Qualification;
import data.models.TransportCompany;
import data.models.employee.Dispatcher;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.modelmapper.ModelMapper;
import services.data.dto.employees.DriverCreateDTO;
import services.data.dto.employees.DriverUpdateDTO;
import services.data.dto.employees.DriverViewDTO;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class DriverMapper {
    private final ModelMapper modelMapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;
    private final IGenericRepository<Dispatcher, Long> dispatcherRepo;
    private final IGenericRepository<Qualification, Long> qualificationRepo;

    public DriverMapper(IGenericRepository<TransportCompany, Long> companyRepo,
                        IGenericRepository<Dispatcher, Long> dispatcherRepo,
                        IGenericRepository<Qualification, Long> qualificationRepo) {
        this.modelMapper = new ModelMapper();
        this.companyRepo = companyRepo;
        this.dispatcherRepo = dispatcherRepo;
        this.qualificationRepo = qualificationRepo;
        configureMappings();
    }

    private void configureMappings() {
        // Skip id for DriverCreateDTO -> Driver (auto-generated)
        modelMapper.createTypeMap(DriverCreateDTO.class, Driver.class)
                .addMappings(mapper -> mapper.skip(Driver::setId));

        // Map qualifications to IDs for ViewDTO
        modelMapper.createTypeMap(Driver.class, DriverViewDTO.class)
                .addMappings(mapper -> mapper.using(ctx -> {
                    Set<Qualification> qualifications = (Set<Qualification>) ctx.getSource();
                    return qualifications != null ?
                            qualifications.stream().map(Qualification::getId).collect(Collectors.toSet()) :
                            Collections.emptySet();
                }).map(Driver::getQualifications, DriverViewDTO::setQualificationIds));
    }

    public Driver toEntity(DriverCreateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DriverCreateDTO must not be null");
        Driver driver = modelMapper.map(dto, Driver.class);
        resolveRelationships(driver, dto);
        return driver;
    }

    public void toEntity(DriverUpdateDTO dto, Driver existing) {
        if (dto == null) throw new IllegalArgumentException("DriverUpdateDTO must not be null");
        // Manually map non-null fields to preserve existing values
        if (dto.getFirstName() != null) existing.setFirstName(dto.getFirstName());
        if (dto.getFamilyName() != null) existing.setFamilyName(dto.getFamilyName());
        if (dto.getSalary() != null) existing.setSalary(dto.getSalary());
        resolveRelationships(existing, dto); // Update relationships only if specified
    }

    public DriverUpdateDTO toUpdateDTO(Driver entity) {
        return modelMapper.map(entity, DriverUpdateDTO.class);
    }

    public DriverViewDTO toViewDTO(Driver entity) {
        return modelMapper.map(entity, DriverViewDTO.class);
    }

    private void resolveRelationships(Driver driver, DriverCreateDTO dto) {
        TransportCompany company = companyRepo.getById(dto.getTransportCompanyId())
                .orElseThrow(() -> new RepositoryException("Transport company not found: " + dto.getTransportCompanyId()));
        driver.setTransportCompany(company);

        if (dto.getDispatcherId() != null) {
            Dispatcher dispatcher = dispatcherRepo.getById(dto.getDispatcherId())
                    .orElseThrow(() -> new RepositoryException("Dispatcher not found: " + dto.getDispatcherId()));
            driver.setDispatcher(dispatcher);
        }

        if (dto.getQualificationIds() != null && !dto.getQualificationIds().isEmpty()) {
            Set<Qualification> qualifications = dto.getQualificationIds().stream()
                    .map(id -> qualificationRepo.getById(id)
                            .orElseThrow(() -> new RepositoryException("Qualification not found: " + id)))
                    .collect(Collectors.toSet());
            driver.setQualifications(qualifications);
        } else {
            driver.setQualifications(Collections.emptySet());
        }
    }

    private void resolveRelationships(Driver driver, DriverUpdateDTO dto) {
        if (dto.getTransportCompanyId() != null) {
            TransportCompany company = companyRepo.getById(dto.getTransportCompanyId())
                    .orElseThrow(() -> new RepositoryException("Transport company not found: " + dto.getTransportCompanyId()));
            driver.setTransportCompany(company);
        }

        if (dto.getDispatcherId() != null) {
            Dispatcher dispatcher = dispatcherRepo.getById(dto.getDispatcherId())
                    .orElseThrow(() -> new RepositoryException("Dispatcher not found: " + dto.getDispatcherId()));
            driver.setDispatcher(dispatcher);
        }

        if (dto.getQualificationIds() != null) {
            Set<Qualification> qualifications = dto.getQualificationIds().isEmpty() ?
                    Collections.emptySet() :
                    dto.getQualificationIds().stream()
                            .map(id -> qualificationRepo.getById(id)
                                    .orElseThrow(() -> new RepositoryException("Qualification not found: " + id)))
                            .collect(Collectors.toSet());
            driver.setQualifications(qualifications);
        }
    }
}