package services.data.dto.vehicles;

import data.common.ModelValidation;
import jakarta.validation.constraints.NotNull;

public class VanUpdateDTO extends TransportPeopleVehicleUpdateDTO {

    @NotNull(message = ModelValidation.PASSENGER_OVERHEAD_STORAGE_CANNOT_BE_NULL)
    private Boolean hasPassengerOverheadStorage;

    public VanUpdateDTO() {
    }

    public VanUpdateDTO(Long id, String registrationPlate, Long transportCompanyId, Integer maxPassengerCapacity, Boolean hasPassengerOverheadStorage) {
        super(id, registrationPlate, transportCompanyId, maxPassengerCapacity);
        this.hasPassengerOverheadStorage = hasPassengerOverheadStorage;
    }

    public Boolean getHasPassengerOverheadStorage() {
        return hasPassengerOverheadStorage;
    }

    public void setHasPassengerOverheadStorage(Boolean hasPassengerOverheadStorage) {
        this.hasPassengerOverheadStorage = hasPassengerOverheadStorage;
    }
}