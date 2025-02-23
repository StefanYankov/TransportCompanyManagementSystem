package services.data.dto.vehicles;

import java.math.BigDecimal;

public class BusViewDTO extends TransportPeopleVehicleViewDTO {

    private Boolean hasRestroom;
    private BigDecimal luggageCapacity;

    public BusViewDTO() {
    }

    public BusViewDTO(Long id, String registrationPlate, Long transportCompanyId, Integer maxPassengerCapacity, Boolean hasRestroom, BigDecimal luggageCapacity) {
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