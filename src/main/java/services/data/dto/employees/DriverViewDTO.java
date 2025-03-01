package services.data.dto.employees;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class DriverViewDTO extends EmployeeViewDTO {

    private Long dispatcherId;
    private Set<Long> qualificationIds = new HashSet<>();

    public DriverViewDTO() {
    }

    public DriverViewDTO(Long id, String firstName, String familyName, BigDecimal salary, Long transportCompanyId, Long dispatcherId, Set<Long> qualificationIds) {
        super(id, firstName, familyName, salary, transportCompanyId);
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