package services.data.dto.employees;

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

    @Override
    public String toString() {
        return "DispatcherViewDTO{" +
                "supervisedDriverIds=" + supervisedDriverIds +
                "} " + super.toString();
    }
}