package data.models.vehicles;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "van")
public class Van extends TransportPeopleVehicle{

    private boolean hasPassengerOverheadStorage;

    public Van() {
    }

    @Column(name = "passenger_overhead_storage")
    public boolean getHasPassengerOverheadStorage() {
        return hasPassengerOverheadStorage;
    }

    public void setHasPassengerOverheadStorage(boolean hasPassengerOverheadStorage) {
        this.hasPassengerOverheadStorage = hasPassengerOverheadStorage;
    }
}
