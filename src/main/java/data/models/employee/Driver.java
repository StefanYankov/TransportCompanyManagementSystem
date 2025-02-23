package data.models.employee;

import data.models.transportservices.TransportService;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "drivers")
public class Driver extends Employee {

    private Dispatcher dispatcher;
    private Set<Qualification> qualifications = new HashSet<>();
    private Set<TransportService> transportServices = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
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
    public Set<Qualification> getQualifications() {
        return qualifications;
    }

    public void setQualifications(Set<Qualification> driverQualifications) {
        this.qualifications = driverQualifications;
    }

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    public Set<TransportService> getTransportServices() {
        return transportServices;
    }

    public void setTransportServices(Set<TransportService> transportServices) {
        this.transportServices = transportServices;
    }
}
