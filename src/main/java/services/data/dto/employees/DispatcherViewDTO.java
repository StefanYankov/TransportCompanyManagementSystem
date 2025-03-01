package services.data.dto.employees;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class DispatcherViewDTO extends EmployeeViewDTO {

    private Set<Long> supervisedDriverIds = new HashSet<>();

    public DispatcherViewDTO() {
    }


    public Set<Long> getSupervisedDriverIds() {
        return supervisedDriverIds;
    }

    public void setSupervisedDriverIds(Set<Long> supervisedDriverIds) {
        this.supervisedDriverIds = supervisedDriverIds;
    }
}