package data.common;

import java.time.LocalDateTime;

public interface IAuditInfo {

    // Getter and Setter for CreatedOn
    public LocalDateTime getCreatedOn();
    public void setCreatedOn(LocalDateTime createdOn);

    // Getter and Setter for ModifiedOn
    public LocalDateTime getModifiedOn();
    public void setModifiedOn(LocalDateTime modifiedOn);
}
