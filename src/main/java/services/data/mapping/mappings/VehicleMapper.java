package services.data.mapping.mappings;

import data.models.TransportCompany;
import data.models.vehicles.*;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import services.data.dto.vehicles.*;
import services.data.mapping.ModelMapperConfig;

public class VehicleMapper {

    private static final ModelMapper modelMapper = ModelMapperConfig.getModelMapper();
    private final EntityManager entityManager;

    public VehicleMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
        configureMappings();
    }

    private void configureMappings() {
        // Mapping for VehicleCreateDto to Vehicle
        modelMapper.typeMap(VehicleCreateDto.class, Vehicle.class)
                .addMappings(mapper -> {
                    mapper.map(VehicleCreateDto::getRegistrationPlate, Vehicle::setRegistrationPlate);
                    mapper.map(src -> entityManager.find(Colour.class, src.getColourId()), Vehicle::setColour);
                    mapper.map(src -> entityManager.find(TransportCompany.class, src.getTransportCompanyId()), Vehicle::setTransportCompany);
                });

        // Mapping for TruckCreateDto to Truck
        modelMapper.typeMap(TruckCreateDto.class, Truck.class)
                .addMappings(mapper -> {
                    mapper.map(TruckCreateDto::getTruckType, Truck::setTruckType);
                });

        // Mapping for VanCreateDto to Van
        modelMapper.typeMap(VanCreateDto.class, Van.class)
                .addMappings(mapper -> {
                    mapper.map(VanCreateDto::getHasPassengerOverheadStorage, Van::setHasPassengerOverheadStorage);
                });

        // Mapping for BusCreateDto to Bus
        modelMapper.typeMap(BusCreateDto.class, Bus.class)
                .addMappings(mapper -> {
                    mapper.map(BusCreateDto::getHasRestroom, Bus::setHasRestroom);
                    mapper.map(BusCreateDto::getLuggageCapacity, Bus::setLuggageCapacity);
                });

        // Mapping for Vehicle to VehicleViewDto
        modelMapper.typeMap(Vehicle.class, VehicleViewDto.class)
                .addMappings(mapper -> {
                    mapper.map(Vehicle::getRegistrationPlate, VehicleViewDto::setRegistrationPlate);
                    mapper.map(src -> src.getColour().getId(), VehicleViewDto::setColourId);
                    mapper.map(src -> src.getTransportCompany().getId(), VehicleViewDto::setTransportCompanyId);
                });
    }

    /**
     * Maps a Vehicle entity to a VehicleViewDto.
     *
     * @param entity The Vehicle entity to map.
     * @return The mapped VehicleViewDto.
     */
    public VehicleViewDto toViewDto(Vehicle entity) {
        return modelMapper.map(entity, VehicleViewDto.class);
    }

    /**
     * Maps a VehicleCreateDto to a Vehicle entity.
     *
     * @param dto The VehicleCreateDto to map.
     * @return The mapped Vehicle entity.
     */
    public Vehicle toEntity(VehicleCreateDto dto) {
        return modelMapper.map(dto, Vehicle.class);
    }

    /**
     * Updates a Vehicle entity from a VehicleUpdateDto.
     *
     * @param dto    The VehicleUpdateDto containing the updated data.
     * @param entity The Vehicle entity to update.
     */
    public void updateEntityFromDto(VehicleUpdateDto dto, Vehicle entity) {
        modelMapper.map(dto, entity);
    }
}