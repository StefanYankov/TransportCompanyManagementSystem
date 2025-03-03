package services.data.dto.employees;

import data.common.ModelValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public class QualificationCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = ModelValidation.QUALIFICATION_NAME_NOT_BLANK)
    @Size(max = ModelValidation.QUALIFICATION_NAME_MAX_NAME_LENGTH, message = ModelValidation.QUALIFICATION_NAME_LENGTH_EXCEEDED)
    private String name;

    @NotBlank(message = ModelValidation.QUALIFICATION_DESCRIPTION_NOT_BLANK)
    @Size(max = ModelValidation.MAX_DESCRIPTION_LENGTH, message = ModelValidation.QUALIFICATION_DESCRIPTION_LENGTH_EXCEEDED)
    private String description;

    public QualificationCreateDTO() {
    }

    public QualificationCreateDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}