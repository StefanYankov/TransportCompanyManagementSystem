package services.data.dto.employees;

import java.math.BigDecimal;
import java.util.Set;

public class DispatcherUpdateDto extends EmployeeUpdateDto {

    private Set<Long> supervisedDriverIds;

    public DispatcherUpdateDto() {
    }

    public DispatcherUpdateDto(Set<Long> supervisedDriverIds) {
        this.supervisedDriverIds = supervisedDriverIds;
    }

    public DispatcherUpdateDto(long id, String firstName, String familyName, BigDecimal salary, Long transportCompanyId, Set<Long> supervisedDriverIds) {
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