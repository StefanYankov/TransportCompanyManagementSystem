package services.data.dto.vehicles;

public class VanCreateDTO extends TransportPeopleVehicleCreateDTO {

    private Boolean hasPassengerOverheadStorage;

    public VanCreateDTO() {
    }

    public VanCreateDTO(String registrationPlate, Long transportCompanyId, Integer maxPassengerCapacity, Boolean hasPassengerOverheadStorage) {
        super(registrationPlate, transportCompanyId, maxPassengerCapacity);
        this.hasPassengerOverheadStorage = hasPassengerOverheadStorage;
    }

    public Boolean getHasPassengerOverheadStorage() {
        return hasPassengerOverheadStorage;
    }

    public void setHasPassengerOverheadStorage(Boolean hasPassengerOverheadStorage) {
        this.hasPassengerOverheadStorage = hasPassengerOverheadStorage;
    }
}