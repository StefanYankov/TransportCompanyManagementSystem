package services.services;

import data.models.employee.Driver;
import data.models.employee.Qualification;
import data.repositories.IGenericRepository;
import data.repositories.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.common.Constants;
import services.data.dto.employees.DriverViewDTO;
import services.data.dto.employees.QualificationCreateDTO;
import services.data.dto.employees.QualificationUpdateDTO;
import services.data.dto.employees.QualificationViewDTO;
import services.data.mapping.mappers.DriverMapper;
import services.data.mapping.mappers.QualificationMapper;
import services.services.contracts.IQualificationService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing {@link Qualification} entities, exposing DTOs instead of entities.
 */
public class QualificationService implements IQualificationService {
    private static final Logger logger = LoggerFactory.getLogger(QualificationService.class);
    private final IGenericRepository<Qualification, Long> qualificationRepo;
    private final IGenericRepository<Driver, Long> driverRepo;
    private final QualificationMapper qualificationMapper;
    private final DriverMapper driverMapper;

    /**
     * Constructs a new QualificationService with the specified dependencies.
     *
     * @param qualificationRepo repository for qualification-related database operations
     * @param driverRepo        repository for driver-related database operations
     * @param qualificationMapper utility for mapping Qualification entities to DTOs and vice versa
     * @param driverMapper      utility for mapping Driver entities to DTOs
     */
    public QualificationService(IGenericRepository<Qualification, Long> qualificationRepo,
                                IGenericRepository<Driver, Long> driverRepo,
                                QualificationMapper qualificationMapper,
                                DriverMapper driverMapper) {
        this.qualificationRepo = qualificationRepo;
        this.driverRepo = driverRepo;
        this.qualificationMapper = qualificationMapper;
        this.driverMapper = driverMapper;
    }

    /** {@inheritDoc} */
    @Override
    public QualificationViewDTO create(QualificationCreateDTO dto) {
        if (dto == null) {
            logger.error("Cannot create {}: DTO is null", Constants.QUALIFICATION);
            throw new IllegalArgumentException("QualificationCreateDTO must not be null");
        }
        logger.debug("Creating {} with DTO: {}", Constants.QUALIFICATION, dto);
        try {
            Qualification entity = qualificationMapper.toEntity(dto);
            Qualification created = qualificationRepo.create(entity);
            logger.info("{} created with ID: {}", Constants.QUALIFICATION, created.getId());
            return qualificationMapper.toViewDTO(created);
        } catch (RepositoryException e) {
            logger.error("Failed to create {}: {}, cause: {}", Constants.QUALIFICATION, dto.getName(), e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public QualificationViewDTO update(QualificationUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            logger.error("Cannot update {}: DTO or ID is null", Constants.QUALIFICATION);
            throw new IllegalArgumentException("QualificationUpdateDTO and ID must not be null");
        }
        logger.debug("Updating {} with DTO: {}", Constants.QUALIFICATION, dto);
        try {
            Qualification existing = qualificationRepo.getById(dto.getId())
                    .orElseThrow(() -> new RepositoryException("Qualification not found with ID: " + dto.getId()));
            qualificationMapper.toEntity(dto, existing);
            QualificationViewDTO result = qualificationRepo.updateAndMap(existing, qualificationMapper::toViewDTO, null);
            logger.info("{} updated with ID: {}", Constants.QUALIFICATION, result.getId());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to update {} with ID: {}, cause: {}", Constants.QUALIFICATION, dto.getId(), e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long id) {
        if (id == null) {
            logger.error("Cannot delete {}: ID is null", Constants.QUALIFICATION);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Deleting {} with ID: {}", Constants.QUALIFICATION, id);
        try {
            Qualification entity = qualificationRepo.getById(id)
                    .orElseThrow(() -> new RepositoryException("Qualification not found with ID: " + id));
            qualificationRepo.delete(entity);
            logger.info("{} deleted with ID: {}", Constants.QUALIFICATION, id);
        } catch (RepositoryException e) {
            logger.error("Failed to delete {} with ID: {}, cause: {}", Constants.QUALIFICATION, id, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public QualificationViewDTO getById(Long id) {
        if (id == null) {
            logger.error("Cannot retrieve {}: ID is null", Constants.QUALIFICATION);
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.debug("Retrieving {} with ID: {}", Constants.QUALIFICATION, id);
        try {
            Optional<QualificationViewDTO> result = qualificationRepo.getByIdAndMap(id, qualificationMapper::toViewDTO, null);
            if (result.isEmpty()) {
                logger.warn("No {} found with ID: {}", Constants.QUALIFICATION, id);
                return null;
            }
            logger.info("{} retrieved with ID: {}", Constants.QUALIFICATION, id);
            return result.get();
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve {} with ID: {}, cause: {}", Constants.QUALIFICATION, id, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<QualificationViewDTO> getAll(int page, int size, String orderBy, boolean ascending, String filter) {
        logger.debug("Retrieving all {}: page={}, size={}, orderBy={}, ascending={}, filter={}", Constants.QUALIFICATION, page, size, orderBy, ascending, filter);
        try {
            List<QualificationViewDTO> result = qualificationRepo.getAllAndMap(page, size, orderBy, ascending, qualificationMapper::toViewDTO, null);
            if (filter != null && !filter.trim().isEmpty()) {
                String filterLower = filter.trim().toLowerCase();
                result = result.stream()
                        .filter(q -> q.getName().toLowerCase().contains(filterLower) ||
                                q.getDescription().toLowerCase().contains(filterLower))
                        .collect(Collectors.toList());
            }
            logger.info("Retrieved {} {}", result.size(), Constants.QUALIFICATION);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve all {}, cause: {}", Constants.QUALIFICATION, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<QualificationViewDTO> findByCriteria(Map<String, Object> conditions, String orderBy, boolean ascending) {
        logger.debug("Finding {} by criteria: conditions={}, orderBy={}, ascending={}", Constants.QUALIFICATION, conditions, orderBy, ascending);
        try {
            List<QualificationViewDTO> result = qualificationRepo.findByCriteria(conditions, orderBy, ascending)
                    .stream()
                    .map(qualificationMapper::toViewDTO)
                    .collect(Collectors.toList());
            logger.info("Found {} {} matching criteria", result.size(), Constants.QUALIFICATION);
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to find {} by criteria, cause: {}", Constants.QUALIFICATION, e.getMessage(), e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Map<Long, List<DriverViewDTO>> getDriversByQualification() {
        logger.debug("Retrieving drivers by {}", Constants.QUALIFICATION);
        try {
            List<Qualification> qualifications = qualificationRepo.getAll(0, Integer.MAX_VALUE, "name", true);
            Map<Long, List<DriverViewDTO>> result = new HashMap<>();
            for (Qualification qual : qualifications) {
                List<Driver> drivers = driverRepo.findWithJoin("qualifications", "id", qual.getId(), "familyName", true, "qualifications");
                result.put(qual.getId(), drivers.stream().map(driverMapper::toViewDTO).collect(Collectors.toList()));
            }
            logger.info("Retrieved drivers for {} qualifications", result.size());
            return result;
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve drivers by {}, cause: {}", Constants.QUALIFICATION, e.getMessage(), e);
            throw e;
        }
    }
}