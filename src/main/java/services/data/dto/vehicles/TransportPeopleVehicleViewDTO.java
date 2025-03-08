package services.data.dto.vehicles;

public abstract class TransportPeopleVehicleViewDTO extends VehicleViewDTO {

    private Integer maxPassengerCapacity;

    public TransportPeopleVehicleViewDTO() {
    }

    public TransportPeopleVehicleViewDTO(Long id, String registrationPlate, Long transportCompanyId, Integer maxPassengerCapacity) {
        super(id, registrationPlate, transportCompanyId);
        this.maxPassengerCapacity = maxPassengerCapacity;
    }

    public Integer getMaxPassengerCapacity() {
        return maxPassengerCapacity;
    }

    public void setMaxPassengerCapacity(Integer maxPassengerCapacity) {
        this.maxPassengerCapacity = maxPassengerCapacity;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("maxPassengerCapacity=").append(maxPassengerCapacity);
        sb.append('}');
        sb.append(super.toString());
        return sb.toString();
    }
}