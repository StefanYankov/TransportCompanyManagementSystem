package data.common;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseModel<TKey> implements IAuditInfo {

    private TKey id;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

    // ID Getter and Setter with JPA Annotations
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public TKey getId() {
        return id;
    }

    public void setId(TKey id) {
        this.id = id;
    }

    // CreatedOn Getter and Setter with JPA Annotations
    @Column(name = "created_on", nullable = false, updatable = false)
    @Override
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    @Override
    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    // ModifiedOn Getter and Setter with JPA Annotations
    @Column(name = "modified_on")
    @Override
    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    @Override
    public void setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    // Lifecycle Callbacks to Auto-Set Timestamps
    @PrePersist
    public void onPrePersist() {
        this.createdOn = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.modifiedOn = LocalDateTime.now();
    }
}
