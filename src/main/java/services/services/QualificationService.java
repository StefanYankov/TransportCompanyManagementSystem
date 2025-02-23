package services.services;

import data.models.employee.Qualification;
import data.repositories.IGenericRepository;
import services.data.mapping.mappers.QualificationMapper;
import services.data.dto.employees.QualificationCreateDTO;
import services.data.dto.employees.QualificationUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.services.contracts.IQualificationService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service implementation for managing {@link Qualification} entities.
 */
public class QualificationService implements IQualificationService {
    private static final Logger logger = LoggerFactory.getLogger(QualificationService.class);
    private final IGenericRepository<Qualification, Long> qualRepo;
    private final QualificationMapper mapper;

    public QualificationService(IGenericRepository<Qualification, Long> qualRepo, QualificationMapper mapper) {
        this.qualRepo = qualRepo;
        this.mapper = mapper;
    }

    /** {@inheritDoc} */
    @Override
    public Qualification create(QualificationCreateDTO dto) {
        logger.debug("Creating qualification with DTO: {}", dto);
        Qualification entity = mapper.toEntity(dto);
        Qualification result = qualRepo.create(entity);
        logger.info("Qualification created with ID: {}", result.getId());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Qualification> createAsync(QualificationCreateDTO dto) {
        logger.debug("Async creating qualification with DTO: {}", dto);
        Qualification entity = mapper.toEntity(dto);
        return qualRepo.createAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to create qualification asynchronously", throwable);
                    } else {
                        logger.info("Qualification created asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public Qualification update(QualificationUpdateDTO dto) {
        logger.debug("Updating qualification with DTO: {}", dto);
        Qualification entity = mapper.toEntity(dto);
        Qualification result = qualRepo.update(entity);
        logger.info("Qualification updated with ID: {}", result.getId());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Qualification> updateAsync(QualificationUpdateDTO dto) {
        logger.debug("Async updating qualification with DTO: {}", dto);
        Qualification entity = mapper.toEntity(dto);
        return qualRepo.updateAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to update qualification asynchronously", throwable);
                    } else {
                        logger.info("Qualification updated asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long id) {
        logger.debug("Deleting qualification with ID: {}", id);
        Qualification entity = qualRepo.getById(id);
        if (entity != null) {
            qualRepo.delete(entity);
            logger.info("Qualification deleted with ID: {}", id);
        } else {
            logger.warn("No qualification found to delete with ID: {}", id);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<Void> deleteAsync(Long id) {
        logger.debug("Async deleting qualification with ID: {}", id);
        return qualRepo.getByIdAsync(id)
                .thenAccept(entity -> {
                    if (entity != null) {
                        qualRepo.deleteAsync(entity)
                                .whenComplete((v, throwable) -> {
                                    if (throwable != null) {
                                        logger.error("Failed to delete qualification asynchronously", throwable);
                                    } else {
                                        logger.info("Qualification deleted asynchronously with ID: {}", id);
                                    }
                                });
                    } else {
                        logger.warn("No qualification found to delete asynchronously with ID: {}", id);
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public Qualification getById(Long id) {
        logger.debug("Retrieving qualification with ID: {}", id);
        Qualification result = qualRepo.getById(id);
        if (result != null) {
            logger.info("Qualification retrieved with ID: {}", id);
        } else {
            logger.warn("No qualification found with ID: {}", id);
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<Qualification> getAll(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving all qualifications, page: {}, size: {}, orderBy: {}, ascending: {}", page, size, orderBy, ascending);
        List<Qualification> result = qualRepo.getAll(page, size, orderBy, ascending);
        logger.info("Retrieved {} qualifications", result.size());
        return result;
    }
}