package services.data.dto.vehicles;

public class VanCreateDto extends TransportPeopleVehicleCreateDto {

    private Boolean hasPassengerOverheadStorage;

    public VanCreateDto() {
    }

    public VanCreateDto(String registrationPlate, Long colourId, Long transportCompanyId, Integer maxPassengerCapacity, Boolean hasPassengerOverheadStorage) {
        super(registrationPlate, colourId, transportCompanyId, maxPassengerCapacity);
        this.hasPassengerOverheadStorage = hasPassengerOverheadStorage;
    }

    public Boolean getHasPassengerOverheadStorage() {
        return hasPassengerOverheadStorage;
    }

    public void setHasPassengerOverheadStorage(Boolean hasPassengerOverheadStorage) {
        this.hasPassengerOverheadStorage = hasPassengerOverheadStorage;
    }
}