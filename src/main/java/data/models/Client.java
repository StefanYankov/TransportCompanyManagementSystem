package data.models;

import data.common.BaseModel;
import data.common.ModelValidation;
import data.common.annotations.ValidEmail;
import data.models.transportservices.TransportService;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clients")
public class Client extends BaseModel {

    private String name;
    private String telephone;
    private String email;
    private Set<TransportService> transportService = new HashSet<>();

    public Client() {
    }

    @Column(nullable = false, length = ModelValidation.MAX_NAME_LENGTH)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false, length = ModelValidation.TELEPHONE_LENGTH)
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(name = "email", nullable = false, unique = true)
    @ValidEmail(message = ModelValidation.INVALID_EMAIL_MESSAGE)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @OneToMany()
    public Set<TransportService> getTransportService() {
        return transportService;
    }

    public void setTransportService(Set<TransportService> transportService) {
        this.transportService = transportService;
    }

}
