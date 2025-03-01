package data.models.employee;

import data.common.BaseModel;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @ManyToMany(mappedBy = "qualifications")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Set<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(Set<Driver> drivers) {
        this.drivers = drivers;
    }

}
