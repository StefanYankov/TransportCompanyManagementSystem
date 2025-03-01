package services.data.dto.transportservices;

import data.common.ModelValidation;
import jakarta.validation.constraints.Min;

public class TransportPassengersServiceUpdateDTO extends TransportServiceUpdateDTO {

    @Min(value = ModelValidation.MINIMUM_NUMBER_OF_PASSENGERS, message = ModelValidation.PASSENGERS_MUST_BE_GREATER_THAN)
    private int numberOfPassengers;

    public TransportPassengersServiceUpdateDTO() {
    }

    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }
}