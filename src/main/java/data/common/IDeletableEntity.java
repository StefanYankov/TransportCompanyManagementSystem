package data.common;

import java.time.LocalDateTime;

public interface IDeletableEntity {

    // Getters and Setters for IsDeleted
    boolean isDeleted();
    void setDeleted(boolean deleted);

    // Getters and Setters for DeletedOn
    LocalDateTime getDeletedOn();
    void setDeletedOn(LocalDateTime deletedOn);
}