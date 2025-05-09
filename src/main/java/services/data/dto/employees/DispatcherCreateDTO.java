package services.data.dto.employees;

import java.math.BigDecimal;
import java.util.Set;

public class DispatcherCreateDTO extends EmployeeCreateDTO {

    private Set<Long> supervisedDriverIds;

    public DispatcherCreateDTO() {
    }

    public DispatcherCreateDTO(Set<Long> supervisedDriverIds) {
        this.supervisedDriverIds = supervisedDriverIds;
    }

    public DispatcherCreateDTO(String firstName, String familyName, BigDecimal salary, Long transportCompanyId, Set<Long> supervisedDriverIds) {
        super(firstName, familyName, salary,  transportCompanyId);
        this.supervisedDriverIds = supervisedDriverIds;
    }

    public Set<Long> getSupervisedDriverIds() {
        return supervisedDriverIds;
    }

    public void setSupervisedDriverIds(Set<Long> supervisedDriverIds) {
        this.supervisedDriverIds = supervisedDriverIds;
    }
}