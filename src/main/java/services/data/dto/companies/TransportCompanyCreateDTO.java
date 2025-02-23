package services.data.dto.companies;

import data.common.ModelValidation;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class TransportCompanyCreateDTO {

    @NotBlank(message = ModelValidation.NAME_IS_REQUIRED)
    @Length(min = ModelValidation.MIN_NAME_LENGTH, max = ModelValidation.MAX_NAME_LENGTH)
    private String companyName;

    @NotBlank(message = ModelValidation.ADDRESS_IS_REQUIRED)
    @Length(min = ModelValidation.MIN_ADDRESS_LENGTH, max = ModelValidation.MAX_ADDRESS_LENGTH)
    private String address;

    public TransportCompanyCreateDTO() {
    }

    public TransportCompanyCreateDTO(String companyName, String address) {
        this.companyName = companyName;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
