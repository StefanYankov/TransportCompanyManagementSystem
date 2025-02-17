package data.models.employee;

import data.common.BaseModel;
import jakarta.persistence.*;

@Entity
@Table(name = "driver_qualifications")
public class DriverQualification extends BaseModel {

    private String name;
    private String description;

    public DriverQualification() {
    }

    @Column(name = "qualification_name", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String qualificationName) {
        this.name = qualificationName;
    }

    @Column(name = "description", nullable = false)

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
