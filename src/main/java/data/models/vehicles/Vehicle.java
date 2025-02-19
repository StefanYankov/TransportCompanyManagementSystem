package data.models.vehicles;

import data.common.BaseModel;
import data.models.TransportCompany;
import jakarta.persistence.*;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "vehicles")
public abstract class Vehicle extends BaseModel {

    private String registrationPlate;
    private Colour colour;
    private TransportCompany transportCompany;

    public Vehicle() {
    }

    @Column(name = "registration_plate", nullable = false, unique = true)
    public String getRegistrationPlate() {
        return registrationPlate;
    }

    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "colour_id", nullable = false)
    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    @ManyToOne
    @JoinColumn(name = "transport_company_id")
    public TransportCompany getTransportCompany() {
        return transportCompany;
    }

    public void setTransportCompany(TransportCompany transportCompany) {
        this.transportCompany = transportCompany;
    }

}