package services.IO;

import data.models.Client;
import data.models.TransportCompany;
import data.models.employee.Dispatcher;
import data.models.employee.Driver;
import data.models.employee.Employee;
import data.models.employee.Qualification;
import data.models.transportservices.Destination;
import data.models.transportservices.TransportCargoService;
import data.models.transportservices.TransportPassengersService;
import data.models.transportservices.TransportService;
import data.models.vehicles.*;
import data.repositories.IGenericRepository;
import data.repositories.GenericRepository;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.data.dto.vehicles.BusCreateDTO;
import services.data.dto.vehicles.BusViewDTO;
import services.data.mapping.mappers.BusMapper;
import services.data.mapping.mappers.TransportPassengersServiceMapper;
import services.services.BusService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceSerializerTests {

    private SessionFactory sessionFactory;
    private IGenericRepository<Bus, Long> busRepo;
    private IGenericRepository<TransportCompany, Long> companyRepo;
    private IGenericRepository<TransportPassengersService, Long> transportServiceRepo;
    private IGenericRepository<Driver, Long> driverRepo;
    private IGenericRepository<Client, Long> clientRepo;
    private IGenericRepository<Destination, Long> destinationRepo;
    private IGenericRepository<Vehicle, Long> vehicleRepo;

    private ServiceSerializer<BusService, BusViewDTO, BusCreateDTO> serializer;
    private BusMapper busMapper;
    private TransportPassengersServiceMapper transportServiceMapper;

    private BusService busService;

    @BeforeEach
    void setUp() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(TransportCompany.class);
        configuration.addAnnotatedClass(Client.class);
        configuration.addAnnotatedClass(Employee.class);
        configuration.addAnnotatedClass(Driver.class);
        configuration.addAnnotatedClass(Dispatcher.class);
        configuration.addAnnotatedClass(Qualification.class);
        configuration.addAnnotatedClass(Destination.class);
        configuration.addAnnotatedClass(TransportService.class);
        configuration.addAnnotatedClass(TransportCargoService.class);
        configuration.addAnnotatedClass(TransportPassengersService.class);
        configuration.addAnnotatedClass(Vehicle.class);
        configuration.addAnnotatedClass(TransportCargoVehicle.class);
        configuration.addAnnotatedClass(TransportPeopleVehicle.class);
        configuration.addAnnotatedClass(Truck.class);
        configuration.addAnnotatedClass(Bus.class);
        configuration.addAnnotatedClass(Van.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        // Initialize serializer
        serializer = new ServiceSerializer<>(busService, BusViewDTO.class, BusCreateDTO.class);

        // Initialize repositories
        companyRepo = new GenericRepository<>(sessionFactory, TransportCompany.class);
        busRepo = new GenericRepository<>(sessionFactory, Bus.class);
        transportServiceRepo = new GenericRepository<>(sessionFactory, TransportPassengersService.class);
        driverRepo = new GenericRepository<>(sessionFactory, Driver.class);
        clientRepo = new GenericRepository<>(sessionFactory, Client.class);
        destinationRepo = new GenericRepository<>(sessionFactory, Destination.class);
        vehicleRepo = new GenericRepository<>(sessionFactory, Vehicle.class);

        // Initialize mappers
        busMapper = new BusMapper(companyRepo);

        // Instantiate BusService with its dependencies
        busService = new BusService(busRepo, companyRepo, transportServiceRepo,busMapper, transportServiceMapper);


    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    // ### Happy Path Tests
    @Test
    void SerializeToJson_ValidList_ReturnsJsonString() {
        BusViewDTO bus1 = new BusViewDTO();
        bus1.setRegistrationPlate("ABC123");
        bus1.setMaxPassengerCapacity(50);
        bus1.setHasRestroom(true);
        bus1.setLuggageCapacity(new BigDecimal("500.00"));
        bus1.setTransportCompanyId(1L);

        BusViewDTO bus2 = new BusViewDTO();
        bus2.setRegistrationPlate("DEF456");
        bus2.setMaxPassengerCapacity(40);
        bus2.setHasRestroom(false);
        bus2.setLuggageCapacity(new BigDecimal("300.00"));
        bus2.setTransportCompanyId(1L);

        List<BusViewDTO> buses = List.of(bus1, bus2);
        String json = serializer.serializeToJson(buses);
        assertNotNull(json);
        assertTrue(json.contains("ABC123"));
        assertTrue(json.contains("DEF456"));
    }

    @Test
    void DeserializeFromJson_ValidJson_ReturnsBusCreateDTO() {
        String json = "{\"registrationPlate\":\"GHI789\",\"maxPassengerCapacity\":30,\"hasRestroom\":true,\"luggageCapacity\":400.00,\"transportCompanyId\":1}";
        BusCreateDTO dto = serializer.deserializeFromJson(json);
        assertNotNull(dto);
        assertEquals("GHI789", dto.getRegistrationPlate());
        assertEquals(30, dto.getMaxPassengerCapacity());
        assertTrue(dto.getHasRestroom());
        assertEquals(new BigDecimal("400.00"), dto.getLuggageCapacity());
        assertEquals(1L, dto.getTransportCompanyId());
    }

    @Test
    void SerializeToBinary_ValidList_CreatesFile() throws Exception {
        BusViewDTO bus1 = new BusViewDTO();
        bus1.setRegistrationPlate("JKL012");
        bus1.setMaxPassengerCapacity(25);
        bus1.setHasRestroom(false);
        bus1.setLuggageCapacity(new BigDecimal("250.00"));
        bus1.setTransportCompanyId(1L);

        BusViewDTO bus2 = new BusViewDTO();
        bus2.setRegistrationPlate("MNO345");
        bus2.setMaxPassengerCapacity(35);
        bus2.setHasRestroom(true);
        bus2.setLuggageCapacity(new BigDecimal("350.00"));
        bus2.setTransportCompanyId(1L);

        List<BusViewDTO> buses = List.of(bus1, bus2);
        String filePath = "test_buses.ser";
        serializer.serializeToBinary(buses, filePath);
        File file = new File(filePath);
        assertTrue(file.exists());
        if (!file.delete()) {
            System.err.println("Failed to delete test file: " + filePath);
        }
    }

    @Test
    void DeserializeFromBinary_ValidFile_ReturnsBusCreateDTO() throws Exception {
        BusCreateDTO dto = new BusCreateDTO();
        dto.setRegistrationPlate("PQR678");
        dto.setMaxPassengerCapacity(20);
        dto.setHasRestroom(true);
        dto.setLuggageCapacity(new BigDecimal("200.00"));
        dto.setTransportCompanyId(1L);

        String filePath = "test_bus_create.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(dto);
        }

        BusCreateDTO deserializedDto = serializer.deserializeFromBinary(filePath);
        assertNotNull(deserializedDto);
        assertEquals("PQR678", deserializedDto.getRegistrationPlate());
        assertEquals(20, deserializedDto.getMaxPassengerCapacity());
        assertTrue(deserializedDto.getHasRestroom());
        assertEquals(new BigDecimal("200.00"), deserializedDto.getLuggageCapacity());
        assertEquals(1L, deserializedDto.getTransportCompanyId());

        File file = new File(filePath);
        if (!file.delete()) {
            System.err.println("Failed to delete test file: " + filePath);
        }
    }

    // ### Edge Case Tests
    @Test
    void SerializeToJson_EmptyList_ReturnsEmptyArrayJson() {
        List<BusViewDTO> emptyList = new ArrayList<>();
        String json = serializer.serializeToJson(emptyList);
        assertEquals("[]", json);
    }

    @Test
    void DeserializeFromJson_EmptyJsonObject_ReturnsDefaultDTO() {
        String json = "{}";
        BusCreateDTO dto = serializer.deserializeFromJson(json);
        assertNotNull(dto);
        assertNull(dto.getRegistrationPlate());
        assertNull(dto.getMaxPassengerCapacity());
        assertNull(dto.getHasRestroom());
        assertNull(dto.getLuggageCapacity());
        assertNull(dto.getTransportCompanyId());
    }

    @Test
    void SerializeToJson_SingleElement_ReturnsValidJson() {
        BusViewDTO bus = new BusViewDTO();
        bus.setRegistrationPlate("STU901");
        bus.setMaxPassengerCapacity(15);
        bus.setHasRestroom(false);
        bus.setLuggageCapacity(new BigDecimal("150.00"));
        bus.setTransportCompanyId(1L);

        List<BusViewDTO> buses = List.of(bus);
        String json = serializer.serializeToJson(buses);
        assertNotNull(json);
        assertTrue(json.contains("STU901"));
    }

    @Test
    void DeserializeFromJson_MinimalFields_ReturnsPartiallyPopulatedDTO() {
        String json = "{\"registrationPlate\":\"VWX234\",\"maxPassengerCapacity\":10}";
        BusCreateDTO dto = serializer.deserializeFromJson(json);
        assertNotNull(dto);
        assertEquals("VWX234", dto.getRegistrationPlate());
        assertEquals(10, dto.getMaxPassengerCapacity());
        assertNull(dto.getHasRestroom());
        assertNull(dto.getLuggageCapacity());
        assertNull(dto.getTransportCompanyId());
    }

    // ### Error Case Tests
    @Test
    void SerializeToJson_NullList_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                serializer.serializeToJson(null));
        assertEquals("DTO list cannot be null", exception.getMessage());
    }

    @Test
    void DeserializeFromJson_NullJson_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                serializer.deserializeFromJson(null));
        assertEquals("JSON string cannot be null or empty", exception.getMessage());
    }

    @Test
    void DeserializeFromJson_EmptyString_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                serializer.deserializeFromJson(""));
        assertEquals("JSON string cannot be null or empty", exception.getMessage());
    }

    @Test
    void DeserializeFromJson_InvalidJson_ThrowsJsonSyntaxException() {
        String invalidJson = "{invalid json}";
        assertThrows(com.google.gson.JsonSyntaxException.class, () ->
                serializer.deserializeFromJson(invalidJson));
    }

    @Test
    void SerializeToBinary_NullList_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                serializer.serializeToBinary(null, "test.ser"));
        assertEquals("DTO list cannot be null", exception.getMessage());
    }

    @Test
    void SerializeToBinary_NullFilePath_ThrowsIllegalArgumentException() {
        List<BusViewDTO> buses = new ArrayList<>();
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                serializer.serializeToBinary(buses, null));
        assertEquals("File path cannot be null or empty", exception.getMessage());
    }

    @Test
    void SerializeToBinary_EmptyFilePath_ThrowsIllegalArgumentException() {
        List<BusViewDTO> buses = new ArrayList<>();
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                serializer.serializeToBinary(buses, ""));
        assertEquals("File path cannot be null or empty", exception.getMessage());
    }

    @Test
    void DeserializeFromBinary_NullFilePath_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                serializer.deserializeFromBinary(null));
        assertEquals("File path cannot be null or empty", exception.getMessage());
    }

    @Test
    void DeserializeFromBinary_EmptyFilePath_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                serializer.deserializeFromBinary(""));
        assertEquals("File path cannot be null or empty", exception.getMessage());
    }

    @Test
    void DeserializeFromBinary_NonExistentFile_ThrowsFileNotFoundException() {
        Exception exception = assertThrows(FileNotFoundException.class, () ->
                serializer.deserializeFromBinary("nonexistent.ser"));
        assertTrue(exception.getMessage().contains("nonexistent.ser"));
    }
}