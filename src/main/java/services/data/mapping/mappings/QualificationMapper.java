package services.data.mapping.mappings;

import data.models.employee.Qualification;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import services.data.dto.employees.QualificationCreateDto;
import services.data.dto.employees.QualificationUpdateDto;
import services.data.dto.employees.QualificationViewDto;
import services.data.mapping.ModelMapperConfig;


public class QualificationMapper {

    private static final ModelMapper modelMapper = ModelMapperConfig.getModelMapper();
    private final EntityManager entityManager;

    public QualificationMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
        configureMappings();
    }

    private void configureMappings() {

        modelMapper.typeMap(QualificationCreateDto.class, Qualification.class)
                .addMappings(mapper -> {
                    mapper.map(QualificationCreateDto::getName, Qualification::setName);
                    mapper.map(QualificationCreateDto::getDescription, Qualification::setDescription);
                });

        modelMapper.typeMap(QualificationUpdateDto.class, Qualification.class)
                .addMappings(mapper -> {
                    mapper.map(QualificationUpdateDto::getId, Qualification::setId);
                    mapper.map(QualificationUpdateDto::getName, Qualification::setName);
                    mapper.map(QualificationUpdateDto::getDescription, Qualification::setDescription);
                });

        modelMapper.typeMap(Qualification.class, QualificationViewDto.class)
                .addMappings(mapper -> {
                    mapper.map(Qualification::getName, QualificationViewDto::setName);
                    mapper.map(Qualification::getDescription, QualificationViewDto::setDescription);
                    mapper.map(Qualification::getId, QualificationViewDto::setId);
                });
    }

    public QualificationViewDto toViewDto(Qualification entity) {
        return modelMapper.map(entity, QualificationViewDto.class);
    }

    public Qualification toEntity(QualificationCreateDto dto) {
        return modelMapper.map(dto, Qualification.class);
    }

    public void updateEntityFromDto(QualificationUpdateDto dto, Qualification entity) {
        modelMapper.map(dto, entity);
    }
}