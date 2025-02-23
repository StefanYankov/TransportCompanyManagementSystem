package services.data.dto.employees;

import java.math.BigDecimal;
import java.util.Set;

public class DispatcherViewDTO extends EmployeeViewDTO {

    private Set<DriverViewDTO> supervisedDrivers;

    public DispatcherViewDTO() {
    }

    public DispatcherViewDTO(Long id, String firstName, String familyName, BigDecimal salary, Long transportCompanyId, Set<DriverViewDTO> supervisedDrivers) {
        super(id, firstName, familyName, salary, transportCompanyId);
        this.supervisedDrivers = supervisedDrivers;
    }

    public Set<DriverViewDTO> getSupervisedDrivers() {
        return supervisedDrivers;
    }

    public void setSupervisedDrivers(Set<DriverViewDTO> supervisedDrivers) {
        this.supervisedDrivers = supervisedDrivers;
    }
}