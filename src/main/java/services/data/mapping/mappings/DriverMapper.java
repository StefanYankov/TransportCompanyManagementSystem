package services.data.mapping.mappings;

import data.models.employee.Dispatcher;
import data.models.employee.Driver;
import data.models.employee.Qualification;
import jakarta.persistence.EntityManager;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import services.data.dto.employees.DriverCreateDto;
import services.data.dto.employees.DriverUpdateDto;
import services.data.dto.employees.DriverViewDto;
import services.data.mapping.ModelMapperConfig;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DriverMapper {

    private static final ModelMapper modelMapper = ModelMapperConfig.getModelMapper();
    private final EntityManager entityManager;

    public DriverMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
        configureMappings();
    }

    private void configureMappings() {
        // Converter for mapping Set<Long> qualification IDs to Set<Qualification>
        Converter<Set<Long>, Set<Qualification>> qualificationConverter = context -> {
            Set<Long> ids = context.getSource();
            if (ids == null) return new HashSet<>();
            return ids.stream()
                    .map(id -> entityManager.find(Qualification.class, id))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        };

        // Mapping for DriverCreateDto to Driver
        modelMapper.typeMap(DriverCreateDto.class, Driver.class)
                .addMappings(mapper -> {
                    mapper.map(DriverCreateDto::getTransportCompanyId, Driver::setTransportCompany);
                    mapper.using(qualificationConverter)
                            .map(DriverCreateDto::getQualificationIds, Driver::setDriverQualifications);
                    mapper.map(src -> entityManager.find(Dispatcher.class, src.getDispatcherId()), Driver::setDispatcher);
                });

        // Mapping for DriverUpdateDto to Driver
        modelMapper.typeMap(DriverUpdateDto.class, Driver.class)
                .addMappings(mapper -> {
                    mapper.using(qualificationConverter)
                            .map(DriverUpdateDto::getDriverQualificationIds, Driver::setDriverQualifications);
                    mapper.map(src -> entityManager.find(Dispatcher.class, src.getDispatcherId()), Driver::setDispatcher);
                });

        // Mapping for Driver to DriverViewDto
        modelMapper.typeMap(Driver.class, DriverViewDto.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getTransportCompany().getId(), DriverViewDto::setTransportCompanyId);
                    mapper.map(src -> src.getDispatcher() != null ? src.getDispatcher().getId() : null, DriverViewDto::setDispatcherId);
                    mapper.map(src -> src.getDriverQualifications().stream()
                            .map(Qualification::getId)
                            .collect(Collectors.toSet()), DriverViewDto::setDriverQualificationIds);
                });
    }

    /**
     * Maps a Driver entity to a DriverViewDto.
     *
     * @param entity The Driver entity to map.
     * @return The mapped DriverViewDto.
     */
    public DriverViewDto toViewDto(Driver entity) {
        return modelMapper.map(entity, DriverViewDto.class);
    }

    /**
     * Maps a DriverCreateDto to a Driver entity.
     *
     * @param dto The DriverCreateDto to map.
     * @return The mapped Driver entity.
     */
    public Driver toEntity(DriverCreateDto dto) {
        return modelMapper.map(dto, Driver.class);
    }

    /**
     * Updates a Driver entity from a DriverUpdateDto.
     *
     * @param dto    The DriverUpdateDto containing the updated data.
     * @param entity The Driver entity to update.
     */
    public void updateEntityFromDto(DriverUpdateDto dto, Driver entity) {
        modelMapper.map(dto, entity);
    }
}