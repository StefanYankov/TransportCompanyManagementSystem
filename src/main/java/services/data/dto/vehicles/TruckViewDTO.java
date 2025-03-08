package services.data.dto.vehicles;

import data.models.transportservices.CargoType;
import data.models.vehicles.TruckType;

public class TruckViewDTO extends TransportCargoVehicleViewDTO {

    private TruckType truckType;

    public TruckViewDTO() {
    }

    public TruckViewDTO(Long id, String registrationPlate, Long transportCompanyId, Double maxCargoCapacityKg, Double currentCargoCapacityKg, CargoType cargoType, TruckType truckType) {
        super(id, registrationPlate, transportCompanyId, maxCargoCapacityKg, currentCargoCapacityKg, cargoType);
        this.truckType = truckType;
    }

    public TruckType getTruckType() {
        return truckType;
    }

    public void setTruckType(TruckType truckType) {
        this.truckType = truckType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TruckViewDTO{");
        sb.append("truckType=").append(truckType);
        sb.append('}');
        sb.append(super.toString());
        return sb.toString();
    }
}