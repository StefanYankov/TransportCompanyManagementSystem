package services.data.dto.employees;

import java.math.BigDecimal;
import java.util.Set;

public class DriverUpdateDto extends EmployeeUpdateDto {

    private Long dispatcherId;
    private Set<Long> driverQualificationIds;

    public DriverUpdateDto() {
    }


    public DriverUpdateDto(long id, String firstName, String familyName, BigDecimal salary, Long transportCompanyId, Long dispatcherId, Set<Long> driverQualificationIds) {
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