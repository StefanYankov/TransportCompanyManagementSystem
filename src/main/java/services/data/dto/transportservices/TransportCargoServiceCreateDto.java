package services.data.dto.transportservices;

import java.math.BigDecimal;

public class TransportCargoServiceCreateDto extends TransportServiceCreateDto {
    // TODO: add data validation
    public BigDecimal weightInKilograms;
    public int lengthInCentimeters;
    public int widthInCentimeters;
    public int heightInCentimeters;
    public String description;

    public TransportCargoServiceCreateDto() {
    }

    public TransportCargoServiceCreateDto(String description, int heightInCentimeters, int lengthInCentimeters, BigDecimal weightInKilograms, int widthInCentimeters) {
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
}