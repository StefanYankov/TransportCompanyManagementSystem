package services.data.dto.vehicles;

public class VanViewDTO extends TransportPeopleVehicleViewDTO {

    private Boolean hasPassengerOverheadStorage;

    public VanViewDTO() {
    }

    public VanViewDTO(Long id, String registrationPlate, Long transportCompanyId, Integer maxPassengerCapacity, Boolean hasPassengerOverheadStorage) {
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