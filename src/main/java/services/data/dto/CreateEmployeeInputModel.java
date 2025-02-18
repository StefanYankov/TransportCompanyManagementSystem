package services.data.dto;

import data.common.ModelValidation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public abstract class CreateEmployeeInputModel {

    @NotBlank(message = ModelValidation.FIRST_NAME_IS_REQUIRED)
    @Length(min = ModelValidation.MIN_NAME_LENGTH, max = ModelValidation.MAX_NAME_LENGTH)
    private String firstName;

    @NotBlank(message = ModelValidation.LAST_NAME_IS_REQUIRED)
    @Length(min = ModelValidation.MIN_NAME_LENGTH, max = ModelValidation.MAX_NAME_LENGTH)
    private String familyName;

    @NotNull(message = ModelValidation.SALARY_IS_REQUIRED)
    @DecimalMin(value = ModelValidation.MINIMUM_ALLOWED_SALARY, inclusive = false, message = ModelValidation.SALARY_CANNOT_BE_A_NEGATIVE_VALUE)
    private BigDecimal salary;

    @NotNull(message = ModelValidation.TRANSPORT_COMPANY_IS_REQUIRED)
    private long transportCompany;

    public CreateEmployeeInputModel() {
    }

    public CreateEmployeeInputModel(
            String firstName,
            String familyName,
            BigDecimal salary,
            long transportCompany
    ) {
        this.firstName = firstName;
        this.familyName = familyName;
        this.salary = salary;
        this.transportCompany = transportCompany;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Length(min = 1, max = ModelValidation.MAX_NAME_LENGTH)
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

    public long getTransportCompany() {
        return transportCompany;
    }

    public void setTransportCompany(long transportCompany) {
        this.transportCompany = transportCompany;
    }
}
