package services.data.dto.vehicles;

import data.common.ModelValidation;
import data.models.transportservices.CargoType;
import jakarta.validation.constraints.*;

public abstract class TransportCargoVehicleCreateDTO extends VehicleCreateDTO {

    @NotNull(message = ModelValidation.MAX_CARGO_CAPACITY_REQUIRED)
    @DecimalMin(value = ModelValidation.MIN_CARGO_CAPACITY, message = ModelValidation.INVALID_CARGO_MAX_CAPACITY)
    private double  maxCargoCapacityKg;

    @NotNull(message = ModelValidation.CURRENT_CARGO_CAPACITY_REQUIRED)
    @DecimalMin(value = ModelValidation.MIN_CARGO_CAPACITY, message = ModelValidation.INVALID_CARGO_MAX_CAPACITY)
    private double  currentCargoCapacityKg;

    @NotNull(message = ModelValidation.CARGO_TYPE_REQUIRED)
    private CargoType cargoType;

    public TransportCargoVehicleCreateDTO() {
    }

    public TransportCargoVehicleCreateDTO(String registrationPlate, Long transportCompanyId, double  maxCargoCapacityKg, double  currentCargoCapacityKg, CargoType cargoType) {
        super(registrationPlate, transportCompanyId);
        this.maxCargoCapacityKg = maxCargoCapacityKg;
        this.currentCargoCapacityKg = currentCargoCapacityKg;
        this.cargoType = cargoType;
    }

    public double  getMaxCargoCapacityKg() {
        return maxCargoCapacityKg;
    }

    public void setMaxCargoCapacityKg(double  maxCargoCapacityKg) {
        this.maxCargoCapacityKg = maxCargoCapacityKg;
    }

    public double getCurrentCargoCapacityKg() {
        return currentCargoCapacityKg;
    }

    public void setCurrentCargoCapacityKg(double  currentCargoCapacityKg) {
        this.currentCargoCapacityKg = currentCargoCapacityKg;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public void setCargoType(CargoType cargoType) {
        this.cargoType = cargoType;
    }
}