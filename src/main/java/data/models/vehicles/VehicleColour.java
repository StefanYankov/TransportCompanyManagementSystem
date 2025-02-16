package data.models.vehicles;

import data.common.BaseDeletableModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicle_colour")
public class VehicleColour extends BaseDeletableModel<Long> {
    private String name;

    public VehicleColour() {

    }

    public VehicleColour(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
