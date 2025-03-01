package services.data.dto.vehicles;

import data.common.ModelValidation;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class BusUpdateDTO extends TransportPeopleVehicleUpdateDTO {

    private Boolean hasRestroom; // Changed back to Boolean

    @NotNull(message = ModelValidation.LUGGAGE_CAPACITY_REQUIRED)
    @DecimalMin(value = ModelValidation.MINIMUM_ALLOWED_LUGGAGE_CAPACITY, message = ModelValidation.INVALID_MINIMUM_LUGGAGE_CAPACITY)
    private BigDecimal luggageCapacity;

    public BusUpdateDTO() {
    }

    public BusUpdateDTO(Long id, String registrationPlate, Long transportCompanyId, Integer maxPassengerCapacity, Boolean hasRestroom, BigDecimal luggageCapacity) {
        super(id, registrationPlate, transportCompanyId, maxPassengerCapacity);
        this.hasRestroom = hasRestroom; // No defaulting here
        this.luggageCapacity = luggageCapacity;
    }

    public Boolean getHasRestroom() { // Changed to Boolean
        return hasRestroom;
    }

    public void setHasRestroom(Boolean hasRestroom) { // Changed to Boolean
        this.hasRestroom = hasRestroom;
    }

    public BigDecimal getLuggageCapacity() {
        return luggageCapacity;
    }

    public void setLuggageCapacity(BigDecimal luggageCapacity) {
        this.luggageCapacity = luggageCapacity;
    }
}