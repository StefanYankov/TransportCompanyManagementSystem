package services.data.dto.employees;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class EmployeeViewDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String firstName;
    private String familyName;
    private BigDecimal salary;
    private Long transportCompanyId;

    // Fix: Added public no-argument constructor
    public EmployeeViewDTO() {
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public Long getTransportCompanyId() { return transportCompanyId; }
    public void setTransportCompanyId(Long transportCompanyId) { this.transportCompanyId = transportCompanyId; }

    @Override
    public String toString() {
        return "EmployeeViewDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", salary=" + salary +
                ", transportCompanyId=" + transportCompanyId +
                '}';
    }
}