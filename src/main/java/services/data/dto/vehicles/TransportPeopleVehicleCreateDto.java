package services.data.dto.vehicles;

import data.common.ModelValidation;
import jakarta.validation.constraints.*;

public abstract class TransportPeopleVehicleCreateDto extends VehicleCreateDto {

    @NotNull(message = ModelValidation.MAX_PASSENGER_CAPACITY_REQUIRED)
    @Min(value = ModelValidation.MINIMUM_NUMBER_OF_PASSENGERS, message = ModelValidation.PASSENGERS_MUST_BE_GREATER_THAN)
    private Integer maxPassengerCapacity;

    public TransportPeopleVehicleCreateDto() {
    }

    public TransportPeopleVehicleCreateDto(String registrationPlate, Long colourId, Long transportCompanyId, Integer maxPassengerCapacity) {
        super(registrationPlate, colourId, transportCompanyId);
        this.maxPassengerCapacity = maxPassengerCapacity;
    }

    public Integer getMaxPassengerCapacity() {
        return maxPassengerCapacity;
    }

    public void setMaxPassengerCapacity(Integer maxPassengerCapacity) {
        this.maxPassengerCapacity = maxPassengerCapacity;
    }
}