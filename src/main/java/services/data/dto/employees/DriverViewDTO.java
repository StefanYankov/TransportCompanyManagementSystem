package services.data.dto.employees;

import java.util.HashSet;
import java.util.Set;

public class DriverViewDTO extends EmployeeViewDTO {

    private Long dispatcherId;
    private Set<Long> qualificationIds = new HashSet<>();

    public DriverViewDTO() {
    }


    public Long getDispatcherId() {
        return dispatcherId;
    }

    public void setDispatcherId(Long dispatcherId) {
        this.dispatcherId = dispatcherId;
    }

    public Set<Long> getQualificationIds() {
        return qualificationIds;
    }

    public void setQualificationIds(Set<Long> qualificationIds) {
        this.qualificationIds = qualificationIds;
    }

    @Override
    public String toString() {
        return "DriverViewDTO{" +
                "dispatcherId=" + dispatcherId +
                ", qualificationIds=" + qualificationIds +
                "} " + super.toString();
    }
}