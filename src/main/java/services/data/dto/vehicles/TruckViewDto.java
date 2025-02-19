package services.data.dto.vehicles;

import data.models.transportservices.CargoType;
import data.models.vehicles.TruckType;

public class TruckViewDto extends TransportCargoVehicleViewDto {

    private TruckType truckType;

    public TruckViewDto() {
    }

    public TruckViewDto(Long id, String registrationPlate, Long colourId, Long transportCompanyId, Double maxCargoCapacityKg, Double currentCargoCapacityKg, CargoType cargoType, TruckType truckType) {
        super(id, registrationPlate, colourId, transportCompanyId, maxCargoCapacityKg, currentCargoCapacityKg, cargoType);
        this.truckType = truckType;
    }

    public TruckType getTruckType() {
        return truckType;
    }

    public void setTruckType(TruckType truckType) {
        this.truckType = truckType;
    }
}