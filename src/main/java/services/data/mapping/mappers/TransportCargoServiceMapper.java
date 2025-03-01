package services.data.mapping.mappers;

import data.models.transportservices.TransportCargoService;
import data.models.TransportCompany;
import data.models.Client;
import data.models.employee.Driver;
import data.models.transportservices.Destination;
import data.models.vehicles.Vehicle;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import services.data.dto.transportservices.TransportCargoServiceCreateDTO;
import services.data.dto.transportservices.TransportCargoServiceUpdateDTO;
import services.data.dto.transportservices.TransportCargoServiceViewDTO;

public class TransportCargoServiceMapper {
    private final ModelMapper modelMapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;
    private final IGenericRepository<Client, Long> clientRepo;
    private final IGenericRepository<Driver, Long> driverRepo;
    private final IGenericRepository<Destination, Long> destinationRepo;
    private final IGenericRepository<Vehicle, Long> vehicleRepo;

    public TransportCargoServiceMapper(IGenericRepository<TransportCompany, Long> companyRepo,
                                       IGenericRepository<Client, Long> clientRepo,
                                       IGenericRepository<Driver, Long> driverRepo,
                                       IGenericRepository<Destination, Long> destinationRepo,
                                       IGenericRepository<Vehicle, Long> vehicleRepo) {
        this.companyRepo = companyRepo;
        this.clientRepo = clientRepo;
        this.driverRepo = driverRepo;
        this.destinationRepo = destinationRepo;
        this.vehicleRepo = vehicleRepo;
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE);
        configureMappings();
    }

    private void configureMappings() {
        modelMapper.createTypeMap(TransportCargoServiceCreateDTO.class, TransportCargoService.class)
                .addMappings(mapper -> mapper.skip(TransportCargoService::setId));

        modelMapper.createTypeMap(TransportCargoService.class, TransportCargoServiceViewDTO.class)
                .addMapping(src -> src.getTransportCompany().getId(), TransportCargoServiceViewDTO::setTransportCompanyId)
                .addMapping(src -> src.getVehicle().getId(), TransportCargoServiceViewDTO::setVehicleId)
                .addMapping(src -> src.getDriver().getId(), TransportCargoServiceViewDTO::setDriverId)
                .addMapping(src -> src.getClient().getId(), TransportCargoServiceViewDTO::setClientId)
                .addMapping(src -> src.getDestination().getId(), TransportCargoServiceViewDTO::setDestinationId);
    }

    public TransportCargoService toEntity(TransportCargoServiceCreateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("TransportCargoServiceCreateDTO must not be null");
        TransportCargoService entity = modelMapper.map(dto, TransportCargoService.class);
        resolveRelationships(entity, dto);
        return entity;
    }

    public TransportCargoService toEntity(TransportCargoServiceUpdateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("TransportCargoServiceUpdateDTO must not be null");
        TransportCargoService entity = modelMapper.map(dto, TransportCargoService.class);
        resolveRelationships(entity, dto);
        return entity;
    }

    public TransportCargoServiceUpdateDTO toUpdateDTO(TransportCargoService entity) {
        return modelMapper.map(entity, TransportCargoServiceUpdateDTO.class);
    }

    public TransportCargoServiceViewDTO toViewDTO(TransportCargoService entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, TransportCargoServiceViewDTO.class);
    }

    private void resolveRelationships(TransportCargoService entity, TransportCargoServiceCreateDTO dto) {
        entity.setTransportCompany(companyRepo.getById(dto.getTransportCompanyId())
                .orElseThrow(() -> new RepositoryException("Transport company not found: " + dto.getTransportCompanyId())));
        entity.setClient(clientRepo.getById(dto.getClientId())
                .orElseThrow(() -> new RepositoryException("Client not found: " + dto.getClientId())));
        entity.setDriver(driverRepo.getById(dto.getDriverId())
                .orElseThrow(() -> new RepositoryException("Driver not found: " + dto.getDriverId())));
        entity.setDestination(destinationRepo.getById(dto.getDestinationId())
                .orElseThrow(() -> new RepositoryException("Destination not found: " + dto.getDestinationId())));
        entity.setVehicle(vehicleRepo.getById(dto.getVehicleId())
                .orElseThrow(() -> new RepositoryException("Vehicle not found: " + dto.getVehicleId())));
    }

    private void resolveRelationships(TransportCargoService entity, TransportCargoServiceUpdateDTO dto) {
        entity.setTransportCompany(companyRepo.getById(dto.getTransportCompanyId())
                .orElseThrow(() -> new RepositoryException("Transport company not found: " + dto.getTransportCompanyId())));
        entity.setClient(clientRepo.getById(dto.getClientId())
                .orElseThrow(() -> new RepositoryException("Client not found: " + dto.getClientId())));
        entity.setDriver(driverRepo.getById(dto.getDriverId())
                .orElseThrow(() -> new RepositoryException("Driver not found: " + dto.getDriverId())));
        entity.setDestination(destinationRepo.getById(dto.getDestinationId())
                .orElseThrow(() -> new RepositoryException("Destination not found: " + dto.getDestinationId())));
        entity.setVehicle(vehicleRepo.getById(dto.getVehicleId())
                .orElseThrow(() -> new RepositoryException("Vehicle not found: " + dto.getVehicleId())));
    }
}