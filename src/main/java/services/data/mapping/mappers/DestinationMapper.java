package services.data.mapping.mappers;

import data.models.transportservices.Destination;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import services.data.dto.transportservices.DestinationCreateDTO;
import services.data.dto.transportservices.DestinationUpdateDTO;
import services.data.dto.transportservices.DestinationViewDTO;

public class DestinationMapper {
    private final ModelMapper modelMapper;

    public DestinationMapper() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE)
                .setSkipNullEnabled(true); // Skip null values during mapping
        configureMappings();
    }

    private void configureMappings() {
        modelMapper.createTypeMap(DestinationCreateDTO.class, Destination.class)
                .addMappings(mapper -> mapper.skip(Destination::setId));
        modelMapper.createTypeMap(Destination.class, DestinationViewDTO.class)
                .addMapping(Destination::getId, DestinationViewDTO::setId)
                .addMapping(Destination::getStartingLocation, DestinationViewDTO::setStartingLocation)
                .addMapping(Destination::getEndingLocation, DestinationViewDTO::setEndingLocation);
        modelMapper.createTypeMap(DestinationUpdateDTO.class, Destination.class)
                .addMapping(DestinationUpdateDTO::getId, Destination::setId)
                .addMapping(DestinationUpdateDTO::getStartingLocation, Destination::setStartingLocation)
                .addMapping(DestinationUpdateDTO::getEndingLocation, Destination::setEndingLocation);
    }

    public Destination toEntity(DestinationCreateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DestinationCreateDTO must not be null");
        return modelMapper.map(dto, Destination.class);
    }

    public Destination toEntity(DestinationUpdateDTO dto, Destination existing) {
        if (dto == null) throw new IllegalArgumentException("DestinationUpdateDTO must not be null");
        modelMapper.map(dto, existing);
        return existing;
    }

    public DestinationViewDTO toViewDTO(Destination destination) {
        if (destination == null) return null;
        return modelMapper.map(destination, DestinationViewDTO.class);
    }
}