package services.data.dto.employees;

import services.data.dto.companies.TransportCompanyViewDTO;

import java.math.BigDecimal;
import java.util.Set;

public class DriverCreateDTO extends EmployeeCreateDTO {

    private DispatcherViewDTO dispatcher;

    private Set<QualificationViewDTO> qualifications;

    public DriverCreateDTO() {
    }

    public DriverCreateDTO(String firstName, String familyName, BigDecimal salary, TransportCompanyViewDTO transportCompany, DispatcherViewDTO dispatcher, Set<QualificationViewDTO> qualifications) {
        super(firstName, familyName, salary, transportCompany);
        this.dispatcher = dispatcher;
        this.qualifications = qualifications;
    }

    public DispatcherViewDTO getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(DispatcherViewDTO dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Set<QualificationViewDTO> getQualifications() {
        return qualifications;
    }

    public void setQualifications(Set<QualificationViewDTO> qualifications) {
        this.qualifications = qualifications;
    }
}