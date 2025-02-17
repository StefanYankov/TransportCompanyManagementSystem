package data.models.employee;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "drivers")
public class Driver extends Employee {

    private Set<DriverQualification> driverQualifications = new HashSet<>();

    private Dispatcher dispatcher;

    @ManyToOne
    @JoinColumn(name = "dispatcher_id", nullable = true)
    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @ManyToMany()
    @JoinTable(
            name = "driver_qualification_mapping",
            joinColumns = @JoinColumn(name = "driver_id"),
            inverseJoinColumns = @JoinColumn(name = "qualification_id")
    )
    public Set<DriverQualification> getDriverQualifications() {
        return driverQualifications;
    }

    public void setDriverQualifications(Set<DriverQualification> driverQualification) {
        this.driverQualifications = driverQualification;
    }
}
