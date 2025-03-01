package services.data.mapping.mappers;

import data.models.employee.Dispatcher;
import data.models.employee.Driver;
import data.models.TransportCompany;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.modelmapper.ModelMapper;
import services.data.dto.employees.DispatcherCreateDTO;
import services.data.dto.employees.DispatcherUpdateDTO;
import services.data.dto.employees.DispatcherViewDTO;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between {@link Dispatcher} entities and their DTOs.
 */
public class DispatcherMapper {
    private final ModelMapper modelMapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;
    private final IGenericRepository<Driver, Long> driverRepo;

    /**
     * Constructs a new DispatcherMapper with its own ModelMapper instance and required repositories.
     */
    public DispatcherMapper(IGenericRepository<TransportCompany, Long> companyRepo,
                            IGenericRepository<Driver, Long> driverRepo) {
        this.modelMapper = new ModelMapper();
        this.companyRepo = companyRepo;
        this.driverRepo = driverRepo;
        configureMappings();
    }

    /**
     * Configures mappings where default behavior needs adjustment.
     */
    private void configureMappings() {
        modelMapper.createTypeMap(DispatcherCreateDTO.class, Dispatcher.class)
                .addMappings(mapper -> mapper.skip(Dispatcher::setId));

        modelMapper.createTypeMap(Dispatcher.class, DispatcherViewDTO.class)
                .addMappings(mapper -> mapper.using(ctx -> {
                    Set<Driver> drivers = (Set<Driver>) ctx.getSource();
                    return drivers != null ?
                            drivers.stream().map(Driver::getId).collect(Collectors.toSet()) :
                            new HashSet<>();
                }).map(Dispatcher::getSupervisedDrivers, DispatcherViewDTO::setSupervisedDriverIds));
    }

    /**
     * Converts a {@link DispatcherCreateDTO} to a {@link Dispatcher} entity.
     *
     * @param dto the DTO to convert
     * @return the mapped Dispatcher entity
     */
    public Dispatcher toEntity(DispatcherCreateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DispatcherCreateDTO must not be null");
        Dispatcher dispatcher = modelMapper.map(dto, Dispatcher.class);
        resolveRelationships(dispatcher, dto);
        return dispatcher;
    }

    /**
     * Updates an existing {@link Dispatcher} entity with fields from a {@link DispatcherUpdateDTO}.
     *
     * @param dto the DTO containing updated fields
     * @param existing the existing Dispatcher entity to update
     */
    public void toEntity(DispatcherUpdateDTO dto, Dispatcher existing) {
        if (dto == null) throw new IllegalArgumentException("DispatcherUpdateDTO must not be null");
        if (dto.getFirstName() != null) existing.setFirstName(dto.getFirstName());
        if (dto.getFamilyName() != null) existing.setFamilyName(dto.getFamilyName());
        if (dto.getSalary() != null) existing.setSalary(dto.getSalary());
        resolveRelationships(existing, dto);
    }

    /**
     * Converts a {@link Dispatcher} entity to a {@link DispatcherUpdateDTO}.
     *
     * @param entity the entity to convert
     * @return the mapped DispatcherUpdateDTO
     */
    public DispatcherUpdateDTO toUpdateDTO(Dispatcher entity) {
        return modelMapper.map(entity, DispatcherUpdateDTO.class);
    }

    /**
     * Converts a {@link Dispatcher} entity to a {@link DispatcherViewDTO} for display purposes.
     *
     * @param entity the entity to convert
     * @return the mapped DispatcherViewDTO
     */
    public DispatcherViewDTO toViewDTO(Dispatcher entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, DispatcherViewDTO.class);
    }

    private void resolveRelationships(Dispatcher dispatcher, DispatcherCreateDTO dto) {
        TransportCompany company = companyRepo.getById(dto.getTransportCompanyId())
                .orElseThrow(() -> new RepositoryException("Transport company not found: " + dto.getTransportCompanyId()));
        dispatcher.setTransportCompany(company);

        if (dto.getSupervisedDriverIds() != null && !dto.getSupervisedDriverIds().isEmpty()) {
            Set<Driver> drivers = dto.getSupervisedDriverIds().stream()
                    .map(id -> driverRepo.getById(id)
                            .orElseThrow(() -> new RepositoryException("Driver not found: " + id)))
                    .collect(Collectors.toSet());
            dispatcher.setSupervisedDrivers(drivers);
        } else {
            dispatcher.setSupervisedDrivers(Collections.emptySet());
        }
    }

    private void resolveRelationships(Dispatcher dispatcher, DispatcherUpdateDTO dto) {
        if (dto.getTransportCompanyId() != null) {
            TransportCompany company = companyRepo.getById(dto.getTransportCompanyId())
                    .orElseThrow(() -> new RepositoryException("Transport company not found: " + dto.getTransportCompanyId()));
            dispatcher.setTransportCompany(company);
        }

        if (dto.getSupervisedDriverIds() != null) {
            Set<Driver> drivers = dto.getSupervisedDriverIds().isEmpty() ?
                    Collections.emptySet() :
                    dto.getSupervisedDriverIds().stream()
                            .map(id -> driverRepo.getById(id)
                                    .orElseThrow(() -> new RepositoryException("Driver not found: " + id)))
                            .collect(Collectors.toSet());
            dispatcher.setSupervisedDrivers(drivers);
        }
    }
}