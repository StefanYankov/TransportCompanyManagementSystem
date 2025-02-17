package data.models;

import data.common.BaseModel;
import data.common.ModelValidation;
import data.common.annotations.ValidEmail;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "clients")
public class Client extends BaseModel {

    private String name;
    private String telephone;
    private String email;

    public Client() {
    }

    @Column(nullable = false, length = ModelValidation.NAME_LENGTH)
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

    @Column(name = "email", nullable = false)
    @ValidEmail(message = ModelValidation.INVALID_EMAIL_MESSAGE)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
