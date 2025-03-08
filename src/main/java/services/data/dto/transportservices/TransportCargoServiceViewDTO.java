package services.data.dto.transportservices;

import java.math.BigDecimal;

public class TransportCargoServiceViewDTO extends TransportServiceViewDTO {
    public BigDecimal weightInKilograms;
    public int lengthInCentimeters;
    public int widthInCentimeters;
    public int heightInCentimeters;
    public String description;

    public TransportCargoServiceViewDTO() {
    }

    public TransportCargoServiceViewDTO(String description, int heightInCentimeters, int lengthInCentimeters, BigDecimal weightInKilograms, int widthInCentimeters) {
        this.description = description;
        this.heightInCentimeters = heightInCentimeters;
        this.lengthInCentimeters = lengthInCentimeters;
        this.weightInKilograms = weightInKilograms;
        this.widthInCentimeters = widthInCentimeters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHeightInCentimeters() {
        return heightInCentimeters;
    }

    public void setHeightInCentimeters(int heightInCentimeters) {
        this.heightInCentimeters = heightInCentimeters;
    }

    public int getLengthInCentimeters() {
        return lengthInCentimeters;
    }

    public void setLengthInCentimeters(int lengthInCentimeters) {
        this.lengthInCentimeters = lengthInCentimeters;
    }

    public BigDecimal getWeightInKilograms() {
        return weightInKilograms;
    }

    public void setWeightInKilograms(BigDecimal weightInKilograms) {
        this.weightInKilograms = weightInKilograms;
    }

    public int getWidthInCentimeters() {
        return widthInCentimeters;
    }

    public void setWidthInCentimeters(int widthInCentimeters) {
        this.widthInCentimeters = widthInCentimeters;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TransportCargoServiceViewDTO{");
        sb.append("weightInKilograms=").append(weightInKilograms);
        sb.append(", lengthInCentimeters=").append(lengthInCentimeters);
        sb.append(", widthInCentimeters=").append(widthInCentimeters);
        sb.append(", heightInCentimeters=").append(heightInCentimeters);
        sb.append(", description='").append(description).append('\'');
        sb.append(", id=").append(id);
        sb.append(", transportCompanyId=").append(transportCompanyId);
        sb.append(", startingDate=").append(startingDate);
        sb.append(", endingDate=").append(endingDate);
        sb.append(", destinationId=").append(destinationId);
        sb.append(", clientId=").append(clientId);
        sb.append(", price=").append(price);
        sb.append(", isDelivered=").append(isDelivered);
        sb.append(", isPaid=").append(isPaid);
        sb.append(", vehicleId=").append(vehicleId);
        sb.append(", driverId=").append(driverId);
        sb.append('}');
        return sb.toString();
    }
}