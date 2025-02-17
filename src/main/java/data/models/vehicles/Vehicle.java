package data.models.vehicles;

import data.common.BaseModel;
import data.models.TransportCompany;
import jakarta.persistence.*;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "vehicles")
public abstract class Vehicle extends BaseModel {

    private String registrationPlate;
    private String vinNumber;
    private Made made;
    private Model model;
    private Colour colour;
    private TransportCompany transportCompany;


    public Vehicle() {}

    @Column(name = "registration_plate", nullable = false, unique = true)
    public String getRegistrationPlate() {
        return registrationPlate;
    }

    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }

    @Column(name = "vin_number", nullable = false, unique = true)
    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "made_id", nullable = false)
    public Made getMade() {
        return made;
    }

    public void setMade(Made made) {
        this.made = made;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
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