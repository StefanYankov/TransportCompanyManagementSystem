package services.data.dto.companies;

import java.util.Set;

public class TransportCompanyViewDTO {

    private long id;
    private String name;
    private String address;
    private Set<Long> employeeIds;

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