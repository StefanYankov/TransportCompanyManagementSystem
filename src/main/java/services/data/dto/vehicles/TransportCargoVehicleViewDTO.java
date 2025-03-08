package services.data.dto.vehicles;


import data.models.transportservices.CargoType;

public abstract class TransportCargoVehicleViewDTO extends VehicleViewDTO {

    private Double maxCargoCapacityKg;
    private Double currentCargoCapacityKg;
    private CargoType cargoType;

    public TransportCargoVehicleViewDTO() {
    }

    public TransportCargoVehicleViewDTO(Long id, String registrationPlate, Long transportCompanyId, Double maxCargoCapacityKg, Double currentCargoCapacityKg, CargoType cargoType) {
        super(id, registrationPlate, transportCompanyId);
        this.maxCargoCapacityKg = maxCargoCapacityKg;
        this.currentCargoCapacityKg = currentCargoCapacityKg;
        this.cargoType = cargoType;
    }

    public Double getMaxCargoCapacityKg() {
        return maxCargoCapacityKg;
    }

    public void setMaxCargoCapacityKg(Double maxCargoCapacityKg) {
        this.maxCargoCapacityKg = maxCargoCapacityKg;
    }

    public Double getCurrentCargoCapacityKg() {
        return currentCargoCapacityKg;
    }

    public void setCurrentCargoCapacityKg(Double currentCargoCapacityKg) {
        this.currentCargoCapacityKg = currentCargoCapacityKg;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public void setCargoType(CargoType cargoType) {
        this.cargoType = cargoType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("maxCargoCapacityKg=").append(maxCargoCapacityKg);
        sb.append(", currentCargoCapacityKg=").append(currentCargoCapacityKg);
        sb.append(", cargoType=").append(cargoType);
        sb.append('}');
        sb.append(super.toString());
        return sb.toString();
    }
}