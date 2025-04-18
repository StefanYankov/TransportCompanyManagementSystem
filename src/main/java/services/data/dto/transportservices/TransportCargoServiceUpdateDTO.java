package services.data.dto.transportservices;

import data.common.ModelValidation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class TransportCargoServiceUpdateDTO extends TransportServiceUpdateDTO {

    @NotNull(message = ModelValidation.DIMENSION_REQUIRED)
    @DecimalMin(value = ModelValidation.MIN_CARGO_CAPACITY, message = ModelValidation.INVALID_ALLOWED_DIMENSIONS_MESSAGE)
    private BigDecimal weightInKilograms;

    @NotNull(message = ModelValidation.DIMENSION_REQUIRED)
    @Min(value = ModelValidation.MINIMUM_ALLOWED_DIMENSIONS, message = ModelValidation.INVALID_ALLOWED_DIMENSIONS_MESSAGE)
    private int lengthInCentimeters;

    @NotNull(message = ModelValidation.DIMENSION_REQUIRED)
    @Min(value = ModelValidation.MINIMUM_ALLOWED_DIMENSIONS, message = ModelValidation.INVALID_ALLOWED_DIMENSIONS_MESSAGE)
    private int widthInCentimeters;

    @NotNull(message = ModelValidation.DIMENSION_REQUIRED)
    @Min(value = ModelValidation.MINIMUM_ALLOWED_DIMENSIONS, message = ModelValidation.INVALID_ALLOWED_DIMENSIONS_MESSAGE)
    private int heightInCentimeters;

    @Size(max = ModelValidation.DESCRIPTION_MAXIMUM_LENGTH, message = ModelValidation.DESCRIPTION_ABOVE_MAXIMUM_LENGTH)
    private String description;

    public TransportCargoServiceUpdateDTO() {
    }

    public TransportCargoServiceUpdateDTO(String description, int heightInCentimeters, int lengthInCentimeters, BigDecimal weightInKilograms, int widthInCentimeters) {
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

