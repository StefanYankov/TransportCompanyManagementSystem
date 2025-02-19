package services.data.dto.employees;

import data.common.ModelValidation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public abstract class EmployeeCreateDto {

    @NotBlank(message = ModelValidation.FIRST_NAME_IS_REQUIRED)
    @Size(min = ModelValidation.MIN_NAME_LENGTH, max = ModelValidation.MAX_NAME_LENGTH)
    private String firstName;

    @NotBlank(message = ModelValidation.LAST_NAME_IS_REQUIRED)
    @Size(min = ModelValidation.MIN_NAME_LENGTH, max = ModelValidation.MAX_NAME_LENGTH)
    private String familyName;

    @NotNull(message = ModelValidation.SALARY_IS_REQUIRED)
    @DecimalMin(value = ModelValidation.MINIMUM_ALLOWED_SALARY, message = ModelValidation.SALARY_CANNOT_BE_A_NEGATIVE_VALUE)
    private BigDecimal salary;

    private Long transportCompanyId;
    public EmployeeCreateDto() {}

    public EmployeeCreateDto(String firstName, String familyName, BigDecimal salary, Long transportCompanyId) {
        this.firstName = firstName;
        this.familyName = familyName;
        this.salary = salary;
        this.transportCompanyId = transportCompanyId;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Long getTransportCompanyId() {
        return transportCompanyId;
    }

    public void setTransportCompanyId(Long transportCompanyId) {
        this.transportCompanyId = transportCompanyId;
    }
}
