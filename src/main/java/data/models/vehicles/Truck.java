package data.models.vehicles;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "trucks")
public class Truck extends TransportCargoVehicle {

    private TruckType truckType;
    public Truck() {}

    // Getter and Setter for truckType
    @Column(name = "truck_type", nullable = false)
    public TruckType getTruckType() {
        return truckType;
    }

    public void setTruckType(TruckType truckType) {
        this.truckType = truckType;
    }
}

