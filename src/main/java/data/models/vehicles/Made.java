package data.models.vehicles;

import data.common.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "made")
public class Made extends BaseModel {
    private String name;

    public Made() {
    }

    public Made(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}