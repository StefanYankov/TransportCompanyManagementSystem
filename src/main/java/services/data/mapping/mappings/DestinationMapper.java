package services.data.mapping.mappings;

import data.models.transportservices.Destination;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import services.data.dto.transportservices.DestinationCreateDto;
import services.data.dto.transportservices.DestinationUpdateDto;
import services.data.dto.transportservices.DestinationViewDto;
import services.data.mapping.ModelMapperConfig;

public class DestinationMapper {

    private static final ModelMapper modelMapper = ModelMapperConfig.getModelMapper();

    public DestinationMapper() {
    }

    public DestinationViewDto toViewDto(Destination entity) {
        return modelMapper.map(entity, DestinationViewDto.class);
    }

    public Destination toEntity(DestinationCreateDto dto) {
        return modelMapper.map(dto, Destination.class);
    }

    public void updateEntityFromDto(DestinationUpdateDto dto, Destination entity) {
        modelMapper.map(dto, entity);
    }
}