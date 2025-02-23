package services.data.dto.vehicles;

import data.models.transportservices.CargoType;
import data.models.vehicles.TruckType;
import jakarta.validation.constraints.NotNull;

public class TruckUpdateDTO extends TransportCargoVehicleUpdateDTO {

    @NotNull(message = "Truck type is required")
    private TruckType truckType;

    public TruckUpdateDTO() {
    }

    public TruckUpdateDTO(Long id, String registrationPlate, Long transportCompanyId, Double maxCargoCapacityKg, Double currentCargoCapacityKg, CargoType cargoType, TruckType truckType) {
        super(id, registrationPlate, transportCompanyId, maxCargoCapacityKg, currentCargoCapacityKg, cargoType);
        this.truckType = truckType;
    }

    public TruckType getTruckType() {
        return truckType;
    }

    public void setTruckType(TruckType truckType) {
        this.truckType = truckType;
    }
}