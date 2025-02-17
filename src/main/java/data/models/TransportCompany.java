package data.models;

import data.common.BaseModel;
import data.common.ModelValidation;
import data.models.employee.Employee;
import data.models.transportservices.TransportService;
import data.models.vehicles.Vehicle;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tranposrt_companies")
public class TransportCompany extends BaseModel {

    private String name;
    private Set<Employee> employees= new HashSet<>();
    private Set<Vehicle> vehicles= new HashSet<>();
    private Set<TransportService> transportServices= new HashSet<>();

    public TransportCompany() {
    }

    public TransportCompany(String name) {
        this();
        this.name = name;
    }

    @Column(name = "transport_company_name", nullable = false, length = ModelValidation.NAME_LENGTH)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // One-to-many relationship with Employee
    @OneToMany(mappedBy = "transportCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    // One-to-many relationship with Vehicle
    @OneToMany(mappedBy = "transportCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(Set<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    // One-to-many relationship with TransportService
    @OneToMany(mappedBy = "transportCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<TransportService> getTransportServices() {
        return transportServices;
    }

    public void setTransportServices(Set<TransportService> transportServices) {
        this.transportServices = transportServices;
    }
}
