package data.models.vehicles;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "transport_people_vehicles")
public abstract class TransportPeopleVehicle extends Vehicle {

    private int maxPassengerCapacity;
    private int currentPassengerCapacity;

    public TransportPeopleVehicle() {
    }

    public TransportPeopleVehicle(String vehicleRegistrationPlate,
                                  String vehicleVINNumber,
                                  String model,
                                  String colour,
                                  int maxPassengerCapacity,
                                  int currentPassengerCapacity) {
        super(vehicleRegistrationPlate, vehicleVINNumber, model, colour);
        this.maxPassengerCapacity = maxPassengerCapacity;
        this.currentPassengerCapacity = currentPassengerCapacity;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TransportPeopleVehicle that = (TransportPeopleVehicle) o;
        return maxPassengerCapacity == that.maxPassengerCapacity &&
                currentPassengerCapacity == that.currentPassengerCapacity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), maxPassengerCapacity, currentPassengerCapacity);
    }

    @Override
    public String toString() {
        return "TransportPeopleVehicle{" +
                "maxPassengerCapacity=" + maxPassengerCapacity +
                ", currentPassengerCapacity=" + currentPassengerCapacity +
                "} " + super.toString();
    }
}