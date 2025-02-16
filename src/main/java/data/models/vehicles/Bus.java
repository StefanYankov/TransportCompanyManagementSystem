package data.models.vehicles;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "buses")
public class Bus extends TransportPeopleVehicle {
    public Bus() {
    }

    public Bus(String registrationPlate,
               String vinNumber,
               String model,
               String colour,
               int maxPassengerCapacity,
               int currentPassengerCapacity) {
        super(registrationPlate, vinNumber, model, colour, maxPassengerCapacity, currentPassengerCapacity);
    }

    @Override
    public String toString() {
        return "Bus{} " + super.toString();
    }
}
