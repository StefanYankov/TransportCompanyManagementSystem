package services.data.mapping.mappers;

import data.models.transportservices.TransportPassengersService;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import services.data.dto.transportservices.TransportPassengersServiceCreateDTO;
import services.data.dto.transportservices.TransportPassengersServiceUpdateDTO;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;

public class TransportPassengersServiceMapper {
    private final ModelMapper modelMapper;

    public TransportPassengersServiceMapper() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE);
        configureMappings();
    }

    private void configureMappings() {
        modelMapper.createTypeMap(TransportPassengersServiceCreateDTO.class, TransportPassengersService.class)
                .addMappings(mapper -> mapper.skip(TransportPassengersService::setId));

        modelMapper.createTypeMap(TransportPassengersService.class, TransportPassengersServiceViewDTO.class)
                .addMapping(src -> src.getTransportCompany().getId(), TransportPassengersServiceViewDTO::setTransportCompanyId)
                .addMapping(src -> src.getVehicle().getId(), TransportPassengersServiceViewDTO::setVehicleId)
                .addMapping(src -> src.getDriver().getId(), TransportPassengersServiceViewDTO::setDriverId)
                .addMapping(src -> src.getClient().getId(), TransportPassengersServiceViewDTO::setClientId)
                .addMapping(src -> src.getDestination().getId(), TransportPassengersServiceViewDTO::setDestinationId);
    }

    public TransportPassengersService toEntity(TransportPassengersServiceCreateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("TransportPassengersServiceCreateDTO must not be null");
        return modelMapper.map(dto, TransportPassengersService.class);
    }

    public TransportPassengersService toEntity(TransportPassengersServiceUpdateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("TransportPassengersServiceUpdateDTO must not be null");
        return modelMapper.map(dto, TransportPassengersService.class);
    }

    public TransportPassengersServiceUpdateDTO toUpdateDTO(TransportPassengersService entity) {
        return modelMapper.map(entity, TransportPassengersServiceUpdateDTO.class);
    }

    public TransportPassengersServiceViewDTO toViewDTO(TransportPassengersService entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, TransportPassengersServiceViewDTO.class);
    }
}