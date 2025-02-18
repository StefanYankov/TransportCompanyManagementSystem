package data.models.vehicles;

import data.common.ModelValidation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

@Entity
@Table(name = "bus")
public class Bus extends TransportPeopleVehicle{

    private boolean hasRestroom;
    private BigDecimal luggageCapacity;


    public Bus() {
    }

    @Column(name = "restroom", nullable = false)
    public boolean isHasRestroom() {
        return hasRestroom;
    }

    public void setHasRestroom(boolean hasRestroom) {
        this.hasRestroom = hasRestroom;
    }

    @Column(name = "luggage_capacity")
    @DecimalMin(value = ModelValidation.MINIMUM_ALLOWED_LUGGAGE_CAPACITY, message = ModelValidation.INVALID_MINIMUM_LUGGAGE_CAPACITY)
    public BigDecimal getLuggageCapacity() {
        return luggageCapacity;
    }

    public void setLuggageCapacity(BigDecimal luggageCapacity) {
        this.luggageCapacity = luggageCapacity;
    }
}
