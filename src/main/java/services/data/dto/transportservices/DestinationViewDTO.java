package services.data.dto.transportservices;

public class DestinationViewDTO {
    private Long id;
    private String startingLocation;
    private String endingLocation;

    public DestinationViewDTO() {}

    public DestinationViewDTO(Long id, String startingLocation, String endingLocation) {
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