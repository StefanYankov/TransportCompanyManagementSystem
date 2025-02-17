package data.models.vehicles;

import jakarta.persistence.*;

@Entity
@Table(name = "transport_people_vehicles")
public class TransportPeopleVehicle extends Vehicle {

    private int maxPassengerCapacity;
    private int currentPassengerCapacity;

    public TransportPeopleVehicle() {
    }

    @Column(name = "max_passenger_capacity", nullable = false)
    public int getMaxPassengerCapacity() {
        return maxPassengerCapacity;
    }

    public void setMaxPassengerCapacity(int maxPassengerCapacity) {
        this.maxPassengerCapacity = maxPassengerCapacity;
    }

    @Column(name = "current_passenger_capacity", nullable = false)
    public int getCurrentPassengerCapacity() {
        return currentPassengerCapacity;
    }

    public void setCurrentPassengerCapacity(int currentPassengerCapacity) {
        this.currentPassengerCapacity = currentPassengerCapacity;
    }
}