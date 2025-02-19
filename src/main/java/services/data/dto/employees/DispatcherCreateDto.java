package services.data.dto.employees;

import java.math.BigDecimal;
import java.util.Set;


public class DispatcherCreateDto extends EmployeeCreateDto {

    private Set<Long> supervisedDriverIds;

    public DispatcherCreateDto() {
    }

    public DispatcherCreateDto(Set<Long> supervisedDriverIds) {
        this.supervisedDriverIds = supervisedDriverIds;
    }

    public DispatcherCreateDto(String firstName, String familyName, BigDecimal salary, Long transportCompanyId, Set<Long> supervisedDriverIds) {
        super(firstName, familyName, salary, transportCompanyId);
        this.supervisedDriverIds = supervisedDriverIds;
    }

    public Set<Long> getSupervisedDriverIds() {
        return supervisedDriverIds;
    }

    public void setSupervisedDriverIds(Set<Long> supervisedDriverIds) {
        this.supervisedDriverIds = supervisedDriverIds;
    }
}