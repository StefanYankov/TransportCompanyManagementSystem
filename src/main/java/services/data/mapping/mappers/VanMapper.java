package services.data.mapping.mappers;

import data.models.TransportCompany;
import data.models.vehicles.Van;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.modelmapper.ModelMapper;
import services.data.dto.vehicles.VanCreateDTO;
import services.data.dto.vehicles.VanUpdateDTO;
import services.data.dto.vehicles.VanViewDTO;

public class VanMapper {
    private final ModelMapper modelMapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;

    public VanMapper(IGenericRepository<TransportCompany, Long> companyRepo) {
        this.modelMapper = new ModelMapper();
        this.companyRepo = companyRepo;
        configureMappings();
    }

    private void configureMappings() {
        modelMapper.createTypeMap(VanCreateDTO.class, Van.class)
                .addMappings(mapper -> mapper.skip(Van::setId));
    }

    public Van toEntity(VanCreateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("VanCreateDTO must not be null");
        Van van = modelMapper.map(dto, Van.class);
        TransportCompany company = companyRepo.getById(dto.getTransportCompanyId())
                .orElseThrow(() -> new RepositoryException("Transport company not found: " + dto.getTransportCompanyId()));
        van.setTransportCompany(company);
        return van;
    }

    public void toEntity(VanUpdateDTO dto, Van existing) {
        if (dto == null) throw new IllegalArgumentException("VanUpdateDTO must not be null");
        if (dto.getRegistrationPlate() != null) existing.setRegistrationPlate(dto.getRegistrationPlate());
        if (dto.getMaxPassengerCapacity() != null) existing.setMaxPassengerCapacity(dto.getMaxPassengerCapacity());
        if (dto.getHasPassengerOverheadStorage() != null) existing.setHasPassengerOverheadStorage(dto.getHasPassengerOverheadStorage());
        if (dto.getTransportCompanyId() != null) {
            TransportCompany company = companyRepo.getById(dto.getTransportCompanyId())
                    .orElseThrow(() -> new RepositoryException("Transport company not found: " + dto.getTransportCompanyId()));
            existing.setTransportCompany(company);
        }
    }

    public VanUpdateDTO toUpdateDTO(Van entity) {
        return modelMapper.map(entity, VanUpdateDTO.class);
    }

    public VanViewDTO toViewDTO(Van entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, VanViewDTO.class);
    }
}