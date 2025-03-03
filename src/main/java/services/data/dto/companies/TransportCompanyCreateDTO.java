package services.data.dto.companies;

import data.common.ModelValidation;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

public class TransportCompanyCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = ModelValidation.NAME_IS_REQUIRED)
    @Length(min = ModelValidation.MIN_NAME_LENGTH, max = ModelValidation.MAX_NAME_LENGTH)
    private String name;

    @NotBlank(message = ModelValidation.ADDRESS_IS_REQUIRED)
    @Length(min = ModelValidation.MIN_ADDRESS_LENGTH, max = ModelValidation.MAX_ADDRESS_LENGTH)
    private String address;

    public TransportCompanyCreateDTO() {
    }

    public TransportCompanyCreateDTO(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
