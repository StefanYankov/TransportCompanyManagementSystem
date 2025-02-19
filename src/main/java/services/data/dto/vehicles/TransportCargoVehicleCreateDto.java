package services.data.dto.vehicles;

import data.common.ModelValidation;
import data.models.transportservices.CargoType;
import jakarta.validation.constraints.*;

public abstract class TransportCargoVehicleCreateDto extends VehicleCreateDto {

    @NotNull(message = ModelValidation.MAX_CARGO_CAPACITY_REQUIRED)
    @DecimalMin(value = ModelValidation.MIN_CARGO_CAPACITY, message = ModelValidation.INVALID_CARGO_MAX_CAPACITY)
    private Double maxCargoCapacityKg;

    @NotNull(message = ModelValidation.CURRENT_CARGO_CAPACITY_REQUIRED)
    @DecimalMin(value = ModelValidation.MIN_CARGO_CAPACITY, message = ModelValidation.INVALID_CARGO_MAX_CAPACITY)
    private Double currentCargoCapacityKg;

    @NotNull(message = ModelValidation.CARGO_TYPE_REQUIRED)
    private CargoType cargoType;

    public TransportCargoVehicleCreateDto() {
    }

    public TransportCargoVehicleCreateDto(String registrationPlate, Long colourId, Long transportCompanyId, Double maxCargoCapacityKg, Double currentCargoCapacityKg, CargoType cargoType) {
        super(registrationPlate, colourId, transportCompanyId);
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