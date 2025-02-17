package data.models.vehicles;

import data.models.transportservices.CargoType;
import jakarta.persistence.*;


@Entity
@Table(name = "transport_cargo_vehicles")
public abstract class TransportCargoVehicle extends Vehicle {

    private double maxCargoCapacityKg;
    private double currentCargoCapacityKg;
    private CargoType cargoType;

    public TransportCargoVehicle() {
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
}