package data.models.transportservices;

import data.common.ModelValidation;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "transport_passengers_service")
public class TransportPassengersService extends TransportService {
    private int numberOfPassengers;

    public TransportPassengersService() {
    }


    @Min(value = ModelValidation.MINIMUM_NUMBER_OF_PASSENGERS, message = ModelValidation.PASSENGERS_MUST_BE_GREATER_THAN)
    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }
}
