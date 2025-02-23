package data.models.vehicles;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "transport_people_vehicles")
public abstract class TransportPeopleVehicle extends Vehicle {

    private int maxPassengerCapacity;
    public TransportPeopleVehicle() {
    }

    @Column(name = "max_passenger_capacity", nullable = false)
    public int getMaxPassengerCapacity() {
        return maxPassengerCapacity;
    }

    public void setMaxPassengerCapacity(int maxPassengerCapacity) {
        this.maxPassengerCapacity = maxPassengerCapacity;
    }
}