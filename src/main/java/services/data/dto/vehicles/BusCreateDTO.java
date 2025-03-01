package services.data.dto.vehicles;

import data.common.ModelValidation;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class BusCreateDTO extends TransportPeopleVehicleCreateDTO {

    @NotNull(message = ModelValidation.RESTROOM_FLAG_IS_REQUIRED)
    private boolean hasRestroom;

    @NotNull(message = ModelValidation.LUGGAGE_CAPACITY_REQUIRED)
    @DecimalMin(value = ModelValidation.MINIMUM_ALLOWED_LUGGAGE_CAPACITY, message = ModelValidation.INVALID_MINIMUM_LUGGAGE_CAPACITY)
    private BigDecimal luggageCapacity;

    public BusCreateDTO() {
    }

    public BusCreateDTO(String registrationPlate, Long transportCompanyId, Integer maxPassengerCapacity, Boolean hasRestroom, BigDecimal luggageCapacity) {
        super(registrationPlate, transportCompanyId, maxPassengerCapacity);
        this.hasRestroom = hasRestroom;
        this.luggageCapacity = luggageCapacity;
    }

    public boolean getHasRestroom() {
        return hasRestroom;
    }

    public void setHasRestroom(boolean hasRestroom) {
        this.hasRestroom = hasRestroom;
    }

    public BigDecimal getLuggageCapacity() {
        return luggageCapacity;
    }

    public void setLuggageCapacity(BigDecimal luggageCapacity) {
        this.luggageCapacity = luggageCapacity;
    }
}