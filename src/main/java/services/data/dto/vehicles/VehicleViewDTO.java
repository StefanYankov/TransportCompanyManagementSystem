package services.data.dto.vehicles;

public abstract class VehicleViewDTO {

    private Long id;
    private String registrationPlate;
    private Long transportCompanyId;

    public VehicleViewDTO() {
    }

    public VehicleViewDTO(Long id, String registrationPlate, Long transportCompanyId) {
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
}