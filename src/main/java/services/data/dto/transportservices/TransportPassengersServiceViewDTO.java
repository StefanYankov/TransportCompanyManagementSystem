package services.data.dto.transportservices;

public class TransportPassengersServiceViewDTO extends TransportServiceViewDTO {

    private int numberOfPassengers;

    public TransportPassengersServiceViewDTO() {
    }

    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }
}