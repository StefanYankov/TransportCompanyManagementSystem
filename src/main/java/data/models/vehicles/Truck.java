package data.models.vehicles;

import jakarta.persistence.*;

@Entity
@Table(name = "trucks")

public class Truck extends TransportCargoVehicle {

    private TruckType truckType;
    public Truck() {}

    // Getter and Setter for truckType
    @Column(name = "truck_type", nullable = false)
    @Enumerated(EnumType.STRING)
    public TruckType getTruckType() {
        return truckType;
    }

    public void setTruckType(TruckType truckType) {
        this.truckType = truckType;
    }
}

