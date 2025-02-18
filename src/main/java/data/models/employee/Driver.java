package data.models.employee;

import data.models.transportservices.TransportService;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "drivers")
public class Driver extends Employee {

    private Dispatcher dispatcher;
    private Set<Qualification> driverQualifications = new HashSet<>();
    private Set<TransportService> transportServices = new HashSet<>();

    // TODO: ManyToMany transport service
    @ManyToOne
    @JoinColumn(name = "dispatcher_id", nullable = true)
    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @ManyToMany
    @JoinTable(
            name = "driver_qualification",
            joinColumns = @JoinColumn(name = "driver_id"),
            inverseJoinColumns = @JoinColumn(name = "qualification_id")
    )
    public Set<Qualification> getDriverQualifications() {
        return driverQualifications;
    }

    public void setDriverQualifications(Set<Qualification> driverQualifications) {
        this.driverQualifications = driverQualifications;
    }

    @OneToMany(mappedBy = "driver")
    public Set<TransportService> getTransportServices() {
        return transportServices;
    }

    public void setTransportServices(Set<TransportService> transportServices) {
        this.transportServices = transportServices;
    }
}
