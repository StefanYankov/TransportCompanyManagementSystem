package services;

import data.models.TransportCompany;
import data.models.transportservices.TransportCargoService;
import data.repositories.IGenericRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.dto.transportservices.TransportCargoServiceViewDto;
import services.data.mapping.mappings.TransportCargoServiceMapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling operations related to transport companies and their employees.
 * This class supports asynchronous operations.
 */
public class TransportCompanyService {

    private static final Logger logger = LoggerFactory.getLogger(TransportCargoServiceService.class);
    private final IGenericRepository<TransportCompany, Long> companyRepository;
    private final IGenericRepository<TransportCargoService, Long> cargoServiceRepository;


    public TransportCompanyService(
            IGenericRepository<TransportCompany, Long> companyRepository,
            IGenericRepository<TransportCargoService, Long> cargoServiceRepository
    ) {
        this.companyRepository = companyRepository;
        this.cargoServiceRepository = cargoServiceRepository;
    }


    public CompletableFuture<List<TransportCargoServiceViewDto>> getAllCargoServicesBetweenDatesAsync(LocalDate startDate, LocalDate endDate, String sortBy, boolean ascending) {
        Map<String, Object> conditions = new HashMap<>();
        if (startDate != null) {
            conditions.put("startingDate", startDate); // Assuming field name is "startingDate"
        }
        if (endDate != null) {
            conditions.put("endingDate", endDate);   // Assuming field name is "endingDate"
        }
        return cargoServiceRepository.findByCriteriaAsync(conditions, sortBy, ascending)
                .thenApply(transportCargoServices -> transportCargoServices.stream()
                        .map(TransportCargoServiceMapper::toViewDto)
                        .collect(Collectors.toList()));
    }

}
