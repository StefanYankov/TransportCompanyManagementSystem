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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TransportPassengersServiceViewDTO{");
        sb.append("numberOfPassengers=").append(numberOfPassengers);
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