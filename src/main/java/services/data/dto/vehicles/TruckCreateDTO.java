package services.data.dto.vehicles;

import data.models.transportservices.CargoType;
import data.models.vehicles.TruckType;
import jakarta.validation.constraints.NotNull;

public class TruckCreateDTO extends TransportCargoVehicleCreateDTO {

    @NotNull(message = "Truck type is required")
    private TruckType truckType;

    public TruckCreateDTO() {
    }

    public TruckCreateDTO(String registrationPlate, Long transportCompanyId, Double maxCargoCapacityKg, Double currentCargoCapacityKg, CargoType cargoType, TruckType truckType) {
        super(registrationPlate, transportCompanyId, maxCargoCapacityKg, currentCargoCapacityKg, cargoType);
        this.truckType = truckType;
    }

    public TruckType getTruckType() {
        return truckType;
    }

    public void setTruckType(TruckType truckType) {
        this.truckType = truckType;
    }
}