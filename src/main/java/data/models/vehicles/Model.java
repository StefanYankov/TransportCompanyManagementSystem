package data.models.vehicles;

import data.common.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "models")
public class Model extends BaseModel {
    private String name;

    public Model() {
    }

    public Model(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}