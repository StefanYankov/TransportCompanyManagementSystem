package services.data.dto.employees;

import java.math.BigDecimal;
import java.util.Set;

public class DispatcherUpdateDTO extends EmployeeUpdateDTO {

    private Set<Long> supervisedDriverIds;

    public DispatcherUpdateDTO() {
    }

    public DispatcherUpdateDTO(Set<Long> supervisedDriverIds) {
        this.supervisedDriverIds = supervisedDriverIds;
    }

    public DispatcherUpdateDTO(long id, String firstName, String familyName, BigDecimal salary, Long transportCompanyId, Set<Long> supervisedDriverIds) {
        super(id, firstName, familyName, salary, transportCompanyId);
        this.supervisedDriverIds = supervisedDriverIds;
    }

    public Set<Long> getSupervisedDriverIds() {
        return supervisedDriverIds;
    }

    public void setSupervisedDriverIds(Set<Long> supervisedDriverIds) {
        this.supervisedDriverIds = supervisedDriverIds;
    }
}