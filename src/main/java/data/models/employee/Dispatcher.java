package data.models.employee;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "dispatchers")
public class Dispatcher extends Employee {

    private Set<Driver> supervisedDrivers = new HashSet<>();;

    public Dispatcher() {}

    @OneToMany(mappedBy = "dispatcher")
    public Set<Driver> getSupervisedDrivers() {
        return supervisedDrivers;
    }

    public void setSupervisedDrivers(Set<Driver> supervisedDrivers) {
        this.supervisedDrivers = supervisedDrivers;
    }
}
