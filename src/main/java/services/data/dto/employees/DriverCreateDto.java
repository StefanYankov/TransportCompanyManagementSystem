package services.data.dto.employees;

import java.util.Set;

public class DriverCreateDto extends EmployeeCreateDto {

    private Long dispatcherId;

    private Set<Long> qualificationIds;

    public DriverCreateDto() {
    }

    public DriverCreateDto(Long dispatcherId, Set<Long> qualificationIds) {
        this.dispatcherId = dispatcherId;
        this.qualificationIds = qualificationIds;
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
}