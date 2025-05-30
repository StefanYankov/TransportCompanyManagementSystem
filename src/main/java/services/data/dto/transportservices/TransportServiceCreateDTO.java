package services.data.dto.transportservices;

import data.common.ModelValidation;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class TransportServiceCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = ModelValidation.ID_REQUIRED)
    private Long transportCompanyId;

    @NotNull(message = ModelValidation.STARTING_DATE_REQUIRED)
    @FutureOrPresent(message = ModelValidation.STARTING_DATE_MUST_BE_FUTURE_OR_PRESENT)
    private LocalDate startingDate;

    @NotNull(message = ModelValidation.ENDING_DATE_REQUIRED)
    @Future(message = ModelValidation.ENDING_DATE_MUST_BE_FUTURE)
    private LocalDate endingDate;

    @NotNull(message = ModelValidation.ID_REQUIRED)
    private Long destinationId;

    @NotNull(message = ModelValidation.ID_REQUIRED)
    private Long clientId;

    @NotNull(message = ModelValidation.PRICE_REQUIRED)
    @DecimalMin(value = ModelValidation.MINIMUM_ALLOWED_PRICE, message = ModelValidation.PRICE_SHOULD_BE_A_POSITIVE_VALUE)
    private BigDecimal price;

    @NotNull(message = ModelValidation.ID_REQUIRED)
    private Long vehicleId;

    @NotNull(message = ModelValidation.ID_REQUIRED)
    private Long driverId;
    public TransportServiceCreateDTO() {
    }

    public TransportServiceCreateDTO(Long clientId, Long destinationId, Long driverId, LocalDate endingDate, BigDecimal price, LocalDate startingDate, Long transportCompanyId, Long vehicleId) {
        this.clientId = clientId;
        this.destinationId = destinationId;
        this.driverId = driverId;
        this.endingDate = endingDate;
        this.price = price;
        this.startingDate = startingDate;
        this.transportCompanyId = transportCompanyId;
        this.vehicleId = vehicleId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public LocalDate getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDate endingDate) {
        this.endingDate = endingDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDate startingDate) {
        this.startingDate = startingDate;
    }

    public Long getTransportCompanyId() {
        return transportCompanyId;
    }

    public void setTransportCompanyId(Long transportCompanyId) {
        this.transportCompanyId = transportCompanyId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
}