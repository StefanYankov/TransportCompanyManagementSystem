package data.common;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
public abstract class BaseModel implements IAuditInfo {

    private Long id;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;
    @Version
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "created_on", nullable = false, updatable = false)
    @Override
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    @Override
    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    @Column(name = "modified_on")
    @Override
    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    @Override
    public void setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @PrePersist
    public void onPrePersist() {
        this.createdOn = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.modifiedOn = LocalDateTime.now();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Compares this entity to another based on their IDs.
     * Two entities are equal if they are of the same class and have the same non-null ID.
     *
     * @param o the object to compare with
     * @return true if the entities are equal by ID, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseModel that = (BaseModel) o;
       // return id != null && Objects.equals(id, that.id);
        return id != null ? Objects.equals(id, that.id) : super.equals(o);

    }

    /**
     * Generates a hash code based on the entity's ID.
     * If the ID is null, returns a default hash to avoid inconsistencies.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 31; // Arbitrary constant for null ID

    }
}