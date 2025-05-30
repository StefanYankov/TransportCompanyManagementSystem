package services.data.dto.employees;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import data.common.ModelValidation;

public class QualificationUpdateDTO {

    private Long id;

    @NotBlank(message = ModelValidation.QUALIFICATION_NAME_NOT_BLANK)
    @Size(max = ModelValidation.QUALIFICATION_NAME_MAX_NAME_LENGTH, message = ModelValidation.QUALIFICATION_NAME_LENGTH_EXCEEDED)
    private String name;

    @NotBlank(message = ModelValidation.QUALIFICATION_DESCRIPTION_NOT_BLANK)
    @Size(max = ModelValidation.MAX_DESCRIPTION_LENGTH, message = ModelValidation.QUALIFICATION_DESCRIPTION_LENGTH_EXCEEDED)
    private String description;

    public QualificationUpdateDTO() {
    }

    public QualificationUpdateDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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