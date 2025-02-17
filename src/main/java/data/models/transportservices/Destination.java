package data.models.transportservices;

import data.common.BaseModel;
import data.common.ModelValidation;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "destinations")
public class Destination extends BaseModel {

    private String startingLocation;
    private String endingLocation;

    public Destination() {
    }

    @Column(name = "starting_location", nullable = false, length = ModelValidation.ADDRESS_LENGTH)
    public String getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    @Column(name = "ending_location", nullable = false, length = ModelValidation.ADDRESS_LENGTH)
    public String getEndingLocation() {
        return endingLocation;
    }

    public void setEndingLocation(String endingLocation) {
        this.endingLocation = endingLocation;
    }
}
