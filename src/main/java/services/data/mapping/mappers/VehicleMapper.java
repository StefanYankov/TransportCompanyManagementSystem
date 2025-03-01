package services.data.mapping.mappers;

import data.models.TransportCompany;
import data.models.vehicles.Bus;
import data.models.vehicles.Truck;
import data.models.vehicles.Van;
import data.models.vehicles.Vehicle;
import data.repositories.IGenericRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import services.data.dto.vehicles.VehicleViewDTO;

public class VehicleMapper {
    private final ModelMapper modelMapper;
    private final BusMapper busMapper;
    private final TruckMapper truckMapper;
    private final VanMapper vanMapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;

    public VehicleMapper(IGenericRepository<TransportCompany, Long> companyRepo) {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE);
        this.companyRepo = companyRepo;
        this.busMapper = new BusMapper(companyRepo);
        this.truckMapper = new TruckMapper(companyRepo);
        this.vanMapper = new VanMapper(companyRepo);
    }

    public VehicleViewDTO toViewDTO(Vehicle entity) {
        if (entity instanceof Bus) {
            return busMapper.toViewDTO((Bus) entity);
        } else if (entity instanceof Truck) {
            return truckMapper.toViewDTO((Truck) entity);
        } else if (entity instanceof Van) {
            return vanMapper.toViewDTO((Van) entity);
        } else {
            throw new IllegalArgumentException("Unknown Vehicle subclass: " + entity.getClass().getName());
        }
    }
}