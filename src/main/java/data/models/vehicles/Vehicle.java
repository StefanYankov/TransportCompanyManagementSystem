package data.models.vehicles;

import data.common.BaseModel;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "vehicles")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Vehicle extends BaseModel<Long> {

    private LocalDate issueDate;
    private String registrationPlate;
    private String vinNumber;
    private String model;
    private String colour;

    public Vehicle() {
    }

    public Vehicle(LocalDate issueDate, String registrationPlate, String vinNumber, String model, String colour) {
        this.issueDate = issueDate;
        this.registrationPlate = registrationPlate;
        this.vinNumber = vinNumber;
        this.model = model;
        this.colour = colour;
    }

    @Column(name = "issue_date", nullable = false)
    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    @Column(name = "registration_plate", nullable = false)
    public String getRegistrationPlate() {
        return registrationPlate;
    }

    public void setRegistrationPlate(String vehicleRegistrationPlate) {
        this.registrationPlate = vehicleRegistrationPlate;
    }

    @Column(name = "vin_number", unique = true, nullable = false)
    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vehicleVINNumber) {
        this.vinNumber = vehicleVINNumber;
    }

    @Column(name = "model", nullable = false)
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Column(name = "colour", nullable = false)
    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(getId(), vehicle.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + getId() +
                ", issueDate=" + issueDate +
                ", vehicleRegistrationPlate='" + registrationPlate + '\'' +
                ", vehicleVINNumber='" + vinNumber + '\'' +
                ", model='" + model + '\'' +
                ", colour='" + colour + '\'' +
                '}';
    }
}