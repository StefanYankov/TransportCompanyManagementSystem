package services.data.dto.vehicles;

import data.common.ModelValidation;
import jakarta.validation.constraints.*;

public abstract class VehicleCreateDto {

    @NotBlank(message = ModelValidation.REGISTRATION_PLATE_REQUIRED)
    @Size(max = ModelValidation.MAX_REGISTRATION_PLATE_LENGTH,  message = ModelValidation.REGISTRATION_PLATE_TOO_LONG)
    private String registrationPlate;

    @NotNull(message = ModelValidation.ID_REQUIRED)
    private Long colourId;

    @NotNull(message = ModelValidation.ID_REQUIRED)
    private Long transportCompanyId;

    public VehicleCreateDto() {
    }

    public VehicleCreateDto(String registrationPlate, Long colourId, Long transportCompanyId) {
        this.registrationPlate = registrationPlate;
        this.colourId = colourId;
        this.transportCompanyId = transportCompanyId;
    }

    public String getRegistrationPlate() {
        return registrationPlate;
    }

    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }

    public Long getColourId() {
        return colourId;
    }

    public void setColourId(Long colourId) {
        this.colourId = colourId;
    }

    public Long getTransportCompanyId() {
        return transportCompanyId;
    }

    public void setTransportCompanyId(Long transportCompanyId) {
        this.transportCompanyId = transportCompanyId;
    }
}