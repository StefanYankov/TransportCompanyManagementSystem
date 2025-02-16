package data.models.vehicles;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "transport_cargo_vehicles")
public abstract class TransportCargoVehicle extends Vehicle {

    private double maxCargoCapacityKg;
    private double currentCargoCapacityKg;
    private CargoType cargoType;

    public TransportCargoVehicle() {
    }

    public TransportCargoVehicle(String vehicleRegistrationPlate,
                                 String vehicleVINNumber,
                                 String model,
                                 String colour,
                                 double maxCargoCapacityKg,
                                 double currentCargoCapacityKg,
                                 CargoType cargoType) {
        super(vehicleRegistrationPlate, vehicleVINNumber, model, colour);
        this.maxCargoCapacityKg = maxCargoCapacityKg;
        this.currentCargoCapacityKg = currentCargoCapacityKg;
        this.cargoType = cargoType;
    }

    @Column(name = "max_cargo_capacity_kg", nullable = false)
    public double getMaxCargoCapacityKg() {
        return maxCargoCapacityKg;
    }

    public void setMaxCargoCapacityKg(double maxCargoCapacityKg) {
        this.maxCargoCapacityKg = maxCargoCapacityKg;
    }

    @Column(name = "current_cargo_capacity_kg", nullable = false)
    public double getCurrentCargoCapacityKg() {
        return currentCargoCapacityKg;
    }

    public void setCurrentCargoCapacityKg(double currentCargoCapacityKg) {
        this.currentCargoCapacityKg = currentCargoCapacityKg;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo_type", nullable = false)
    public CargoType getCargoType() {
        return cargoType;
    }

    public void setCargoType(CargoType cargoType) {
        this.cargoType = cargoType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), maxCargoCapacityKg, currentCargoCapacityKg, cargoType);
    }

    @Override
    public String toString() {
        return "TransportCargoVehicle{" +
                "maxCargoCapacityKg=" + maxCargoCapacityKg +
                ", currentCargoCapacityKg=" + currentCargoCapacityKg +
                ", cargoType=" + cargoType +
                "} " + super.toString();
    }
}