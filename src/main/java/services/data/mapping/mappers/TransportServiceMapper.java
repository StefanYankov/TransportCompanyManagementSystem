package services.data.mapping.mappers;

import data.models.Client;
import data.models.TransportCompany;
import data.models.employee.Driver;
import data.models.transportservices.Destination;
import data.models.transportservices.TransportCargoService;
import data.models.transportservices.TransportPassengersService;
import data.models.transportservices.TransportService;
import data.models.vehicles.Vehicle;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import services.data.dto.transportservices.TransportCargoServiceViewDTO;
import services.data.dto.transportservices.TransportPassengersServiceViewDTO;
import services.data.dto.transportservices.TransportServiceCreateDTO;
import services.data.dto.transportservices.TransportServiceUpdateDTO;
import services.data.dto.transportservices.TransportServiceViewDTO;

public class TransportServiceMapper {
    private final ModelMapper modelMapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;
    private final IGenericRepository<Client, Long> clientRepo;
    private final IGenericRepository<Driver, Long> driverRepo;
    private final IGenericRepository<Destination, Long> destinationRepo;
    private final IGenericRepository<Vehicle, Long> vehicleRepo;

    public TransportServiceMapper(IGenericRepository<TransportCompany, Long> companyRepo,
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
        modelMapper.createTypeMap(TransportServiceCreateDTO.class, TransportService.class)
                .addMappings(mapper -> mapper.skip(TransportService::setId));

        // Map concrete subclasses to their respective ViewDTOs
        modelMapper.createTypeMap(TransportCargoService.class, TransportCargoServiceViewDTO.class)
                .addMapping(src -> src.getTransportCompany().getId(), TransportCargoServiceViewDTO::setTransportCompanyId)
                .addMapping(src -> src.getVehicle().getId(), TransportCargoServiceViewDTO::setVehicleId)
                .addMapping(src -> src.getDriver().getId(), TransportCargoServiceViewDTO::setDriverId)
                .addMapping(src -> src.getClient().getId(), TransportCargoServiceViewDTO::setClientId)
                .addMapping(src -> src.getDestination().getId(), TransportCargoServiceViewDTO::setDestinationId);

        modelMapper.createTypeMap(TransportPassengersService.class, TransportPassengersServiceViewDTO.class)
                .addMapping(src -> src.getTransportCompany().getId(), TransportPassengersServiceViewDTO::setTransportCompanyId)
                .addMapping(src -> src.getVehicle().getId(), TransportPassengersServiceViewDTO::setVehicleId)
                .addMapping(src -> src.getDriver().getId(), TransportPassengersServiceViewDTO::setDriverId)
                .addMapping(src -> src.getClient().getId(), TransportPassengersServiceViewDTO::setClientId)
                .addMapping(src -> src.getDestination().getId(), TransportPassengersServiceViewDTO::setDestinationId);

        modelMapper.createTypeMap(TransportServiceUpdateDTO.class, TransportService.class)
                .addMapping(TransportServiceUpdateDTO::getId, TransportService::setId);
    }

    public TransportService toEntity(TransportServiceCreateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("TransportServiceCreateDTO must not be null");
        TransportService entity = modelMapper.map(dto, TransportService.class);
        resolveRelationships(entity, dto);
        return entity;
    }

    public TransportService toEntity(TransportServiceUpdateDTO dto, TransportService existing) {
        if (dto == null) throw new IllegalArgumentException("TransportServiceUpdateDTO must not be null");
        modelMapper.map(dto, existing);
        resolveRelationships(existing, dto);
        return existing;
    }

    public TransportServiceViewDTO toViewDTO(TransportService entity) {
        if (entity == null) return null;
        if (entity instanceof TransportCargoService) {
            return modelMapper.map(entity, TransportCargoServiceViewDTO.class);
        } else if (entity instanceof TransportPassengersService) {
            return modelMapper.map(entity, TransportPassengersServiceViewDTO.class);
        } else {
            throw new IllegalArgumentException("Unknown TransportService subclass: " + entity.getClass().getName());
        }
    }

    private void resolveRelationships(TransportService entity, TransportServiceCreateDTO dto) {
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

    private void resolveRelationships(TransportService entity, TransportServiceUpdateDTO dto) {
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