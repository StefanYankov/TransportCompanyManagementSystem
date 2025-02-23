package services.data.dto.vehicles;

import data.common.ModelValidation;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class BusUpdateDTO extends TransportPeopleVehicleUpdateDTO {

    private Boolean hasRestroom;

    @NotNull(message = ModelValidation.LUGGAGE_CAPACITY_REQUIRED)
    @DecimalMin(value = ModelValidation.MINIMUM_ALLOWED_LUGGAGE_CAPACITY, message = ModelValidation.INVALID_MINIMUM_LUGGAGE_CAPACITY)
    private BigDecimal luggageCapacity;

    public BusUpdateDTO() {
    }

    public BusUpdateDTO(Long id, String registrationPlate, Long transportCompanyId, Integer maxPassengerCapacity, Boolean hasRestroom, BigDecimal luggageCapacity) {
        super(id, registrationPlate, transportCompanyId, maxPassengerCapacity);
        this.hasRestroom = hasRestroom;
        this.luggageCapacity = luggageCapacity;
    }

    public Boolean getHasRestroom() {
        return hasRestroom;
    }

    public void setHasRestroom(Boolean hasRestroom) {
        this.hasRestroom = hasRestroom;
    }

    public BigDecimal getLuggageCapacity() {
        return luggageCapacity;
    }

    public void setLuggageCapacity(BigDecimal luggageCapacity) {
        this.luggageCapacity = luggageCapacity;
    }
}