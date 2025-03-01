package services.data.dto.companies;

import data.common.ModelValidation;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class TransportCompanyUpdateDTO {

    private Long id;

    private int version;

    @NotBlank(message = ModelValidation.NAME_IS_REQUIRED)
    @Length(min = ModelValidation.MIN_NAME_LENGTH, max = ModelValidation.MAX_NAME_LENGTH)
    private String name;

    @NotBlank(message = ModelValidation.ADDRESS_IS_REQUIRED)
    @Length(min = ModelValidation.MIN_ADDRESS_LENGTH, max = ModelValidation.MAX_ADDRESS_LENGTH)
    private String address;

    public TransportCompanyUpdateDTO() {
    }

    public TransportCompanyUpdateDTO(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
