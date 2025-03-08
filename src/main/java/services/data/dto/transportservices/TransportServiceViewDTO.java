package services.data.dto.transportservices;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.io.Serial;
import java.io.Serializable;

public class TransportServiceViewDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public Long id;
    public Long transportCompanyId;
    public LocalDate startingDate;
    public LocalDate endingDate;
    public Long destinationId;
    public Long clientId;
    public BigDecimal price;
    public boolean isDelivered;
    public boolean isPaid;
    public Long vehicleId;
    public Long driverId;

    public TransportServiceViewDTO() {
    }

    public TransportServiceViewDTO(Long clientId,
                                   Long destinationId,
                                   Long driverId,
                                   LocalDate endingDate,
                                   Long id,
                                   boolean isDelivered,
                                   boolean isPaid,
                                   BigDecimal price,
                                   LocalDate startingDate,
                                   Long transportCompanyId,
                                   Long vehicleId) {
        this.clientId = clientId;
        this.destinationId = destinationId;
        this.driverId = driverId;
        this.endingDate = endingDate;
        this.id = id;
        this.isDelivered = isDelivered;
        this.isPaid = isPaid;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TransportServiceViewDTO{");
        sb.append("id=").append(id);
        sb.append(", transportCompanyId=").append(transportCompanyId);
        sb.append(", startingDate=").append(startingDate);
        sb.append(", endingDate=").append(endingDate);
        sb.append(", destinationId=").append(destinationId);
        sb.append(", clientId=").append(clientId);
        sb.append(", price=").append(price);
        sb.append(", isDelivered=").append(isDelivered);
        sb.append(", isPaid=").append(isPaid);
        sb.append(", vehicleId=").append(vehicleId);
        sb.append(", driverId=").append(driverId);
        sb.append('}');
        return sb.toString();
    }
}