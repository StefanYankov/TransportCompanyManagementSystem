package data.models.vehicles;

import data.common.BaseModel;
import data.common.ModelValidation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "colours")
public class Colour extends BaseModel {
    private String name;

    public Colour() {
    }

    public Colour(String name) {
        this.name = name;
    }

    @Column(unique = true, nullable = false, length = ModelValidation.MAX_NAME_LENGTH)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}