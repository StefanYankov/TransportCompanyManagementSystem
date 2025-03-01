package services.data.mapping.mappers;

import data.models.vehicles.Truck;
import data.models.TransportCompany;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.modelmapper.ModelMapper;
import services.data.dto.vehicles.TruckCreateDTO;
import services.data.dto.vehicles.TruckUpdateDTO;
import services.data.dto.vehicles.TruckViewDTO;

/**
 * Mapper class for converting between {@link Truck} entities and their DTOs using ModelMapper.
 */
public class TruckMapper {
    private final ModelMapper modelMapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;

    public TruckMapper(IGenericRepository<TransportCompany, Long> companyRepo) {
        this.modelMapper = new ModelMapper();
        this.companyRepo = companyRepo;
        configureMappings();
    }

    private void configureMappings() {
        modelMapper.createTypeMap(TruckCreateDTO.class, Truck.class)
                .addMappings(mapper -> mapper.skip(Truck::setId));
    }

    public Truck toEntity(TruckCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("TruckCreateDTO must not be null");
        }
        Truck truck = modelMapper.map(dto, Truck.class);
        TransportCompany company = companyRepo.getById(dto.getTransportCompanyId())
                .orElseThrow(() -> new RepositoryException("Transport company not found: " + dto.getTransportCompanyId()));
        truck.setTransportCompany(company);
        return truck;
    }

    public void toEntity(TruckUpdateDTO dto, Truck existing) {
        if (dto == null) throw new IllegalArgumentException("TruckUpdateDTO must not be null");
        if (dto.getRegistrationPlate() != null) existing.setRegistrationPlate(dto.getRegistrationPlate());
        if (dto.getMaxCargoCapacityKg() != null) existing.setMaxCargoCapacityKg(dto.getMaxCargoCapacityKg());
        if (dto.getCurrentCargoCapacityKg() != null)
            existing.setCurrentCargoCapacityKg(dto.getCurrentCargoCapacityKg());
        if (dto.getCargoType() != null) existing.setCargoType(dto.getCargoType());
        if (dto.getTruckType() != null) existing.setTruckType(dto.getTruckType());
        if (dto.getTransportCompanyId() != null) {
            TransportCompany company = companyRepo.getById(dto.getTransportCompanyId())
                    .orElseThrow(() -> new RepositoryException("Transport company not found: " + dto.getTransportCompanyId()));
            existing.setTransportCompany(company);
        }
    }

    public TruckUpdateDTO toUpdateDTO(Truck entity) {
        return modelMapper.map(entity, TruckUpdateDTO.class);
    }

    public TruckViewDTO toViewDTO(Truck entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, TruckViewDTO.class);
    }
}