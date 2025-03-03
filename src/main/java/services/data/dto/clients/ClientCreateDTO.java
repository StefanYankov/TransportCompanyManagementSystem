package services.data.dto.clients;

import data.common.ModelValidation;
import data.common.annotations.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public class ClientCreateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = ModelValidation.CLIENT_NAME_NOT_BLANK)
    @Size(max = ModelValidation.CLIENT_NAME_MAX_NAME_LENGTH, message = ModelValidation.CLIENT_NAME_LENGTH_EXCEEDED)
    private String name;

    @NotBlank(message = ModelValidation.TELEPHONE_CANNOT_BE_BLANK)
    @Size(max = ModelValidation.TELEPHONE_LENGTH, message = ModelValidation.TELEPHONE_NUMBER_LENGTH_EXCEEDED)
    private String telephone;

    @NotBlank(message = ModelValidation.EMAIL_CANNOT_BE_BLANK)
    @ValidEmail(message = ModelValidation.INVALID_EMAIL_MESSAGE)
    private String email;

    public ClientCreateDTO() {
    }

    public ClientCreateDTO(String name, String telephone, String email) {
        this.name = name;
        this.telephone = telephone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
