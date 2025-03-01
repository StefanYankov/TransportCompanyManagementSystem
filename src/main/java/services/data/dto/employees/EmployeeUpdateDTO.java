package services.data.dto.employees;

import data.common.ModelValidation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;


public abstract class EmployeeUpdateDTO {

    private int version;

    @NotNull(message = ModelValidation.ID_REQUIRED)
    private Long id;

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

    public EmployeeUpdateDTO() {
    }

    public EmployeeUpdateDTO(long id, String firstName, String familyName, BigDecimal salary, Long transportCompanyId) {
        this.id = id;
        this.firstName = firstName;
        this.familyName = familyName;
        this.salary = salary;
        this.transportCompanyId = transportCompanyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
