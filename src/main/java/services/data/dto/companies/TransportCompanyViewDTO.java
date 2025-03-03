package services.data.dto.companies;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class TransportCompanyViewDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    private long id;
    private String name;
    private String address;
    private Set<Long> employeeIds;
    private Set<Long> transportServiceIds;
    private Set<Long> vehicleIds;

    public TransportCompanyViewDTO() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(Set<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public Set<Long> getTransportServiceIds() {
        return transportServiceIds;
    }

    public void setTransportServiceIds(Set<Long> transportServiceIds) {
        this.transportServiceIds = transportServiceIds;
    }

    public Set<Long> getVehicleIds() {
        return vehicleIds;
    }

    public void setVehicleIds(Set<Long> vehicleIds) {
        this.vehicleIds = vehicleIds;
    }

    @Override
    public String toString() {
        return "TransportCompanyViewDTO{" +
                "id=" + id +
                ", companyName='" + name + '\'' +
                ", address='" + address + '\'' +
                ", employeeIds=" + employeeIds +
                '}';
    }
}