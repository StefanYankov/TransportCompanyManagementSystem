package services.data.mapping.mappers;

import data.models.employee.Driver;
import org.modelmapper.ModelMapper;
import services.data.dto.employees.DriverCreateDTO;
import services.data.dto.employees.DriverUpdateDTO;
import services.data.dto.employees.DriverViewDTO;

/**
 * Mapper class for converting between {@link Driver} entities and their DTOs using ModelMapper.
 */
public class DriverMapper {
    private final ModelMapper modelMapper;

    /**
     * Constructs a new DriverMapper with its own ModelMapper instance.
     */
    public DriverMapper() {
        this.modelMapper = new ModelMapper();
        configureMappings();
    }

    /**
     * Configures mappings where default behavior needs adjustment,
     * such as skipping the {@code id} field for creation since it’s auto-generated by the database.
     */
    private void configureMappings() {

    }

    /**
     * Converts a {@link DriverCreateDTO} to a {@link Driver} entity.
     * Fields like firstName, familyName, salary, transportCompanyId, dispatcherId, and qualificationIds
     * are mapped automatically; id is skipped.
     *
     * @param dto the DTO to convert
     * @return the mapped Driver entity
     */
    public Driver toEntity(DriverCreateDTO dto) {
        return modelMapper.map(dto, Driver.class);
    }

    /**
     * Converts a {@link DriverUpdateDTO} to a {@link Driver} entity.
     * All fields (id, firstName, familyName, salary, transportCompanyId, dispatcherId, qualificationIds)
     * are mapped automatically.
     *
     * @param dto the DTO to convert
     * @return the mapped Driver entity
     */
    public Driver toEntity(DriverUpdateDTO dto) {
        return modelMapper.map(dto, Driver.class);
    }

    /**
     * Converts a {@link Driver} entity to a {@link DriverUpdateDTO}.
     * All fields (id, firstName, familyName, salary, transportCompanyId, dispatcherId, qualificationIds)
     * are mapped automatically.
     *
     * @param entity the entity to convert
     * @return the mapped DriverUpdateDTO
     */
    public DriverUpdateDTO toUpdateDTO(Driver entity) {
        return modelMapper.map(entity, DriverUpdateDTO.class);
    }

    /**
     * Converts a {@link Driver} entity to a {@link DriverViewDTO} for display purposes.
     * Fields like id, firstName, familyName, and salary are mapped automatically; relations
     * (e.g., transportCompany, qualifications) are excluded unless explicitly added.
     *
     * @param entity the entity to convert
     * @return the mapped DriverViewDTO
     */
    public DriverViewDTO toViewDTO(Driver entity) {
        return modelMapper.map(entity, DriverViewDTO.class);
    }


}