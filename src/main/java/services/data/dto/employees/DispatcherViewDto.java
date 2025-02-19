package services.data.dto.employees;

import java.math.BigDecimal;
import java.util.Set;

public class DispatcherViewDto extends EmployeeViewDto {

    private Set<Long> supervisedDriverIds;

    public DispatcherViewDto() {
    }

    public DispatcherViewDto(Long id, String firstName, String familyName, BigDecimal salary, Long transportCompanyId, Set<Long> supervisedDriverIds) {
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