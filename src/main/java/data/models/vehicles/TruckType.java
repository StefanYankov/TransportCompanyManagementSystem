package data.models.vehicles;

public enum TruckType {
    // Enum constants with more descriptive details
    BOX("Box Truck: A fully enclosed cargo area, commonly used for moving goods such as furniture, appliances, or packages."),
    FLATBED("Flatbed Truck: A truck with a flat, open bed, ideal for transporting large, heavy, or awkwardly shaped cargo like construction materials, machinery, or large equipment."),
    REFRIGERATED("Refrigerated Truck: A temperature-controlled vehicle designed for transporting perishable goods such as food, pharmaceuticals, and flowers, requiring constant refrigeration."),
    TANKER("Tanker Truck: A truck designed for hauling bulk liquids, including fuel, chemicals, water, or other liquid products, with a large cylindrical container."),
    TRACTOR("Tractor Truck: A heavy-duty vehicle typically used for long-distance hauling of trailers, containers, or large freight loads, often used for transporting cargo across states or countries.");

    // Descriptive details for each truck type
    private final String description;

    // Constructor to initialize the description
    TruckType(String description) {
        this.description = description;
    }

    // Getter for the description
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}