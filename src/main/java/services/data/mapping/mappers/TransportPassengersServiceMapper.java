package services.data.mapping.mappers;

import data.models.Client;
import data.models.transportservices.Destination;
import data.models.employee.Driver;
import data.models.TransportCompany;
import data.models.vehicles.Vehicle;
import data.models.transportservices.TransportPassengersService;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import services.data.dto.transportservices.TransportPassengersServiceCreateDTO;
import services.data.dto.transportservices.TransportPassengersServiceUpdateDTO;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;

public class TransportPassengersServiceMapper {
    private final ModelMapper modelMapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;
    private final IGenericRepository<Client, Long> clientRepo;
    private final IGenericRepository<Driver, Long> driverRepo;
    private final IGenericRepository<Vehicle, Long> vehicleRepo;
    private final IGenericRepository<Destination, Long> destinationRepo;

    public TransportPassengersServiceMapper(IGenericRepository<TransportCompany, Long> companyRepo,
                                            IGenericRepository<Client, Long> clientRepo,
                                            IGenericRepository<Driver, Long> driverRepo,
                                            IGenericRepository<Vehicle, Long> vehicleRepo,
                                            IGenericRepository<Destination, Long> destinationRepo) {
        this.companyRepo = companyRepo;
        this.clientRepo = clientRepo;
        this.driverRepo = driverRepo;
        this.vehicleRepo = vehicleRepo;
        this.destinationRepo = destinationRepo;
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE)
                .setSkipNullEnabled(true);
        configureMappings();
    }

    private void configureMappings() {
        modelMapper.createTypeMap(TransportPassengersService.class, TransportPassengersServiceViewDTO.class)
                .addMapping(src -> src.getTransportCompany().getId(), TransportPassengersServiceViewDTO::setTransportCompanyId)
                .addMapping(src -> src.getVehicle().getId(), TransportPassengersServiceViewDTO::setVehicleId)
                .addMapping(src -> src.getDriver().getId(), TransportPassengersServiceViewDTO::setDriverId)
                .addMapping(src -> src.getClient().getId(), TransportPassengersServiceViewDTO::setClientId)
                .addMapping(src -> src.getDestination().getId(), TransportPassengersServiceViewDTO::setDestinationId);
    }

    public TransportPassengersService toEntity(TransportPassengersServiceCreateDTO dto) throws RepositoryException {
        if (dto == null) throw new IllegalArgumentException("TransportPassengersServiceCreateDTO must not be null");
        TransportPassengersService entity = modelMapper.map(dto, TransportPassengersService.class);
        if (dto.getTransportCompanyId() != null) entity.setTransportCompany(companyRepo.getById(dto.getTransportCompanyId()).orElse(null));
        if (dto.getClientId() != null) entity.setClient(clientRepo.getById(dto.getClientId()).orElse(null));
        if (dto.getDriverId() != null) entity.setDriver(driverRepo.getById(dto.getDriverId()).orElse(null));
        if (dto.getVehicleId() != null) entity.setVehicle(vehicleRepo.getById(dto.getVehicleId()).orElse(null));
        if (dto.getDestinationId() != null) entity.setDestination(destinationRepo.getById(dto.getDestinationId()).orElse(null));
        return entity;
    }

    public TransportPassengersService toEntity(TransportPassengersServiceUpdateDTO dto) throws RepositoryException {
        if (dto == null) throw new IllegalArgumentException("TransportPassengersServiceUpdateDTO must not be null");
        TransportPassengersService entity = modelMapper.map(dto, TransportPassengersService.class);
        if (dto.getTransportCompanyId() != null) entity.setTransportCompany(companyRepo.getById(dto.getTransportCompanyId()).orElse(null));
        if (dto.getClientId() != null) entity.setClient(clientRepo.getById(dto.getClientId()).orElse(null));
        if (dto.getDriverId() != null) entity.setDriver(driverRepo.getById(dto.getDriverId()).orElse(null));
        if (dto.getVehicleId() != null) entity.setVehicle(vehicleRepo.getById(dto.getVehicleId()).orElse(null));
        if (dto.getDestinationId() != null) entity.setDestination(destinationRepo.getById(dto.getDestinationId()).orElse(null));
        return entity;
    }

    public void toEntity(TransportPassengersServiceUpdateDTO dto, TransportPassengersService entity) throws RepositoryException {
        if (dto == null || entity == null) throw new IllegalArgumentException("DTO and entity must not be null");
        modelMapper.map(dto, entity);
        if (dto.getTransportCompanyId() != null) entity.setTransportCompany(companyRepo.getById(dto.getTransportCompanyId()).orElse(null));
        if (dto.getClientId() != null) entity.setClient(clientRepo.getById(dto.getClientId()).orElse(null));
        if (dto.getDriverId() != null) entity.setDriver(driverRepo.getById(dto.getDriverId()).orElse(null));
        if (dto.getVehicleId() != null) entity.setVehicle(vehicleRepo.getById(dto.getVehicleId()).orElse(null));
        if (dto.getDestinationId() != null) entity.setDestination(destinationRepo.getById(dto.getDestinationId()).orElse(null));
    }

    public TransportPassengersServiceUpdateDTO toUpdateDTO(TransportPassengersService entity) {
        return modelMapper.map(entity, TransportPassengersServiceUpdateDTO.class);
    }

    public TransportPassengersServiceViewDTO toViewDTO(TransportPassengersService entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, TransportPassengersServiceViewDTO.class);
    }
}