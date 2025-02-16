package data.models.vehicles;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "trucks")
public class Truck extends TransportCargoVehicle {
    // Default constructor (required by Hibernate)
    public Truck() {}

    // Parameterized constructor
    public Truck(LocalDate issueDate,
                 String registrationPlate,
                 String vinNumber,
                 String model,
                 String colour,
                 double maxCargoCapacityKg,
                 double currentCargoCapacityKg,
                 CargoType cargoType) {
        super(issueDate, registrationPlate, vinNumber, model, colour, maxCargoCapacityKg, currentCargoCapacityKg, cargoType);
    }

    @Override
    public String toString() {
        return "Truck{} " + super.toString();
    }
}