package services.data.dto.transportcompany;

import data.common.ModelValidation;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class TransportCompanyUpdateDto {

    private long id;

    @NotBlank(message = ModelValidation.NAME_IS_REQUIRED)
    @Length(min = ModelValidation.MIN_NAME_LENGTH, max = ModelValidation.MAX_NAME_LENGTH)
    private String companyName;

    @NotBlank(message = ModelValidation.ADDRESS_IS_REQUIRED)
    @Length(min = ModelValidation.MIN_ADDRESS_LENGTH, max = ModelValidation.MAX_ADDRESS_LENGTH)
    private String address;

    public TransportCompanyUpdateDto() {
    }

    public TransportCompanyUpdateDto(long id, String companyName, String address) {
        this.id = id;
        this.companyName = companyName;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
