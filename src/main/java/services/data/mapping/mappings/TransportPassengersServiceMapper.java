package services.data.mapping.mappings;

import data.models.transportservices.TransportPassengersService;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import services.data.dto.transportservices.TransportPassengersServiceCreateDto;
import services.data.dto.transportservices.TransportPassengersServiceUpdateDto;
import services.data.dto.transportservices.TransportPassengersServiceViewDto;
import services.data.mapping.ModelMapperConfig;

public class TransportPassengersServiceMapper {

    private static final ModelMapper modelMapper = ModelMapperConfig.getModelMapper();
    private final EntityManager entityManager;

    public TransportPassengersServiceMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
        configureMappings();
    }

    private void configureMappings() {
        // Mapping for TransportPassengersServiceCreateDto to TransportPassengersService
        modelMapper.typeMap(TransportPassengersServiceCreateDto.class, TransportPassengersService.class)
                .addMappings(mapper -> {
                    mapper.map(TransportPassengersServiceCreateDto::getNumberOfPassengers, TransportPassengersService::setNumberOfPassengers);
                });

        // Mapping for TransportPassengersServiceUpdateDto to TransportPassengersService
        modelMapper.typeMap(TransportPassengersServiceUpdateDto.class, TransportPassengersService.class)
                .addMappings(mapper -> {
                    mapper.map(TransportPassengersServiceUpdateDto::getNumberOfPassengers, TransportPassengersService::setNumberOfPassengers);
                });

        // Mapping for TransportPassengersService to TransportPassengersServiceViewDto
        modelMapper.typeMap(TransportPassengersService.class, TransportPassengersServiceViewDto.class)
                .addMappings(mapper -> {
                    mapper.map(TransportPassengersService::getNumberOfPassengers, TransportPassengersServiceViewDto::setNumberOfPassengers);
                });
    }

    /**
     * Maps a TransportPassengersService entity to a TransportPassengersServiceViewDto.
     *
     * @param entity The TransportPassengersService entity to map.
     * @return The mapped TransportPassengersServiceViewDto.
     */
    public TransportPassengersServiceViewDto toViewDto(TransportPassengersService entity) {
        return modelMapper.map(entity, TransportPassengersServiceViewDto.class);
    }

    /**
     * Maps a TransportPassengersServiceCreateDto to a TransportPassengersService entity.
     *
     * @param dto The TransportPassengersServiceCreateDto to map.
     * @return The mapped TransportPassengersService entity.
     */
    public TransportPassengersService toEntity(TransportPassengersServiceCreateDto dto) {
        return modelMapper.map(dto, TransportPassengersService.class);
    }

    /**
     * Updates a TransportPassengersService entity from a TransportPassengersServiceUpdateDto.
     *
     * @param dto    The TransportPassengersServiceUpdateDto containing the updated data.
     * @param entity The TransportPassengersService entity to update.
     */
    public void updateEntityFromDto(TransportPassengersServiceUpdateDto dto, TransportPassengersService entity) {
        modelMapper.map(dto, entity);
    }
}