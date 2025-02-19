package services.data.dto.vehicles;

import data.models.transportservices.CargoType;
import data.models.vehicles.TruckType;
import jakarta.validation.constraints.NotNull;

public class TruckCreateDto extends TransportCargoVehicleCreateDto {

    @NotNull(message = "Truck type is required")
    private TruckType truckType;

    public TruckCreateDto() {
    }

    public TruckCreateDto(String registrationPlate, Long colourId, Long transportCompanyId, Double maxCargoCapacityKg, Double currentCargoCapacityKg, CargoType cargoType, TruckType truckType) {
        super(registrationPlate, colourId, transportCompanyId, maxCargoCapacityKg, currentCargoCapacityKg, cargoType);
        this.truckType = truckType;
    }

    public TruckType getTruckType() {
        return truckType;
    }

    public void setTruckType(TruckType truckType) {
        this.truckType = truckType;
    }
}