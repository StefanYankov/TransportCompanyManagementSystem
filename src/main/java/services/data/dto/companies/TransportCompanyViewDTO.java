package services.data.dto.companies;

import services.data.dto.employees.EmployeeViewDTO;

import java.util.Set;

public class TransportCompanyViewDTO {

    public long id;
    private String companyName;
    private String address;
    private Set<EmployeeViewDTO> employees;

    public TransportCompanyViewDTO() {
    }

    public TransportCompanyViewDTO(String address, String companyName, long id) {
        this.address = address;
        this.companyName = companyName;
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TransportCompanyViewDTO{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", address='" + address + '\'' +
                ", employees=" + employees +
                '}';
    }
}
