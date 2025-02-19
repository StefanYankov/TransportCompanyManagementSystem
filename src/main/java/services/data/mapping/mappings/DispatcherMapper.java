package services.data.mapping.mappings;

import data.models.employee.Dispatcher;
import data.models.employee.Driver;
import jakarta.persistence.EntityManager;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import services.data.dto.employees.DispatcherCreateDto;
import services.data.dto.employees.DispatcherUpdateDto;
import services.data.dto.employees.DispatcherViewDto;
import services.data.mapping.ModelMapperConfig;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DispatcherMapper {

    private static final ModelMapper modelMapper = ModelMapperConfig.getModelMapper();
    private final EntityManager entityManager;

    public DispatcherMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
        configureMappings();
    }

    private void configureMappings() {
        // Converter for mapping Set<Long> driver IDs to Set<Driver>
        Converter<Set<Long>, Set<Driver>> driverConverter = context -> {
            Set<Long> ids = context.getSource();
            if (ids == null) return new HashSet<>();
            return ids.stream()
                    .map(id -> entityManager.find(Driver.class, id))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        };

        // Mapping for DispatcherCreateDto to Dispatcher
        modelMapper.typeMap(DispatcherCreateDto.class, Dispatcher.class)
                .addMappings(mapper -> {
                    mapper.map(DispatcherCreateDto::getTransportCompanyId, Dispatcher::setTransportCompany);
                    mapper.using(driverConverter)
                            .map(DispatcherCreateDto::getSupervisedDriverIds, Dispatcher::setSupervisedDrivers);
                });

        // Mapping for DispatcherUpdateDto to Dispatcher
        modelMapper.typeMap(DispatcherUpdateDto.class, Dispatcher.class)
                .addMappings(mapper -> {
                    mapper.using(driverConverter)
                            .map(DispatcherUpdateDto::getSupervisedDriverIds, Dispatcher::setSupervisedDrivers);
                });

        // Mapping for Dispatcher to DispatcherViewDto
        modelMapper.typeMap(Dispatcher.class, DispatcherViewDto.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getTransportCompany().getId(), DispatcherViewDto::setTransportCompanyId);
                    mapper.map(src -> src.getSupervisedDrivers().stream()
                            .map(Driver::getId)
                            .collect(Collectors.toSet()), DispatcherViewDto::setSupervisedDriverIds);
                });
    }

    /**
     * Maps a Dispatcher entity to a DispatcherViewDto.
     *
     * @param entity The Dispatcher entity to map.
     * @return The mapped DispatcherViewDto.
     */
    public DispatcherViewDto toViewDto(Dispatcher entity) {
        return modelMapper.map(entity, DispatcherViewDto.class);
    }

    /**
     * Maps a DispatcherCreateDto to a Dispatcher entity.
     *
     * @param dto The DispatcherCreateDto to map.
     * @return The mapped Dispatcher entity.
     */
    public Dispatcher toEntity(DispatcherCreateDto dto) {
        return modelMapper.map(dto, Dispatcher.class);
    }

    /**
     * Updates a Dispatcher entity from a DispatcherUpdateDto.
     *
     * @param dto    The DispatcherUpdateDto containing the updated data.
     * @param entity The Dispatcher entity to update.
     */
    public void updateEntityFromDto(DispatcherUpdateDto dto, Dispatcher entity) {
        modelMapper.map(dto, entity);
    }
}