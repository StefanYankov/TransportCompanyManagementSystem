package data.models.employee;

import data.common.BaseModel;
import data.common.ModelValidation;
import data.common.annotations.NonNegativeBigDecimal;
import data.models.TransportCompany;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "employees")
public abstract class Employee extends BaseModel {
    private String firstName;
    private String familyName;
    private BigDecimal salary;
    private TransportCompany transportCompany;

    public Employee() {
    }

    @Column(name = "first_name", nullable = false, length = ModelValidation.NAME_LENGTH)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    @Column(precision = ModelValidation.DECIMAL_PRECISION, scale = ModelValidation.DECIMAL_SCALE)
    @NonNegativeBigDecimal(message = ModelValidation.SALARY_CANNOT_BE_A_NEGATIVE_VALUE)
    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    @Column(name = "family_name", nullable = false, length = ModelValidation.NAME_LENGTH)
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @ManyToOne
    @JoinColumn(name = "transport_company_id", nullable = false)
    public TransportCompany getTransportCompany() {
        return transportCompany;
    }

    public void setTransportCompany(TransportCompany transportCompany) {
        this.transportCompany = transportCompany;
    }
}
