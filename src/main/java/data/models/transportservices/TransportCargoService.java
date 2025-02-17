package data.models.transportservices;

import data.common.ModelValidation;
import data.common.annotations.NonNegativeBigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "transport_cargo_service")
public class TransportCargoService extends TransportService {

    private BigDecimal weightInKilograms;
    private int lengthInCentimeters;
    private int widthInCentimeters;
    private int heightInCentimeters;
    private String description;

    public TransportCargoService() {
    }

    @Column(name = "weight_in_kilograms", precision = ModelValidation.DECIMAL_PRECISION, scale = ModelValidation.DECIMAL_SCALE, nullable = false)
    @NonNegativeBigDecimal
    public BigDecimal getWeightInKilograms() {
        return weightInKilograms;
    }

    public void setWeightInKilograms(BigDecimal weightInKilograms) {
        this.weightInKilograms = weightInKilograms;
    }

    @Column(name = "length_in_cm", nullable = false)
    @Min(value = ModelValidation.MINIMUM_ALLOWED_DIMENSIONS, message = ModelValidation.INVALID_ALLOWED_DIMENSIONS_MESSAGE)
    public int getLengthInCentimeters() {
        return lengthInCentimeters;
    }

    public void setLengthInCentimeters(int lengthInCentimeters) {
        this.lengthInCentimeters = lengthInCentimeters;
    }

    @Column(name = "width_in_cm", nullable = false)
    @Min(value = ModelValidation.MINIMUM_ALLOWED_DIMENSIONS, message = ModelValidation.INVALID_ALLOWED_DIMENSIONS_MESSAGE)
    public int getWidthInCentimeters() {
        return widthInCentimeters;
    }

    public void setWidthInCentimeters(int widthInCentimeters) {
        this.widthInCentimeters = widthInCentimeters;
    }

    @Column(name = "height_in_cm", nullable = false)
    @Min(value = ModelValidation.MINIMUM_ALLOWED_DIMENSIONS, message = ModelValidation.INVALID_ALLOWED_DIMENSIONS_MESSAGE)
    public int getHeightInCentimeters() {
        return heightInCentimeters;
    }

    public void setHeightInCentimeters(int heightInCentimeters) {
        this.heightInCentimeters = heightInCentimeters;
    }

    @Column(name = "description", nullable = true)
    @Size(max = ModelValidation.DESCRIPTION_MAXIMUM_LENGTH, message = ModelValidation.DESCRIPTION_ABOVE_MAXIMUM_LENGTH)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
