package data.models.transportservices;

import data.common.BaseModel;
import data.common.ModelValidation;
import data.common.annotations.EndDateCannotBeBeforeStartDate;
import data.models.Client;
import data.models.TransportCompany;
import data.models.employee.Driver;
import data.models.vehicles.Vehicle;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@EndDateCannotBeBeforeStartDate
@Table(name = "transport_services")
public abstract class TransportService extends BaseModel {

    private TransportCompany transportCompany;
    private LocalDate startingDate;
    private LocalDate endingDate;
    private Destination destination;
    private Client client;
    private BigDecimal price;
    private boolean isDelivered;
    private boolean isPaid;
    private Vehicle vehicle;
    private Driver driver;

    public TransportService() {
    }

    @ManyToOne
    @JoinColumn(name = "driver_id")
    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @ManyToOne
    @JoinColumn(name = "client_id")
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Column(name = "starting_date", nullable = false)
    public LocalDate getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDate startingDate) {
        this.startingDate = startingDate;
    }

    public LocalDate getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDate endingDate) {
        this.endingDate = endingDate;
    }

    @ManyToOne
    @JoinColumn(name = "destination_id")
    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    @ManyToOne
    @JoinColumn(name = "transport_company_id", nullable = false)
    public TransportCompany getTransportCompany() {
        return transportCompany;
    }

    public void setTransportCompany(TransportCompany transportCompany) {
        this.transportCompany = transportCompany;
    }

    @Column(precision = ModelValidation.DECIMAL_PRECISION, scale = ModelValidation.DECIMAL_SCALE)
    @DecimalMin(value = ModelValidation.MINIMUM_ALLOWED_PRICE, message = ModelValidation.PRICE_SHOULD_BE_A_POSITIVE_VALUE)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
}
