package services.data.dto.vehicles;

public class VanUpdateDto extends TransportPeopleVehicleUpdateDto {

    private Boolean hasPassengerOverheadStorage;

    public VanUpdateDto() {
    }

    public VanUpdateDto(Long id, String registrationPlate, Long colourId, Long transportCompanyId, Integer maxPassengerCapacity, Boolean hasPassengerOverheadStorage) {
        super(id, registrationPlate, colourId, transportCompanyId, maxPassengerCapacity);
        this.hasPassengerOverheadStorage = hasPassengerOverheadStorage;
    }

    public Boolean getHasPassengerOverheadStorage() {
        return hasPassengerOverheadStorage;
    }

    public void setHasPassengerOverheadStorage(Boolean hasPassengerOverheadStorage) {
        this.hasPassengerOverheadStorage = hasPassengerOverheadStorage;
    }
}