package services.data.mapping.mappers;

import data.models.vehicles.Bus;
import data.models.TransportCompany;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.modelmapper.ModelMapper;
import services.data.dto.vehicles.BusCreateDTO;
import services.data.dto.vehicles.BusUpdateDTO;
import services.data.dto.vehicles.BusViewDTO;

/**
 * Mapper class for converting between {@link Bus} entities and their DTOs using ModelMapper.
 */
public class BusMapper {
    private final ModelMapper modelMapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;

    public BusMapper(IGenericRepository<TransportCompany, Long> companyRepo) {
        this.modelMapper = new ModelMapper();
        this.companyRepo = companyRepo;
        configureMappings();
    }

    private void configureMappings() {
        modelMapper.createTypeMap(BusCreateDTO.class, Bus.class)
                .addMappings(mapper -> mapper.skip(Bus::setId));
    }

    public Bus toEntity(BusCreateDTO dto) {
        if (dto == null){
            throw new IllegalArgumentException("BusCreateDTO must not be null");
        }
        Bus bus = modelMapper.map(dto, Bus.class);
        TransportCompany company = companyRepo.getById(dto.getTransportCompanyId())
                .orElseThrow(() -> new RepositoryException("Transport company not found: " + dto.getTransportCompanyId()));
        bus.setTransportCompany(company);
        return bus;
    }

    public void toEntity(BusUpdateDTO dto, Bus existing) {
        if (dto == null) throw new IllegalArgumentException("BusUpdateDTO must not be null");
        if (dto.getRegistrationPlate() != null) existing.setRegistrationPlate(dto.getRegistrationPlate());
        if (dto.getMaxPassengerCapacity() != null) existing.setMaxPassengerCapacity(dto.getMaxPassengerCapacity());
        if (dto.getHasRestroom() != null) existing.setHasRestroom(dto.getHasRestroom());
        if (dto.getLuggageCapacity() != null) existing.setLuggageCapacity(dto.getLuggageCapacity());
        if (dto.getTransportCompanyId() != null) {
            TransportCompany company = companyRepo.getById(dto.getTransportCompanyId())
                    .orElseThrow(() -> new RepositoryException("Transport company not found: " + dto.getTransportCompanyId()));
            existing.setTransportCompany(company);
        }
    }

    public BusUpdateDTO toUpdateDTO(Bus entity) {
        return modelMapper.map(entity, BusUpdateDTO.class);
    }

    public BusViewDTO toViewDTO(Bus entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, BusViewDTO.class);
    }
}