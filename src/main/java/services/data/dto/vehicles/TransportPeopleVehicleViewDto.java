package services.data.dto.vehicles;

public abstract class TransportPeopleVehicleViewDto extends VehicleViewDto {

    private Integer maxPassengerCapacity;

    public TransportPeopleVehicleViewDto() {
    }

    public TransportPeopleVehicleViewDto(Long id, String registrationPlate, Long colourId, Long transportCompanyId, Integer maxPassengerCapacity) {
        super(id, registrationPlate, colourId, transportCompanyId);
        this.maxPassengerCapacity = maxPassengerCapacity;
    }

    public Integer getMaxPassengerCapacity() {
        return maxPassengerCapacity;
    }

    public void setMaxPassengerCapacity(Integer maxPassengerCapacity) {
        this.maxPassengerCapacity = maxPassengerCapacity;
    }
}