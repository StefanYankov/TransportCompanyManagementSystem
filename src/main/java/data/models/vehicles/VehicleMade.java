package data.models.vehicles;

import data.common.BaseDeletableModel;

public abstract class VehicleMade extends BaseDeletableModel<Long> {
    private String name;

    public VehicleMade(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
