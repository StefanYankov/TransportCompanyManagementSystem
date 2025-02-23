package services.services;

import data.common.seeding.LocalDateAdapter;
import data.common.seeding.LocalDateTimeAdapter;
import data.models.Client;
import data.models.TransportCompany;
import data.models.employee.Driver;
import data.models.transportservices.Destination;
import data.models.transportservices.TransportCargoService;
import data.models.vehicles.Vehicle;
import data.repositories.IGenericRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.dto.transportservices.TransportCargoServiceCreateDTO;
import services.data.dto.transportservices.TransportCargoServiceUpdateDTO;
import services.data.mapping.mappers.TransportCargoServiceMapper;
import services.services.contracts.ITransportCargoServiceService;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * Service implementation for managing {@link TransportCargoService} entities.
 */
public class TransportCargoServiceService implements ITransportCargoServiceService {
    private static final Logger logger = LoggerFactory.getLogger(TransportCargoServiceService.class);
    private final IGenericRepository<TransportCargoService, Long> transportRepo;
    private final TransportCargoServiceMapper mapper;
    private final IGenericRepository<TransportCompany, Long> companyRepo;
    private final IGenericRepository<Destination, Long> destinationRepo;
    private final IGenericRepository<Client, Long> clientRepo;
    private final IGenericRepository<Vehicle, Long> vehicleRepo;
    private final IGenericRepository<Driver, Long> driverRepo;
    private final Gson gson;

    public TransportCargoServiceService(IGenericRepository<TransportCargoService, Long> transportRepo,
                                        TransportCargoServiceMapper mapper,
                                        IGenericRepository<TransportCompany, Long> companyRepo,
                                        IGenericRepository<Destination, Long> destinationRepo,
                                        IGenericRepository<Client, Long> clientRepo,
                                        IGenericRepository<Vehicle, Long> vehicleRepo,
                                        IGenericRepository<Driver, Long> driverRepo) {
        this.transportRepo = transportRepo;
        this.mapper = mapper;
        this.companyRepo = companyRepo;
        this.destinationRepo = destinationRepo;
        this.clientRepo = clientRepo;
        this.vehicleRepo = vehicleRepo;
        this.driverRepo = driverRepo;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransportCargoService create(TransportCargoServiceCreateDTO dto) {
        logger.debug("Creating cargo transport service with DTO: {}", dto);
        TransportCargoService entity = mapper.toEntity(dto);
        TransportCargoService result = transportRepo.create(entity);
        logger.info("Cargo transport service created with ID: {}", result.getId());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<TransportCargoService> createAsync(TransportCargoServiceCreateDTO dto) {
        logger.debug("Async creating cargo transport service with DTO: {}", dto);
        TransportCargoService entity = mapper.toEntity(dto);
        return transportRepo.createAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to create cargo transport service asynchronously", throwable);
                    } else {
                        logger.info("Cargo transport service created asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransportCargoService update(TransportCargoServiceUpdateDTO dto) {
        logger.debug("Updating cargo transport service with DTO: {}", dto);
        TransportCargoService entity = mapper.toEntity(dto);
        TransportCargoService result = transportRepo.update(entity);
        logger.info("Cargo transport service updated with ID: {}", result.getId());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<TransportCargoService> updateAsync(TransportCargoServiceUpdateDTO dto) {
        logger.debug("Async updating cargo transport service with DTO: {}", dto);
        TransportCargoService entity = mapper.toEntity(dto);
        return transportRepo.updateAsync(entity)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to update cargo transport service asynchronously", throwable);
                    } else {
                        logger.info("Cargo transport service updated asynchronously with ID: {}", result.getId());
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        logger.debug("Deleting cargo transport service with ID: {}", id);
        TransportCargoService entity = transportRepo.getById(id);
        if (entity != null) {
            transportRepo.delete(entity);
            logger.info("Cargo transport service deleted with ID: {}", id);
        } else {
            logger.warn("No cargo transport service found to delete with ID: {}", id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Void> deleteAsync(Long id) {
        logger.debug("Async deleting cargo transport service with ID: {}", id);
        return transportRepo.getByIdAsync(id)
                .thenAccept(entity -> {
                    if (entity != null) {
                        transportRepo.deleteAsync(entity)
                                .whenComplete((v, throwable) -> {
                                    if (throwable != null) {
                                        logger.error("Failed to delete cargo transport service asynchronously", throwable);
                                    } else {
                                        logger.info("Cargo transport service deleted asynchronously with ID: {}", id);
                                    }
                                });
                    } else {
                        logger.warn("No cargo transport service found to delete asynchronously with ID: {}", id);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransportCargoService getById(Long id) {
        logger.debug("Retrieving cargo transport service with ID: {}", id);
        TransportCargoService result = transportRepo.getById(id);
        if (result != null) {
            logger.info("Cargo transport service retrieved with ID: {}", id);
        } else {
            logger.warn("No cargo transport service found with ID: {}", id);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransportCargoService> getAll(int page, int size, String orderBy, boolean ascending) {
        logger.debug("Retrieving all cargo transport services, page: {}, size: {}, orderBy: {}, ascending: {}", page, size, orderBy, ascending);
        List<TransportCargoService> result = transportRepo.getAll(page, size, orderBy, ascending);
        logger.info("Retrieved {} cargo transport services", result.size());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransportCargoService> getTransportsSortedByDestination(boolean ascending) {
        logger.debug("Retrieving cargo transports sorted by destination, ascending: {}", ascending);
        List<TransportCargoService> result = transportRepo.getAll(0, Integer.MAX_VALUE, "destination.startingLocation", ascending);
        logger.info("Retrieved {} cargo transports sorted by destination", result.size());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToBinaryFile(String filePath) throws IOException {
        logger.debug("Saving cargo transport data to binary file: {}", filePath);
        List<TransportCargoService> transports = transportRepo.getAll(0, Integer.MAX_VALUE, "id", true);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(transports);
            logger.info("Saved {} cargo transports to binary file: {}", transports.size(), filePath);
        } catch (IOException e) {
            logger.error("Failed to save cargo transport data to binary file: {}", filePath, e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void loadFromBinaryFile(String filePath) throws IOException, ClassNotFoundException {
        logger.debug("Loading cargo transport data from binary file: {}", filePath);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            List<TransportCargoService> transports = (List<TransportCargoService>) ois.readObject();
            for (TransportCargoService transport : transports) {
                transportRepo.create(transport);
            }
            logger.info("Loaded {} cargo transports from binary file: {}", transports.size(), filePath);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Failed to load cargo transport data from binary file: {}", filePath, e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToJsonFile(String filePath) throws IOException {
        logger.debug("Saving cargo transport data to JSON file: {}", filePath);
        List<TransportCargoService> transports = transportRepo.getAll(0, Integer.MAX_VALUE, "id", true);
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(transports, writer);
            logger.info("Saved {} cargo transports to JSON file: {}", transports.size(), filePath);
        } catch (IOException e) {
            logger.error("Failed to save cargo transport data to JSON file: {}", filePath, e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromJsonFile(String filePath) throws IOException {
        logger.debug("Loading cargo transport data from JSON file: {}", filePath);
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<List<TransportCargoService>>() {
            }.getType();
            List<TransportCargoService> transports = gson.fromJson(reader, listType);
            for (TransportCargoService transport : transports) {
                transportRepo.create(transport);
            }
            logger.info("Loaded {} cargo transports from JSON file: {}", transports.size(), filePath);
        } catch (IOException e) {
            logger.error("Failed to load cargo transport data from JSON file: {}", filePath, e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalTransportCount() {
        logger.debug("Calculating total cargo transport count");
        List<TransportCargoService> transports = transportRepo.getAll(0, Integer.MAX_VALUE, "id", true);
        int count = transports.size();
        logger.info("Total cargo transport count: {}", count);
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getTotalRevenue() {
        logger.debug("Calculating total cargo transport revenue");
        List<TransportCargoService> transports = transportRepo.getAll(0, Integer.MAX_VALUE, "id", true);
        BigDecimal revenue = transports.stream()
                .map(TransportCargoService::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("Total cargo transport revenue: {}", revenue);
        return revenue;
    }
}