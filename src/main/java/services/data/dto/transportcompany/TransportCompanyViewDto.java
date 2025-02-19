package services.data.dto.transportcompany;

import services.data.dto.employees.EmployeeViewDto;

import java.util.Set;

public class TransportCompanyViewDto {

    public long id;
    private String companyName;
    private String address;
    private Set<EmployeeViewDto> employees;

    public TransportCompanyViewDto() {
    }

    public TransportCompanyViewDto(String address, String companyName, long id) {
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


}
