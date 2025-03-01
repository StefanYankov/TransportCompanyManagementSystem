package services.data.dto.vehicles;

import data.common.ModelValidation;
import jakarta.validation.constraints.*;

public abstract class VehicleUpdateDTO {

    private int version;
    @NotNull(message = ModelValidation.ID_REQUIRED)
    private Long id;

    @NotBlank(message = ModelValidation.REGISTRATION_PLATE_REQUIRED)
    @Size(max = ModelValidation.MAX_REGISTRATION_PLATE_LENGTH, message = ModelValidation.REGISTRATION_PLATE_TOO_LONG)
    private String registrationPlate;

    @NotNull(message = ModelValidation.ID_REQUIRED)
    private Long transportCompanyId;

    public VehicleUpdateDTO() {
    }

    public VehicleUpdateDTO(Long id, String registrationPlate, Long transportCompanyId) {
        this.id = id;
        this.registrationPlate = registrationPlate;
        this.transportCompanyId = transportCompanyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}