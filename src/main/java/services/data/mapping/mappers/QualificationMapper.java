package services.data.mapping.mappers;

import data.models.employee.Qualification;
import org.modelmapper.ModelMapper;
import services.data.dto.employees.QualificationCreateDTO;
import services.data.dto.employees.QualificationUpdateDTO;
import services.data.dto.employees.QualificationViewDTO;

/**
 * Mapper class for converting between {@link Qualification} entities and their DTOs using ModelMapper.
 */
public class QualificationMapper {
    private final ModelMapper modelMapper;

    /**
     * Constructs a new QualificationMapper with its own ModelMapper instance.
     */
    public QualificationMapper() {
        this.modelMapper = new ModelMapper();
        configureMappings();
    }

    /**
     * Configures mappings where default behavior needs adjustment,
     * such as skipping the {@code id} field for creation since it’s auto-generated by the database.
     */
    private void configureMappings() {
        // Skip id for QualificationCreateDTO -> Qualification (auto-generated by H2)
        modelMapper.createTypeMap(QualificationCreateDTO.class, Qualification.class)
                .addMappings(mapper -> mapper.skip(Qualification::setId));
        // Other fields (name, description) map automatically
    }

    /**
     * Converts a {@link QualificationCreateDTO} to a {@link Qualification} entity.
     * Fields like name and description are mapped automatically; id is skipped.
     *
     * @param dto the DTO to convert
     * @return the mapped Qualification entity
     */
    public Qualification toEntity(QualificationCreateDTO dto) {
        return modelMapper.map(dto, Qualification.class);
    }

    /**
     * Converts a {@link QualificationUpdateDTO} to a {@link Qualification} entity.
     * All fields (id, name, description) are mapped automatically.
     *
     * @param dto the DTO to convert
     * @return the mapped Qualification entity
     */
    public Qualification toEntity(QualificationUpdateDTO dto) {
        return modelMapper.map(dto, Qualification.class);
    }

    /**
     * Converts a {@link Qualification} entity to a {@link QualificationUpdateDTO}.
     * All fields are mapped automatically.
     *
     * @param entity the entity to convert
     * @return the mapped QualificationUpdateDTO
     */
    public QualificationUpdateDTO toUpdateDTO(Qualification entity) {
        return modelMapper.map(entity, QualificationUpdateDTO.class);
    }

    /**
     * Converts a {@link Qualification} entity to a {@link QualificationViewDTO} for display purposes.
     * Fields like id, name, and description are mapped automatically.
     *
     * @param entity the entity to convert
     * @return the mapped QualificationViewDTO
     */
    public QualificationViewDTO toViewDTO(Qualification entity) {
        return modelMapper.map(entity, QualificationViewDTO.class);
    }
}