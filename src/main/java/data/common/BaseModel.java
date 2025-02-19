package data.common;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
public abstract class BaseModel implements IAuditInfo {

    private Long id;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

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
}