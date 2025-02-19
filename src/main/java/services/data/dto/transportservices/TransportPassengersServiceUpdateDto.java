package services.data.dto.transportservices;

import data.common.ModelValidation;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransportPassengersServiceUpdateDto extends TransportServiceUpdateDto {

    @Min(value = ModelValidation.MINIMUM_NUMBER_OF_PASSENGERS, message = ModelValidation.PASSENGERS_MUST_BE_GREATER_THAN)
    private int numberOfPassengers;

    public TransportPassengersServiceUpdateDto() {
    }

    public TransportPassengersServiceUpdateDto(Long clientId, Long destinationId, Long driverId, LocalDate endingDate, Long id, BigDecimal price, LocalDate startingDate, Long transportCompanyId, Long vehicleId, int numberOfPassengers) {
        super(clientId, destinationId, driverId, endingDate, id, price, startingDate, transportCompanyId, vehicleId);
        this.numberOfPassengers = numberOfPassengers;
    }

    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }
}