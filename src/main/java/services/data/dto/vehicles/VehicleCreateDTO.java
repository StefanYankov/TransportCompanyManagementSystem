package services.data.dto.vehicles;

import data.common.ModelValidation;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;

public abstract class VehicleCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = ModelValidation.REGISTRATION_PLATE_REQUIRED)
    @Size(max = ModelValidation.MAX_REGISTRATION_PLATE_LENGTH,  message = ModelValidation.REGISTRATION_PLATE_TOO_LONG)
    private String registrationPlate;

    @NotNull(message = ModelValidation.ID_REQUIRED)
    private Long transportCompanyId;

    public VehicleCreateDTO() {
    }

    public VehicleCreateDTO(String registrationPlate, Long transportCompanyId) {
        this.registrationPlate = registrationPlate;
        this.transportCompanyId = transportCompanyId;
    }

    public String getRegistrationPlate() {
        return registrationPlate;
    }

    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }


    public Long getTransportCompanyId() {
        return transportCompanyId;
    }

    public void setTransportCompanyId(Long transportCompanyId) {
        this.transportCompanyId = transportCompanyId;
    }
}