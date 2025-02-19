package services.data.dto.vehicles;


import data.models.transportservices.CargoType;

public abstract class TransportCargoVehicleViewDto extends VehicleViewDto {

    private Double maxCargoCapacityKg;
    private Double currentCargoCapacityKg;
    private CargoType cargoType;

    public TransportCargoVehicleViewDto() {
    }

    public TransportCargoVehicleViewDto(Long id, String registrationPlate, Long colourId, Long transportCompanyId, Double maxCargoCapacityKg, Double currentCargoCapacityKg, CargoType cargoType) {
        super(id, registrationPlate, colourId, transportCompanyId);
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
}