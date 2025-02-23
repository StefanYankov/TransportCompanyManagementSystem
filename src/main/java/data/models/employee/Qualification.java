package data.models.employee;

import data.common.BaseModel;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "qualifications")
public class Qualification extends BaseModel {
    private String name;
    private String description;
    private Set<Driver> drivers = new HashSet<>();

    public Qualification() {
    }

    public Qualification(String name, String description) {
        this.name = name;
        this.description = description;
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

    @ManyToMany(mappedBy = "qualifications") // This is the other side of the relation
    public Set<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(Set<Driver> drivers) {
        this.drivers = drivers;
    }

}
