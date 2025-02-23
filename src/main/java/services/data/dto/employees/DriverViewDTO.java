package services.data.dto.employees;

import java.math.BigDecimal;
import java.util.Set;

public class DriverViewDTO extends EmployeeViewDTO {
    private Long dispatcherId;
    private Set<Long> driverQualificationIds;

    public DriverViewDTO() {
    }
    public DriverViewDTO(Long id, String firstName, String familyName, BigDecimal salary, Long transportCompanyId, Long dispatcherId, Set<Long> driverQualificationIds) {
        super(id, firstName, familyName, salary, transportCompanyId);
        this.dispatcherId = dispatcherId;
        this.driverQualificationIds = driverQualificationIds;
    }

    public Long getDispatcherId() {
        return dispatcherId;
    }

    public void setDispatcherId(Long dispatcherId) {
        this.dispatcherId = dispatcherId;
    }

    public Set<Long> getDriverQualificationIds() {
        return driverQualificationIds;
    }

    public void setDriverQualificationIds(Set<Long> driverQualificationIds) {
        this.driverQualificationIds = driverQualificationIds;
    }
}