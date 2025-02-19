package services.data.dto.transportservices;

import java.time.LocalDate;
import java.math.BigDecimal;

public class TransportPassengersServiceViewDto extends TransportServiceViewDto {

    private int numberOfPassengers;

    public TransportPassengersServiceViewDto() {
    }

    public TransportPassengersServiceViewDto(Long clientId, Long destinationId, Long driverId, LocalDate endingDate, Long id, boolean isDelivered, boolean isPaid, BigDecimal price, LocalDate startingDate, Long transportCompanyId, Long vehicleId, int numberOfPassengers) {
        super(clientId, destinationId, driverId, endingDate, id, isDelivered, isPaid, price, startingDate, transportCompanyId, vehicleId);
        this.numberOfPassengers = numberOfPassengers;
    }

    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }
}