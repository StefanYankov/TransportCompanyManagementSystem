//package services.services;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.reflect.TypeToken;
//import data.common.seeding.LocalDateAdapter;
//import data.common.seeding.LocalDateTimeAdapter;
//import data.models.Client;
//import data.models.TransportCompany;
//import data.models.employee.Driver;
//import data.models.transportservices.Destination;
//import data.models.transportservices.TransportPassengersService;
//import data.models.vehicles.Vehicle;
//import data.repositories.IGenericRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import services.data.dto.transportservices.TransportPassengersServiceCreateDTO;
//import services.data.dto.transportservices.TransportPassengersServiceUpdateDTO;
//import services.data.mapping.mappers.TransportPassengersServiceMapper;
//import services.services.contracts.ITransportPassengersServiceService;
//
//import java.io.*;
//import java.lang.reflect.Type;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.CompletableFuture;
//
///**
// * Service implementation for managing {@link TransportPassengersService} entities.
// */
//public class TransportPassengersServiceService implements ITransportPassengersServiceService, Serializable {
//
//    @Serial
//    private static final long serialVersionUID = 1L;
//    private static final Logger logger = LoggerFactory.getLogger(TransportPassengersServiceService.class);
//    private final IGenericRepository<TransportPassengersService, Long> transportRepo;
//    private final TransportPassengersServiceMapper mapper;
//    private final IGenericRepository<TransportCompany, Long> companyRepo;
//    private final IGenericRepository<Destination, Long> destinationRepo;
//    private final IGenericRepository<Client, Long> clientRepo;
//    private final IGenericRepository<Vehicle, Long> vehicleRepo;
//    private final IGenericRepository<Driver, Long> driverRepo;
//    private final Gson gson;
//
//    public TransportPassengersServiceService(IGenericRepository<TransportPassengersService, Long> transportRepo,
//                                             TransportPassengersServiceMapper mapper,
//                                             IGenericRepository<TransportCompany, Long> companyRepo,
//                                             IGenericRepository<Destination, Long> destinationRepo,
//                                             IGenericRepository<Client, Long> clientRepo,
//                                             IGenericRepository<Vehicle, Long> vehicleRepo,
//                                             IGenericRepository<Driver, Long> driverRepo) {
//        this.transportRepo = transportRepo;
//        this.mapper = mapper;
//        this.companyRepo = companyRepo;
//        this.destinationRepo = destinationRepo;
//        this.clientRepo = clientRepo;
//        this.vehicleRepo = vehicleRepo;
//        this.driverRepo = driverRepo;
//        this.gson = new GsonBuilder()
//                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
//                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
//                .setPrettyPrinting()
//                .create();
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public TransportPassengersService create(TransportPassengersServiceCreateDTO dto) {
//        logger.debug("Creating passenger transport service with DTO: {}", dto);
//        TransportPassengersService entity = mapper.toEntity(dto);
//        TransportPassengersService result = transportRepo.create(entity);
//        logger.info("Passenger transport service created with ID: {}", result.getId());
//        return result;
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public CompletableFuture<TransportPassengersService> createAsync(TransportPassengersServiceCreateDTO dto) {
//        logger.debug("Async creating passenger transport service with DTO: {}", dto);
//        TransportPassengersService entity = mapper.toEntity(dto);
//        return transportRepo.createAsync(entity)
//                .whenComplete((result, throwable) -> {
//                    if (throwable != null) {
//                        logger.error("Failed to create passenger transport service asynchronously", throwable);
//                    } else {
//                        logger.info("Passenger transport service created asynchronously with ID: {}", result.getId());
//                    }
//                });
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public TransportPassengersService update(TransportPassengersServiceUpdateDTO dto) {
//        logger.debug("Updating passenger transport service with DTO: {}", dto);
//        TransportPassengersService entity = mapper.toEntity(dto);
//        TransportPassengersService result = transportRepo.update(entity);
//        logger.info("Passenger transport service updated with ID: {}", result.getId());
//        return result;
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public CompletableFuture<TransportPassengersService> updateAsync(TransportPassengersServiceUpdateDTO dto) {
//        logger.debug("Async updating passenger transport service with DTO: {}", dto);
//        TransportPassengersService entity = mapper.toEntity(dto);
//        return transportRepo.updateAsync(entity)
//                .whenComplete((result, throwable) -> {
//                    if (throwable != null) {
//                        logger.error("Failed to update passenger transport service asynchronously", throwable);
//                    } else {
//                        logger.info("Passenger transport service updated asynchronously with ID: {}", result.getId());
//                    }
//                });
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public void delete(Long id) {
//        logger.debug("Deleting passenger transport service with ID: {}", id);
//        TransportPassengersService entity = transportRepo.getById(id);
//        if (entity != null) {
//            transportRepo.delete(entity);
//            logger.info("Passenger transport service deleted with ID: {}", id);
//        } else {
//            logger.warn("No passenger transport service found to delete with ID: {}", id);
//        }
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public CompletableFuture<Void> deleteAsync(Long id) {
//        logger.debug("Async deleting passenger transport service with ID: {}", id);
//        return transportRepo.getByIdAsync(id)
//                .thenAccept(entity -> {
//                    if (entity != null) {
//                        transportRepo.deleteAsync(entity)
//                                .whenComplete((v, throwable) -> {
//                                    if (throwable != null) {
//                                        logger.error("Failed to delete passenger transport service asynchronously", throwable);
//                                    } else {
//                                        logger.info("Passenger transport service deleted asynchronously with ID: {}", id);
//                                    }
//                                });
//                    } else {
//                        logger.warn("No passenger transport service found to delete asynchronously with ID: {}", id);
//                    }
//                });
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public TransportPassengersService getById(Long id) {
//        logger.debug("Retrieving passenger transport service with ID: {}", id);
//        TransportPassengersService result = transportRepo.getById(id);
//        if (result != null) {
//            logger.info("Passenger transport service retrieved with ID: {}", id);
//        } else {
//            logger.warn("No passenger transport service found with ID: {}", id);
//        }
//        return result;
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public List<TransportPassengersService> getAll(int page, int size, String orderBy, boolean ascending) {
//        logger.debug("Retrieving all passenger transport services, page: {}, size: {}, orderBy: {}, ascending: {}", page, size, orderBy, ascending);
//        List<TransportPassengersService> result = transportRepo.getAll(page, size, orderBy, ascending);
//        logger.info("Retrieved {} passenger transport services", result.size());
//        return result;
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public List<TransportPassengersService> getTransportsSortedByDestination(boolean ascending) {
//        logger.debug("Retrieving passenger transports sorted by destination, ascending: {}", ascending);
//        List<TransportPassengersService> result = transportRepo.getAll(0, Integer.MAX_VALUE, "destination.startingLocation", ascending);
//        logger.info("Retrieved {} passenger transports sorted by destination", result.size());
//        return result;
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public void saveToBinaryFile(String filePath) throws IOException {
//        logger.debug("Saving passenger transport data to binary file: {}", filePath);
//        List<TransportPassengersService> transports = transportRepo.getAll(0, Integer.MAX_VALUE, "id", true);
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
//            oos.writeObject(transports);
//            logger.info("Saved {} passenger transports to binary file: {}", transports.size(), filePath);
//        } catch (IOException e) {
//            logger.error("Failed to save passenger transport data to binary file: {}", filePath, e);
//            throw e;
//        }
//    }
//
//    /** {@inheritDoc} */
//    @SuppressWarnings("unchecked")
//    @Override
//    public void loadFromBinaryFile(String filePath) throws IOException, ClassNotFoundException {
//        logger.debug("Loading passenger transport data from binary file: {}", filePath);
//        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
//            List<TransportPassengersService> transports = (List<TransportPassengersService>) ois.readObject();
//            for (TransportPassengersService transport : transports) {
//                transportRepo.create(transport);
//            }
//            logger.info("Loaded {} passenger transports from binary file: {}", transports.size(), filePath);
//        } catch (IOException | ClassNotFoundException e) {
//            logger.error("Failed to load passenger transport data from binary file: {}", filePath, e);
//            throw e;
//        }
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public void saveToJsonFile(String filePath) throws IOException {
//        logger.debug("Saving passenger transport data to JSON file: {}", filePath);
//        List<TransportPassengersService> transports = transportRepo.getAll(0, Integer.MAX_VALUE, "id", true);
//        try (FileWriter writer = new FileWriter(filePath)) {
//            gson.toJson(transports, writer);
//            logger.info("Saved {} passenger transports to JSON file: {}", transports.size(), filePath);
//        } catch (IOException e) {
//            logger.error("Failed to save passenger transport data to JSON file: {}", filePath, e);
//            throw e;
//        }
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public void loadFromJsonFile(String filePath) throws IOException {
//        logger.debug("Loading passenger transport data from JSON file: {}", filePath);
//        try (FileReader reader = new FileReader(filePath)) {
//            Type listType = new TypeToken<List<TransportPassengersService>>(){}.getType();
//            List<TransportPassengersService> transports = gson.fromJson(reader, listType);
//            for (TransportPassengersService transport : transports) {
//                transportRepo.create(transport);
//            }
//            logger.info("Loaded {} passenger transports from JSON file: {}", transports.size(), filePath);
//        } catch (IOException e) {
//            logger.error("Failed to load passenger transport data from JSON file: {}", filePath, e);
//            throw e;
//        }
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public int getTotalTransportCount() {
//        logger.debug("Calculating total passenger transport count");
//        List<TransportPassengersService> transports = transportRepo.getAll(0, Integer.MAX_VALUE, "id", true);
//        int count = transports.size();
//        logger.info("Total passenger transport count: {}", count);
//        return count;
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public BigDecimal getTotalRevenue() {
//        logger.debug("Calculating total passenger transport revenue");
//        List<TransportPassengersService> transports = transportRepo.getAll(0, Integer.MAX_VALUE, "id", true);
//        BigDecimal revenue = transports.stream()
//                .map(TransportPassengersService::getPrice)
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        logger.info("Total passenger transport revenue: {}", revenue);
//        return revenue;
//    }
//}