package data.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass // will not have a separate table.
public abstract class BaseDeletableModel<TKey> extends BaseModel<TKey> implements IDeletableEntity {

    private boolean isDeleted;
    private LocalDateTime deletedOn;

    // isDeleted Getter and Setter with JPA Annotation
    @Column(name = "is_deleted", nullable = false)
    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }

    // deletedOn Getter and Setter with JPA Annotation
    @Column(name = "deleted_on")
    @Override
    public LocalDateTime getDeletedOn() {
        return deletedOn;
    }

    @Override
    public void setDeletedOn(LocalDateTime deletedOn) {
        this.deletedOn = deletedOn;
    }
}
