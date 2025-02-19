package services.data.mapping.mappings;

import data.models.transportservices.TransportCargoService;
import services.data.dto.transportservices.TransportCargoServiceCreateDto;
import services.data.dto.transportservices.TransportCargoServiceUpdateDto;
import services.data.dto.transportservices.TransportCargoServiceViewDto;
import org.modelmapper.ModelMapper;
import services.data.mapping.ModelMapperConfig;

public class TransportCargoServiceMapper {
    private static final ModelMapper modelMapper = ModelMapperConfig.getModelMapper();

    static {

        modelMapper.typeMap(TransportCargoService.class, TransportCargoServiceViewDto.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getTransportCompany().getId(), TransportCargoServiceViewDto::setTransportCompanyId);
                    mapper.map(src -> src.getDestination().getId(), TransportCargoServiceViewDto::setDestinationId);
                    mapper.map(src -> src.getClient().getId(), TransportCargoServiceViewDto::setClientId);
                    mapper.map(src -> src.getVehicle().getId(), TransportCargoServiceViewDto::setVehicleId);
                    mapper.map(src -> src.getDriver().getId(), TransportCargoServiceViewDto::setDriverId);
                });


    }

    public static TransportCargoServiceViewDto toViewDto(TransportCargoService entity) {
        return modelMapper.map(entity, TransportCargoServiceViewDto.class);
    }

    public static TransportCargoService toEntity(TransportCargoServiceCreateDto dto) {
        return modelMapper.map(dto, TransportCargoService.class);
    }

    public static void updateEntityFromDto(TransportCargoServiceUpdateDto dto, TransportCargoService entity) {
        modelMapper.map(dto, entity);
    }
}