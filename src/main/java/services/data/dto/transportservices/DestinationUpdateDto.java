package services.data.dto.transportservices;

import data.common.ModelValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DestinationUpdateDto {
    private Long id;

    @NotBlank(message = ModelValidation.DESTINATION_CANNOT_BLANK)
    @Size(max = ModelValidation.MAX_ADDRESS_LENGTH, message = ModelValidation.DESTINATION_ADDRESS_MAX_LENGTH_EXCEEDED)
    private String startingLocation;

    @NotBlank(message = ModelValidation.DESTINATION_CANNOT_BLANK)
    @Size(max = ModelValidation.MAX_ADDRESS_LENGTH, message = ModelValidation.DESTINATION_ADDRESS_MAX_LENGTH_EXCEEDED)
    private String endingLocation;

    public DestinationUpdateDto() {}

    public DestinationUpdateDto(Long id, String startingLocation, String endingLocation) {
        this.id = id;
        this.startingLocation = startingLocation;
        this.endingLocation = endingLocation;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    public String getEndingLocation() {
        return endingLocation;
    }

    public void setEndingLocation(String endingLocation) {
        this.endingLocation = endingLocation;
    }
}