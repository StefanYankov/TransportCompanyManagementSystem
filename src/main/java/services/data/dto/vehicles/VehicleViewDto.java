package services.data.dto.vehicles;

public abstract class VehicleViewDto {

    private Long id;
    private String registrationPlate;
    private Long colourId;
    private Long transportCompanyId;

    public VehicleViewDto() {
    }

    public VehicleViewDto(Long id, String registrationPlate, Long colourId, Long transportCompanyId) {
        this.id = id;
        this.registrationPlate = registrationPlate;
        this.colourId = colourId;
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