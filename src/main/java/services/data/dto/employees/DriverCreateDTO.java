package services.data.dto.employees;

import java.math.BigDecimal;
import java.util.Set;

public class DriverCreateDTO extends EmployeeCreateDTO {

    private Long dispatcherId;
    private Set<Long> qualificationIds;

    public DriverCreateDTO() {
    }

    public DriverCreateDTO(String firstName, String familyName, BigDecimal salary, Long transportCompanyId, Long dispatcherId, Set<Long> qualificationIds) {
        super(firstName, familyName, salary, transportCompanyId);
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