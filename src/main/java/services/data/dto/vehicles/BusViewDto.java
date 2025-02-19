package services.data.dto.vehicles;

import java.math.BigDecimal;

public class BusViewDto extends TransportPeopleVehicleViewDto {

    private Boolean hasRestroom;
    private BigDecimal luggageCapacity;

    public BusViewDto() {
    }

    public BusViewDto(Long id, String registrationPlate, Long colourId, Long transportCompanyId, Integer maxPassengerCapacity, Boolean hasRestroom, BigDecimal luggageCapacity) {
        super(id, registrationPlate, colourId, transportCompanyId, maxPassengerCapacity);
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